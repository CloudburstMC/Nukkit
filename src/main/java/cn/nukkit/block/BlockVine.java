package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.IntBlockProperty;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.block.BlockGrowEvent;
import cn.nukkit.event.block.BlockSpreadEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Pub4Game
 * @since 15.01.2016
 */
public class BlockVine extends BlockTransparentMeta {
    private static final IntBlockProperty VINE_DIRECTION_BITS = new IntBlockProperty("vine_direction_bits", false, 15);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(VINE_DIRECTION_BITS);

    public BlockVine(int meta) {
        super(meta);
    }

    public BlockVine() {
        this(0);
    }

    @Override
    public String getName() {
        return "Vines";
    }

    @Override
    public int getId() {
        return VINE;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0.2;
    }

    @Override
    public double getResistance() {
        return 1;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public boolean canBeClimbed() {
        return true;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeFlowedInto() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        entity.resetFallDistance();
        entity.onGround = true;
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Since("1.3.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean isSolid(BlockFace side) {
        return false;
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        double f1 = 1;
        double f2 = 1;
        double f3 = 1;
        double f4 = 0;
        double f5 = 0;
        double f6 = 0;
        boolean flag = this.getDamage() > 0;
        if ((this.getDamage() & 0x02) > 0) {
            f4 = Math.max(f4, 0.0625);
            f1 = 0;
            f2 = 0;
            f5 = 1;
            f3 = 0;
            f6 = 1;
            flag = true;
        }
        if ((this.getDamage() & 0x08) > 0) {
            f1 = Math.min(f1, 0.9375);
            f4 = 1;
            f2 = 0;
            f5 = 1;
            f3 = 0;
            f6 = 1;
            flag = true;
        }
        if ((this.getDamage() & 0x01) > 0) {
            f3 = Math.min(f3, 0.9375);
            f6 = 1;
            f1 = 0;
            f4 = 1;
            f2 = 0;
            f5 = 1;
            flag = true;
        }
        if (!flag && this.up().isSolid()) {
            f2 = Math.min(f2, 0.9375);
            f5 = 1;
            f1 = 0;
            f4 = 1;
            f3 = 0;
            f6 = 1;
        }
        return new SimpleAxisAlignedBB(
                this.x + f1,
                this.y + f2,
                this.z + f3,
                this.x + f4,
                this.y + f5,
                this.z + f6
        );
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        if (block.getId() != VINE && target.isSolid() && face.getHorizontalIndex() != -1) {
            this.setDamage(getMetaFromFace(face.getOpposite()));
            this.getLevel().setBlock(block, this, true, true);
            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[]{
                    toItem()
            };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block up = this.up();
            Set<BlockFace> upFaces = up instanceof BlockVine ? ((BlockVine) up).getFaces() : null;
            Set<BlockFace> faces = this.getFaces();
            for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
                if (!this.getSide(face).isSolid() && (upFaces == null || !upFaces.contains(face))) {
                    faces.remove(face);
                }
            }
            if (faces.isEmpty() && !up.isSolid()) {
                this.getLevel().useBreakOn(this, null, null, true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            int meta = getMetaFromFaces(faces);
            if (meta != this.getDamage()) {
                this.level.setBlock(this, Block.get(VINE, meta), true);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        } else if (type == Level.BLOCK_UPDATE_RANDOM) {
            Random random = ThreadLocalRandom.current();
            if (random.nextInt(4) == 0) {
                BlockFace face = BlockFace.random(random);
                Block block = this.getSide(face);
                int faceMeta = getMetaFromFace(face);
                int meta = this.getDamage();

                if (this.y < 255 && face == BlockFace.UP && block.getId() == AIR) {
                    if (this.canSpread()) {
                        for (BlockFace horizontalFace : BlockFace.Plane.HORIZONTAL) {
                            if (random.nextBoolean() || !this.getSide(horizontalFace).getSide(face).isSolid()) {
                                meta &= ~getMetaFromFace(horizontalFace);
                            }
                        }
                        putVineOnHorizontalFace(block, meta, this);
                    }
                } else if (face.getHorizontalIndex() != -1 && (meta & faceMeta) != faceMeta) {
                    if (this.canSpread()) {
                        if (block.getId() == AIR) {
                            BlockFace cwFace = face.rotateY();
                            BlockFace ccwFace = face.rotateYCCW();
                            Block cwBlock = block.getSide(cwFace);
                            Block ccwBlock = block.getSide(ccwFace);
                            int cwMeta = getMetaFromFace(cwFace);
                            int ccwMeta = getMetaFromFace(ccwFace);
                            boolean onCw = (meta & cwMeta) == cwMeta;
                            boolean onCcw = (meta & ccwMeta) == ccwMeta;

                            if (onCw && cwBlock.isSolid()) {
                                putVine(block, getMetaFromFace(cwFace), this);
                            } else if (onCcw && ccwBlock.isSolid()) {
                                putVine(block, getMetaFromFace(ccwFace), this);
                            } else if (onCw && cwBlock.getId() == AIR && this.getSide(cwFace).isSolid()) {
                                putVine(cwBlock, getMetaFromFace(face.getOpposite()), this);
                            } else if (onCcw && ccwBlock.getId() == AIR && this.getSide(ccwFace).isSolid()) {
                                putVine(ccwBlock, getMetaFromFace(face.getOpposite()), this);
                            } else if (block.up().isSolid()) {
                                putVine(block, 0, this);
                            }
                        } else if (!block.isTransparent()) {
                            meta |= getMetaFromFace(face);
                            putVine(this, meta, null);
                        }
                    }
                } else if (this.y > 0) {
                    Block below = this.down();
                    int id = below.getId();
                    if (id == AIR || id == VINE) {
                        for (BlockFace horizontalFace : BlockFace.Plane.HORIZONTAL) {
                            if (random.nextBoolean()) {
                                meta &= ~getMetaFromFace(horizontalFace);
                            }
                        }
                        putVineOnHorizontalFace(below, below.getDamage() | meta, id == AIR ? this : null);
                    }
                }
                return Level.BLOCK_UPDATE_RANDOM;
            }
        }
        return 0;
    }

    private boolean canSpread() {
        int blockX = this.getFloorX();
        int blockY = this.getFloorY();
        int blockZ = this.getFloorZ();

        int count = 0;
        for (int x = blockX - 4; x <= blockX + 4; x++) {
            for (int z = blockZ - 4; z <= blockZ + 4; z++) {
                for (int y = blockY - 1; y <= blockY + 1; y++) {
                    if (this.level.getBlock(x, y, z).getId() == VINE) {
                        if (++count >= 5) return false;
                    }
                }
            }
        }
        return true;
    }

    private void putVine(Block block, int meta, Block source) {
        if (block.getId() == VINE && block.getDamage() == meta) return;
        Block vine = get(VINE, meta);
        BlockGrowEvent event;
        if (source != null) {
            event = new BlockSpreadEvent(block, source, vine);
        } else {
            event = new BlockGrowEvent(block, vine);
        }
        this.level.getServer().getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            this.level.setBlock(block, vine, true);
        }
    }

    private void putVineOnHorizontalFace(Block block, int meta, Block source) {
        if (block.getId() == VINE && block.getDamage() == meta) return;
        boolean isOnHorizontalFace = false;
        for (BlockFace face : BlockFace.Plane.HORIZONTAL) {
            int faceMeta = getMetaFromFace(face);
            if ((meta & faceMeta) == faceMeta) {
                isOnHorizontalFace = true;
                break;
            }
        }
        if (isOnHorizontalFace) {
            putVine(block, meta, source);
        }
    }

    private Set<BlockFace> getFaces() {
        Set<BlockFace> faces = EnumSet.noneOf(BlockFace.class);

        int meta = this.getDamage();
        if ((meta & 1) > 0) {
            faces.add(BlockFace.SOUTH);
        }
        if ((meta & 2) > 0) {
            faces.add(BlockFace.WEST);
        }
        if ((meta & 4) > 0) {
            faces.add(BlockFace.NORTH);
        }
        if ((meta & 8) > 0) {
            faces.add(BlockFace.EAST);
        }

        return faces;
    }

    private static int getMetaFromFaces(Set<BlockFace> faces) {
        int meta = 0;

        for (BlockFace face : faces) {
            meta |= getMetaFromFace(face);

        }

        return meta;
    }

    private static int getMetaFromFace(BlockFace face) {
        switch (face) {
            case SOUTH:
            default:
                return 0x01;
            case WEST:
                return 0x02;
            case NORTH:
                return 0x04;
            case EAST:
                return 0x08;
        }
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }

    @Override
    public boolean breaksWhenMoved() {
        return true;
    }

    @Override
    public boolean sticksToPiston() {
        return false;
    }

    @Override
    public boolean canSilkTouch() {
        return true;
    }
}
