package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelSoundEventPacket;

public class BlockCandle extends BlockTransparentMeta {

    public BlockCandle() {
        this(0);
    }

    public BlockCandle(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Candle";
    }

    @Override
    public int getId() {
        return CANDLE;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!isSupportValidBelow()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (target.getId() == this.getId() && (target.getDamage() & 0b11) < 3) {
            target.setDamage(target.getDamage() + 1);
            this.getLevel().setBlock(target, target, true, true);
            return true;
        }

        if (block instanceof BlockWater || block.level.isBlockWaterloggedAt(block.getChunk(), (int) block.x, (int) block.y, (int) block.z)) {
            return false;
        }

        if (isSupportValidBelow()) {
            return this.getLevel().setBlock(this, this, true, true);
        }
        return false;
    }

    private boolean isSupportValidBelow() {
        Block block = this.down();
        if (!block.isTransparent() || block.isNarrowSurface()) {
            return true;
        }
        return Block.canStayOnFullSolid(block);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{new ItemBlock(Block.get(this.getId()), 0, getCandleCount())};
    }

    @Override
    public int getLightLevel() {
        if (!this.isLit()) {
            return 0;
        }

        return getCandleCount() * 3;
    }

    public int getCandleCount() {
        return (getDamage() & 0b11) + 1;
    }

    public boolean isLit() {
        return (getDamage() & 0x4) == 0x4;
    }

    public void setLit(boolean dead) {
        if (dead) {
            this.setDamage(getDamage() | 0x4);
        } else {
            this.setDamage(getDamage() ^ 0x4);
        }
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == ItemID.FLINT_AND_STEEL && !this.isLit()) {
            item.useOn(this);
            this.setLit(true);
            level.addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_IGNITE);
            level.setBlock(this, this, true, true);
            return true;
        } else if (item.getId() == AIR && this.isLit()) {
            this.setLit(false);
            level.setBlock(this, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public double getMinX() {
        return this.x + 0.4;
    }

    @Override
    public double getMinZ() {
        return this.z + 0.4;
    }

    @Override
    public double getMaxX() {
        return this.x + 0.6;
    }

    @Override
    public double getMaxZ() {
        return this.z + 0.6;
    }

    @Override
    public double getMaxY() {
        return this.y + 0.4;
    }
}
