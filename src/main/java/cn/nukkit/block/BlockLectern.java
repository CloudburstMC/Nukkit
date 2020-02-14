package cn.nukkit.block;

import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityTypes;
import cn.nukkit.blockentity.Lectern;
import cn.nukkit.event.block.BlockRedstoneEvent;
import cn.nukkit.event.block.LecternDropBookEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockEntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

public class BlockLectern extends BlockTransparent implements Faceable {

    public BlockLectern(Identifier id) {
        super(id);
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public float getHardness() {
        return 2;
    }

    @Override
    public float getResistance() {
        return 12.5f;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public float getMaxY() {
        return this.getY() + 0.89999f;
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
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this.getPosition());
        if (blockEntity instanceof Lectern) {
            Lectern lectern = (Lectern) blockEntity;
            if (lectern.hasBook()) {
                maxPage = lectern.getTotalPages();
                page = lectern.getPage() + 1;
                power = (int) (((float) page / maxPage) * 16);
            }
        }
        return power;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(getMeta() & 0b11);
    }

    public void setBlockFace(BlockFace face) {
        final int dataMask = (1 << 6) - 1;

        int horizontalIndex = face.getHorizontalIndex();
        if (horizontalIndex >= 0) {
            setMeta(getMeta() & (dataMask ^ 0b11) | (horizontalIndex & 0b11));
        }
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        setBlockFace(player != null ? player.getDirection().getOpposite() : BlockFace.SOUTH);

        Lectern lectern = BlockEntityRegistry.get().newEntity(BlockEntityTypes.LECTERN, this.getChunk(), this.getPosition());

        this.getLevel().setBlock(block.getPosition(), this, true, true);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            BlockEntity t = this.getLevel().getBlockEntity(this.getPosition());
            Lectern lectern;
            if (t instanceof Lectern) {
                lectern = (Lectern) t;
            } else {
                lectern = BlockEntityRegistry.get().newEntity(BlockEntityTypes.LECTERN, this.getChunk(), this.getPosition());
            }

            Item currentBook = lectern.getBook();
            if (currentBook.getId() == BlockIds.AIR) {
                if (item.getId() == ItemIds.WRITTEN_BOOK || item.getId() == ItemIds.WRITABLE_BOOK) {
                    Item newBook = item.clone();
                    if (player.isSurvival()) {
                        newBook.setCount(newBook.getCount() - 1);
                        player.getInventory().setItemInHand(newBook);
                    }
                    newBook.setCount(1);
                    lectern.setBook(newBook);
                    lectern.spawnToAll();
                    this.getLevel().addSound(this.getPosition(), Sound.ITEM_BOOK_PUT);
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
        return (this.getMeta() & 0x04) == 0x04;
    }

    public void setActivated(boolean activated) {
        if (activated) {
            setMeta(getMeta() | 0x04);
        } else {
            setMeta(getMeta() ^ 0x04);
        }
    }

    public void executeRedstonePulse() {
        if (isActivated()) {
            level.cancelSheduledUpdate(this.getPosition(), this);
        } else {
            this.level.getServer().getPluginManager().callEvent(new BlockRedstoneEvent(this, 0, 15));
        }

        level.scheduleUpdate(this, this.getPosition(), 4);
        setActivated(true);
        level.setBlock(this.getPosition(), this, true, false);
        level.addSound(this.getPosition(), Sound.ITEM_BOOK_PAGE_TURN);

        level.updateAroundRedstone(this.getPosition(), null);
    }

    @Override
    public int getWeakPower(BlockFace face) {
        return isActivated() ? 15 : 0;
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
                level.setBlock(this.getPosition(), this, true, false);
                level.updateAroundRedstone(this.getPosition(), null);
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
        BlockEntity blockEntity = this.getLevel().getBlockEntity(this.getPosition());
        if (blockEntity instanceof Lectern) {
            Lectern lectern = (Lectern) blockEntity;
            Item book = lectern.getBook();
            if (book.getId() != BlockIds.AIR) {
                LecternDropBookEvent dropBookEvent = new LecternDropBookEvent(player, lectern, book);
                this.getLevel().getServer().getPluginManager().callEvent(dropBookEvent);
                if (!dropBookEvent.isCancelled()) {
                    lectern.setBook(Item.get(BlockIds.AIR));
                    lectern.spawnToAll();
                    this.level.dropItem(lectern.getPosition().add(0.5f, 1, 0.5f), dropBookEvent.getBook());
                }
            }
        }
    }
}