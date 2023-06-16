package gobang.contorller;

import gobang.service.GoBangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@RequestMapping("interior")
@Controller
public class GobangInteriorController {

    @Autowired
    private GoBangService goBangService;

    @ResponseBody
    @RequestMapping("/joinMatch")
    public Long joinMatch(Long uid, String type, String mode){
        return goBangService.joinMatch(uid, type, mode);
    }

    @ResponseBody
    @RequestMapping("/removeMatch")
    public void unMatch(Long uid){
        goBangService.removeMatch(uid);
    }

}
