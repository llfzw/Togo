package gobang.test;

import gobang.GoBangSocketApplication;
import gobang.service.GoBangService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GoBangSocketApplication.class)
public class MyTest {

    @Autowired
    private GoBangService goBangService;

    @Test
    public void test01(){
        System.out.println(goBangService);
        goBangService.settlement(1L, 7L);
    }

}
