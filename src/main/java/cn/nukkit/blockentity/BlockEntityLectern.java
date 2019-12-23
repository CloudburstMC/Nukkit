package cn.nukkit.blockentity;

import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockID;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.IntTag;

public class BlockEntityLectern extends BlockEntitySpawnable {

    private int totalPages;

    public BlockEntityLectern(FullChunk chunk, CompoundTag nbt) {
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

        updateTotalPages();
    }

    @Override
    public CompoundTag getSpawnCompound() {
        CompoundTag c = new CompoundTag()
                .putString("id", BlockEntity.LECTERN)
                .putInt("x", (int) this.x)
                .putInt("y", (int) this.y)
                .putInt("z", (int) this.z)
                .putBoolean("isMovable", this.movable);

        Item book = getBook();
        if (book.getId() != Item.AIR) {
            c.putCompound("book", NBTIO.putItemHelper(book));
            c.putBoolean("hasBook", true);
            c.putInt("page", getPage());
            c.putInt("totalPages", totalPages);
        } else {
            c.putBoolean("hasBook", false);
        }

        return c;
    }

    @Override
    public boolean isBlockEntityValid() {
        return getBlock().getId() == BlockID.LECTERN;
    }

    @Override
    public void onBreak() {
        level.dropItem(this, getBook());
    }

    public Item getBook() {
        if (!this.namedTag.contains("book") || !(this.namedTag.get("book") instanceof CompoundTag)) {
            return new ItemBlock(new BlockAir(), 0, 0);
        } else {
            return NBTIO.getItemHelper(this.namedTag.getCompound("book"));
        }
    }

    public void setBook(Item item) {
        if (item.getId() == Item.WRITTEN_BOOK || item.getId() == Item.BOOK_AND_QUILL) {
            this.namedTag.putCompound("book", NBTIO.putItemHelper(item));
        } else {
            this.namedTag.putCompound("book", NBTIO.putItemHelper(Item.get(Item.AIR)));
        }
        updateTotalPages();
    }

    public void setPage(int page) {
        this.namedTag.putInt("page", Math.min(page, totalPages));
    }

    public int getPage() {
        return this.namedTag.getInt("page");
    }

    public int getTotalPages() {
        return totalPages;
    }

    private void updateTotalPages() {
        Item book = getBook();
        if (book.getId() == Item.AIR || !book.hasCompoundTag()) {
            totalPages = 0;
        } else {
            totalPages = book.getNamedTag().getList("pages", CompoundTag.class).size();
        }
    }
}
