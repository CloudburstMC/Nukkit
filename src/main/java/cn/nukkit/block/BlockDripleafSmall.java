package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.BlockPropertiesHelper;
import cn.nukkit.block.properties.VanillaProperties;
import cn.nukkit.block.custom.properties.BlockProperties;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

public class BlockDripleafSmall extends BlockFlowable implements BlockPropertiesHelper, Faceable {

    private static final BlockProperties PROPERTIES = new BlockProperties(VanillaProperties.UPPER_BLOCK, VanillaProperties.DIRECTION);

    public BlockDripleafSmall() {
        this(0);
    }

    public BlockDripleafSmall(int meta) {
        super(meta);
    }

    @Override
    public BlockProperties getBlockProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean canPlaceOn(Block floor, Position pos) {
        switch (floor.getId()) {
            case CLAY_BLOCK:
            case DIRT:
            case FARMLAND:
            case GRASS:
            case MOSS_BLOCK:
            case MYCELIUM:
            case SMALL_DRIPLEAF:
            case WATER:
            case STILL_WATER:
                return super.canPlaceOn(floor, pos);
        }

        if (floor.getLayer() == LAYER_WATERLOGGED && floor.getId() == AIR) {
            return super.canPlaceOn(floor, pos);
        }
        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = block.down();
        if (!this.canPlaceOn(down, target)) {
            return false;
        }

        if (down.getId() == SMALL_DRIPLEAF) {
            BlockDripleafSmall floor = (BlockDripleafSmall) down;
            floor.setHasHead(false);
            this.getLevel().setBlock(floor, floor, true, true);
            this.setDirection(floor.getDirection());
        } else {
            this.setDirection(player.getDirection().getOpposite());
        }

        this.setHasHead(true);
        return this.getLevel().setBlock(this, this, true, true);
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        Block down = this.down();
        while (down instanceof BlockDripleafSmall) {
            this.getLevel().setBlock(down, Block.get(BlockID.AIR), true, true);
            this.getLevel().addParticle(new DestroyBlockParticle(down.add(0.5), down));
            down = down.down();
        }

        Block up = this.up();
        while (up instanceof BlockDripleafSmall) {
            this.getLevel().setBlock(up, Block.get(BlockID.AIR), true, true);
            this.getLevel().addParticle(new DestroyBlockParticle(up.add(0.5), up));
            up = up.up();
        }

        return super.onBreak(item, player);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() != Item.DYE || item.getDamage() != ItemDye.BONE_MEAL) {
            return false;
        }

        BlockGrowEvent event = new BlockGrowEvent(this, Block.get(BlockID.BIG_DRIPLEAF, 0, this));
        this.getLevel().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return false;
        }

        Block down = this.down();
        while (down instanceof BlockDripleafSmall) {
            BlockDripleafBig block = (BlockDripleafBig) Block.get(BlockID.BIG_DRIPLEAF);
            block.setDirection(this.getDirection());
            this.getLevel().setBlock(down, block, false, true);
            down = down.down();
        }

        Block up = this;
        while (up instanceof BlockDripleafSmall) {
            BlockDripleafBig block = (BlockDripleafBig) Block.get(BlockID.BIG_DRIPLEAF);
            block.setDirection(this.getDirection());
            this.getLevel().setBlock(up, block, false, true);
            up = up.up();
        }

        Block highestPart = this.getLevel().getBlock(up.down());
        if (highestPart instanceof BlockDripleafBig) {
            ((BlockDripleafBig) highestPart).setHasHead(true);
            this.getLevel().setBlock(highestPart, highestPart, false, true);
        }

        this.level.addParticle(new BoneMealParticle(this));

        if (player != null && !player.isCreative()) {
            item.count--;
        }
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public String getName() {
        return "Small Dripleaf";
    }

    @Override
    public int getId() {
        return SMALL_DRIPLEAF;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    public void setHasHead(boolean value) {
        this.setBooleanValue(VanillaProperties.UPPER_BLOCK, value);
    }

    public boolean hasHead() {
        return this.getBooleanValue(VanillaProperties.UPPER_BLOCK);
    }

    public void setDirection(BlockFace blockFace) {
        this.setPropertyValue(VanillaProperties.DIRECTION, blockFace);
    }

    public BlockFace getDirection() {
        return this.getPropertyValue(VanillaProperties.DIRECTION);
    }

    @Override
    public BlockFace getBlockFace() {
        return this.getDirection();
    }

    @Override
    public boolean canBeFlowedInto() {
        return false;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }
}
