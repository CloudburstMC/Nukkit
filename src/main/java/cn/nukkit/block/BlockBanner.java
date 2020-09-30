package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityBanner;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.nbt.tag.Tag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by PetteriM1
 */
public class BlockBanner extends BlockTransparentMeta implements Faceable {

    public BlockBanner() {
        this(0);
    }

    public BlockBanner(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return STANDING_BANNER;
    }

    @Override
    public double getHardness() {
        return 1;
    }

    @Override
    public double getResistance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public String getName() {
        return "Banner";
    }

    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        return null;
    }

    @Override
    public boolean canPassThrough() {
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (face != BlockFace.DOWN) {
            if (face == BlockFace.UP) {
                this.setDamage(NukkitMath.floorDouble(((player.yaw + 180) * 16 / 360) + 0.5) & 0x0f);
                this.getLevel().setBlock(block, this, true);
            } else {
                this.setDamage(face.getIndex());
                this.getLevel().setBlock(block, Block.get(BlockID.WALL_BANNER, this.getDamage()), true);
            }

            CompoundTag nbt = BlockEntity.getDefaultCompound(this, BlockEntity.BANNER)
                    .putInt("Base", item.getDamage() & 0xf);

            Tag type = item.getNamedTagEntry("Type");
            if (type instanceof IntTag) {
                nbt.put("Type", type);
            }
            Tag patterns = item.getNamedTagEntry("Patterns");
            if (patterns instanceof ListTag) {
                nbt.put("Patterns", patterns);
            }

            BlockEntityBanner banner = (BlockEntityBanner) BlockEntity.createBlockEntity(BlockEntity.BANNER, this.getChunk(), nbt);
            return banner != null;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() == Block.AIR) {
                this.getLevel().useBreakOn(this);

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem() {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        Item item = Item.get(Item.BANNER);
        if (blockEntity instanceof BlockEntityBanner) {
            BlockEntityBanner banner = (BlockEntityBanner) blockEntity;
            item.setDamage(banner.getBaseColor() & 0xf);
            int type = banner.namedTag.getInt("Type");
            if (type > 0) {
                item.setNamedTag((item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag())
                        .putInt("Type", type));
            }
            ListTag<CompoundTag> patterns = banner.namedTag.getList("Patterns", CompoundTag.class);
            if (patterns.size() > 0) {
                item.setNamedTag((item.hasCompoundTag() ? item.getNamedTag() : new CompoundTag())
                        .putList(patterns));
            }
        }
        return item;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        if (this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this);

            if (blockEntity instanceof BlockEntityBanner) {
                return ((BlockEntityBanner) blockEntity).getDyeColor();
            }
        }

        return DyeColor.WHITE;
    }
}
