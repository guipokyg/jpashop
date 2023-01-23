package jpabook.japshop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jpabook.japshop.domain.Order;
import jpabook.japshop.service.OrderService;
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

//        List<Order> resultList = em.createQuery("select o from Order o join o.member m" +
//                " where o.status = :status" +
//                "   and m.name like :name", Order.class)
//                .setParameter("status",orderSearch.getOrderStatus())
//                .setParameter("name",orderSearch.getMemberNamae())
//                .setFirstResult(0)//페이징
//                .setMaxResults(1000)//최대1000건
//                .getResultList();

        List<Order> resultList = em.createQuery("select o from Order o join o.member m" +
                                " where o.status = :status" +
                                "   and m.name like :name", Order.class)
                                .getResultList();
        return resultList;
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

        if(StringUtils.hasText(orderSearch.getMemberNamae())){
            Predicate name = cb.like(m.get("name"), "%" + orderSearch.getMemberNamae() + "%");
            criteria.add(name);
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query = em.createQuery(cq);
        query.setMaxResults(10000);

        return query.getResultList();
    }

   /* public List<Order> findAll(OrderSearch orderSearch) {

    }*/

}
