package com.nukkitx.server.block.behavior;

import com.google.common.base.Preconditions;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.TierType;
import com.nukkitx.api.item.ToolType;
import com.nukkitx.api.item.ToolTypes;

import javax.annotation.Nullable;

import static com.nukkitx.api.item.TierTypes.*;
import static com.nukkitx.api.item.ToolTypes.PICKAXE;

public class DroppableBySpecificToolsBlockBehavior extends SimpleBlockBehavior {
    public static final BlockBehavior ALL_PICKAXES = new DroppableBySpecificToolsBlockBehavior(PICKAXE);
    public static final BlockBehavior STONE_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(PICKAXE, STONE);
    public static final BlockBehavior IRON_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(PICKAXE, IRON);
    public static final BlockBehavior GOLD_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(PICKAXE, GOLD);
    public static final BlockBehavior DIAMOND_PICKAXE_AND_ABOVE = new DroppableBySpecificToolsBlockBehavior(PICKAXE, DIAMOND);
    public static final BlockBehavior SHEARS = new DroppableBySpecificToolsBlockBehavior(ToolTypes.SHEARS);


    private final ToolType toolType;
    private final TierType tierType;

    public DroppableBySpecificToolsBlockBehavior(ToolType toolType) {
        this.toolType = Preconditions.checkNotNull(toolType, "toolType");
        this.tierType = null;
    }

    public DroppableBySpecificToolsBlockBehavior(ToolType toolType, TierType tierType) {
        this.toolType = Preconditions.checkNotNull(toolType, "toolType");
        this.tierType = tierType;
    }

    @Override
    public boolean isCorrectTool(@Nullable ItemStack item) {
        return item != null &&
                item.getItemType().getToolType().isPresent() &&
                item.getItemType().getToolType().get() == toolType &&
                (tierType == null || item.getItemType().getTierType().isPresent() &&
                        item.getItemType().getTierType().get().compareTo(tierType) >= 0);
    }
}
