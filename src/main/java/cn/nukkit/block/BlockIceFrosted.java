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
        this.getLevel().scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public boolean onBreak(Item item) {
        this.getLevel().setBlock(this, new BlockWaterStill(), true);
        for (BlockFace face : BlockFace.values()) {
            Block nearBlock = this.getSide(face);
            if (nearBlock instanceof BlockIceFrosted) {
                int age = nearBlock.getDamage();
                if (age < 3) {
                    nearBlock.setDamage(age + 1);
                    this.getLevel().scheduleUpdate(nearBlock, ThreadLocalRandom.current().nextInt(20, 40));
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
            if (this.getLevel().getBlockLightAt(this.getFloorX(), this.getFloorY(), this.getFloorZ()) > 11) {
                List<Block> nearFrosted = new ArrayList<Block>();
                for (BlockFace face : BlockFace.values()) {
                    Block nearBlock = this.getSide(face);
                    if (nearBlock instanceof BlockIceFrosted) {
                        nearFrosted.add(nearBlock);
                    }
                }
                if (ThreadLocalRandom.current().nextInt(3) == 0 || nearFrosted.size() < 4) {
                    int age = this.getDamage();
                    if (age < 3) {
                        this.setDamage(age + 1);
                        this.getLevel().scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
                    } else {
                        this.getLevel().useBreakOn(this);
                    }
                } else {
                    this.getLevel().scheduleUpdate(this, ThreadLocalRandom.current().nextInt(20, 40));
                }
            }
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
