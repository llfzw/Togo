package toogoo.entity;

import lombok.Data;


@Data
public class Assets {

    private Long uid;       // user -> id
    private Integer exp;    // 经验
    private Integer grade;  // 等级
    private Integer rank;   // 积分

}
