package pl.marcin.ordermanagerapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.marcin.ordermanagerapi.entity.Order;
import pl.marcin.ordermanagerapi.entity.OrderItem;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("FROM OrderItem oi JOIN FETCH oi.order WHERE oi.id = :orderItemId")
    Optional<OrderItem> findByIdWithFetchOrder(@Param(("orderItemId")) Long orderItemId);

    List<OrderItem> findAllByOrderIdIn(List<Long> orderIds);
    List<OrderItem> findAllByOrderIn(List<Order> orders);

}
