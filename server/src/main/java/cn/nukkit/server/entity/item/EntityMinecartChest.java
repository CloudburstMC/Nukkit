package cn.nukkit.server.entity.item;

import cn.nukkit.server.block.BlockChest;
import cn.nukkit.server.item.ItemMinecartChest;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.util.MinecartType;

/**
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.server.entity.item in project Nukkit.
 */
public class EntityMinecartChest extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 98;

    public EntityMinecartChest(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        super.setDisplayBlock(new BlockChest());
    }

    // TODO: 2016/1/30 inventory

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(1);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void dropItem() {
        level.dropItem(this, new ItemMinecartChest());
    }


}
