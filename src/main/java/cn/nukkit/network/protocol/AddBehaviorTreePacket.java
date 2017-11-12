package cn.nukkit.network.protocol;

public class AddBehaviorTreePacket extends DataPacket {

    public String unknown;

    @Override
    public byte pid(PlayerProtocol protocol) {
        return protocol.equals(PlayerProtocol.PLAYER_PROTOCOL_113) ?
                ProtocolInfo113.ADD_BEHAVIOR_TREE_PACKET :
                ProtocolInfo.ADD_BEHAVIOR_TREE_PACKET;
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
