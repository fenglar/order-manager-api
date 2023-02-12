package pl.marcin.ordermanagerapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {
    private Long id;
    private Long availableQuantity;
    private Long reservedQuantity;

    private ProductDto product;
}
