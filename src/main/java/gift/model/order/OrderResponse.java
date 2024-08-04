package gift.model.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record OrderResponse(
    Long id,
    @JsonProperty("product_id") Long productId,
    @JsonProperty("option_id") Long optionId,
    int quantity,
    LocalDateTime orderDateTime,
    String message,
    int price
) {
    public static OrderResponse from(Order order, int price) {
        return new OrderResponse(
            order.getId(),
            order.getProductId(),
            order.getOption().getId(),
            order.getQuantity(),
            order.getOrderDateTime(),
            order.getMessage(),
            price
        );
    }
}