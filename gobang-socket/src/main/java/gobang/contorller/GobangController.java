package gobang.contorller;

import gobang.service.GoBangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import toogoo.RespPojo.Message;

@RequestMapping("gobang")
@CrossOrigin
@Controller
@ResponseBody
public class GobangController {

    @Autowired
    private GoBangService goBangService;

    @RequestMapping("/assets")
    public Message myAssetsInfo(@CookieValue("token") String token){
        return goBangService.getAssets(token);
    }

    @RequestMapping("/rival")
    public Message assetsInfo(@CookieValue("token") String token){
        return goBangService.rivalInfoByToken(token);
    }

    @RequestMapping("/gameInfo")
    public Message gameInfo(@CookieValue("token") String token){
        return goBangService.gameInfoByToken(token);
    }

    @RequestMapping("/gameTop")
    public Message gameTop(){
        return goBangService.gameTop();
    }

}
