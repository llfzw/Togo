package toogoo.RespPojo;

import lombok.Data;

/**
 * 响应消息通用类
 */
@Data
public class Message {

    private Integer code = 200;

    private String msg;

    private Object data;

    private Message(String msg, Object data) {
        this.msg = msg;
        this.data = data;
    }
    private Message(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public static Message ok(Object data){
        return new Message(null, data);
    }
    public static Message ok(String msg){
        return new Message(msg, null);
    }
    public static Message ok(String msg, Object data){
        return new Message( msg, data);
    }

    public static Message fail(int code, String msg){
        return new Message(code, msg);
    }

    public static Message fail(String msg){
        return new Message(400, msg);
    }


}
