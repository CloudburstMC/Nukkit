package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

public class BlockCoral extends BlockTransparentMeta {

    public static final int TYPE_TUBE = 0;
    public static final int TYPE_BRAIN = 1;
    public static final int TYPE_BUBBLE = 2;
    public static final int TYPE_FIRE = 3;
    public static final int TYPE_HORN = 4;

    private static final String[] NAMES = {
            "Tube Coral",
            "Brain Coral",
            "Bubble Coral",
            "Fire Coral",
            "Horn Coral",
            "",
            "",
            "",
            "Dead Tube Coral",
            "Dead Brain Coral",
            "Dead Bubble Coral",
            "Dead Fire Coral",
            "Dead Horn Coral"
    };

    public BlockCoral() {
        this(0);
    }

    public BlockCoral(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        int variant = this.getDamage();
        if (variant >= NAMES.length) {
            return NAMES[0];
        }
        return NAMES[variant];
    }

    @Override
    public int getId() {
        return CORAL;
    }

    public double getHardness() {
        return 0;
    }

    public double getResistance() {
        return 0;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().isTransparent()) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.down().isTransparent()) {
            return false;
        }
        if (this.getDamage() < 8 && !(block instanceof BlockWater || block.level.isBlockWaterloggedAt(block.getChunk(), (int) block.x, (int) block.y, (int) block.z))) {
            this.setDamage(8 + this.getDamage()); // Dead
        }
        if (this.getLevel().setBlock(this, this, true, true)) {
            if (block instanceof BlockWater) {
                this.getLevel().setBlock((int) this.x, (int) this.y, (int) this.z, Block.LAYER_WATERLOGGED, Block.get(Block.STILL_WATER), true, true);
            }
            return true;
        }
        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.getEnchantment(Enchantment.ID_SILK_TOUCH) != null) {
            return super.getDrops(item);
        } else {
            return new Item[0];
        }
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BLUE_BLOCK_COLOR;
    }
}
