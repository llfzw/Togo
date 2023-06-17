package gobang.utils;

import gobang.pojo.game.Step;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class GobangAi {

    /**
     * 计算一个坐标的权重
     */
    private int score(int x, int y, int[][] checkBoard) {

        int cLength = checkBoard.length;

        HashMap<String, Integer> map = new HashMap<>();
        map.put("010", 30);//活1
        map.put("020", 60);//活2
        map.put("030", 700);//活3
        map.put("040", 10000);//活4
        map.put("01-1", 15);//眠1
        map.put("02-1", 40);//眠2
        map.put("03-1",	500);//眠3
        map.put("04-1", 10000);//眠4

        int[] X = {1,1,0,-1,-1,-1,0,1};
        int[] Y = {0,1,1,1,0,-1,-1,-1};
        int tmp_sum = 0;
        for(int i=0;i<X.length;i++){
            //ans数组存放的是当前棋盘的落子情况，1代表是己方的黑子，2代表对方的白子
            int tmp_x = x + X[i];
            int tmp_y = y + Y[i];
            if (tmp_x < 0 || tmp_y < 0 || tmp_x >= cLength || tmp_y >= cLength) continue;
            if(checkBoard[tmp_x][tmp_y]==1) {	//如果这个坐标为1，说明当前方向第一子为己方
                int num_positive = 0;
                int temp =  -2;
                //一直循环知道后面的子与第一子颜色不同为止
                while(checkBoard[tmp_x][tmp_y] == 1) {
                    num_positive++;
                    tmp_x+=X[i];
                    tmp_y+=Y[i];
                    if((tmp_x < cLength && tmp_x >= 0) && (tmp_y >= 0 && tmp_y < cLength)) {
                    }//判断边界问题，不能超边界
                    else {
                        temp = 2;
                        break;
                    }
                }
                /*-2是初始化的值，如果temp没被改变过，说明没有超边界，就把最终结束的ans赋给temp*/
                if(temp==-2) {
                    temp= checkBoard[tmp_x][tmp_y];
                }
                if(temp==2) {
                    //根据hashmap给最后的tmp_sum加权值
                    Integer value=map.get("0"+num_positive+"-1");
                    if(value != null) {
                        tmp_sum+=value;
                    }
                }
                else {
                    Integer value=map.get("0"+num_positive+"0");
                    if(value != null) {
                        tmp_sum+=value;
                    }
                }
            }
            //这里讨论的是另一种情况，即遇上了对方白子的情况
            else if(checkBoard[tmp_x][tmp_y]==2){
                int num_negative = 0;
                int temp=-2;
                while(checkBoard[tmp_x][tmp_y]==2) {
                    num_negative++;
                    tmp_x+=X[i];
                    tmp_y+=Y[i];
                    if((tmp_x < cLength && tmp_x>=0) && (tmp_y >= 0 && tmp_y < cLength)) {
                    }
                    else {
                        temp=1;
                        break;
                    }
                }
                if(temp==-2) {
                    temp= checkBoard[tmp_x][tmp_y];
                }
                //如果遇上了黑子而停止，执行以下操作
                if(temp==1) {
                    Integer value=map.get("0-"+num_negative+"1");
                    if(value!=null) {
                        tmp_sum+=value;
                    }
                }
                //如果是遇上了空白棋盘，执行下面操作
                else if(temp==0){
                    Integer value=map.get("0-"+num_negative+"0");
                    if(value != null) {
                        tmp_sum +=value;
                    }
                }
            }
        }

        return tmp_sum;
    }

    /**
     * 判断人机最佳下棋位置
     */
    public Step getStep(int[][] checkBoard) {
        int cLength = checkBoard.length;
        int[][] weight = new int[cLength][cLength];
        int max=-1;
        int x0=-1;
        int y0=-1;
        //双重for循环，进行整个棋盘的遍历
        for(int i = 0; i < cLength; i++) {
            for(int j = 0; j < cLength; j++) {
                if(checkBoard[i][j] == 0) {	//为0代表棋盘上没有棋子，可以计算其权值
                    weight[i][j] = score(i, j, checkBoard);		//填充这个位置上的权值
                    if(max<weight[i][j]) {		//选取权值中最大的一个点
                        max=weight[i][j];
                        x0=i;
                        y0=j;
                    }
                }
            }
        }
        return new Step(x0, y0, -1L);
    }

}
