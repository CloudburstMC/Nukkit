package cn.nukkit.network.protocol;

public enum PlayerProtocol {

    //1.1.0-1.1.3
    PLAYER_PROTOCOL_113 (113),
    //1.2.0-1.2.3
    PLAYER_PROTOCOL_130 (130),
    //1.2.5 - x
    PLAYER_PROTOCOL_141 (141);

    private int number;
    public int getNumber() {
        return this.number;
    }

    PlayerProtocol(int number) {
        this.number = number;
    }

}
