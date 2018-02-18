package cn.nukkit.network.protocol;

import java.lang.reflect.Field;

public enum PlayerProtocol {

    //1.1.0-1.1.3
    PLAYER_PROTOCOL_113 (113, ProtocolInfo113.class),
    //1.2.0-1.2.3
    PLAYER_PROTOCOL_130 (130, ProtocolInfo130.class),
    //1.2.5 - x
    PLAYER_PROTOCOL_141 (141, 130, ProtocolInfo130.class);

    private int number;
    public int getNumber(){
        return this.number;
    }

    private int mainNumber;
    public int getMainNumber() {
        return this.mainNumber;
    }

    private Class<? extends ProtocolInfo> infoClass;
    public byte getPacketId(String name){
        try {
            Field field = infoClass.getDeclaredField(name);
            return field.getByte(infoClass);
        }
        catch (NoSuchFieldException|IllegalAccessException exception) {
            return 0;
        }
    }

    PlayerProtocol(int number, Class<? extends ProtocolInfo> infoClass){
        this.number = number;
        this.mainNumber = number;
        this.infoClass = infoClass;
    }
    PlayerProtocol(int number, int mainNumber, Class<? extends ProtocolInfo> infoClass){
        this.number = number;
        this.mainNumber = mainNumber;
        this.infoClass = infoClass;
    }

    public static PlayerProtocol getNewestProtocol(){
        int highestNum = 0; PlayerProtocol highest = null;
        for (PlayerProtocol protocol : values()) {
            if (protocol.getNumber() > highestNum) {
                highestNum = protocol.getNumber();
                highest = protocol;
            }
        }
        return highest;
    }

}
