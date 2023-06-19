package gobang.pojo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import toogoo.entity.Assets;


@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class GobangAssets extends Assets {

    public GobangAssets(Long uid, Integer exp, Integer grade, Integer rank) {
        super(uid, exp, grade, rank);
    }
}
