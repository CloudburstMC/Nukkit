package com.nukkitx.server.block.behavior;

import com.google.common.collect.ImmutableList;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.item.ItemStack;
import com.nukkitx.api.item.ItemStackBuilder;
import com.nukkitx.server.network.bedrock.session.NukkitPlayerSession;
import lombok.extern.log4j.Log4j2;

import javax.annotation.Nullable;
import java.util.Collection;

@Log4j2
public class SimpleBlockBehavior implements BlockBehavior {
    public static final SimpleBlockBehavior INSTANCE = new SimpleBlockBehavior();

    @Override
    public Collection<ItemStack> getDrops(Player player, Block block, @Nullable ItemStack item) {
        if (!block.getBlockState().getBlockType().isDiggable()) {
            return ImmutableList.of();
        }
        // TODO: Fortune drops
        ItemStackBuilder builder = player.getServer().itemInstanceBuilder()
                .itemType(block.getBlockState().getBlockType())
                .amount(1);

        block.getBlockState().getMetadata().ifPresent(builder::itemData);

        return ImmutableList.of(builder.build());
    }

    @Override
    public boolean isCorrectTool(@Nullable ItemStack item) {
        return false;
    }

    @Override
    public Result onBreak(NukkitPlayerSession session, Block block, ItemStack withItem) {
        return Result.BREAK_BLOCK;
    }

    @Override
    public boolean onPlace(NukkitPlayerSession session, Block against, ItemStack withItem) {
        return true;
    }

    @Override
    public boolean onUse(Block block, NukkitPlayerSession player) {
        return false;
    }
}
