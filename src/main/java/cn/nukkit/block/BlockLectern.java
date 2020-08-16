package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityLectern;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.LecternDropBookEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nullable;

public class BlockLectern extends BlockTransparentMeta implements Faceable {
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
        BlockEntity be = this.getLevel().getBlockEntity(this);
        if (be instanceof BlockEntityLectern) {
            BlockEntityLectern lectern = (BlockEntityLectern) be;
            if (lectern.hasBook()) {
                maxPage = lectern.getTotalPages();
                page = lectern.getLeftPage() + 1;
                power = (int)(((float)page / maxPage) * 16);
            }
        }
        return power;
    }
    
    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getDamage() & 0b11);
    }
    
    public void setBlockFace(BlockFace face) {
        int horizontalIndex = face.getHorizontalIndex();
        if (horizontalIndex >= 0) {
            setDamage(getDamage() & (DATA_MASK ^ 0b11) | (horizontalIndex & 0b11));
        }
    }
    
    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);
        CompoundTag nbt = new CompoundTag()
                .putString("id", BlockEntity.LECTERN)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        BlockEntityLectern lectern = (BlockEntityLectern) BlockEntity.createBlockEntity(BlockEntity.LECTERN, this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
        if (lectern == null) {
            return false;
        }

        this.getLevel().setBlock(block, this, true, true);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this);
            BlockEntityLectern lectern;
            if (t instanceof BlockEntityLectern) {
                lectern = (BlockEntityLectern) t;
            } else {
                CompoundTag nbt = new CompoundTag()
                        .putString("id", BlockEntity.LECTERN)
                        .putInt("x", (int) this.x)
                        .putInt("y", (int) this.y)
                        .putInt("z", (int) this.z);

                lectern = (BlockEntityLectern) BlockEntity.createBlockEntity(BlockEntity.LECTERN, this.getLevel().getChunk((int) (this.x) >> 4, (int) (this.z) >> 4), nbt);
                if (lectern == null) {
                    return false;
                }
            }

            Item currentBook = lectern.getBook();
            if (currentBook.getId() == Item.AIR) {
                if (item.getId() == Item.WRITTEN_BOOK || item.getId() == Item.BOOK_AND_QUILL) {
                    Item newBook = item.clone();
                    if (player.isSurvival()) {
                        newBook.setCount(newBook.getCount() - 1);
                        player.getInventory().setItemInHand(newBook);
                    }
                    newBook.setCount(1);
                    lectern.setBook(newBook);
                    lectern.spawnToAll();
                    this.getLevel().addSound(this.add(0.5, 0.5, 0.5), Sound.ITEM_BOOK_PUT);
                }
            }
        }

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

        level.updateAroundRedstone(this, null);
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return isActivated()? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return 0;
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            if (isActivated()) {
                this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 15, 0));

                setActivated(false);
                level.setBlock(this, this, true, false);
                level.updateAroundRedstone(this, null);
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
        BlockEntity t = this.getLevel().getBlockEntity(this);
        if (t instanceof BlockEntityLectern) {
            BlockEntityLectern lectern = (BlockEntityLectern) t;
            Item book = lectern.getBook();
            if (book.getId() != Item.AIR) {
                LecternDropBookEvent dropBookEvent = new LecternDropBookEvent(player,lectern, book);
                this.getLevel().getServer().getPluginManager().callEvent(dropBookEvent);
                if (!dropBookEvent.isCancelled()) {
                    lectern.setBook(Item.get(Item.AIR));
                    lectern.spawnToAll();
                    this.level.dropItem(lectern.add(0.5f, 0.6f, 0.5f), dropBookEvent.getBook());
                }
            }
        }
    }

    @Nullable
    @Override
    public BlockEntityLectern getBlockEntity() {
        return getTypedBlockEntity(BlockEntityLectern.class);
    }
}
