package pl.marcin.ordermanagerapi.dto;

import lombok.*;
import pl.marcin.ordermanagerapi.entity.Status;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDto {
    private Long orderid;
    private Long orderNumber;
    private BigDecimal totalAmount;
    private Status status;
    private List<OrderItemSummaryDto> orderItems;
}
