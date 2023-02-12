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
public class OrderItemSummaryDto {
    private Long productId;
    private Long quantity;
    private BigDecimal amount;
    private BigDecimal totalAmount;
}
