package gobang.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WinnerVo {

    private Long winner;     // 获胜ID

    private Integer exp;    // 经验

    private Integer rank;    // 积分

}
