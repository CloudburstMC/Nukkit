package com.nukkitx.server.block.behavior;

import com.google.common.collect.ImmutableList;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ToolTypes;
import com.nukkitx.server.item.NukkitItemStackBuilder;

import javax.annotation.Nullable;
import java.util.Collection;

public class DirtBlockBehavior extends SimpleBlockBehavior {
    public static final DirtBlockBehavior INSTANCE = new DirtBlockBehavior();
    private static final ItemStack DIRT = new NukkitItemStackBuilder().itemType(BlockTypes.DIRT).amount(1).build();

    @Override
    public Collection<ItemStack> getDrops(Player player, Block block, @Nullable ItemStack item) {
        return ImmutableList.of(DIRT);
    }

    @Override
    public boolean isCorrectTool(@Nullable ItemStack item) {
        return item != null && item.getItemType().getToolType().isPresent() &&
                item.getItemType().getToolType().get() == ToolTypes.SHOVEL;
    }
}
