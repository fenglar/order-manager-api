package pl.marcin.ordermanagerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderItemDto {
    private Long productId;
    private Long quantity;
    private BigDecimal priceItem;
    private BigDecimal totalAmount;

    public BigDecimal getTotalAmount(Long quantity, BigDecimal priceItem) {
        totalAmount = totalAmount.add(priceItem.multiply(BigDecimal.valueOf(quantity)));
        return totalAmount;
    }
}

