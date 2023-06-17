package gobang.service;

import gobang.pojo.Game;
import gobang.pojo.game.Step;
import gobang.ws.GoBangSocket;
import toogoo.RespPojo.Message;
import toogoo.game.MatchGame;

public interface GoBangService {

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
     *
     * @param game 游戏
     */
    void settlement(Game game);

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
}
