package cn.nukkit.entity.impl.vehicle;

import cn.nukkit.block.Block;
import cn.nukkit.block.BlockIds;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityType;
import cn.nukkit.entity.vehicle.ChestMinecart;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.level.Location;
import cn.nukkit.player.Player;
import cn.nukkit.utils.MinecartType;
import com.nukkitx.math.vector.Vector3f;

/**
 * Created by Snake1999 on 2016/1/30.
 * Package cn.nukkit.entity.item in project Nukkit.
 */
public class EntityChestMinecart extends EntityAbstractMinecart implements ChestMinecart {

    public EntityChestMinecart(EntityType<ChestMinecart> type, Location location) {
        super(type, location);
    }

    @Override
    public void initEntity() {
        super.initEntity();

        this.setDisplayBlock(Block.get(BlockIds.CHEST));
        this.setDisplay(true);
    }

    // TODO: 2016/1/30 inventory

    @Override
    public MinecartType getMinecartType() {
        return MinecartType.valueOf(1);
    }

    @Override
    public boolean isRideable() {
        return false;
    }

    @Override
    public void dropItem() {
        this.getLevel().dropItem(this.getPosition(), Item.get(ItemIds.CHEST_MINECART));
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
