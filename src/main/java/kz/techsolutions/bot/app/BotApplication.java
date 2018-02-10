package kz.techsolutions.bot.app;

import kz.techsolutions.bot.service.BotLoader;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.telegram.telegrambots.ApiContextInitializer;

import javax.sql.DataSource;
import java.sql.Connection;

@SpringBootApplication(scanBasePackages = "kz.techsolutions")
public class BotApplication {
    public static void main(String[] args) throws Exception {
        ApiContextInitializer.init();
        ConfigurableApplicationContext applicationContext = SpringApplication.run(BotApplication.class, args);
        BotLoader botLoader = applicationContext.getBean(BotLoader.class);
        botLoader.loadBots();
    }
}