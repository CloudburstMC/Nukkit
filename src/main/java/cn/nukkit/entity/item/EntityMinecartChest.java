package cn.nukkit.entity.item;

import cn.nukkit.block.BlockChest;
import cn.nukkit.item.ItemMinecartChest;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.EnumMinecart;

/**
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public class EntityMinecartChest extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 98;

    public EntityMinecartChest(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        super.setDisplayBlock(new BlockChest());
    }

    // TODO: 2016/1/30 inventory
    
    @Override
    public EnumMinecart getType() {
        return EnumMinecart.valueOf(1);
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
