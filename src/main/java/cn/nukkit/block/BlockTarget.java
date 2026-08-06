package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.entity.projectile.EntityThrownTrident;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

public class BlockTarget extends BlockSolid {

    public BlockTarget() {
        super();
    }

    @Override
    public String getName() {
        return "Target";
    }

    @Override
    public int getId() {
        return TARGET;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_HOE;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 15;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.QUARTZ_BLOCK_COLOR;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{new ItemBlock(Block.get(TARGET))};
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace face) {
        for (Entity e : level.getCollidingEntities(new SimpleAxisAlignedBB(x - 0.000001, y - 0.000001, z - 0.000001, x + 1.000001, y + 1.000001, z + 1.000001))) {
            if (e instanceof EntityProjectile) {
                EntityProjectile projectile = (EntityProjectile) e;
                boolean isArrowTrident = e instanceof EntityArrow || e instanceof EntityThrownTrident;
                if (projectile.getCollidedTick() > 0 && level.getServer().getTick() - projectile.getCollidedTick() < (isArrowTrident ? 20 : 8)) {
                    return 10;
                }
            }
        }

        return 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity instanceof EntityProjectile) {
            this.level.updateAroundRedstone(this, null);
            boolean isArrowTrident = entity instanceof EntityArrow || entity instanceof EntityThrownTrident;
            this.level.scheduleUpdate(this, isArrowTrident ? 20 : 8);
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.level.updateAroundRedstone(this, null);
            return Level.BLOCK_UPDATE_SCHEDULED;
        }
        return 0;
    }
}
