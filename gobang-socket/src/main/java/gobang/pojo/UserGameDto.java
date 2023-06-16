package gobang.pojo;

import lombok.Data;
import toogoo.entity.Assets;

@Data
public class UserGameDto {

    private Long id;

    private String name;

    private String email;

    private Assets assets;  // 资产

}
