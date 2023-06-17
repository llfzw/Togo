package gobang.contorller;

import cn.hutool.json.JSONUtil;
import gobang.service.GoBangService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import toogoo.game.MatchGame;

@RequestMapping("interior")
@Controller
public class GobangInteriorController {

    @Autowired
    private GoBangService goBangService;

    @ResponseBody
    @RequestMapping("/joinMatch")
    public Long joinMatch(String match){
        return goBangService.joinMatch(JSONUtil.toBean(match, MatchGame.class));
    }

    @ResponseBody
    @RequestMapping("/removeMatch/{uid}")
    public void unMatch(@PathVariable("uid") Long uid){
        goBangService.removeMatch(uid);
    }

}
