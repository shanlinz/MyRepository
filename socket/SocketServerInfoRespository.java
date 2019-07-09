package socket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by Administrator on 2019/6/29.
 */
public interface SocketServerInfoRespository extends JpaRepository<SocketServerInfo,Long> {

    @Transactional
    @Modifying
    @Query("update SocketServerInfo set status = 1 where id = :id ")
    void update(@Param("id") Integer id);
}
