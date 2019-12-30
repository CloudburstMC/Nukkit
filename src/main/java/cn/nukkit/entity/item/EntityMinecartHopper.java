package cn.nukkit.entity.item;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.MinecartType;

public class EntityMinecartHopper extends EntityMinecartAbstract {

    public static final int NETWORK_ID = 96;

    public EntityMinecartHopper(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        super.setDisplayBlock(Block.get(BlockIds.HOPPER), false);
    }

    // TODO: 2016/12/18 inventory

    @Override
    public MinecartType getType() {
        return MinecartType.valueOf(5);
    }

    @Override
    public boolean isRideable(){
        return false;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public void dropItem() {
        level.dropItem(this, Item.get(ItemIds.HOPPER_MINECART));
    }

    @Override
    protected void activate(int x, int y, int z, boolean flag) {

    }

    @Override
    public boolean mountEntity(Entity entity) {
        return false;
    }

    @Override
    public boolean onInteract(Player p, Item item, Vector3 clickedPos) {
        return false;
    }
}
