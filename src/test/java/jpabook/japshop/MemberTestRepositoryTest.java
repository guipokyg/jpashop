package jpabook.japshop;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MemberTestRepositoryTest {
    @Autowired MemberRepository memberRepository;

    @Test
    @Transactional
    @Rollback(false)
    public void testMember() throws Exception {
        MemberTest memberTest = new MemberTest();
        memberTest.setUsername("memberA");
        Long saveId = memberRepository.save(memberTest);
        MemberTest findMemberTest = memberRepository.find(saveId);

        Assertions.assertThat(findMemberTest.getId()).isEqualTo(memberTest.getId());
        Assertions.assertThat(findMemberTest.getUsername()).isEqualTo(memberTest.getUsername());
        //Assertions.assertThat(findMember).isEqualTo(member);
    }
}