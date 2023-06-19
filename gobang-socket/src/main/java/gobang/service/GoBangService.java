package gobang.service;

import gobang.pojo.Game;
import gobang.pojo.game.Step;
import gobang.ws.GoBangSocket;
import toogoo.RespPojo.Message;
import toogoo.RespPojo.ws.RespMessage;
import toogoo.game.MatchGame;

public interface GoBangService{

    /**
     * 落子
     */
    void drop(Long id, Object data);

    void drop(Step step);

    /**
     * 加入匹配队列
     *
     * @return
     */
    Long joinMatch(MatchGame matchGame);

    /**
     * 删除匹配
     *
     * @param uid uid
     */
    void removeMatch(Long uid);

    /**
     * 获取Uid
     *
     * @param token 令牌
     * @return {@link Long}
     */
    Long getUidByToken(String token);

    void onOpen(GoBangSocket goBangSocket);

    /**
     * 在关闭
     *
     * @param uid uid
     */
    void onClose(Long uid);

    /**
     * 游戏结束
     */
    void gameOver(String roomId);


    /**
     * 结算
     */
    void settlement(Long winId, Long failId);

    /**
     * 保存游戏
     *
     * @param game 游戏
     */
    void saveGame(Game game);

    /**
     * 得到资产
     *
     * @param token token
     * @return {@link Message}
     */
    Message getAssets(String token);

    Message rivalInfoByToken(String token);

    Message gameInfoByToken(String token);

    void startGame(Long uid);

    /**
     * 投降
     *
     * @param uid uid
     */
    void surrender(Long uid);

    /**
     * 悔棋
     *
     * @param uid  uid
     * @param data 数据
     */
    void backChess(Long uid, Object data);

    /**
     * 发送消息 (为了在游戏线程中通知前端，特意写的一个方法)
     *
     * @param game        游戏
     * @param respMessage 分别地消息
     */
    void sendMessage(Game game, RespMessage respMessage);

    /**
     * 游戏排行
     *
     * @return {@link Message}
     */
    Message gameTop();
}
