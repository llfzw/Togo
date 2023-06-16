package togo.utils;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

import java.util.regex.Pattern;


public class EmailUtil {

    private static final String EMAIL_CODE = "KGAEJYGBWBDSQMKS";

    private static final String emailMatcher="[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+";

    public static void setEmail(HtmlEmail email){
        email.setHostName("smtp.163.com");//邮箱的SMTP服务器，一般163邮箱的是smtp.163.com
        email.setCharset("utf-8");//设置发送的字符类型
        try {
            email.setFrom("llf__llf@163.com","TooGoo");//发送人的邮箱为自己的，用户名可以随便填
        } catch (EmailException e) {
            throw new RuntimeException(e);
        }
        email.setAuthentication("llf__llf@163.com",EMAIL_CODE);//设置发送人到的邮箱和用户名和授权码(授权码是自己设置的)
    }

    /**
     *  发送邮件
     * @param email 收件人
     * @param topic 标题
     * @param value 消息内容
     * @return 成功
     */
    public static void sendEmail(String email, String topic, String value){
        HtmlEmail e = new HtmlEmail();
        setEmail(e);
        try {
            e.addTo(email);//设置收件人
            e.setSubject(topic);//设置发送主题
            e.setMsg(value);//设置发送内容
            e.send();//进行发送
        } catch (EmailException err) {
            throw new RuntimeException(err);
        }
    }

    public static boolean emailIsMatch(String email){
        return Pattern.matches(emailMatcher, email);
    }


}
