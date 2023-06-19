package toogoo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;

    private String name;

    private String email;   // email

    private Integer gold;   // 金币

    private String head;   //头像

    private String region;  // 地区

    public UserDto(Long id, String name, Integer gold, String head) {
        this.id = id;
        this.name = name;
        this.gold = gold;
        this.head = head;
    }
}
