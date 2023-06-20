package gobang.pojo.vo;

import lombok.Data;
import toogoo.entity.UserDto;

@Data
public class GameTopVo {
    private Long uid;

    private Integer rank;

    private Integer grade;

    private UserDto userDto;

}
