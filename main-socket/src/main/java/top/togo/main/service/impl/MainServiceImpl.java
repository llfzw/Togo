package top.togo.main.service.impl;

import cn.hutool.core.bean.BeanUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import toogoo.RespPojo.ws.RespMessage;
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

    @Override
    public void match(Long uid, Object msg) {
        MatchPojo matchPojo = BeanUtil.toBean(msg, MatchPojo.class);
        onlineUserManage.sendMessage(uid, new RespMessage(AlreadyMatching, AlreadyMatching.getValue()));
        switch (matchPojo.getGameType()){

            case "gobang" ->{


                Long rival = gobangClient.joinMatch(uid, "online", matchPojo.getGameMode());
                if (rival == null) {
//                    onlineUserManage.sendMessage(uid, new RespMessage(AlreadyMatching, AlreadyMatching.getValue()));
                    return;
                }
                RespMessage message = new RespMessage(MatchSuccess, "gobang");
                onlineUserManage.sendMessage(uid, message);
                onlineUserManage.sendMessage(rival, message);

            }

            case "chess" -> {

                log.error("chess功能未完成");
            }

        }
    }

    @Override
    public void unMatch(Long id, Object data) {
        gobangClient.unMatch(id);
        onlineUserManage.sendMessage(id, new RespMessage(UnMatchSuccess, UnMatchSuccess.getValue()));
    }
}
