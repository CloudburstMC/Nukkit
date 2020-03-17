package cn.nukkit.block;

import cn.nukkit.blockentity.Banner;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.block.BlockIds.WALL_BANNER;
import static cn.nukkit.blockentity.BlockEntityTypes.BANNER;

/**
 * Created by PetteriM1
 */
public class BlockBanner extends BlockTransparent implements Faceable {

    public BlockBanner(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 1;
    }

    @Override
    public float getResistance() {
        return 5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
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
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        if (face != BlockFace.DOWN) {
            if (face == BlockFace.UP) {
                this.setMeta(NukkitMath.floorDouble(((player.getYaw() + 180) * 16 / 360) + 0.5) & 0x0f);
                this.getLevel().setBlock(block.getPosition(), this, true);
            } else {
                this.setMeta(face.getIndex());
                this.getLevel().setBlock(block.getPosition(), Block.get(WALL_BANNER, this.getMeta()), true);
            }

            CompoundTagBuilder tag = CompoundTag.builder();
            item.saveAdditionalData(tag);
            tag.intTag("Base", item.getMeta());

            BlockEntityRegistry.get().newEntity(BANNER, this.getChunk(), this.getPosition()).loadAdditionalData(tag.buildRootTag());

            return true;
        }
        return false;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (this.down().getId() == AIR) {
                this.getLevel().useBreakOn(this.getPosition());

                return Level.BLOCK_UPDATE_NORMAL;
            }
        }

        return 0;
    }

    @Override
    public Item toItem() {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this.getPosition());
        Item item = Item.get(ItemIds.BANNER);
        if (blockEntity instanceof Banner) {
            Banner banner = (Banner) blockEntity;
            item.setMeta(banner.getBase().getDyeData());

            CompoundTagBuilder tag = CompoundTag.builder();
            banner.saveAdditionalData(tag);
            tag.remove("Base");

            item.loadAdditionalData(tag.buildRootTag());
        }
        return item;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getMeta() & 0x7);
    }

    @Override
    public BlockColor getColor() {
        return this.getDyeColor().getColor();
    }

    public DyeColor getDyeColor() {
        if (this.level != null) {
            BlockEntity blockEntity = this.level.getBlockEntity(this.getPosition());

            if (blockEntity instanceof Banner) {
                return ((Banner) blockEntity).getBase();
            }
        }

        return DyeColor.WHITE;
    }

    @Override
    public boolean canWaterlogSource() {
        return true;
    }
}
