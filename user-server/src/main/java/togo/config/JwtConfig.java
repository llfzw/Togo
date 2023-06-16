package togo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import toogoo.Utils.JwtTokenUtil;

@Configuration
public class JwtConfig {

    @Bean
    public JwtTokenUtil jwtTokenUtil(){
        return new JwtTokenUtil();
    }

}
