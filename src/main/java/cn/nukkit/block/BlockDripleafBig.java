package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.BlockPropertiesHelper;
import cn.nukkit.block.properties.DripleafTilt;
import cn.nukkit.block.properties.VanillaProperties;
import cn.nukkit.block.custom.properties.BlockProperties;
import cn.nukkit.block.custom.properties.BlockProperty;
import cn.nukkit.block.custom.properties.BooleanBlockProperty;
import cn.nukkit.block.custom.properties.EnumBlockProperty;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.Event;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemDye;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.Faceable;

public class BlockDripleafBig extends BlockSolidMeta implements BlockPropertiesHelper, Faceable {

    public static final BlockProperty<DripleafTilt> TILT_PROPERTY = new EnumBlockProperty<>("big_dripleaf_tilt", false, DripleafTilt.class);
    public static final BlockProperty<Boolean> HEAD_PROPERTY = new BooleanBlockProperty("big_dripleaf_head", false);

    private static final BlockProperties PROPERTIES = new BlockProperties(TILT_PROPERTY, HEAD_PROPERTY, VanillaProperties.DIRECTION);

    public BlockDripleafBig() {
        this(0);
    }

    public BlockDripleafBig(int meta) {
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
            case BIG_DRIPLEAF:
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

        if (down.getId() == BIG_DRIPLEAF) {
            BlockDripleafBig floor = (BlockDripleafBig) down;
            floor.setHasHead(false);
            this.getLevel().setBlock(floor, floor, true, true);
            this.setDirection(floor.getDirection());
        } else {
            this.setDirection(player.getDirection().getOpposite());
        }

        this.setTilt(DripleafTilt.NONE);
        this.setHasHead(true);
        return this.getLevel().setBlock(this, this, true, true);
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        Block down = this.down();
        while (down instanceof BlockDripleafBig) {
            this.getLevel().setBlock(down, Block.get(BlockID.AIR), true, true);
            this.getLevel().addParticle(new DestroyBlockParticle(down.add(0.5), down));
            down = down.down();
        }

        Block up = this.up();
        while (up instanceof BlockDripleafBig) {
            this.getLevel().setBlock(up, Block.get(BlockID.AIR), true, true);
            this.getLevel().addParticle(new DestroyBlockParticle(up.add(0.5), up));
            up = up.up();
        }

        return super.onBreak(item, player);
    }

    @Override
    public void onEntityCollide(Entity entity) {
        if (!(entity instanceof Player) || !this.hasHead() || this.getTilt() != DripleafTilt.NONE) {
            return;
        }

        Event event = new PlayerInteractEvent((Player) entity, null, this, null, PlayerInteractEvent.Action.PHYSICAL);
        this.getLevel().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        this.setTiltAndScheduleTick(DripleafTilt.UNSTABLE, false);
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_SCHEDULED) {
            return super.onUpdate(type);
        }

        DripleafTilt tilt = this.getTilt();
        if (tilt == DripleafTilt.UNSTABLE) {
            this.setTiltAndScheduleTick(DripleafTilt.PARTIAL_TILT, true);
        } else if (tilt == DripleafTilt.PARTIAL_TILT) {
            this.setTiltAndScheduleTick(DripleafTilt.FULL_TILT, true);
        } else if (tilt == DripleafTilt.FULL_TILT) {
            this.resetTilt();
        }
        return 0;
    }

    private void setTiltAndScheduleTick(DripleafTilt tilt, boolean sound) {
        this.setTilt(tilt);
        this.getLevel().setBlock(this, this, true, true);

        if (sound) {
            this.getLevel().addSound(this, Sound.TILT_DOWN_BIG_DRIPLEAF);
        }

        int delay = tilt.getNetxStateDelay();
        if (delay != -1) {
            this.getLevel().scheduleUpdate(this, delay);
        }
    }

    private void resetTilt() {
        this.setTilt(DripleafTilt.NONE);
        this.getLevel().setBlock(this, this, true, true);
        this.getLevel().addSound(this, Sound.TILT_UP_BIG_DRIPLEAF);
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

        Block up = this;
        BlockDripleafBig highestPart = null;
        while (up instanceof BlockDripleafBig) {
            highestPart = (BlockDripleafBig) up;
            up = up.up();
        }

        if (highestPart == null) {
            return false;
        }

        highestPart.setHasHead(false);
        this.getLevel().setBlock(highestPart, highestPart, false, true);

        BlockDripleafBig block = (BlockDripleafBig) this.clone();
        block.setHasHead(true);

        this.getLevel().setBlock(highestPart.up(), block, false, true);

        this.level.addParticle(new BoneMealParticle(this));

        if (player != null && !player.isCreative()) {
            item.count--;
        }
        return true;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return super.recalculateBoundingBox(); // TODO:
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }

    @Override
    public String getName() {
        return "Big Dripleaf";
    }

    @Override
    public int getId() {
        return BIG_DRIPLEAF;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.FLOW_INTO_BLOCK;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getResistance() {
        return 0.1;
    }

    @Override
    public double getHardness() {
        return 0.1;
    }

    public void setTilt(DripleafTilt tilt) {
        this.setPropertyValue(TILT_PROPERTY, tilt);
    }

    public DripleafTilt getTilt() {
        return this.getPropertyValue(TILT_PROPERTY);
    }

    public void setHasHead(boolean value) {
        this.setBooleanValue(HEAD_PROPERTY, value);
    }

    public boolean hasHead() {
        return this.getBooleanValue(HEAD_PROPERTY);
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
    public boolean breakWhenPushed() {
        return true;
    }

    @Override
    public boolean isTransparent() {
        return !this.hasHead() || !this.getTilt().isStable();
    }

    @Override
    public boolean isSolid() {
        return this.hasHead() && this.getTilt().isStable();
    }

    @Override
    public boolean canPassThrough() {
        return !this.hasHead() || !this.getTilt().isStable();
    }
}
