package gobang.ws;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import gobang.config.GetHttpSessionConfigurator;
import gobang.pojo.game.Step;
import gobang.service.GoBangService;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import toogoo.RespPojo.ws.RespMessage;
import toogoo.RespPojo.ws.SocketMsg;

import java.io.IOException;


@Component
@ServerEndpoint(value = "/gobang/socket", configurator = GetHttpSessionConfigurator.class)
@Slf4j
@Data
public class GoBangSocket {

    private Session session;
    private Long uid;

    public static GoBangService goBangService;

    @OnOpen
    public void onOpen(Session session, EndpointConfig config){
        this.session = session;
        this.uid = goBangService.getUidByToken(String.valueOf(config.getUserProperties().get("token")));
        log.info("uid: " + uid + "    正在尝试连接");
        if (uid == null) return;
        log.info("id : " + uid + "  连接gobangSocket成功！");
        goBangService.onOpen(this);
    }

    @OnMessage
    public void onMessage(String message){
        log.info("server: 收到消息" + message);
        SocketMsg msg = null;
        try {
            msg = JSONUtil.toBean(message, SocketMsg.class);
        }catch (Exception e){
            log.info("收到的消息转换类型错误 -->  : " + message);
        }
        if (msg == null) return;

        switch (msg.getType()){
            case Drop -> {
                log.info("drop");
                goBangService.drop(uid, msg.getData());
            }

            case Start -> {
                goBangService.startGame(uid);

            }


            default -> {
                log.info("drop1");
            }
        }

    }


    @OnClose
    public void onClose(Session session){
        goBangService.onClose(uid);
        log.info(String.format("server: %s断开连接", uid));
    }

    @OnError
    public void onError(Session session,Throwable error){
//        onClose(session);
        log.info("连接错误");
        error.printStackTrace();
    }


    public void sendMessage(RespMessage message){
        this.session.getAsyncRemote().sendText(JSONUtil.toJsonStr(JSONUtil.parseObj(message, false)));
    }


    @Autowired
    public void setGoBangService(GoBangService goBangService) {
        GoBangSocket.goBangService = goBangService;
    }
}
