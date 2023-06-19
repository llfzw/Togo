package gobang.dao;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import gobang.pojo.GobangAssets;
import gobang.pojo.vo.GameTopVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import toogoo.RespPojo.Message;
import toogoo.entity.Assets;

import java.util.List;

@Repository
@Mapper
public interface GobangAssetsDao extends BaseMapper<Assets> {

    GobangAssets findByUid(Long uid);

    void insertAssets(Long uid);

    List<GameTopVo> getTopByRank(int num);
}
