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
    public void handle(Player player) {
        if (!player.spawned || !player.isAlive()) {
            return;
        }

        player.craftingType = CRAFTING_SMALL;
        //this.resetCraftingGridType();

        Entity targetEntity = player.level.getEntity(this.target);

        if (targetEntity == null || !targetEntity.isAlive()) {
            return;
        }

        if (targetEntity instanceof EntityItem || targetEntity instanceof EntityArrow || targetEntity instanceof EntityXPOrb) {
            player.kick(PlayerKickEvent.Reason.INVALID_PVE, "Attempting to interact with an invalid entity");
            player.server.getLogger().warning(player.server.getLanguage().translateString("nukkit.player.invalidEntity", player.getName()));
            return;
        }

        Item item = player.inventory.getItemInHand();

        switch (this.action) {
            case InteractPacket.ACTION_MOUSEOVER:
                player.server.getPluginManager().callEvent(new PlayerMouseOverEntityEvent(player, targetEntity));
                break;
            case InteractPacket.ACTION_VEHICLE_EXIT:
                if (!(targetEntity instanceof EntityRideable) || player.riding == null) {
                    break;
                }

                ((EntityRideable) player.riding).mountEntity(player);
                break;
        }
    }
}
