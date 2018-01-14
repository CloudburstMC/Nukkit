package cn.nukkit.server.entity.item;

import cn.nukkit.server.block.BlockHopper;
import cn.nukkit.server.item.ItemMinecartHopper;
import cn.nukkit.server.level.format.FullChunk;
import cn.nukkit.server.nbt.tag.CompoundTag;
import cn.nukkit.server.util.MinecartType;

public class EntityMinecartHopper extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 96;

    public EntityMinecartHopper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        super.setDisplayBlock(new BlockHopper());
    }

    // TODO: 2016/12/18 inventory

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(5);
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void dropItem() {
        level.dropItem(this, new ItemMinecartHopper());
    }
}
