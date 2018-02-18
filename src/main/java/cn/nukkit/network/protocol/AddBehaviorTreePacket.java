package cn.nukkit.network.protocol;

public class AddBehaviorTreePacket extends DataPacket {

    public String unknown;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.getPacketId("ADD_BEHAVIOR_TREE_PACKET");
    }

    @Override
    public void decode(PlayerProtocol protocol) {

    }

    @Override
    public void encode(PlayerProtocol protocol) {
        this.reset(protocol);
        this.putString(unknown);
    }
}
