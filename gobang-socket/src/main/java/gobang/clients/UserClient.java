package gobang.clients;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import toogoo.entity.UserDto;


@FeignClient("userservice")
public interface UserClient {

    @GetMapping("/interior/findById/{id}")
    UserDto findById(@PathVariable("id") Long id);

}
