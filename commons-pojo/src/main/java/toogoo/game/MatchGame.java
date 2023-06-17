package toogoo.game;

import lombok.Data;

@Data
public class MatchGame {

    private Long uid;

    private String gameType;

    private String gameMode;

    private Object config;

}
