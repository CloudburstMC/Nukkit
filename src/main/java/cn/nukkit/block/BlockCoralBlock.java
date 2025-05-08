package cn.nukkit.block;

import cn.nukkit.event.block.BlockFadeEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import java.util.concurrent.ThreadLocalRandom;

public class BlockCoralBlock extends BlockSolidMeta {

    private static final String[] NAMES = {
            "Tube Coral Block",
            "Brain Coral Block",
            "Bubble Coral Block",
            "Fire Coral Block",
            "Horn Coral Block",
    };

    public BlockCoralBlock() {
        this(0);
    }

    public BlockCoralBlock(int meta) {
        super(meta);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!this.isDead()) {
                this.getLevel().scheduleUpdate(this, 60 + ThreadLocalRandom.current().nextInt(40));
            }
            return type;
        }

        if (type != Level.BLOCK_UPDATE_SCHEDULED) {
            return 0;
        }

        if (this.isDead()) {
            return type;
        }

        for (BlockFace face : BlockFace.values()) {
            if (this.getSide(BlockLayer.NORMAL, face) instanceof BlockWater || this.getSide(BlockLayer.WATERLOGGED, face) instanceof BlockWater
                    || this.getSide(BlockLayer.NORMAL, face) instanceof BlockIceFrosted || this.getSide(BlockLayer.WATERLOGGED, face) instanceof BlockIceFrosted) {
                return type;
            }
        }

        BlockFadeEvent event = new BlockFadeEvent(this, Block.get(CORAL_BLOCK, this.getDamage() | 0x8));
        level.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.setDead(true);
            this.getLevel().setBlock(this, event.getNewState(), true, true);
        }
        return type;
    }

    public double getHardness() {
        return 1.5;
    }

    public double getResistance() {
        return 6;
    }

    @Override
    public String getName() {
        int variant = this.getDamage() & 0x7;
        String name;
        if (variant >= NAMES.length) {
            name = NAMES[0];
        } else {
            name = NAMES[variant];
        }
        return this.isDead() ? "Dead " + name : name;
    }

    @Override
    public int getId() {
        return CORAL_BLOCK;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
            if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
                return new Item[]{this.toItem() };
            } else {
                return new Item[]{ new ItemBlock(this.clone(), this.getDamage() | 0x8) };
            }
        } else {
            return new Item[0];
        }
    }

    public boolean isDead() {
        return (this.getDamage() & 0x8) == 0x8;
    }

    public void setDead(boolean dead) {
        if (dead) {
            this.setDamage(this.getDamage() | 0x8);
        } else {
            this.setDamage(this.getDamage() ^ 0x8);
        }
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLUE_BLOCK_COLOR;
    }
}
