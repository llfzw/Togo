package gobang.service.impl;

import cn.hutool.core.bean.BeanUtil;
import gobang.clients.UserClient;
import gobang.dao.GobangAssetsDao;
import gobang.manage.GameManage;
import gobang.manage.GobangManage;
import gobang.manage.MatchQueuesManage;
import gobang.pojo.Game;
import gobang.pojo.GobangAssets;
import gobang.pojo.game.GameConfigPojo;
import gobang.pojo.game.Step;
import gobang.pojo.vo.RivalInfoVo;
import gobang.service.GoBangService;
import gobang.utils.GobangAi;
import gobang.ws.GoBangSocket;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import toogoo.RespPojo.Message;
import toogoo.RespPojo.ws.RespMessage;
import toogoo.RespPojo.ws.RespMessageType;
import toogoo.Utils.JwtTokenUtil;
import toogoo.entity.UserDto;
import toogoo.game.MatchGame;

import java.util.concurrent.CompletableFuture;

import static gobang.pojo.game.GameConfigPojo.GAME_CONFIG;
import static toogoo.RespPojo.ws.RespMessageType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoBangServiceImpl implements GoBangService {

    private final JwtTokenUtil jwtTokenUtil;
    private final GobangAssetsDao gobangAssetsDao;
    private final MatchQueuesManage matchQueuesManage;
    private final GameManage gameManage;
    private final GobangManage gobangManage;
    private final UserClient userClient;
    private final GobangAi gobangAi;

    @Override
    public void drop(Long uid, Object data) {

        Step step = BeanUtil.toBean(data, Step.class);
        if (step == null) {
            log.error("收到落子信息格式转化错误 ： " + data);
            return;
        }
        step.setUid(uid);
        drop(step);
    }

    @Override
    public void drop(Step step) {
        Long uid = step.getUid();
        Game game = gameManage.getGame(uid);
        if (game== null || !game.fallStep(step)) {
            gobangManage.sendMessage(uid, new RespMessage(RespMessageType.Error, "落子失败"));
            return;
        }
        gobangManage.sendMessage(game, new RespMessage(RespMessageType.Drop, step));

        if (game.getPlayer1().equals(-1L) || game.getPlayer2().equals(-1L)){
            CompletableFuture.runAsync(()->{
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Step aiStep = gobangAi.getStep(game.getCheckBoard().getCheckBoard());
                if (game.fallStep(aiStep)) {
                    log.info("AI落子成功");
                    gobangManage.sendMessage(game, new RespMessage(RespMessageType.Drop, aiStep));
                }else {
                    log.info("AI落子失败，请排查");
                }
            });
        }



    }

    @Override
    public Long joinMatch(MatchGame match) {
        log.info("收到匹配消息");
        System.out.println(match);
        if (match == null || match.getUid() == null) return null;

        Long uid = match.getUid();
        switch (match.getGameMode()) {
            /**
             * 在线模式
             */
            case "online" -> {
                GobangAssets gobangAssets = gobangAssetsDao.findByUid(uid);
                Integer rank = 0;
                if (gobangAssets == null){
//            如果是第一次进入 没有创建表
                    gobangAssetsDao.insertAssets(uid);
                    return matchQueuesManage.joinMatch(uid, 0);
                }else {
                    rank = gobangAssets.getRank();
                }
                return matchQueuesManage.joinMatch(uid, rank);
            }

            case "ai" -> {
                GameConfigPojo config =  BeanUtil.toBean(match.getConfig(), GameConfigPojo.class);
                config.setCheckBoardLength(GAME_CONFIG.getCheckBoardLength());
                Game game = gameManage.createGame(-1L, uid, config);
                if (!game.fallStep(new Step(7, 7, -1L))) {
                    log.info("人机第一颗落子失败");
                }
                return -1L;
            }

            default -> {
                log.info("未完成的模式1");
                return null;
            }
        }
    }

    @Override
    public void removeMatch(Long uid){
        if (uid != null) matchQueuesManage.remove(uid);
    }

    @Override
    public Long getUidByToken(String token) {
        Claims claims = jwtTokenUtil.getClaimsFormToken(token);
        if (claims == null) return null;
        return claims.get("id", Long.class);
    }

    @Override
    public void onOpen(GoBangSocket goBangSocket) {
        Long uid = goBangSocket.getUid();
        Game game = gameManage.getGame(uid);
        if (game == null) {
            log.error("未在对局中连接gobang socket 已拒绝连接！");
            return;
        }
        gobangManage.add(goBangSocket);
//        gobangManage.sendMessage(goBangSocket.getUid(), new RespMessage(GameData, game));
    }

    @Override
    public void onClose(Long uid) {
        gobangManage.remove(uid);
    }

    @Override
    public void gameOver(String roomId) {
        Game game = gameManage.getGame(roomId);
        // 发送游戏结束消息
        gobangManage.sendMessage(game, new RespMessage(GameOver, game));
        //  保存对局  和  结算积分 并行执行
        CompletableFuture.runAsync(()->saveGame(game)).thenRunAsync(()->settlement(game));
        gameManage.gameOver(roomId);
    }

    @Override
    public void settlement(Game game) {
        // TODO 结算积分
        CompletableFuture.supplyAsync(()-> gobangAssetsDao.findByUid(game.getPlayer1()));
    }

    @Override
    public void saveGame(Game game) {
        // TODO 保存对局

    }

    @Override
    public Message getAssets(String token) {
        Long uid = getUidByToken(token);
        if (uid == null) return Message.fail("token is not");
        GobangAssets assets = gobangAssetsDao.findByUid(uid);
        if (assets == null) return Message.fail(400, "error !");
        return Message.ok(assets);
    }

    @Override
    public Message rivalInfoByToken(String token) {
        Long uid = getUidByToken(token);
        Game game = gameManage.getGame(uid);
        if (game == null) return Message.fail("不在游戏中");
        Long rivalId = game.getPlayer1().equals(uid) ? game.getPlayer2() : game.getPlayer1();
        if (rivalId.equals(-1L)){

            return Message.ok(new RivalInfoVo(rivalId, "AI人机", 1, "a.png", "地区"));
        }

        RivalInfoVo rivalInfoVo = null;

        try {

            GobangAssets assets = gobangAssetsDao.findByUid(rivalId);
            UserDto userDto = userClient.findById(rivalId);
            rivalInfoVo = new RivalInfoVo(rivalId, userDto.getName(), assets.getGrade(), userDto.getHead(), userDto.getRegion());

        }catch (Exception e){
            log.info(e.toString());
            log.info("获取对手信息失败");
        }

        return Message.ok(rivalInfoVo);
    }

    @Override
    public Message gameInfoByToken(String token) {
        Long uid = getUidByToken(token);
        if (uid == null) return Message.fail("errr");
        Game game = gameManage.getGame(uid);
        if (game == null) return Message.fail("不在游戏中");
        return Message.ok(game);
    }

    @Override
    public void startGame(Long uid) {
        gameManage.getGame(uid).start();
    }


}
