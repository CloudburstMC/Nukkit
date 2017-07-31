package cn.nukkit.entity.item;

import cn.nukkit.block.BlockHopper;
import cn.nukkit.item.ItemMinecartHopper;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.EnumMinecart;

public class EntityMinecartHopper extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 96;

    public EntityMinecartHopper(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        super.setDisplayBlock(new BlockHopper());
    }

    // TODO: 2016/12/18 inventory

    @Override
    public EnumMinecart getType() {
        return EnumMinecart.valueOf(5);
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
