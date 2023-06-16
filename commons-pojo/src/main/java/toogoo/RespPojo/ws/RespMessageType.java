package toogoo.RespPojo.ws;

/**
 * 请求消息类型枚举
 *
 * @author 29669
 * @date 2023/06/12
 */
public enum RespMessageType {

    MatchTimeOut("匹配超时"),
    AlreadyMatching("已加入匹配队列"),
    MatchSuccess("匹配成功"),
    UnMatchSuccess("取消匹配成功"),
    GameData("游戏数据"),
    Drop("落子"),
    GameOver("游戏结束"),
    Error("错误")
    ;
    private final String value;

    public String getValue(){
        return value;
    }
    RespMessageType(String value){
        this.value = value;
    }
}
