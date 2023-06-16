package top.togo.main;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableDiscoveryClient   // 开启服务注册发现功能
@SpringBootApplication
@EnableFeignClients
public class MainSocketApplication {
    public static void main(String[] args) {
        SpringApplication.run(MainSocketApplication.class, args);

    }
}
