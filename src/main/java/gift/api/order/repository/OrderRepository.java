package gift.api.order.repository;

import gift.api.order.domain.Order;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByMemberId(Long memberId);
}
