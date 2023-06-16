package toogoo.RespPojo.ws;

import lombok.Data;
import toogoo.RespPojo.ws.ReqMessageType;

@Data
public class SocketMsg {
    private ReqMessageType type;

    private Object data;
}
