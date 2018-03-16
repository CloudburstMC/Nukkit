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
    public void handle(Player player) {
        Block block = player.level.getBlock(player.temporalVector.setComponents(this.x, this.y, this.z));
        Item item = block.toItem();

        if (this.addUserData) {
            BlockEntity blockEntity = player.getLevel().getBlockEntity(new Vector3(this.x, this.y, this.z));
            if (blockEntity != null) {
                CompoundTag nbt = blockEntity.getCleanedNBT();
                if (nbt != null) {
                    Item item1 = player.getInventory().getItemInHand();
                    item1.setCustomBlockData(nbt);
                    item1.setLore("+(DATA)");
                    player.getInventory().setItemInHand(item1);
                }
            }
        }

        PlayerBlockPickEvent pickEvent = new PlayerBlockPickEvent(player, block, item);
        if (!player.isCreative()) {
            player.server.getLogger().debug("Got block-pick request from " + player.getName() + " when not in creative mode (gamemode " + player.getGamemode() + ")");
            pickEvent.setCancelled();
        }

        player.server.getPluginManager().callEvent(pickEvent);

        if (!pickEvent.isCancelled()) {
            player.inventory.setItemInHand(pickEvent.getItem());
        }
    }
}
