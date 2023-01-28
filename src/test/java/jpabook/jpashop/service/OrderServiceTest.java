package jpabook.jpashop.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.domain.item.Item;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Fail.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @PersistenceContext
    EntityManager em;

    @Autowired
    OrderService orderService;

    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{

        Member member = createMember();

        Item item = createItem("시골 jpa", 10000, 10);

        Long orderId = orderService.order(member.getId(), item.getId(), 2);

        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("상품주문시 상태는 ORDER", OrderStatus.ORDER,getOrder.getStatus());
        Assert.assertEquals("주문한 상품 종류수가 정확해야한다.",1,getOrder.getOrderItems().size());
        Assert.assertEquals("주문 가격은 가격*수량이다",10000*2,getOrder.getTotalPrice());
        Assert.assertEquals("주문수량만큼 재고가 줄어야한다",10-2,item.getStockQuantity());
    }

    private Item createItem(String itemName, int price, int stockQuantity) {
        Item item = new Book();
        item.setName(itemName);
        item.setPrice(price);
        item.setStockQuantity(stockQuantity);
        em.persist(item);
        return  item;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("회원1");
        member.setAddress(new Address("서울","강동구","123-123"));

        em.persist(member);
        return member;
    }

    @Test(expected = NotEnoughStockException.class)
    public void 상품주문_재고수량초과() throws Exception{
        Member member = createMember();
        Item book= createItem("시골 jpa", 10000, 10);

        int orderCount = 11;

        orderService.order(member.getId(), book.getId(), orderCount);

        fail("재고수량 부족예외가 발생해야 한다.");
    }

    @Test
    public void 주문취소() throws Exception{
        Member member = createMember();
        Item item = createItem("시골JPA",10000,10);

        int orderCount =  2;

        Long orderId = orderService.order(member.getId(), item.getId(), orderCount);

        orderService.cancelOrder(orderId);

        Order getOrder = orderRepository.findOne(orderId);

        Assert.assertEquals("주문취소시 상태는 CANCEL 이다",OrderStatus.CANCEL,getOrder.getStatus());
        Assert.assertEquals("주문 취소된 상품은 그만큼 재고가 증가해야 한다.", 10, item.getStockQuantity());
    }
}