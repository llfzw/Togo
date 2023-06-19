package toogoo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("gobang")
public class Assets {
    private Long uid;       // user -> id
    private Integer exp;    // 经验
    private Integer grade;  // 等级

    @TableField("`rank`")
    private Integer rank;   // 积分

}
