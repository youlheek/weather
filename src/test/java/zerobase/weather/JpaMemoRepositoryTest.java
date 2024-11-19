package zerobase.weather;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zerobase.weather.domain.Memo;
import zerobase.weather.repository.JpaMemoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional // test 코드에 Transactional 을 쓰게 되면 Commit을 안 시킴, Rollback 처리
// -> @Test 등 테스트 어노테이션의 존재 여부를 통해 스프링이 알 수 있음
public class JpaMemoRepositoryTest {

    @Autowired
    JpaMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {
        //given
        Memo newMemo = new Memo(10, "This is jpa memo");
        //when
        jpaMemoRepository.save(newMemo);
        //then
        List<Memo> memoList = jpaMemoRepository.findAll();
        assertTrue(memoList.size() > 0);
    }

    @Test
    void findByIdtest () {
        //given
        Memo newMemo = new Memo(11, "jpa");
        //when
        Memo memo = jpaMemoRepository.save(newMemo);
        System.out.println(memo.getId()); // 5
        // -> Memo 클래스에 id타입을 GenerationType.IDENTITY로 해놓아서
        // 스프링부트가 아닌 mysql에서 id를 생성해주기 때문에
        // 사실상 위에 생성자로 id : 11 을 지정하는것은 의미가 없음

        //then
        Optional<Memo> result = jpaMemoRepository.findById(memo.getId());
        assertEquals(newMemo.getText(), result.get().getText());
    }

}
