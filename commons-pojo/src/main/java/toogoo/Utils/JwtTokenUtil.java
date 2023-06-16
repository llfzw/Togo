package toogoo.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Map;

/**
 * JwtToken工具类
 */
public class JwtTokenUtil {
    private final String secret = "toogoocdlandllf";
    private final Long expiration = 1000 * 60 * 60 * 2L;

    /**
     * 根据荷载生成token
     * 主要是通过Jwts把荷载、失效时间、以及密钥加密生成token
     * @param claims
     * @return
     */
    public String createToken(Map<String,Object> claims){
        //有了荷载claims就可以通过Jwts生成token,方式如下：
        return Jwts.builder()
                .setClaims(claims)//把荷载存储到里面
                .setExpiration(generateExpirationDate())//设置失效时间
                .signWith(SignatureAlgorithm.HS256,secret) //签名
                .compact();
    }


    /**
     * 生成token失效时间
     * @return
     */
    private Date generateExpirationDate() {
        //失效时间是当前系统的时间+我们在配置文件里定义的时间
        return new Date(System.currentTimeMillis()+expiration);
    }

    /**
     * 根据token获取用户名
     * @param token
     * @return
     */
    public String getUserNameFormToken(String token){
        String username;//用户名是通过在token中获取到荷载claims，然后再从荷载中取用户名
        try{
            Claims claims=getClaimsFormToken(token);
            username=claims.getSubject();
        }catch (Exception e){
            username=null;
        }
        return username;
    }

    /**
     * 从token中获取荷载
     * 获取荷载是通过jwts，然后船两参数，分别是secret、和token
     * @param token
     * @return
     */
    public Claims getClaimsFormToken(String token) {
        Claims claims=null;
        try{
            claims=Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
//            e.printStackTrace();
            return null;
        }
        return claims;
    }

    /**
     * 判断token是否已经失效
     * @param token
     * @return
     */
    public boolean isTokenExpired(String token) {
        //先获取之前设置的token的失效时间
        Date expireDate=getExpiredDateFormToken(token);
        return expireDate.before(new Date()); //判断下，当前时间是都已经在expireDate之后
    }

    /**
     * 根据token获取失效时间
     * 也是先从token中获取荷载
     * 然后从荷载中拿到到设置的失效时间
     * @param token
     * @return
     */
    private Date getExpiredDateFormToken(String token) {
        Claims claims=getClaimsFormToken(token);
        return claims.getExpiration();
    }

}

