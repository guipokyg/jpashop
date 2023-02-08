package jpabook.jpashop.repository;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;

import jpabook.jpashop.api.OrderSimpleApiController;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.item.Item;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public void save(Order order){
        em.persist(order);
    }

    public Order findOne(Long id){
        return em.find(Order.class,id);
    }

    public List<Order> findAllByString(OrderSearch orderSearch){

//language=JPAQL
        String jpql = "select o From Order o join o.member m";
        boolean isFirstCondition = true;
        //주문 상태 검색
        if (orderSearch.getOrderStatus() != null) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " o.status = :status";
        }
        //회원 이름 검색
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            if (isFirstCondition) {
                jpql += " where";
                isFirstCondition = false;
            } else {
                jpql += " and";
            }
            jpql += " m.name like :name";
        }
        TypedQuery<Order> query = em.createQuery(jpql, Order.class)
                .setMaxResults(1000); //최대 1000건
        if (orderSearch.getOrderStatus() != null) {
            query = query.setParameter("status", orderSearch.getOrderStatus());
        }
        if (StringUtils.hasText(orderSearch.getMemberName())) {
            query = query.setParameter("name", orderSearch.getMemberName());
        }
        return query.getResultList();
    }

    //jpa 표준 동적쿼리
    /**
     * JPA Criteria
    * */
    public List<Order> findAllByCriteria(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);
        Join<Object, Object> m = o.join("member", JoinType.INNER);

        List<Predicate> criteria = new ArrayList<>();

        if(orderSearch.getOrderStatus() != null){
            Predicate status = cb.equal(o.get("status"), orderSearch.getOrderStatus());
            criteria.add(status);
        }

        if(StringUtils.hasText(orderSearch.getMemberName())){
            Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberName() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq);
        query.setMaxResults(10000);

        return query.getResultList();
    }

    //fetch join이 재사용성이 좋다.
    //Entity 를 조회했기 때문에 데이터 변경을 할 수 있다.
    public List<Order> findAllWithMemberDelivery() {
        return em.createQuery(
                " select o from Order o" +
                        "  join fetch o.member m" +
                        "  join fetch o.delivery d", Order.class).getResultList();
        //left join fetch 하면 outer조인
    }


    //비슷해보이지만.. 로직의 재활용이 단점
    //v4가 성능최적화에서는 좋다.
    //데이터 변경 할 수 없다.
    public List<SimpleOrderQueryDto> findOrderDtos() {
        return em.createQuery("select new jpabook.jpashop.repository.SimpleOrderQueryDto(o.id, m.name," +
                "o.orderDate, o.status, d.address)" +
                            " from Order o"+
                            " join o.member m" +
                             " join o.delivery d",SimpleOrderQueryDto.class).getResultList();
    }

    /**
     * 1:다 의 관계에서는 패치조인에서는 페이징을 하면 안된다...
    페이징 불가능
     -> 페이징은 되나.. 메모리에서 처리한다. 데이터가 많아지면 메모리 부족현상 발생가능..
    */
    public List<Order> findAllWithItem() {
        return em.createQuery(
                "select distinct o from Order o"+
                        " join fetch o.member m"+
                        " join fetch o.delivery d"+
                        " join fetch o.orderItems oi"+
                        " join fetch oi.item i",Order.class)
                .setFirstResult(1)
                .setMaxResults(100)
                .getResultList();
    }

    public List<Order> findAllWithMemberDelivery(int offset, int limit) {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d", Order.class)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

   /* public List<Order> findAll(OrderSearch orderSearch) {

    }*/

}
