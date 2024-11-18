package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;

import zerobase.weather.repository.JdbcMemoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional // Test코드에서 실제로 DB에 실행이 되는 것을 막아주는 어노테이션
// 테스트 실행하고 나면 원상태로 복구해줌
// -> 그래서 주석처리하면 실제로 save된 데이터를 확인할 수 있게됨!
public class JdbcMemoRepositoryTest {
    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insertMemoTest () {

        //given
        Memo newMemo = new Memo(2, "insert Memo Test");
        //when -> ~을 했을 때
        jdbcMemoRepository.save(newMemo);
        //then
        Optional<Memo> result = jdbcMemoRepository.findById(2);
        assertEquals(result.get().getText(), "insert Memo Test");
    }

    @Test
    void findAllMemoTest () {
        //given
        List<Memo> memoList = jdbcMemoRepository.findAll();
        System.out.println(memoList);
        assertNotNull(memoList);
        //when
        //then
    }
}
