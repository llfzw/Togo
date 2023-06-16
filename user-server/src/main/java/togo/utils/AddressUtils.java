package togo.utils;


import cn.hutool.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AddressUtils {

    @Autowired
    private RestTemplate restTemplate;

    private final String url = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?resource_id=6006&format=json&query=";

    public String getRegionByIp(String ip){
        try {
            String data = restTemplate.getForObject(url + ip, String.class);
            return JSONUtil.parseObj(data).getJSONObject("data").getJSONObject("0").get("location", String.class);
        }catch (Exception e){
            return "";
        }

    }

}
