package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class BlockIceFrosted extends BlockTransparentMeta {

    public BlockIceFrosted() {
        this(0);
    }

    public BlockIceFrosted(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return FROSTED_ICE;
    }

    @Override
    public String getName() {
        return "Frosted Ice";
    }

    @Override
    public double getResistance() {
        return 2.5;
    }

    @Override
    public double getHardness() {
        return 0.5;
    }

    @Override
    public double getFrictionFactor() {
        return 0.98;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        boolean t = super.place(item, block, target, face, fx, fy, fz, player);
        if (t) {
            this.getLevel().scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
        }
        return t;
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, new BlockWaterStill(), true);
        for (BlockFace face : BlockFace.values()) {
            Block nearBlock = this.getSide(face);
            if (nearBlock instanceof BlockIceFrosted && this.getLevel().getFullLight(nearBlock) > 11) {
                BlockIceFrosted block = (BlockIceFrosted) nearBlock.clone();
                int age = block.getDamage();
                if (age < 3) {
                    block.setDamage(age + 1);
                    this.getLevel().setBlock(nearBlock, block);
                } else {
                    this.getLevel().setBlock(nearBlock, new BlockWaterStill(), true);
                }
            }
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (this.getLevel().getFullLight(this) > 11) {
                List<Block> nearFrosted = new ArrayList<>();
                for (BlockFace face : BlockFace.values()) {
                    Block nearBlock = this.getSide(face);
                    if (nearBlock instanceof BlockIceFrosted) {
                        nearFrosted.add(nearBlock);
                    }
                }
                if (ThreadLocalRandom.current().nextInt(3) == 0 || nearFrosted.size() < 4) {
                    BlockIceFrosted block = (BlockIceFrosted) this.clone();
                    int age = block.getDamage();
                    if (age < 3) {
                        block.setDamage(age + 1);
                        this.getLevel().setBlock(this, block);
                    } else {
                        this.getLevel().useBreakOn(this);
                        return Level.BLOCK_UPDATE_NORMAL;
                    }
                }
            }
            this.getLevel().scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
        }
        return 0;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(new BlockAir());
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.ICE_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }
}
