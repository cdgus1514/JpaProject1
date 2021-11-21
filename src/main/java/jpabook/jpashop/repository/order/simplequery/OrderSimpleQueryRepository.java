package jpabook.jpashop.repository.order.simplequery;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderSimpleQueryRepository {

    private final EntityManager em;


    /**
     * JPQL 결과를 DTO로 바로변환(성능최적화 미비)
     * 특정화면(용도)에 의존적임 -> 별도 리포지토리로 분류(유지보수 용이)
     * @return
     */
    public List<OrderSimpleQueryDto> findOrderDtos() {
        return em.createQuery(
                        "select new jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.status, d.address)" +
                                " from Order o" +
                                " join o.member m " +
                                " join o.delivery d", OrderSimpleQueryDto.class)
                .getResultList();
    }
}
