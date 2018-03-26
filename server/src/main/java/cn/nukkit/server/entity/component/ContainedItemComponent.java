package cn.nukkit.server.entity.component;

import cn.nukkit.api.block.BlockTypes;
import cn.nukkit.api.entity.component.ContainedItem;
import cn.nukkit.api.item.ItemInstance;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;

public class ContainedItemComponent implements ContainedItem {
    private ItemInstance item;
    private volatile boolean stale = false;

    public ContainedItemComponent(@Nonnull ItemInstance item) {
        Preconditions.checkNotNull(item, "item");
        Preconditions.checkArgument(item.getItemType() != BlockTypes.AIR);
        this.item = item;
    }

    @Override
    public ItemInstance getItem() {
        return item;
    }

    @Override
    public void setItem(@Nonnull ItemInstance item) {
        Preconditions.checkNotNull(item);
        if (!this.item.equals(item)) {
            this.item = item;
            stale = true;
        }
    }

    public boolean isStale() {
        return stale;
    }

    public void refresh() {
        stale = false;
    }
}
