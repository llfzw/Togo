package togo;

import org.junit.Test;

import static togo.utils.EmailUtil.sendEmail;

public class Test1 {

    @Test
    public void test_1(){
        sendEmail("2772550899@qq.com", "TooGoo", "您正在注册TooGoo游戏，验证码为：6666" );
    }

}
