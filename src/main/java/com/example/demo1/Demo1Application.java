package com.example.demo1;

import com.example.demo1.service.PaymentsService;
import com.stripe.exception.StripeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class Demo1Application extends SpringBootServletInitializer {

    private final Logger logger = LoggerFactory.getLogger(Demo1Application.class);

    @Autowired
    Environment environment;

    /**
     * TODO
     * 1. Run the jar via command line
     * 2. Set the env, load env specific app.properties file
     * 3. Access 'Payments Service' Bean
     *
     */

    public static void main(String[] args) {
        System.out.println("Successful in starting the application");

        ApplicationContext applicationContext = SpringApplication.run(Demo1Application.class, args);

        SpringApplication app = new SpringApplication(Demo1Application.class);
        app.setAddCommandLineProperties(true);

        PaymentsService ps = applicationContext.getBean(PaymentsService.class);
        try {
            ps.getPaymentByIntentId(args[0]);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }


    }

    @Bean
    ApplicationRunner applicationRunner(Environment environment) {
        return args -> logger.info("message from application.properties : "
                + environment.getProperty("message.file"));
    }

}
