package cn.nukkit.blockentity;

import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.level.format.Chunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;

public class BlockEntityLectern extends BlockEntitySpawnable {

    private int totalPages;

    public BlockEntityLectern(Chunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    protected void initBlockEntity() {
        if (!(this.namedTag.get("book") instanceof CompoundTag)) {
            this.namedTag.remove("book");
        }

        if (!(this.namedTag.get("page") instanceof IntTag)) {
            this.namedTag.remove("page");
        }

        updateTotalPages(false);
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = new CompoundTag()
                .putString("id", BlockEntity.LECTERN)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z);

        Item book = getBook();
        if (book.getId() != BlockID.AIR) {
            c.putCompound("book", NBTIO.putNetworkItemHelper(book));
            c.putBoolean("hasBook", true);
            c.putInt("page", getRawPage());
            c.putInt("totalPages", totalPages);
        } else {
            c.putBoolean("hasBook", false);
        }

        return c;
    }

    @Override
    public boolean isBlockEntityValid() {
        return level.getBlockIdAt(chunk, (int) x, (int) y, (int) z) == BlockID.LECTERN;
    }

    @Override
    public void onBreak() {
        Item book = getBook();
        if (book.getId() != BlockID.AIR) {
            level.dropItem(this, book);
        }
        this.namedTag.remove("book");
    }

    public boolean hasBook() {
        return this.namedTag.contains("book") && this.namedTag.get("book") instanceof CompoundTag;
    }

    public Item getBook() {
        if (!hasBook()) {
            return Item.get(BlockID.AIR, 0, 0);
        } else {
            return NBTIO.getItemHelper(this.namedTag.getCompound("book"));
        }
    }

    public void setBook(Item item) {
        if (item.getId() == ItemID.WRITTEN_BOOK || item.getId() == ItemID.BOOK_AND_QUILL) {
            this.namedTag.putCompound("book", NBTIO.putItemHelper(item));
        } else {
            this.namedTag.remove("book");
            this.namedTag.remove("page");
        }

        updateTotalPages(true);
        setDirty();
    }

    public int getLeftPage() {
        return (getRawPage() * 2) + 1;
    }

    public int getRightPage() {
        return getLeftPage() + 1;
    }

    public void setLeftPage(int newLeftPage) {
        setRawPage((newLeftPage - 1) /2);
    }

    public void setRightPage(int newRightPage) {
        setLeftPage(newRightPage - 1);
    }

    public void setRawPage(int page) {
        this.namedTag.putInt("page", Math.min(page, totalPages));
        setDirty();
        this.getLevel().updateAround(this);
    }

    public int getRawPage() {
        return this.namedTag.getInt("page");
    }

    public int getTotalPages() {
        return totalPages;
    }

    private void updateTotalPages(boolean updateRedstone) {
        Item book = getBook();
        if (book.getId() == BlockID.AIR || !book.hasCompoundTag()) {
            totalPages = 0;
        } else {
            totalPages = book.getNamedTag().getList("pages", CompoundTag.class).size();
        }

        if (updateRedstone) {
            this.getLevel().updateAroundRedstone(this, null);
        }
    }
}
