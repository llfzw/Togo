package gobang.pojo;

import cn.hutool.core.lang.UUID;
import gobang.pojo.game.CheckBoard;
import gobang.pojo.game.GameConfigPojo;
import gobang.pojo.game.Step;
import gobang.pojo.game.WinInfo;
import gobang.ws.GoBangSocket;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import toogoo.RespPojo.ws.RespMessage;
import toogoo.game.GameRoom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static gobang.pojo.game.GameConfigPojo.GAME_CONFIG;
import static toogoo.RespPojo.ws.RespMessageType.refuseBackChess;

@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class Game extends GameRoom {

    private Long createTime = System.currentTimeMillis();
    private Long firstPlayer;        // 先手
    private CheckBoard checkBoard;   // 棋盘
    private List<Step> steps = new ArrayList<>();  //棋盘

    private int stepTime;           //步骤时长
    private int totalTime = 0;      //总时长
    private int remainder1;
    private int remainder2;


    private Boolean state;
    private GameConfigPojo config;   // 游戏对局配置
    private transient Thread timer; // 计时线程

    private transient boolean backChess = false; // 悔棋标识， 用户发送悔棋指令时变为true, 这是对手同意悔棋才能执行悔棋
    private transient boolean drawChess = false;

    private WinInfo winInfo;

    public Game(Long player1, Long player2, GameConfigPojo config){
        super.setGameType("gobang");
        super.setPlayer1(player1);
        super.setPlayer2(player2);
        super.setRoomId(UUID.randomUUID().toString());

        this.state = false;
        this.config = config;
        this.stepTime = config.getStepTime() + 6;
        this.remainder1 = config.getPlayerTime();
        this.remainder2 = config.getPlayerTime();

        checkBoard = new CheckBoard(config.getCheckBoardLength());
//       根据配置设置先手等信息
        setFirst(config.getFirstPlayer());
    }
    public void start(){
        if (state) return;

        //        3.开启计时线程
        this.state = true;
        this.startThread();
    }

    private void setFirst(int c) {
        switch (c){
            case 0: {
                int i = new Random().nextInt(2);
                setFirst(i + 1);
                break;
            }
            case 1: {
                firstPlayer = getPlayer1();
            }
            case 2: {
                firstPlayer = getPlayer2();
            }
        }
    }

    /**
     * 落子
     * @return boolean
     */
    public synchronized boolean fallStep(Step step){
        //  落子功能
//        判断是否该本人下棋
        int x = step.getX();
        int y = step.getY();
        int length = config.getCheckBoardLength() - 1;
        if (!getNextPlayerId().equals(step.getUid()) || x < 0 || y < 0 || x > length || y > length || checkBoard.contains(x, y)){
            return false;
        }

        int time = config.getStepTime() - this.stepTime;
        this.stepTime = config.getStepTime();
        checkBoard.set(step, step.getUid().equals(firstPlayer) ? 1 : 2);
        step.setTime((long) time);
        steps.add(step);

        CompletableFuture.supplyAsync(() -> {
            if (decide(x, y)){
                winInfo = new WinInfo(step.getUid(), "win");
                gameOver();
            }
            return null;
        });

        return true;
    }

    /**
     * 悔棋 （加入是你发起悔棋应该由对手来调用该方法）
     *
     * @param uid 同意悔棋的人
     * @return boolean
     */
    public boolean backChess(Long uid){
        if (!this.backChess || !uid.equals(getNextPlayerId()) ) return false;
        int index = steps.size() - 1;
        Step step = steps.get(index);
        checkBoard.set(step, 0);
        steps.remove(index);
        this.backChess = false;
        return true;
    }

    /**
     * 获取下一步该谁下棋
     * @return userId
     */
    public Long getNextPlayerId(){
        // 获取下一步该谁下棋
        if (steps.size() % 2 == 0){
            return firstPlayer;
        }
        return firstPlayer.equals(getPlayer1()) ? getPlayer2(): getPlayer1();
    }

    private Long backPlayer() {
        return getNextPlayerId().equals(getPlayer1()) ? getPlayer2() : getPlayer1();
    }

    /**
     * 开启计时线程
     */
    private void startThread() {
        log.info("准备开启计时线程");
        // 开启计时线程
        this.timer = new Thread(() -> {
            log.info("已开启计时线程");
            int backChessTime = 0;
            int backMaxWaitTime = config.getBackMaxWaitTime();
            while (this.state){
                if (this.stepTime <= 0 || remainder1 == 0 || remainder2 == 0){
//                    设置获胜信息
                    winInfo = new WinInfo(backPlayer(), "对手超时");
                    gameOver();
                    break;
                }else {

//                    悔棋线程
                    {
                        if (backChess || drawChess){
                            backChessTime++;
                            log.info("当前处于悔棋或和棋等待时间，计时线程不减少");
                            if (backChessTime >= backMaxWaitTime){
                                this.backChess = false;
//                              超时未响应悔棋操作 ，自动拒绝  通知前端
//                                GoBangSocket.goBangService.sendMessage
                                GoBangSocket.goBangService.sendMessage(this, new RespMessage(refuseBackChess, getNextPlayerId()));
                            }
                        } else {
                            backChessTime = 0;
                            this.stepTime--;
                        }
                    }

//                    总时间线程
                    this.totalTime++;

//                    每个人总剩余时间
                    {
                        if (getNextPlayerId().equals(getPlayer1()))
                            remainder1--;
                        else
                            remainder2--;
                    }


                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    gameOver();
                    throw new RuntimeException(e);
                }
            }
        });
        this.timer.start();
    }

    /**
     * 认输
     * @param uid uid
     */
    public void surrender(Long uid){
        if (uid.equals(getPlayer1()) || uid.equals(getPlayer2())){
            log.info(uid + " 已经认输 游戏结束");
            winInfo = new WinInfo(uid.equals(getPlayer1()) ? getPlayer2() : getPlayer1(), "对手投降");
            gameOver();
        }

    }

    /**
     * 游戏结束
     */
    private void gameOver() {
        this.state = false;
        log.info("game over!");
        GoBangSocket.goBangService.gameOver(this.getRoomId());
       //TODO 通知双方获胜信息
        // TODO 对局结束的处理
    }


    /**
     * 判断当前落子是否取得胜利
     */
    public boolean decide(int x, int y){
//         判断当前落子是否胜利
        int[][] stepArr = checkBoard.getCheckBoard();

        int tag;
        int p = stepArr[x][y];
        int h = stepArr.length;

//        横向
        tag = 0;
        for (int i = x; i >= 0; i--) {
            if (stepArr[i][y] == p){
                tag++;
            }else {
                break;
            }
        }
        for (int j = x + 1; j < h; j++) {
            if (stepArr[j][y] == p){
                tag++;
            }else {
                break;
            }
        }
        if (tag >= 5) return true;
//        纵向
        tag = 0;
        for (int i = y; i >= 0; i--) {
            if (stepArr[x][i] == p){
                tag++;
            }else {
                break;
            }
        }
        for (int j = y + 1; j < h; j++) {
            if (stepArr[x][j] == p){
                tag++;
            }else {
                break;
            }
        }

        if (tag >= 5) return true;


//        二象限 - 四象限 递减
        tag = 1;
        for (int i = x,j = y; i+1 < h && i+1 > 0 && j-1 < h && j-1 > 0; i++,j--) {
            if (stepArr[i+1][j-1] == p){
                tag++;
            }else {
                break;
            }
        }
        for (int i = x,j = y; i-1 < h && i-1 > 0 && j+1 < h && j+1 > 0; i--,j++) {
            if (stepArr[i-1][j+1] == p){
                tag++;
            }else {
                break;
            }
        }
        if (tag >= 5) return true;

//        四象限 - 二象限 递增
        tag = 1;
        for (int i = x,j = y; i+1 < h && i+1 > 0 && j+1 < h && j+1 > 0; i++,j++) {
            if (stepArr[i+1][j+1] == p){
                tag++;
            }else {
                break;
            }
        }
        for (int i = x,j = y; i-1 < h && i-1 > 0 && j-1 < h && j-1 > 0; i--,j--) {
            if (stepArr[i-1][j-1] == p){
                tag++;
            }else {
                break;
            }
        }
        return tag >= 5;

    }

}
