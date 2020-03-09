package cn.nukkit.blockentity;

import cn.nukkit.item.Item;

import javax.annotation.Nonnegative;
import javax.annotation.Nullable;

public interface Lectern extends BlockEntity {

    boolean hasBook();

    @Nullable
    Item getBook();

    void setBook(@Nullable Item book);

    int getPage();

    void setPage(@Nonnegative int page);

    int getTotalPages();
}
