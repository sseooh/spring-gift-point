package gift.order.service;

import gift.auth.client.KakaoApiClient;
import gift.member.dto.MemberResDto;
import gift.member.entity.Member;
import gift.member.service.MemberService;
import gift.option.entity.Option;
import gift.option.service.OptionService;
import gift.order.dto.OrderReqDto;
import gift.order.dto.OrderResDto;
import gift.order.entity.Order;
import gift.order.exception.OrderNotFoundException;
import gift.order.repository.OrderRepository;
import gift.product.entity.Product;
import gift.wishlist.entity.WishList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    private final MemberService memberService;
    private final OptionService optionService;
    private final OrderRepository orderRepository;
    private final KakaoApiClient kakaoApiClient;

    public OrderService(MemberService memberService, OptionService optionService, OrderRepository orderRepository, KakaoApiClient kakaoApiClient) {
        this.memberService = memberService;
        this.optionService = optionService;
        this.orderRepository = orderRepository;
        this.kakaoApiClient = kakaoApiClient;
    }

    @Transactional
    public OrderResDto createOrder(MemberResDto memberDto, OrderReqDto orderReqDto) {

        Option option = optionService.findByIdOrThrow(orderReqDto.optionId());
        optionService.subtractQuantity(option, orderReqDto.quantity());

        Product product = option.getProduct();

        Member member = memberService.findMemberByIdOrThrow(memberDto.id());
        deleteIfExistInWishList(member, option.getId());

        memberService.processOrderPoints(member, orderReqDto.points(), product.getPrice() * orderReqDto.quantity());

        Order savedOrder = orderRepository.save(new Order(member, option, orderReqDto.quantity(), orderReqDto.message()));

        sendKakaoMessage(member, savedOrder);

        return new OrderResDto(savedOrder);
    }

    private void sendKakaoMessage(Member member, Order order) {
        String kakaoAccessToken = member.getKakaoAccessToken();
        if (kakaoAccessToken != null) {
            kakaoApiClient.messageToMe(kakaoAccessToken, order.getMessage(), "/orders/" + order.getId(), "주문 상세 보기");
        }
    }

    // 주문한 상품이 위시 리스트에 있는 경우 위시 리스트에서 삭제
    private void deleteIfExistInWishList(Member member, Long optionId) {
        List<WishList> wishLists = member.getWishLists();

        wishLists.stream()
                .filter(wishList -> wishList.getProduct().getOptions().stream()
                        .anyMatch(option -> option.getId().equals(optionId)))
                .findFirst()
                .ifPresent(wishLists::remove);
    }

    @Transactional(readOnly = true)
    public Page<OrderResDto> getOrders(MemberResDto memberDto, Pageable pageable) {
        Member member = memberService.findMemberByIdOrThrow(memberDto.id());

        Page<Order> orders = orderRepository.findAllByMember(member, pageable);

        List<OrderResDto> orderResDtos = orders.stream()
                .map(OrderResDto::new)
                .toList();

        return new PageImpl<>(orderResDtos, pageable, orders.getTotalElements());
    }

    @Transactional
    public void cancelOrder(MemberResDto memberDto, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> OrderNotFoundException.EXCEPTION);

        if (!order.getMember().getId().equals(memberDto.id())) {
            throw OrderNotFoundException.EXCEPTION;
        }

        optionService.addQuantity(order.getOption(), order.getQuantity());

        orderRepository.delete(order);
    }
}