package cn.nukkit.entity.impl.vehicle;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.vehicle.HopperMinecart;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.chunk.Chunk;
import cn.nukkit.math.Vector3f;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.player.Player;
import cn.nukkit.utils.MinecartType;

public class EntityHopperMinecart extends EntityAbstractMinecart implements HopperMinecart {

    public static final int NETWORK_ID = 96;

    public EntityHopperMinecart(EntityType<HopperMinecart> type, Chunk chunk, CompoundTag nbt) {
        super(type, chunk, nbt);
        super.setDisplayBlock(Block.get(BlockIds.HOPPER), false);
    }

    // TODO: 2016/12/18 inventory

    @Override
    public MinecartType getMinecartType() {
        return MinecartType.valueOf(5);
    }

    @Override
    public boolean isRideable() {
        return false;
    }

    @Override
    public void dropItem() {
        level.dropItem(this, Item.get(ItemIds.HOPPER_MINECART));
    }

    @Override
    protected void activate(int x, int y, int z, boolean flag) {

    }

    @Override
    public boolean mount(Entity entity) {
        return false;
    }

    @Override
    public boolean onInteract(Player p, Item item, Vector3f clickedPos) {
        return false;
    }
}
