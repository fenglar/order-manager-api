package pl.marcin.ordermanagerapi.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.marcin.ordermanagerapi.dto.ProductDto;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "order_item")
@AllArgsConstructor
@NoArgsConstructor
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", updatable = false, insertable = false)
    private Long orderId;

    // TODO: Remove the fetch and check the behavior of the framework, it'll run more queries for each OrderItem.order
    //       with Lazy fetch, it'll run only when and for the object that we call orderItem.order.
    //       Just fetch EAGER, will not solve the N+1 issue, it is actually create more queries.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    @NotNull
    private Long productId;
    @NotNull
    private Long quantity;
    @NotNull
    private BigDecimal priceItem;
    @NotNull
    private BigDecimal totalAmount;

    public BigDecimal getTotalAmount(Long quantity, BigDecimal priceItem) {
        totalAmount = totalAmount.add(priceItem.multiply(BigDecimal.valueOf(quantity)));
        return totalAmount;
    }
}
