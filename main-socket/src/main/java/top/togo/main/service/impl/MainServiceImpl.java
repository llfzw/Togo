package top.togo.main.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toogoo.RespPojo.ws.RespMessage;
import toogoo.game.MatchGame;
import top.togo.main.clients.GobangClient;
import top.togo.main.manage.OnlineUserManage;
import top.togo.main.pojo.MatchPojo;
import top.togo.main.service.MainService;

import static toogoo.RespPojo.ws.RespMessageType.*;

@Slf4j
@Service
public class MainServiceImpl implements MainService {

    @Autowired
    private GobangClient gobangClient;

    @Autowired
    private OnlineUserManage onlineUserManage;

    public void match(Object matchObj) {
        MatchGame match = BeanUtil.toBean(matchObj, MatchGame.class);
        if (!match.getGameMode().equals("ai")){
            onlineUserManage.sendMessage(match.getUid(), new RespMessage(AlreadyMatching, AlreadyMatching.getValue()));
        }
        switch (match.getGameType()){

            case "gobang" ->{

                Long rival = gobangClient.joinMatch(JSONUtil.toJsonStr(match));
//                -1L 是排除人机
                if (rival == null || rival.equals(-1L)) {
//                    onlineUserManage.sendMessage(uid, new RespMessage(AlreadyMatching, AlreadyMatching.getValue()));
                    return;
                }
                RespMessage message = new RespMessage(MatchSuccess, "gobang");
                onlineUserManage.sendMessage(match.getUid(), message);
                onlineUserManage.sendMessage(rival, message);

            }

            case "chess" -> {

                log.error("chess功能未完成");
            }

        }
    }


    @Override
    public void match(Long uid, Object msg, String mode) {
        System.out.println(msg);
        MatchGame matchGame = BeanUtil.toBean(msg, MatchGame.class);
        System.out.println(matchGame);
        matchGame.setUid(uid);
        matchGame.setGameMode(mode);
        match(matchGame);
    }

    @Override
    public void unMatch(Long id, Object data) {
        gobangClient.unMatch(id);
        onlineUserManage.sendMessage(id, new RespMessage(UnMatchSuccess, UnMatchSuccess.getValue()));
    }
}
