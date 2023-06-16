package toogoo.entity;

import lombok.Data;

@Data
public class UserDto {
    private Long id;

    private String name;

    private String email;   // email

    private String gold;   // 金币

    private String head;   //头像

    private String region;  // 地区
}
