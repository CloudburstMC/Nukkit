package cn.nukkit.network.protocol;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityItemFrame;
import cn.nukkit.event.block.ItemFrameDropItemEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;

/**
 * Created by Pub4Game on 03.07.2016.
 */
public class ItemFrameDropItemPacket extends DataPacket {

    public static final byte NETWORK_ID = ProtocolInfo.ITEM_FRAME_DROP_ITEM_PACKET;

    public int x;
    public int y;
    public int z;

    @Override
    public void decode() {
        BlockVector3 v = this.getBlockVector3();
        this.z = v.z;
        this.y = v.y;
        this.x = v.x;
    }

    @Override
    public void encode() {

    }

    @Override
    public byte pid() {
        return NETWORK_ID;
    }

    @Override
    public void handle(Player player) {
        Vector3 vector3 = player.temporalVector.setComponents(this.x, this.y, this.z);
        BlockEntity blockEntityItemFrame = player.level.getBlockEntity(vector3);
        BlockEntityItemFrame itemFrame = (BlockEntityItemFrame) blockEntityItemFrame;
        if (itemFrame != null) {
            Block block = itemFrame.getBlock();
            Item itemDrop = itemFrame.getItem();
            ItemFrameDropItemEvent itemFrameDropItemEvent = new ItemFrameDropItemEvent(player, block, itemFrame, itemDrop);
            player.server.getPluginManager().callEvent(itemFrameDropItemEvent);
            if (!itemFrameDropItemEvent.isCancelled()) {
                if (itemDrop.getId() != Item.AIR) {
                    vector3 = player.temporalVector.setComponents(itemFrame.x + 0.5, itemFrame.y, itemFrame.z + 0.5);
                    player.level.dropItem(vector3, itemDrop);
                    itemFrame.setItem(new ItemBlock(new BlockAir()));
                    itemFrame.setItemRotation(0);
                    player.getLevel().addSound(player, Sound.BLOCK_ITEMFRAME_REMOVE_ITEM);
                }
            } else {
                itemFrame.spawnTo(player);
            }
        }
    }
}
