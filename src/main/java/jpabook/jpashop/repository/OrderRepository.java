package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    public void save(Order order) {
        em.persist(order);
    }

    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }


    /**
     * 주문조회 동적쿼리1
     * @param orderSearch
     * @return
     */
    public List<Order> findAllByString(OrderSearch orderSearch) {

        String jpql = "select o from Order o join o.member m";
        boolean isFirstCondition = true;

        // 주문상태 검색
        if(orderSearch.getOrderStatus() != null) {
            if(isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            }else{
                jpql += " and";
            }
            jpql += " o.status = :status";
        }

        // 회원이름 검색
        if(StringUtils.hasText(orderSearch.getMemberName())) {
            if(isFirstCondition) {
                jpql += " where";
            }else{
                jpql += " and";
            }
            jpql += " m.name like :name";
        }

        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000);

        if(orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if(orderSearch.getMemberName() != null) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }

        return query.getResultList();
    }


    /**
     * 주문조회 동적쿼리2 (Criteria)
     * @param orderSearch
     * @return
     */
    public List<Order> findAllByCriteria(OrderSearch  orderSearch) {

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        // 주문상태 검색
        if(orderSearch.getOrderStatus() != null) {
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        if(orderSearch.getMemberName() != null) {
            Predicate name = cb.equal(o.get("name"), "%"+orderSearch.getMemberName()+"%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq).setMaxResults(1000);
        return query.getResultList();
    }


    /**
     * 패치조인(재사용성 높음)
     * @return
     */
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                "select o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d", Order.class
        ).setFirstResult(offset).setMaxResults(limit).getResultList();
    }



    /**
     * 컬렉션 패치조인(중복제거)
     * 1대다 조인으로 row수 증가함
     * distinct 사용 -> JPA에서 엔티티의 id값이 중복이면 제거
     * @return
     */
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o" +
                        " join fetch o.member m" +
                        " join fetch o.delivery d" +
                        " join fetch o.orderItems oi" +
                        " join fetch oi.item i", Order.class)
                .getResultList();
    }
}
