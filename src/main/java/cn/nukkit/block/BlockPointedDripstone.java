package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.block.properties.BlockPropertiesHelper;
import cn.nukkit.block.properties.DripstoneThickness;
import cn.nukkit.block.custom.properties.BlockProperties;
import cn.nukkit.block.custom.properties.BooleanBlockProperty;
import cn.nukkit.block.custom.properties.EnumBlockProperty;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFallingBlock;
import cn.nukkit.event.block.BlockFallEvent;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.particle.DestroyBlockParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import it.unimi.dsi.fastutil.ints.IntObjectPair;

import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class BlockPointedDripstone extends BlockSolidMeta implements BlockPropertiesHelper, Faceable {

    private static final float GROWTH_PROBABILITY = 0.011377778F;
    private static final int MAX_HEIGHT = 7;

    private static final EnumBlockProperty<DripstoneThickness> THICKNESS = new EnumBlockProperty<>("dripstone_thickness", false, DripstoneThickness.class);
    private static final BooleanBlockProperty HANGING = new BooleanBlockProperty("hanging", false);
    private static final BlockProperties PROPERTIES = new BlockProperties(HANGING, THICKNESS);

    public BlockPointedDripstone() {
        this(0);
    }

    public BlockPointedDripstone(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Pointed Dripstone";
    }

    @Override
    public int getId() {
        return POINTED_DRIPSTONE;
    }

    @Override
    public BlockProperties getBlockProperties() {
        return PROPERTIES;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (!this.canPlaceOn(block.down(), target)) {
            return false;
        }

        Block up = this.up();
        Block down = this.down();

        boolean hanging = false;
        if (face == BlockFace.UP || face == BlockFace.DOWN) {
            if ((face == BlockFace.UP && !down.isSolid()) || (face == BlockFace.DOWN && !up.isSolid())) {
                return false;
            }
            hanging = face == BlockFace.DOWN;
        } else if (up.isSolid()) {
            hanging = true;
        } else if (!down.isSolid()) {
            return false;
        }


        Block tip = null;
        if (up instanceof BlockPointedDripstone && hanging) {
            tip = up;
        } else if (down instanceof BlockPointedDripstone) {
            tip = down;
        }

        if (tip != null) {
            IntObjectPair<Block> pair = this.getDripstoneHeightFromTip(tip, hanging);
            int height = pair.keyInt();
            if (height == 0 || height == MAX_HEIGHT) {
                return false;
            }
            Location location = pair.right().getLocation();
            this.growPointedDripstone(location, hanging, height);
        } else {
            this.setHanging(hanging);
            this.setThickness(DripstoneThickness.TIP);
            this.getLevel().setBlock(this, this, true, true);
        }
        return true;
    }

    @Override
    public boolean onBreak(Item item, Player player) {
        boolean hanging = this.isHanging();

        Block newTip = hanging ? this.up() : this.down();
        if (newTip instanceof BlockPointedDripstone) {
            ((BlockPointedDripstone) newTip).setThickness(DripstoneThickness.TIP);
            this.getLevel().setBlock(newTip, newTip);
        }

        DripstoneThickness thickness = this.getThickness();
        if (thickness == DripstoneThickness.TIP || thickness == DripstoneThickness.MERGE) {
            return super.onBreak(item, player);
        }

        Block block = this;
        while (block instanceof BlockPointedDripstone) {
            BlockPointedDripstone dripstone = (BlockPointedDripstone) block;
            if (this != dripstone) {
                this.getLevel().addParticle(new DestroyBlockParticle(block.add(0.5), block));
                if (hanging) {
                    this.spawnFallingBlock(dripstone);
                } else {
                    this.getLevel().dropItem(block.add(0.5, 0.5, 0.5), block.toItem());
                }
            }
            this.getLevel().setBlock(block, Block.get(BlockID.AIR), false, true);
            block = hanging ? block.down() : block.up();
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type != Level.BLOCK_UPDATE_RANDOM) {
            return 0;
        }

        if (ThreadLocalRandom.current().nextFloat() >= GROWTH_PROBABILITY || !this.isHanging() || this.up().getId() == POINTED_DRIPSTONE) {
            return 0;
        }

        int height;
        if (this.canGrow() && (height = this.getDripstoneHeightFromBase(this, true)) < MAX_HEIGHT) {
            BlockGrowEvent event = new BlockGrowEvent(this, Block.get(BlockID.POINTED_DRIPSTONE));
            this.getLevel().getServer().getPluginManager().callEvent(event);
            if (event.isCancelled()) {
                return 0;
            }
            this.growPointedDripstone(this.getLocation(), true, height);
        }

        // TODO: grow from ground too
        return 0;
    }

    private void growPointedDripstone(Position position, boolean hanging, int height) {
        this.buildBaseToTipColumn(height + 1, false, thickness -> {
            BlockPointedDripstone dripstone = (BlockPointedDripstone) Block.get(POINTED_DRIPSTONE);
            dripstone.setHanging(hanging);
            dripstone.setThickness(thickness);
            this.getLevel().setBlock(position, dripstone);
            position.setY(hanging ? position.getY() - 1 : position.getY() + 1);
        });
    }

    private IntObjectPair<Block> getDripstoneHeightFromTip(Block block, boolean hanging) {
        int height = 0;
        BlockPointedDripstone dripstone = null;
        while (block instanceof BlockPointedDripstone) {
            height++;
            dripstone = (BlockPointedDripstone) block;
            block = hanging ? block.up() : block.down();
        }
        return IntObjectPair.of(height, dripstone);
    }

    private int getDripstoneHeightFromBase(Block block, boolean hanging) {
        int height = 0;
        while (block instanceof BlockPointedDripstone) {
            height++;
            block = hanging ? block.down() : block.up();
        }
        return height;
    }

    private void buildBaseToTipColumn(int height, boolean merge, Consumer<DripstoneThickness> callback) {
        if (height >= 3) {
            callback.accept(DripstoneThickness.BASE);
            for(int i = 0; i < height - 3; ++i) {
                callback.accept(DripstoneThickness.MIDDLE);
            }
        }

        if (height >= 2) {
            callback.accept(DripstoneThickness.FRUSTUM);
        }

        if (height >= 1) {
            callback.accept(merge ? DripstoneThickness.MERGE : DripstoneThickness.TIP);
        }
    }

    private boolean canGrow() {
        // TODO: grow from ground too
        return this.down().getId() == AIR && this.up().getId() == DRIPSTONE_BLOCK && Block.isWater(this.up(2).getId());
    }

    private void spawnFallingBlock(BlockPointedDripstone block) {
        BlockFallEvent event = new BlockFallEvent(block);
        this.level.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }

        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", this.x + 0.5))
                        .add(new DoubleTag("", this.y))
                        .add(new DoubleTag("", this.z + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0))
                        .add(new DoubleTag("", 0)))

                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)))
                .putInt("TileID", this.getId())
                .putByte("Data", this.getDamage());

        Entity.createEntity(EntityFallingBlock.NETWORK_ID, this.getLevel().getChunk((int) this.x >> 4, (int) this.z >> 4), nbt).spawnToAll();
    }

    @Override
    public double getHardness() {
        return 1.5;
    }

    @Override
    public double getResistance() {
        return 3;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId()), 0, 1);
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.BROWN_TERRACOTA_BLOCK_COLOR;
    }

    @Override
    public BlockFace getBlockFace() {
        return this.getBooleanValue(HANGING) ? BlockFace.DOWN : BlockFace.UP;
    }

    public boolean isHanging() {
        return this.getBooleanValue(HANGING);
    }

    public void setHanging(boolean hanging) {
        this.setBooleanValue(HANGING, hanging);
    }

    public DripstoneThickness getThickness() {
        return this.getPropertyValue(THICKNESS);
    }

    public void setThickness(DripstoneThickness value) {
        this.setPropertyValue(THICKNESS, value);
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
