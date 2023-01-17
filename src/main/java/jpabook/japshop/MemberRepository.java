package jpabook.japshop;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    public Long save(MemberTest memberTest){
        em.persist(memberTest);
        return memberTest.getId();
    }

    public MemberTest find(Long id){
        return em.find(MemberTest.class,id);
    }
}
