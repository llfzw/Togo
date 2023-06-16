package toogoo.game;

import lombok.Data;

@Data
public class GameRoom {
    private String roomId;
    private Long player1;
    private Long player2;
    private String gameType;
}
