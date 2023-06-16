package gobang.dao;

import gobang.pojo.GobangAssets;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface GobangAssetsDao {

    GobangAssets findByUid(Long uid);

    void insertAssets(Long uid);
}
