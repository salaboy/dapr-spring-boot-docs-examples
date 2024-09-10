package io.dapr.docs.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class TestConsumerApplication {

  public static void main(String[] args) {

    SpringApplication
            .from(ConsumerApplication::main)
            .with(DaprTestContainersConfig.class)
            .run(args);
  }



}
