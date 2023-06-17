package toogoo.RespPojo.ws;


/**
 * 收到消息类型枚举
 *
 * @author 29669
 * @date 2023/06/12
 */

public enum ReqMessageType {

    Ping("模拟心跳检核"),
    /**
     * 开始匹配
     */
    StartMatching("开始匹配"),
    MatchAi("匹配人机"),
    UnMatch("取消匹配"),
    CreatRoom("创建房间"),
    Drop("落子"),
    Start("有人发起开启游戏");
    private final String value;
    public String getValue(){
        return value;
    }

    ReqMessageType(String value){
        this.value = value;
    }
}
