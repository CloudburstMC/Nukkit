package cn.nukkit.network.protocol;

import cn.nukkit.Player;

public class AddBehaviorTreePacket extends DataPacket {

    public String unknown;

    @Override
    public byte pid() {
        return ProtocolInfo.ADD_BEHAVIOR_TREE_PACKET;
    }

    @Override
    public void decode() {

    }

    @Override
    public void encode() {
        this.reset();
        this.putString(unknown);
    }

    @Override
    protected void handle(Player player) {

    }
}
