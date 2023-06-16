package top.togo.main.manage.impl;

import org.springframework.stereotype.Component;
import toogoo.RespPojo.ws.RespMessage;
import toogoo.game.GameRoom;
import top.togo.main.manage.OnlineUserManage;
import top.togo.main.ws.MainSocket;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class OnlineUserManageImpl implements OnlineUserManage {

    private final ConcurrentHashMap<Long, MainSocket> onlineMap = new ConcurrentHashMap<>();

    @Override
    public void add(MainSocket socket) {
        if (socket == null) return;
        Long uid = socket.getUid();
        if (uid == null) return;
        onlineMap.put(uid, socket);
    }

    @Override
    public void sendMessage(Long uid, RespMessage message) {
        if (uid == null) return;
        MainSocket mainSocket = onlineMap.get(uid);
        if (mainSocket != null) mainSocket.sendMessage(message);
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
    }

    @Override
    public void remove(Long uid) {
        if (uid != null) onlineMap.remove(uid);
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
