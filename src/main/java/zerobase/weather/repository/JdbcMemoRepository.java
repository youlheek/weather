package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcMemoRepository(DataSource dataSource) {
    // application.properties 에 설정해둔 Datasource들이 @Autowired를 통해 datasource에 들어감

        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo) {
    // Memo라는 클래스의 값을 저장하면 mysql 에 Memo라는 클래스의 값이 저장이 되고 반환값으로 Memo를 반환
        String sql = "insert into memo values(?, ?)";
        jdbcTemplate.update(sql, memo.getId(), memo.getText());
        return memo;
        // JDBC를 활용해서 Springboot의 Memo라는 객체를 쿼리문을 활용해
        // mysql 데이터베이스에 넣어준 것
    }

    public List<Memo> findAll() {
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper()); // select 함수는 query 메서드를 사용하고 Resultset반환형태를 memoRowMapper메서드 사용해서 처리
    }

    public Optional<Memo> findById(int id) {
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst();
    }



    private RowMapper<Memo> memoRowMapper() {
        // RowMapper : JDBC를 통해서 mysql 데이터베이스에서 데이터를 가져오면,
        // 그 가져온 데이터 값은 ResultSet이라는 형식임
        // ResultSet의 형식 : {id = 1, text = 'this is memo'}
        // 이 ResultSet을 Memo라는 형식으로 매핑해줘야 하는데, 그것을 RowMapper라고 함

        return (rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }
}
