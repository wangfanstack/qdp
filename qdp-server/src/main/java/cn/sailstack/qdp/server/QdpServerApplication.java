package cn.sailstack.qdp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 项目的启动类
 *
 * @author sailstack源码
 */
@SpringBootApplication(scanBasePackages = {"${qdp.info.base-package}.server", "${qdp.info.base-package}.module"})
public class QdpServerApplication {

    public static void main(String[] args) {

        SpringApplication.run(QdpServerApplication.class, args);
    }

}
