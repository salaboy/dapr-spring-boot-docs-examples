package io.dapr.docs.producer;

import io.dapr.spring.data.repository.config.EnableDaprRepositories;
import io.dapr.spring.messaging.DaprMessagingTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@EnableDaprRepositories
public class OrdersRestController {
  @Autowired
  private OrderRepository repository;

  @Autowired
  private DaprMessagingTemplate<Order> messagingTemplate;

  @PostMapping("/orders")
  public void storeOrder(@RequestBody Order order){
    repository.save(order);
    messagingTemplate.send("topic", order);
  }

  @GetMapping("/orders")
  public Iterable<Order> getAll(){
    return repository.findAll();
  }

  @GetMapping("/orders/byItem/")
  public Iterable<Order> getAllByItem(@RequestParam("item") String item){
    return repository.findByItem(item);
  }

  @GetMapping("/orders/byAmount/")
  public Iterable<Order> getAllByItem(@RequestParam("amount") Integer amount){
    return repository.findByAmount(amount);
  }

}

