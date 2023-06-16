package gobang.manage.impl;

import gobang.manage.GameManage;
import gobang.manage.GobangManage;
import gobang.pojo.Game;
import gobang.ws.GoBangSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import toogoo.RespPojo.ws.RespMessage;
import toogoo.game.GameRoom;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class GobangManageImpl implements GobangManage {

    private final ConcurrentHashMap<Long, GoBangSocket> gobangMap = new ConcurrentHashMap<>();

    @Autowired
    private GameManage gameManage;

    @Override
    public void add(GoBangSocket socket) {
        if (socket == null) return;
        Long uid = socket.getUid();
        if (uid == null) return;
        gobangMap.put(uid, socket);
    }

    @Override
    public synchronized void sendMessage(Long uid, RespMessage message) {
        GoBangSocket goBangSocket = gobangMap.get(uid);
        if (goBangSocket != null) goBangSocket.sendMessage(message);
    }

    @Override
    public void sendMessage(GameRoom gameRoom, RespMessage message) {
        if (gameRoom != null){
            sendMessage(gameRoom.getPlayer1(), message);
            sendMessage(gameRoom.getPlayer2(), message);
        }
    }

    @Override
    public void sendMessage(String roomId, RespMessage message) {
        // TODO
        sendMessage(gameManage.getGame(roomId), message);
    }

    @Override
    public void remove(Long uid) {
        if (uid != null) gobangMap.remove(uid);
    }

    @Override
    public void remove(GameRoom gameRoom) {
        if (gameRoom != null){
            remove(gameRoom.getPlayer1());
            remove(gameRoom.getPlayer2());
        }
    }

    @Override
    public void remove(String roomId) {
        //  TODO
    }
}
