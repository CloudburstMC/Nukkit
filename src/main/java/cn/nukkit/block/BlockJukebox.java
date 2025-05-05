package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityJukebox;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemRecord;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.TextPacket;
import cn.nukkit.utils.BlockColor;

/**
 * Created by CreeperFace on 7.8.2017.
 */
public class BlockJukebox extends BlockSolid {

    @Override
    public String getName() {
        return "Jukebox";
    }

    @Override
    public int getId() {
        return JUKEBOX;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 6;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this);
        if (!(blockEntity instanceof BlockEntityJukebox)) {
            return false;
        }

        BlockEntityJukebox jukebox = (BlockEntityJukebox) blockEntity;
        if (jukebox.getRecordItem().getId() != 0) {
            jukebox.dropItem();
        } else if (item instanceof ItemRecord) {
            jukebox.setRecordItem(item);
            jukebox.play();

            if (player != null) {
                TextPacket pk = new TextPacket();
                pk.type = TextPacket.TYPE_JUKEBOX_POPUP;
                pk.message = "%record.nowPlaying";
                pk.parameters = new String[]{((ItemRecord) item).getDiscName()};
                pk.isLocalized = true;
                player.dataPacket(pk);

                item.count--;
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (super.place(item, block, target, face, fx, fy, fz, player)) {
            CompoundTag nbt = new CompoundTag()
                    .putList(new ListTag<>("Items"))
                    .putString("id", BlockEntity.JUKEBOX)
                    .putInt("x", getFloorX())
                    .putInt("y", getFloorY())
                    .putInt("z", getFloorZ());

            BlockEntity.createBlockEntity(BlockEntity.JUKEBOX, this.getChunk(), nbt);
            return true;
        }

        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.DIRT_BLOCK_COLOR;
    }

    @Override
    public boolean canBePushed() {
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        BlockEntity blockEntity = this.getLevel().getBlockEntityIfLoaded(this);
        return blockEntity instanceof BlockEntityJukebox ? ((BlockEntityJukebox) blockEntity).getComparatorSignal() : 0;
    }
}
