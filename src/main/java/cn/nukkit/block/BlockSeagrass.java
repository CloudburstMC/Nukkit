package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.*;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.anvil.Anvil;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.BlockColor;

public class BlockSeagrass extends BlockFlowable {

    public BlockSeagrass() {
        this(0);
    }

    public BlockSeagrass(int meta) {
        super(meta % 3);
    }

    @Override
    public String getName() {
        return "Seagrass";
    }

    @Override
    public int getId() {
        return SEAGRASS;
    }

    public int getToolType() {
        return ItemTool.SHEARS;
    }

    public double getHardness() {
        return 0;
    }

    public double getResistance() {
        return 0;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (level != null && level.getProvider() instanceof Anvil) {
            if (!(block instanceof BlockWater && block.getDamage() == 0) || this.down().isTransparent()) {
                return false;
            }
            return this.getLevel().setBlock(this, this, true, true);
        }


        Block down = this.down();
        Block layer1Block = block.getLevelBlock(BlockLayer.WATERLOGGED);
        int waterDamage;
        if (down.isSolid() && down.getId() != MAGMA && down.getId() != SOUL_SAND &&
                (layer1Block instanceof BlockWater && ((waterDamage = (block.getDamage())) == 0 || waterDamage == 8))
        ) {
            if (waterDamage == 8) {
                this.getLevel().setBlock((int) this.x, (int) this.y, (int) this.z, Block.LAYER_WATERLOGGED, Block.get(Block.STILL_WATER), true, true);
            }
            this.getLevel().setBlock(this, BlockLayer.NORMAL, this, true, true);
            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (level != null && level.getProvider() instanceof Anvil) {
            if (type == Level.BLOCK_UPDATE_NORMAL) {
                Block down = this.down();
                if (down.isTransparent() && down.getId() != SEAGRASS) {
                    this.getLevel().useBreakOn(this);
                }
            }
            return type;
        }


        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block blockLayer1 = this.getLevelBlock(BlockLayer.WATERLOGGED);
            int damage;
            if (!(blockLayer1 instanceof BlockIceFrosted)
                    && (!(blockLayer1 instanceof BlockWater) || ((damage = blockLayer1.getDamage()) != 0 && damage != 8))) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }

            Block down = this.down();
            damage = this.getDamage();
            if (damage == 0 || damage == 2) {
                if (!down.isSolid() || down.getId() == MAGMA || down.getId() == SOUL_SAND) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }

                if (damage == 2) {
                    Block up = up();
                    if (up.getId() != getId() || up.getDamage() != 1) {
                        this.getLevel().useBreakOn(this);
                    }
                }
            } else if (down.getId() != getId() || down.getDamage() != 2) {
                this.getLevel().useBreakOn(this);
            }

            return Level.BLOCK_UPDATE_NORMAL;
        }

        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[] { toItem() };
        } else {
            return new Item[0];
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(SEAGRASS), 0, 1);
    }

    @Override
    public void setDamage(int meta) {
        super.setDamage(meta % 3);
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    @Override
    public boolean canBeFlowedInto() {
        return level == null || !(level.getProvider() instanceof Anvil);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WATER_BLOCK_COLOR;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }


    @Override
    public boolean canBeActivated() {
        return this.getDamage() == 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (this.getDamage() == 0 && item.getId() == ItemID.DYE && item.getDamage() == ItemDye.BONE_MEAL && up() instanceof BlockWater) {
            Vector3 up = this.getSideVec(BlockFace.UP);
            if (this.level.setBlock(up, Block.get(SEAGRASS, 1), true, true)) {
                this.level.setBlock(this, Block.get(SEAGRASS, 2), true, true);
            }

            if (player != null && !player.isCreative()) {
                item.count--;
            }

            this.level.addParticle(new BoneMealParticle(this));

            return true;
        }
        return false;
    }
}
