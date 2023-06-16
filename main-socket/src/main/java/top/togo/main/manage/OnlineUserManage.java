package top.togo.main.manage;

import toogoo.RespPojo.ws.RespMessage;
import toogoo.game.GameRoom;
import top.togo.main.ws.MainSocket;

public interface OnlineUserManage {
    /**
     * 添加
     */
    void add(MainSocket socket);

    /**
     * 发送消息uid
     */
    void sendMessage(Long uid, RespMessage message);
    void sendMessage(GameRoom gameRoom, RespMessage message);
    void sendMessage(String roomId, RespMessage message);


    /**
     * 删除id
     */
    void remove(Long uid);
    void remove(GameRoom gameRoom);
    void remove(String roomId);

}
