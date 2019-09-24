package testdemo;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.math.BigDecimal;

public class DemoTest {

    @Test
    public void test1() {
        ApplicationContext ac = new ClassPathXmlApplicationContext("spring.xml");
        DriverManagerDataSource dataSource = (DriverManagerDataSource) ac.getBean("dataSource");
        String url = dataSource.getUrl();
        System.out.println(url);
    }

    @Test
    public void test2() {
//        double a = 0.1;
//        double b = 0.46;
        BigDecimal a = new BigDecimal("0.1");
        BigDecimal b = new BigDecimal("0.5");
        System.out.println(a.add(b));
        System.out.println(a.subtract(b));
        System.out.println(a.multiply(b));
        System.out.println(a.divide(b));
    }

    @Test
    public void test3() {
        System.out.println(Math.round(Math.random()*100));
    }
}
