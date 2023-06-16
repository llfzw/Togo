package toogoo.RespPojo.ws;

import lombok.Data;

@Data
public class RespMessage{

    private RespMessageType type;
    private Object data;

    public RespMessage(RespMessageType type, Object data) {
        this.type = type;
        this.data = data;
    }

}