package gift.domain.order.service;

import gift.domain.member.entity.Member;
import gift.domain.order.dto.OrderItemRequest;
import gift.domain.order.entity.Order;
import gift.domain.order.entity.OrderItem;
import gift.domain.product.entity.Option;
import gift.domain.product.entity.Product;
import gift.domain.product.repository.ProductJpaRepository;
import gift.domain.product.service.OptionService;
import gift.domain.wishlist.repository.WishlistJpaRepository;
import gift.exception.InvalidOptionInfoException;
import gift.exception.InvalidProductInfoException;
import java.util.AbstractMap.SimpleEntry;
import java.util.List;
import java.util.Map.Entry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderItemService {

    private final ProductJpaRepository productJpaRepository;
    private final WishlistJpaRepository wishlistJpaRepository;
    private final OptionService optionService;

    public OrderItemService(
        ProductJpaRepository productJpaRepository,
        WishlistJpaRepository wishlistJpaRepository,
        OptionService optionService
    ) {
        this.productJpaRepository = productJpaRepository;
        this.wishlistJpaRepository = wishlistJpaRepository;
        this.optionService = optionService;
    }

    @Transactional
    public void create(Member member, Order order, List<OrderItemRequest> orderItemRequests) {
        for (OrderItemRequest orderItemRequest : orderItemRequests) {
            Entry<Product, Option> item = buy(
                orderItemRequest.productId(), orderItemRequest.optionId(), orderItemRequest.quantity()
            );
            Product product = item.getKey();
            Option option = item.getValue();

            OrderItem orderItem = orderItemRequest.toOrderItem(order, product, option);
            order.addOrderItem(orderItem);

            wishlistJpaRepository.deleteByMemberAndProduct(member, product);
        }
    }

    @Transactional
    public void createOne(Member member, Order order, OrderItemRequest orderItemRequest) {
        Long optionId = orderItemRequest.optionId();
        Product product = productJpaRepository.findByOptionId(optionId)
            .orElseThrow(() -> new InvalidOptionInfoException("error.invalid.option.id"));

        Entry<Product, Option> item = buy(product.getId(), optionId, orderItemRequest.quantity());

        OrderItem orderItem = orderItemRequest.toOrderItem(order, item.getKey(), item.getValue());
        order.addOrderItem(orderItem);

        wishlistJpaRepository.deleteByMemberAndProduct(member, product);
    }

    private Entry<Product, Option> buy(Long productId, Long optionId, int quantity) {
        Product product = productJpaRepository.findById(productId)
            .orElseThrow(() -> new InvalidProductInfoException("error.invalid.product.id"));

        if (!product.hasOption(optionId)) {
            throw new InvalidOptionInfoException("error.invalid.option.id");
        }
        Option option = optionService.subtractQuantity(optionId, quantity);

        return new SimpleEntry<>(product, option);
    }
}