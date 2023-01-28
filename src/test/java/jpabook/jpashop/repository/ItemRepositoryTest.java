package jpabook.jpashop.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.service.ItemService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class ItemRepositoryTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    @Test
    public void 상품_등록(){
        Book regBook = new Book();

        regBook.setName("shop jpa 개발 과정");

        itemService.saveItem(regBook);

        Assert.assertEquals(regBook,itemRepository.findOne(regBook.getId()));
    }


}