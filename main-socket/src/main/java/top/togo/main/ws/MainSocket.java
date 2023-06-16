package top.togo.main.ws;


import cn.hutool.json.JSONUtil;
import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import toogoo.Utils.JwtTokenUtil;
import top.togo.main.config.GetHttpSessionConfigurator;
import toogoo.RespPojo.ws.RespMessage;
import toogoo.RespPojo.ws.RespMessageType;
import toogoo.RespPojo.ws.SocketMsg;
import top.togo.main.manage.OnlineUserManage;
import top.togo.main.service.MainService;

import java.io.IOException;


@Component
@ServerEndpoint(value = "/socket/main", configurator = GetHttpSessionConfigurator.class)
@Slf4j
@Data
public class MainSocket {
    private Session session;
    private Long uid;


    private static JwtTokenUtil jwtTokenUtil;
    private static OnlineUserManage onlineUserManage;
    private static MainService mainService;

    @OnOpen
    public void onOpen(Session session,EndpointConfig config) {
        this.session = session;

        String token = String.valueOf(config.getUserProperties().get("token"));
        if (token == null || jwtTokenUtil.isTokenExpired(token)) return;

        this.uid = jwtTokenUtil.getClaimsFormToken(token).get("id", Long.class);

        onlineUserManage.add(this);
        log.info("id --> " + uid + " ---> 连接成功");

    }


    @OnMessage
    public void onMessage(String message){
        SocketMsg msg = null;
        try {
            msg = JSONUtil.toBean(message, SocketMsg.class);
            log.info(msg.getType().getValue());
        }catch (Exception e){
            log.info("收到的消息转换类型错误 -->  : " + message);
        }
        if (msg == null) return;

        switch (msg.getType()){

            /**
             * 匹配
             */
            case StartMatching -> {
                mainService.match(uid, msg.getData());
            }


            /**
             * 取消匹配
             */
            case UnMatch -> {
                mainService.unMatch(uid, msg.getData());
            }



            case Ping -> {
                log.info("客户端心跳检测！ Ping");
            }
        }

    }

    @OnClose
    public void onClose(Session session){
        onlineUserManage.remove(uid);          // 删除在线用户
        mainService.unMatch(uid, null);   // 取消匹配队列
        log.info(String.format("server: %s断开连接", uid));
    }

    @OnError
    public void onError(Session session,Throwable error){
        log.info("连接错误 执行断开连接");
        onClose(session);
    }


    /**
     * 发送消息
     */
    public void sendMessage(RespMessage message){
        this.session.getAsyncRemote().sendText(JSONUtil.toJsonStr(JSONUtil.parseObj(message, false)));
    }


    @Autowired
    public void setJwtTokenUtil(JwtTokenUtil jwtTokenUtil) {
        MainSocket.jwtTokenUtil = jwtTokenUtil;
    }

    @Autowired
    public void setMainService(MainService mainService) {
        MainSocket.mainService = mainService;
    }

    @Autowired
    public void setOnlineUserManage(OnlineUserManage onlineUserManage) {
        MainSocket.onlineUserManage = onlineUserManage;
    }
}
