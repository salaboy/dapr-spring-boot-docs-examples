package io.dapr.docs.producer;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, String> {
    List<Order> findByItem(String item);
    List<Order> findByAmount(Integer amount);
}
