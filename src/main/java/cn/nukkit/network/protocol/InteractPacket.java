package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityRideable;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.entity.item.EntityXPOrb;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerMouseOverEntityEvent;
import cn.nukkit.item.Item;

import static cn.nukkit.Player.CRAFTING_SMALL;

/**
 * Created on 15-10-15.
 */
public class InteractPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.INTERACT_PACKET;

    public static final int ACTION_VEHICLE_EXIT = 3;
    public static final int ACTION_MOUSEOVER = 4;

    public static final int ACTION_OPEN_INVENTORY = 6;

    public int action;
    public long target;

    @Override
    public void decode() {
        this.action = this.getByte();
        this.target = this.getEntityRuntimeId();
    }

    @Override
    public void encode() {
        this.reset();
        this.putByte((byte) this.action);
        this.putEntityRuntimeId(this.target);
    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    protected void handle(Player player) {
        player.handle(this);
    }
}
