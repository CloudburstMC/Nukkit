package com.nukkitx.server.block.behavior;

import com.google.common.collect.ImmutableList;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemTypes;
import com.nukkitx.api.item.ToolTypes;
import com.nukkitx.server.item.NukkitItemStackBuilder;

import javax.annotation.Nullable;
import java.util.Collection;

public class NetherQuartzOreBlockBehavior extends SimpleBlockBehavior {
    public static final NetherQuartzOreBlockBehavior INSTANCE = new NetherQuartzOreBlockBehavior();
    private static final ItemStack NETHER_QUARTZ = new NukkitItemStackBuilder().itemType(ItemTypes.NETHER_QUARTZ).amount(1).build();

    @Override
    public Collection<ItemStack> getDrops(Player player, Block block, @Nullable ItemStack item) {
        return ImmutableList.of(NETHER_QUARTZ);
    }

    @Override
    public boolean isCorrectTool(@Nullable ItemStack item) {
        return item != null && item.getItemType().getToolType().isPresent() &&
                item.getItemType().getToolType().get() == ToolTypes.PICKAXE;
    }
}
