package socket;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Created by Administrator on 2019/7/8.
 */
public interface SourceInfoRespository extends JpaRepository<SourceInfo,Integer> {

    @Query(value = " from SourceInfo")
    SourceInfo getSource(@Param("neUID") String neUID);
}
