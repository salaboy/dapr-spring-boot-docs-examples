package io.dapr.docs.producer;

import io.dapr.testcontainers.Component;
import io.dapr.testcontainers.DaprContainer;
import io.dapr.testcontainers.DaprLogLevel;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.utility.DockerImageName;
import org.junit.runner.Description;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.runners.model.Statement;

@TestConfiguration(proxyBeanMethods = false)
public class DaprTestContainersConfig {

   static final String CONNECTION_STRING =
          "host=postgres user=postgres password=password port=5432 connect_timeout=10 database=dapr_db_repository";
   static final Map<String, String> STATE_STORE_PROPERTIES = createStateStoreProperties();

   static final Map<String, String> BINDING_PROPERTIES = Collections.singletonMap("connectionString", CONNECTION_STRING);


   @Bean
   public Network getNetwork() {
      Network defaultDaprNetwork = new Network() {
        @Override
        public String getId() {
          return "dapr-network";
        }

        @Override
        public void close() {

        }

        @Override
        public Statement apply(Statement base, Description description) {
          return null;
        }
      };

      List<com.github.dockerjava.api.model.Network> networks = DockerClientFactory.instance().client().listNetworksCmd().withNameFilter("dapr-network").exec();
      if (networks.isEmpty()) {
        Network.builder()
                .createNetworkCmdModifier(cmd -> cmd.withName("dapr-network"))
                .build().getId();
        return defaultDaprNetwork;
      } else {
        return defaultDaprNetwork;
      }
   }


   @Bean
   public RabbitMQContainer rabbitMQContainer(Network daprNetwork){
      return new RabbitMQContainer(DockerImageName.parse("rabbitmq:3.7.25-management-alpine"))
              .withExposedPorts(5672)
              .withNetworkAliases("rabbitmq")
//              .withReuse(true)
              .withNetwork(daprNetwork);

   }

   @Bean
   public  PostgreSQLContainer<?> postgreSQLContainer(Network daprNetwork){
     return new PostgreSQLContainer<>("postgres:16-alpine")
             .withNetworkAliases("postgres")
             .withDatabaseName("dapr_db_repository")
             .withUsername("postgres")
             .withPassword("password")
             .withExposedPorts(5432)
             .withNetwork(daprNetwork);

   }

   @Bean
   @ServiceConnection
   public DaprContainer daprContainer(Network daprNetwork, PostgreSQLContainer<?> postgreSQLContainer, RabbitMQContainer rabbitMQContainer){

     Map<String, String> rabbitMqProperties = new HashMap<>();
     rabbitMqProperties.put("connectionString", "amqp://guest:guest@rabbitmq:5672");
     rabbitMqProperties.put("user", "guest");
     rabbitMqProperties.put("password", "guest");


     return new DaprContainer("daprio/daprd:1.14.1")
             .withAppName("producer-app")
             .withNetwork(daprNetwork)
             .withComponent(new Component("kvstore", "state.postgresql", "v1", STATE_STORE_PROPERTIES))
             .withComponent(new Component("kvbinding", "bindings.postgresql", "v1", BINDING_PROPERTIES))
             .withComponent(new Component("pubsub", "pubsub.rabbitmq", "v1", rabbitMqProperties))
             .withDaprLogLevel(DaprLogLevel.DEBUG)
             .withLogConsumer(outputFrame -> System.out.println(outputFrame.getUtf8String()))
             .withAppPort(8080)
             .withAppChannelAddress("host.testcontainers.internal")
//             .withReusablePlacement(true)
             .dependsOn(rabbitMQContainer)
             .dependsOn(postgreSQLContainer);
   }


  private static Map<String, String> createStateStoreProperties() {
    Map<String, String> result = new HashMap<>();

    result.put("keyPrefix", "name");
    result.put("actorStateStore", String.valueOf(true));
    result.put("connectionString", CONNECTION_STRING);

    return result;
  }


}
