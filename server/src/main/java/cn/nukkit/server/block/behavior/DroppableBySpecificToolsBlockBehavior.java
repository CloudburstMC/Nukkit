package cn.nukkit.server.block.behavior;

import cn.nukkit.api.item.ItemInstance;
import cn.nukkit.api.item.ItemType;
import cn.nukkit.api.item.ItemTypes;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nullable;
import java.util.List;

import static cn.nukkit.api.item.ItemTypes.*;

public class DroppableBySpecificToolsBlockBehavior extends SimpleBlockBehavior {
    public static final BlockBehavior ALL_PICKAXES = new DroppableBySpecificToolsBlockBehavior(
            ImmutableList.of(WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE));
    public static final BlockBehavior STONE_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(
            ImmutableList.of(STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE));
    public static final BlockBehavior IRON_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(
            ImmutableList.of(IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE));
    public static final BlockBehavior GOLDEN_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(
            ImmutableList.of(GOLDEN_PICKAXE, DIAMOND_PICKAXE));
    public static final BlockBehavior DIAMOND_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(
            ImmutableList.of(DIAMOND_PICKAXE));
    public static final BlockBehavior SHEARS = new DroppableBySpecificToolsBlockBehavior(ImmutableList.of(ItemTypes.SHEARS));


    private final List<ItemType> itemsAllowed;

    public DroppableBySpecificToolsBlockBehavior(List<ItemType> itemsAllowed) {
        Preconditions.checkNotNull(itemsAllowed, "itemsAllowed");
        this.itemsAllowed = itemsAllowed;
    }

    @Override
    public boolean isCorrectTool(@Nullable ItemInstance item) {
        return item != null && itemsAllowed.contains(item.getItemType());
    }
}
