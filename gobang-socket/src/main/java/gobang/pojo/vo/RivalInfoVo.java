package gobang.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RivalInfoVo {

    private Long id;   // id

    private String name;

    private Integer grade;  // 等级

    private String head;   // 头像

    private String region;   // 地区




}
