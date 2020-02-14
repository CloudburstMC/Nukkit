package cn.nukkit.blockentity.impl;

import cn.nukkit.block.BlockIds;
import cn.nukkit.blockentity.BlockEntityType;
import cn.nukkit.blockentity.Lectern;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemIds;
import cn.nukkit.item.ItemUtils;
import cn.nukkit.level.chunk.Chunk;
import com.nukkitx.math.GenericMath;
import com.nukkitx.math.vector.Vector3i;
import com.nukkitx.nbt.CompoundTagBuilder;
import com.nukkitx.nbt.tag.CompoundTag;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

public class LecternBlockEntity extends BaseBlockEntity implements Lectern {

    private static final String TAG_HAS_BOOK = "hasBook";
    private static final String TAG_BOOK = "book";
    private static final String TAG_PAGE = "page";
    private static final String TAG_TOTAL_PAGES = "totalPages";

    private Item book;
    private int page;
    private int totalPages;

    public LecternBlockEntity(BlockEntityType<?> type, Chunk chunk, Vector3i position) {
        super(type, chunk, position);
    }

    @Override
    public boolean isValid() {
        return getBlock().getId() == BlockIds.LECTERN;
    }

    @Override
    protected void initBlockEntity() {
        updateTotalPages(false);
    }

    @Override
    public void loadAdditionalData(CompoundTag tag) {
        super.loadAdditionalData(tag);

        if (tag.getBoolean(TAG_HAS_BOOK)) {
            this.book = ItemUtils.deserializeItem(tag.getCompound(TAG_BOOK));
            this.page = tag.getInt(TAG_PAGE);
            this.totalPages = tag.getInt(TAG_TOTAL_PAGES);
        }
    }

    @Override
    public void saveAdditionalData(CompoundTagBuilder tag) {
        super.saveAdditionalData(tag);

        if (this.book != null) {
            tag.booleanTag(TAG_HAS_BOOK, true);
            tag.tag(ItemUtils.serializeItem(this.book).toBuilder().build(TAG_BOOK));
            tag.intTag(TAG_PAGE, this.page);
            tag.intTag(TAG_TOTAL_PAGES, this.totalPages);
        }
    }

    @Override
    public void onBreak() {
        if (this.book != null) {
            this.getLevel().dropItem(this.getPosition(), book);
        }
    }

    public boolean hasBook() {
        return this.book != null;
    }

    @Nullable
    public Item getBook() {
        return book;
    }

    public void setBook(Item item) {
        if (item != null && (item.getId() == ItemIds.WRITTEN_BOOK || item.getId() == ItemIds.WRITABLE_BOOK)) {
            this.book = item;
        } else {
            this.book = null;
        }

        updateTotalPages(true);
    }

    public int getLeftPage() {
        return (getPage() * 2) + 1;
    }

    public void setLeftPage(int newLeftPage) {
        setPage((newLeftPage - 1) / 2);
    }

    public int getRightPage() {
        return getLeftPage() + 1;
    }

    public void setRightPage(int newRightPage) {
        setLeftPage(newRightPage - 1);
    }

    @Override
    public int getPage() {
        return this.page;
    }

    @Override
    public void setPage(@Nonnegative int page) {
        this.page = GenericMath.clamp(page, 0, this.totalPages);
        this.getLevel().updateAround(this.getPosition());
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    private void updateTotalPages(boolean updateRedstone) {
        if (hasBook()) {
            this.totalPages = this.book.getTag().getList("pages", CompoundTag.class).size();
        } else {
            this.totalPages = 0;
        }

        if (updateRedstone) {
            this.getLevel().updateAroundRedstone(this.getPosition(), null);
        }
    }

    @Override
    public boolean isSpawnable() {
        return true;
    }
}
