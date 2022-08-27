package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemSnowball;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

/**
 * Created on 2015/12/6 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockSnowLayer extends BlockFallable {

    private int meta;

    public BlockSnowLayer() {
        this(0);
    }

    public BlockSnowLayer(int meta) {
        this.meta = meta;
    }

    @Override
    public final int getFullId() {
        return (this.getId() << 4) + this.getDamage();
    }

    @Override
    public final int getDamage() {
        return this.meta;
    }

    @Override
    public final void setDamage(int meta) {
        this.meta = meta;
    }

    @Override
    public String getName() {
        return "Top Snow";
    }

    @Override
    public int getId() {
        return SNOW_LAYER;
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    @Override
    public double getResistance() {
        return 0.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHOVEL;
    }

    @Override
    public boolean canBeReplaced() {
        return (this.getDamage() & 0x7) != 0x7;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.canSurvive()) {
            this.getLevel().setBlock(block, this, true);

            return true;
        }
        return false;
    }

    private boolean canSurvive() {
        Block below = this.down();
        return below.getId() != ICE && below.getId() != PACKED_ICE && below.getId() != ICE_FROSTED && (below.isSolid() || (this.getDamage() & 0x7) == 0x7);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if ((this.getDamage() & 0x7) != 0x7 || this.up().getId() != SNOW_LAYER) {
                super.onUpdate(type);
            }

            if (this.level.getBlockIdAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()) == SNOW_LAYER && !this.canSurvive()) {
                this.level.useBreakOn(this, null, null, true);
                if (this.level.getGameRules().getBoolean(GameRule.DO_TILE_DROPS)) {
                    this.level.dropItem(this, this.toItem());
                }
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            if (this.getLevel().getBlockLightAt((int) this.x, (int) this.y, (int) this.z) >= 10) {
                BlockFadeEvent event = new BlockFadeEvent(this, (this.getDamage() & 0x7) > 0 ? get(SNOW_LAYER, this.getDamage() - 1) : get(AIR));
                level.getServer().getPluginManager().callEvent(event);
                if (!event.isCancelled()) {
                    level.setBlock(this, event.getNewState(), true);
                }
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemSnowball();
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShovel() && item.getTier() >= ItemTool.TIER_WOODEN) {
            Item drop = this.toItem();
            int height = this.getDamage() & 0x7;
            drop.setCount(height < 3 ? 1 : height < 5 ? 2 : height == 7 ? 4 : 3);
            return new Item[]{drop};
        } else {
            return new Item[0];
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.SNOW_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
    
    @Override
    public boolean isTransparent() {
        return (this.getDamage() & 0x7) != 0x7;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public boolean isSolid() {
        return (this.getDamage() & 0x7) == 0x7;
    }

    @Override
    public double getMaxY() {
        int height = this.getDamage() & 0x7;
        return height < 3 ? this.y : height == 7 ? this.y + 1 : this.y + 0.5;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.isShovel() && (player.gamemode & 0x2) == 0) {
            item.useOn(this);
            this.level.useBreakOn(this, item.clone().clearNamedTag(), null, true);
            return true;
        } else if (item.getId() == SNOW_LAYER && (player.gamemode & 0x2) == 0) {
            if ((this.getDamage() & 0x7) != 0x7) {
                this.setDamage(this.getDamage() + 1);
                this.level.setBlock(this ,this, true);

                if (player != null && (player.gamemode & 0x1) == 0) {
                    item.count--;
                }
                return true;
            } else {
                this.level.setBlock(this ,this, true);
            }
        }
        return false;
    }
}
