package io.dapr.docs.consumer;

import io.dapr.testcontainers.Component;
import io.dapr.testcontainers.DaprContainer;
import io.dapr.testcontainers.DaprLogLevel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration(proxyBeanMethods = false)
public class DaprTestContainersConfig {


   @Bean
   public Network daprNetwork(){
     return Network.newNetwork();
   }

   @Bean
   public RabbitMQContainer rabbitMQContainer(Network daprNetwork){
      return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.7.25-management-alpine"))
              .withExposedPorts(5672)
              .withNetworkAliases("rabbitmq")
              .withReuse(true)
              .withNetwork(daprNetwork);

   }

   @Bean
   @ServiceConnection
   public DaprContainer daprContainer(Network daprNetwork, RabbitMQContainer rabbitMQContainer){

     Map<String, String> rabbitMqProperties = new HashMap<>();
     rabbitMqProperties.put("connectionString", "amqp://guest:guest@rabbitmq:5672");
     rabbitMqProperties.put("user", "guest");
     rabbitMqProperties.put("password", "guest");


     return new DaprContainer("daprio/daprd:1.14.1")
             .withAppName("consumer-app")
             .withNetwork(daprNetwork)
             .withComponent(new Component("pubsub", "pubsub.rabbitmq", "v1", rabbitMqProperties))
             .withDaprLogLevel(DaprLogLevel.DEBUG)
             .withLogConsumer(outputFrame -> System.out.println(outputFrame.getUtf8String()))
             .withAppPort(8081)
             .withAppChannelAddress("host.testcontainers.internal")
             .withReusablePlacement(true)
             .dependsOn(rabbitMQContainer);
   }




}
