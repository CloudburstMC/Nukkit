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
import cn.nukkit.utils.BlockColor;

public class BlockTarget extends BlockSolidMeta {

    public BlockTarget() {
        this(0);
    }

    public BlockTarget(int meta) {
        super(meta);
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
        return BlockColor.WHITE_BLOCK_COLOR;
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
        return this.getDamage() > 0 ? 10 : 0;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (entity instanceof EntityProjectile) {
            this.setDamage(1);
            this.level.setBlock((int) this.x, (int) this.y, (int) this.z, BlockLayer.NORMAL, this, false, true, false); // No need to send this to client
            this.level.updateAroundRedstone(this, null);

            if (entity instanceof EntityArrow || entity instanceof EntityThrownTrident) {
                this.level.scheduleUpdate(this, 20);
            } else {
                this.level.scheduleUpdate(this, 8);
            }
        }
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            this.setDamage(0);
            this.level.setBlock((int) this.x, (int) this.y, (int) this.z, BlockLayer.NORMAL, this, false, true, false); // No need to send this to client
            this.level.updateAroundRedstone(this, null);
            return Level.BLOCK_UPDATE_SCHEDULED;
        }
        return 0;
    }
}
