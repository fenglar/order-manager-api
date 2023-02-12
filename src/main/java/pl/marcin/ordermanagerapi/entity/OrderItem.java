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
    @ManyToOne
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
