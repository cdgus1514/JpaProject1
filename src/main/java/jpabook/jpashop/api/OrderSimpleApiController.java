package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @ToOne 관계 최적화
 * Order 조회
 * Order -> Member (ManyToOne)
 * Order -> Delivery (OneToOne)
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

    private final OrderRepository orderRepository;


    /*
     * 엔티티 직접사용
     */
    @GetMapping("/api/v1/simple-orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());

        // 강제로 지연로딩
        // order join member 1번 실행
        // memeber, delivery 각각 1번 실행
        // orderItems 실행 x
        for(Order order : all) {
            // getMember() 까지는 proxy 객체
            order.getMember().getName();        // getName() 실행 -> LAZY 강제 초기화
            order.getDelivery().getAddress();   // getAddress() 실행 -> LAZY 강제 초기화
        }

        return all;
    }


    /*
     * 엔티티를 DTO로 변환
     */
    @GetMapping("/api/v2/simple-orders")
    public List<SimpleOrderDto> orderV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());

        // (v1, v2 문제점) -> n+1 쿼리 실행
        // 첫 번째 order 실행 > 결과 2개
        // 결과만큼 LAZY 초기화 하면서 쿼리 실행됨
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }


    /**
     * 패치조인 사용
     * @return
     */
    @GetMapping("/api/v3/simple-orders")
    public List<SimpleOrderDto> orderV3() {
        List<Order> orders = orderRepository.findAllWithMemberDelivery();

        // 패치조인으로 쿼리 한번에 데이터 가져옴
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }






    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();             // LAZY 초기화
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();     // LAZY 초기화
        }
    }
}
