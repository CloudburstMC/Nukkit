package cn.nukkit.network.protocol;

public enum PlayerProtocol {

    PLAYER_PROTOCOL_113 (113),
    PLAYER_PROTOCOL_130 (130);

    private int number;
    public int getNumber(){
        return this.number;
    }

    PlayerProtocol(int number){
        this.number = number;
    }

}
