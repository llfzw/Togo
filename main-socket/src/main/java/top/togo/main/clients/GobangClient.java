package top.togo.main.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import toogoo.game.MatchGame;

@FeignClient("gobangsocket")
public interface GobangClient {

//    @GetMapping("/interior/joinMatch?uid={uid}&type={type}&mode={mode}")
//    Long joinMatch(@PathVariable("uid") Long uid, @PathVariable("type") String type, @PathVariable("mode") String mode);

    @GetMapping("/interior/removeMatch/{uid}")
    void unMatch(@PathVariable("uid") Long uid);

    @RequestMapping(method = RequestMethod.GET, value = "/interior/joinMatch?match={match}", consumes = "application/json")
    Long joinMatch(@PathVariable("match") String match);

}
