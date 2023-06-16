package gobang.pojo.game;

import lombok.Data;

@Data
public class WinInfo {

    private Long winId;

    private String winType;

    public WinInfo(Long winId, String winType) {
        this.winId = winId;
        this.winType = winType;
    }
}
