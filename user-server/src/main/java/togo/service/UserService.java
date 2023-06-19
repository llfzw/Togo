package togo.service;

import jakarta.servlet.http.HttpServletResponse;
import togo.pojo.LoginUserDto;
import togo.pojo.RegisterDto;
import togo.pojo.SubmitCodeFrom;
import toogoo.RespPojo.Message;
import toogoo.entity.UserDto;

public interface UserService {
    Message login(LoginUserDto user, HttpServletResponse response);

    Message login(String email);

    Message submitLoginCode(SubmitCodeFrom submitCodeFrom, HttpServletResponse response);

    Message register(RegisterDto user);

    Message submitCode(SubmitCodeFrom submitCodeFrom);

    Message getUserInfoByToken(String token);

    UserDto findById(Long id);

    Message visitorLogin(String name, HttpServletResponse response);
}
