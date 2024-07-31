package gift.order.api;

import gift.member.validator.LoginMember;
import gift.order.application.OrderService;
import gift.order.dto.OrderRequest;
import gift.order.dto.OrderResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> orderProduct(@LoginMember Long memberId,
                                                      @RequestBody @Valid OrderRequest request) {
        OrderResponse order = orderService.order(memberId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(order);
    }

    @GetMapping
    public Page<OrderResponse> getOrders(
            @PageableDefault(
                    sort = "id",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {
        return orderService.getPagedOrders(pageable);
    }

}