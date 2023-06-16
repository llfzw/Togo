package top.togo.main.config;

/*
 * 获取HttpSession
 *
 */

import jakarta.servlet.http.HttpSession;
import jakarta.websocket.HandshakeResponse;
import jakarta.websocket.server.HandshakeRequest;
import jakarta.websocket.server.ServerEndpointConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Cookie;

import java.util.Iterator;
import java.util.List;

@Slf4j
public class GetHttpSessionConfigurator extends ServerEndpointConfig.Configurator {

    @Override
    public void modifyHandshake(ServerEndpointConfig sec,
                                HandshakeRequest request, HandshakeResponse response) {

        String token = null;
        // TODO Auto-generated method stub
        List<String> cookies = request.getHeaders().get("cookie");
        for (String str : cookies) {
            String[] split1 = str.split(";");
            for (String str1 : split1){
                String[] t = str1.split("=");
//                log.info(t[0].trim());
                if (t.length != 2) continue;
                if ("token".equals(t[0].trim())){
                    token = t[1];
                }
            }
        }
        sec.getUserProperties().put("token", token);
    }

}

