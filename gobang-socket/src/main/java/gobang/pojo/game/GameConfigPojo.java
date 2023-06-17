package gobang.pojo.game;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GameConfigPojo {

    private int CheckBoardLength;  //棋盘宽度
    private int stepTime;          // 步骤时长
    private int backMaxWaitTime;   //悔棋最大等待时长
    private int firstPlayer;       // 先手判断规则  0为随机 1、2代表player1、2
    private int regretChess;        // 是否允许悔棋
    private int playerTime;        // 玩家总时间

    public static GameConfigPojo GAME_CONFIG = new GameConfigPojo(16, 30, 10, 0, 1, 60 * 5);

    public GameConfigPojo(int stepTime, int firstPlayer, int regretChess, int playerTime) {
        this.stepTime = stepTime;
        this.firstPlayer = firstPlayer;
        this.regretChess = regretChess;
        this.playerTime = playerTime;
    }
}
