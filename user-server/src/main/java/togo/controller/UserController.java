package togo.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import togo.pojo.LoginUserDto;
import togo.pojo.RegisterDto;
import togo.pojo.SubmitCodeFrom;
import togo.service.UserService;
import toogoo.RespPojo.Message;


@CrossOrigin
@Controller
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @PostMapping("/login")
    public Message login(@RequestBody LoginUserDto user, HttpServletResponse response){
        return userService.login(user, response);
    }

    @ResponseBody
    @PostMapping("/register")
    public Message register(@RequestBody RegisterDto user){
        return userService.register(user);
    }

    @ResponseBody
    @PostMapping("/submitCode")
    public Message submitCode(@RequestBody SubmitCodeFrom submitCodeFrom){
        return userService.submitCode(submitCodeFrom);
    }

    @ResponseBody
    @RequestMapping("/userInfo")
    public Message getUserByToken(@CookieValue("token") String token){
        return userService.getUserInfoByToken(token);
    }


    @ResponseBody
    @RequestMapping("/visitorLogin")
    public Message visitorLogin(String name, HttpServletResponse response){
        return userService.visitorLogin(name, response);
    }


}
