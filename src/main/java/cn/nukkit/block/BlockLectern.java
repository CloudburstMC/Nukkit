package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.LecternDropBookEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.RedstoneComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitOnly
@PowerNukkitDifference(info = "Implements RedstoneComponent and uses methods from it.", since = "1.4.0.0-PN")
public class BlockLectern extends BlockTransparentMeta implements RedstoneComponent, Faceable, BlockEntityHolder<BlockEntityLectern> {
    public BlockLectern() {
        this(0);
    }

    public BlockLectern(int meta) {
        super(meta);
    }

    @Override
    public String getName() {
        return "Lectern";
    }

    @Override
    public int getId() {
        return LECTERN;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public Class<? extends BlockEntityLectern> getBlockEntityClass() {
        return BlockEntityLectern.class;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    @Override
    public String getBlockEntityType() {
        return BlockEntity.LECTERN;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 12.5;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public double getMaxY() {
        return y + 0.89999;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride() {
        int power = 0;
        int page = 0;
        int maxPage = 0;
        BlockEntityLectern lectern = getBlockEntity();
        if (lectern != null && lectern.hasBook()) {
            maxPage = lectern.getTotalPages();
            page = lectern.getLeftPage() + 1;
            power = (int) (((float) page / maxPage) * 16);
        }
        return power;
    }
    
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & 0b11);
    }

    @Override
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public void setBlockFace(BlockFace face) {
        int horizontalIndex = face.getHorizontalIndex();
        if (horizontalIndex >= 0) {
            setDamage(getDamage() & (DATA_MASK ^ 0b11) | (horizontalIndex & 0b11));
        }
    }
    
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, @Nullable Player player) {
        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);
        return BlockEntityHolder.setBlockAndCreateEntity(this) != null;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        BlockEntityLectern lectern = getOrCreateBlockEntity();
        Item currentBook = lectern.getBook();
        if (!currentBook.isNull()) {
            return false;
        }

        if (item.getId() != ItemID.WRITTEN_BOOK && item.getId() != ItemID.BOOK_AND_QUILL) {
            return false;
        }
        
        if (player == null || !player.isCreative()) {
            item.count--;
        }

        Item newBook = item.clone();
        newBook.setCount(1);
        lectern.setBook(newBook);
        lectern.spawnToAll();
        this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.ITEM_BOOK_PUT);
        return true;
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    public boolean isActivated() {
        return (this.getDamage() & 0x04) == 0x04;
    }

    public void setActivated(boolean activated) {
        if (activated) {
            setDamage(getDamage() | 0x04);
        } else {
            setDamage(getDamage() ^ 0x04);
        }
    }

    @PowerNukkitDifference(info = "Down side is strongly powered.", since = "1.4.0.0-PN")
    public void executeRedstonePulse() {
        if (isActivated()) {
            level.cancelSheduledUpdate(this, this);
        } else {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
        }

        level.scheduleUpdate(this, this, 4);
        setActivated(true);
        level.setBlock(this, this, true, false);
        level.addSound(this.add(0.5, 0.5, 0.5), Sound.ITEM_BOOK_PAGE_TURN);

        updateAroundRedstone();
        RedstoneComponent.updateAroundRedstone(getSide(BlockFace.DOWN), BlockFace.UP);
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return isActivated()? 15 : 0;
    }

    @Override
    @PowerNukkitDifference(info = "Down side is strongly powered.", since = "1.4.0.0-PN")
    public int getStrongPower(BlockFace face) {
        return face == BlockFace.DOWN ? this.getWeakPower(face) : 0;
    }

    @Override
    @PowerNukkitDifference(info = "Down side is strongly powered.", since = "1.4.0.0-PN")
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (isActivated()) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));

                setActivated(false);
                level.setBlock(this, this, true, false);
                updateAroundRedstone();
                RedstoneComponent.updateAroundRedstone(getSide(BlockFace.DOWN), BlockFace.UP);
            }

            return Level.BLOCK_UPDATE_SCHEDULED;
        }

        return 0;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.WOOD_BLOCK_COLOR;
    }

    public void dropBook(Player player) {
        BlockEntityLectern lectern = getBlockEntity();
        if (lectern == null) {
            return;
        }

        Item book = lectern.getBook();
        if (book.isNull()) {
            return;
        }
        
        LecternDropBookEvent dropBookEvent = new LecternDropBookEvent(player, lectern, book);
        this.getLevel().getServer().getPluginManager().callEvent(dropBookEvent);
        if (dropBookEvent.isCancelled()) {
            return;
        }
        
        lectern.setBook(Item.getBlock(BlockID.AIR));
        lectern.spawnToAll();
        this.level.dropItem(lectern.add(0.5f, 0.6f, 0.5f), dropBookEvent.getBook());
    }
}
