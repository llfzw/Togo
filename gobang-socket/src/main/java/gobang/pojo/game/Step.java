package gobang.pojo.game;

import lombok.Data;

/**
 * 棋子
 */
@Data
public class Step {

    private Integer x;

    private Integer y;

    private Long uid;

    private Long time;

    public boolean isEmpty(){
        return x == null || y == null || uid == null;
    }

}
