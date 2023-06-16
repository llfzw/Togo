package toogoo.entity;

import lombok.Data;

@Data
public class User {

    private Long id;
    private String name;
    private String password;
    private String email;   // 邮箱
    private Long gold   ;   // 金币  货币
    private String head;
    private String region;

}
