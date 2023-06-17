package gobang.manage;

import gobang.pojo.game.GameConfigPojo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

import static gobang.pojo.game.GameConfigPojo.GAME_CONFIG;

@Component
public class MatchQueuesManage {


    /**
     * 匹配队列映射
     * <uid><rank>
     */
    private final ConcurrentHashMap<Long, Integer> matchQueueMap = new ConcurrentHashMap<>();


    @Autowired
    private GameManage gameManage;

    @Autowired
    private GobangManage gobangManage;

    /**
     * 加入比赛
     *
     * @param id   id
     * @param rank 排名
     * @return {@link Long}
     */
    public synchronized Long joinMatch(Long id, Integer rank){
        if (id == null || rank == null) return null;
        matchQueueMap.remove(id);

        /*
         * TODO 匹配逻辑
         */
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (gameManage.getGame(id) != null){
            return null;
        }

        for (Long rival : matchQueueMap.keySet()){
            if (Math.abs(matchQueueMap.get(rival) - rank) < 100){

                remove(rival);
                gameManage.createGame(rival, id, GAME_CONFIG);

                return rival;
            }

        }
        matchQueueMap.put(id, rank);
        return null;
    }

    public synchronized void remove(Long uid) {
        if (uid != null) matchQueueMap.remove(uid);
    }
}
