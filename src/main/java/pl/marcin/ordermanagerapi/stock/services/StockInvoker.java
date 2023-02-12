package pl.marcin.ordermanagerapi.stock.services;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import pl.marcin.ordermanagerapi.dto.ProductDto;
import pl.marcin.ordermanagerapi.dto.StockDto;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@FeignClient(value = "stockService", url = "${app.stockServiceUrl}")
public interface StockInvoker {
    @GetMapping("/api/product/{productId}")
    ProductDto getProduct(@PathVariable Long productId);

    @GetMapping("/api/product/all")
    List<ProductDto> getAllProducts();

    @GetMapping("/api/stock/{productId}")
    StockDto getStock(@PathVariable Long productId);

    @PostMapping("/api/stock/{productId}")
    StockDto reserveQuantityOfProduct(@PathVariable Long productId, @PathVariable Long quantity);

    @PatchMapping("/api/stock/{productId}/{quantity}")
    StockDto updateStock(Map<Long, Long> cancelledProducts);
}
