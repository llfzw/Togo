package togo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import togo.service.UserService;
import toogoo.entity.UserDto;

@CrossOrigin
@Controller
@RequestMapping("interior")
public class UserInteriorController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @RequestMapping("/findById/{id}")
    public UserDto findById(@PathVariable("id") Long id){
        return userService.findById(id);
    }

}
