package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.event.player.PlayerBlockPickEvent;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;

public class BlockPickRequestPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.BLOCK_PICK_REQUEST_PACKET;

    public int x;
    public int y;
    public int z;
    public boolean addUserData;
    public int selectedSlot;

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void decode() {
        BlockVector3 v = this.getSignedBlockPosition();
        this.x = v.x;
        this.y = v.y;
        this.z = v.z;
        this.putBoolean(this.addUserData);
        this.selectedSlot = this.getByte();
    }

    @Override
    public void encode() {

    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
