package gobang.manage;

import gobang.pojo.Game;
import gobang.pojo.game.GameConfigPojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class GameManage {

    private final ConcurrentHashMap<String, Game> gameMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, String> gameIng = new ConcurrentHashMap<>();

    /**
     * 创建一局游戏
     * @param p1 玩家一
     * @param p2 玩家2
     * @param config 游戏配置
     * @return
     */
    public Game createGame(Long p1, Long p2, GameConfigPojo config){
        Game game = new Game(p1, p2, config);
        String gid = game.getRoomId();
        this.gameMap.put(gid, game);
        this.gameIng.put(p1, gid);
        this.gameIng.put(p2, gid);
        return game;
    }

    /**
     * 获取一局游戏
     * @param gid
     * @return
     */
    public Game getGame(String gid){
        return gameMap.get(gid);
    }

    /**
     * 获取一局游戏
     * @param id
     * @return
     */
    public Game getGame(Long id){
        if (!gameIng.containsKey(id)){
            return null;
        }
        return gameMap.get(gameIng.get(id));

    }

    /**
     * 删除一局游戏
     * @param gid
     */
    public void deleteGame(String gid){
        if (!gameMap.containsKey(gid)){
            log.info("需要删除的对局不存在");
            return ;
        }
        Game game = gameMap.get(gid);
//        从正在游戏的列表中删除
        gameIng.remove(game.getPlayer1());
        gameIng.remove(game.getPlayer2());
//        删除游戏
        gameMap.remove(gid);
    }


    public void gameOver(String roomId) {

        log.info("删除对局");
        deleteGame(roomId);
    }
}
