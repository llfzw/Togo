package togo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import togo.dao.UserDao;
import togo.pojo.LoginUserDto;
import togo.pojo.RegisterDto;
import togo.pojo.SubmitCodeFrom;
import togo.service.UserService;
import toogoo.RespPojo.Message;
import toogoo.Utils.JwtTokenUtil;
import toogoo.entity.User;
import toogoo.entity.UserDto;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static togo.utils.EmailUtil.emailIsMatch;
import static togo.utils.EmailUtil.sendEmail;
import static toogoo.config.PrefixConfig.*;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private final int loginMaxAge = 1000 * 60 * 60 * 24;

    /**
     * 密码登录功能
     * @param user u
     * @return r
     */
    @Override
    public Message login(LoginUserDto user, HttpServletResponse response) {
        if (user == null || user.getEmail() == null || !emailIsMatch(user.getEmail()) || user.getPassword() == null){
            return Message.fail(404, "参数缺失");
        }
        log.info("登录用户 data: -> \n " + user);
        User user1 = userDao.selectUserByEmail(user.getEmail());

        if (user1 == null){
            return Message.fail(400, "账号不存在");
        }
        if (!user1.getPassword().equals(user.getPassword())){
            return Message.fail(400, "密码不正确");
        }

        String token = createTokenByUser(user1.getId());

        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(loginMaxAge);  //5小时
        cookie.setPath("/");//设置作用域
        response.addCookie(cookie);
        return Message.ok("登陆成功");
    }

    /**
     * 首次验证码登录
     * @param email email
     * @return re
     */
    @Override
    public Message login(String email) {
        if (!emailIsMatch(email)) return Message.fail(400, "邮箱格式错误");

        if (userDao.selectUserByEmail(email) == null) {
            return Message.fail(400, "该邮箱未注册");
        }

        String code = getNumCode(4);

        CompletableFuture.supplyAsync( () -> {
            sendEmail(email, "TooGoo", "您正在注册TooGoo游戏，验证码为：" + code);
            return null;
        }).exceptionallyAsync((e) -> {
            log.info(email + " -> 登录验证码发送失败");
            log.info(e.toString());
            return null;
        });

//        添加redis并设置两分钟过期时间
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_PREFIX + email, code, 2, TimeUnit.MINUTES);

        return Message.ok(null, email);
    }

    /**
     * 登录时提交二维码
     * @param submitCodeFrom
     * @return
     */
    @Override
    public Message submitLoginCode(SubmitCodeFrom submitCodeFrom, HttpServletResponse response) {
//        TODO 验证参数
        String email = submitCodeFrom.getCodeId();

        String redisKey = LOGIN_CODE_PREFIX + email;
        String code = stringRedisTemplate.opsForValue().get(redisKey);
        if (code == null) return Message.fail(400,  "验证码不存在或已过期");

        if (!code.equals(submitCodeFrom.getCode())) return Message.fail(400, "验证码错误");

        User user = userDao.selectUserByEmail(email);
        if (user == null) {
//            TODO 未注册用户二维码登录， 自动注册功能
            log.error("未完成功能：-> 未注册用户验证码登录， 自动注册功能");
            return Message.ok("未完成功能：-> 未注册用户验证码登录， 自动注册功能");
        }

//        生成token
        String token = createTokenByUser(user.getId());

        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(loginMaxAge);  //5小时
        cookie.setPath("/");//设置作用域
        response.addCookie(cookie);
        stringRedisTemplate.delete(redisKey);
        return Message.ok("注册成功");
    }

    /**
     * 根据Id创建Token
     * @param id
     * @return
     */
    private String createTokenByUser(Long id) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", id);
        return jwtTokenUtil.createToken(claims);
    }

    /**
     * 首次提交注册功能
     * @param user user
     * @return return
     */
    @Override
    public Message register(RegisterDto user) {

//        1、验证数据格式
        if (user == null || user.getEmail() == null || !emailIsMatch(user.getEmail())
                || user.getPassword() == null || user.getPassword().length() > 20
                || user.getPassword().length() < 6){
            return Message.fail(404, "参数格式错误");
        }

        log.info(user.toString());
        User user1 = userDao.selectUserByEmail(user.getEmail());
        if (user1 != null) {
            return Message.fail(400, "用户已存在");
        }

        String code = getNumCode(4);

        String codeId = UUID.randomUUID().toString();

        Map<String, Object> registerMap = BeanUtil.beanToMap(user);
        registerMap.put("code", code);

        log.info(user.getEmail());

        CompletableFuture.supplyAsync(() -> {
            sendEmail(user.getEmail(), "TooGoo", "您正在注册TooGoo游戏，验证码为：" + code);
            log.info(user.getEmail() + "发送注册验证码成功 ");
            return null;
        }).exceptionallyAsync((e) -> {
            log.error(user.getEmail() + "发送注册验证码失败 ");
            return null;
        });

        stringRedisTemplate.opsForHash().putAll(REGISTER_CODE_PREFIX + codeId, registerMap);
        stringRedisTemplate.expire(REGISTER_CODE_PREFIX + codeId, 2, TimeUnit.MINUTES);

        return Message.ok(null, codeId);
    }

    /**
     * 提交注册验证码功能
     * @param submitCodeFrom
     * @return
     */
    @Override
    public Message submitCode(SubmitCodeFrom submitCodeFrom){
//        TODO 验证参数

        String codeId = submitCodeFrom.getCodeId();

        Map<Object, Object> map = stringRedisTemplate.opsForHash().entries(REGISTER_CODE_PREFIX + codeId);

        if (map.isEmpty()){
            log.error("验证码不存在或已过期");
            return Message.fail("验证码不存在或已过期");
        }

        if (!map.get("code").equals(submitCodeFrom.getCode())) {
            log.error("验证码错误");
            return Message.fail("验证码错误");
        }

        RegisterDto registerDto = BeanUtil.toBeanIgnoreCase(map, RegisterDto.class, true);

        int insertUser = userDao.insertUser(registerDto);

        if (insertUser == 0) return Message.fail("注册失败");

        return Message.ok("注册成功！");
    }

    @Override
    public Message getUserInfoByToken(String token) {
        log.info("token 获取用户信息: \n --->" + token);
        Claims claims = jwtTokenUtil.getClaimsFormToken(token);
        if (claims == null) return Message.fail("Token not have token!");
        Long id = claims.get("id", Long.class);
        UserDto userDto =findById(id);
        if (userDto == null) return Message.fail("Token not have token!");
        return Message.ok(userDto);
    }

    @Override
    public UserDto findById(Long id) {
        if (id == null){
            return null;
        }
        if (id < 0){
//            TODO 获取游客信息
            String userDtoStr = stringRedisTemplate.opsForValue().get(VISITOR_PREFIX + id);
            log.info(userDtoStr);
            if (userDtoStr == null) {
                return null;
            }
            return JSONUtil.toBean(userDtoStr, UserDto.class);
        }

        User user = userDao.selectUserById(id);

      return BeanUtil.copyProperties(user, UserDto.class);

    }

    @Override
    public Message visitorLogin(String name, HttpServletResponse response) {

        log.info("name: {}" , name);
//        1、生成游客Id
        Long vid = getVisitorNextId();
        UserDto userDto = new UserDto(vid, name, 10, "a.png");

        CompletableFuture.runAsync(()->{
            //        保存到redis中并设置一天有效期
            stringRedisTemplate.opsForValue().set(VISITOR_PREFIX + vid, JSONUtil.toJsonStr(userDto), 24, TimeUnit.HOURS);
        });
        String token = createTokenByUser(vid);

        Cookie cookie = new Cookie("token", token);
        cookie.setMaxAge(loginMaxAge);  //24小时
        cookie.setPath("/");//设置作用域
        response.addCookie(cookie);

        return Message.ok(token);
    }

    /**
     *  获取指定位数的随机数
     * @param num num
     * @return return
     */
    private String getNumCode(int num){
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < num; i++) {
            int j = (int) ((Math.random() * 9 + 1));
            code.append(j);
        }
        return code.toString();

    }


    /**
     * 得到游客下一个id
     *
     * @return {@link Long}
     */
    private Long getVisitorNextId(){
        Long id = stringRedisTemplate.opsForValue().increment(VISITOR_ID_PREFIX, -1);
        if (id != null && id.equals(-1L)){
            return getVisitorNextId();
        }
        return id;

    }

}
