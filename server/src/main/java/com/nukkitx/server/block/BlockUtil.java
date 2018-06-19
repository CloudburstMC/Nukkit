package com.nukkitx.server.block;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.base.Preconditions;
import com.nukkitx.api.Player;
import com.nukkitx.api.block.Block;
import com.nukkitx.api.block.BlockState;
import com.nukkitx.api.block.BlockType;
import com.nukkitx.api.block.BlockTypes;
import com.nukkitx.api.item.ItemInstance;
import com.nukkitx.api.level.chunk.Chunk;
import com.nukkitx.api.metadata.Metadata;
import com.nukkitx.api.util.data.BlockFace;
import com.nukkitx.server.block.behavior.BlockBehaviors;
import com.nukkitx.server.item.NukkitItemInstance;
import com.nukkitx.server.level.NukkitLevel;
import lombok.experimental.UtilityClass;

import java.util.Optional;

@UtilityClass
public class BlockUtil {
    public static final NukkitItemInstance AIR = new NukkitItemInstance(BlockTypes.AIR, 0, null);

    public static boolean setBlockState(Player player, Vector3i position, BlockState state) {
        int chunkX = position.getX() >> 4;
        int chunkZ = position.getZ() >> 4;

        Optional<Chunk> chunkOptional = player.getLevel().getChunkIfLoaded(chunkX, chunkZ);
        if (!chunkOptional.isPresent()) {
            // Chunk not loaded, danger ahead!
            return false;
        }

        Block old = chunkOptional.get().getBlock(position.getX() & 0x0f, position.getY(), position.getZ() & 0x0f);
        chunkOptional.get().setBlock(position.getX() & 0x0f, position.getY(), position.getZ() & 0x0f, state);
        ((NukkitLevel) player.getLevel()).broadcastBlockUpdate(player, position);
        return true;
    }

    public static boolean replaceBlockState(Player player, Block block, BlockState replacementState) {
        block.getChunk().setBlock(block.getChunkLocation().getX(), block.getChunkLocation().getY(), block.getChunkLocation().getZ(), replacementState);
        ((NukkitLevel) player.getLevel()).broadcastBlockUpdate(player, block.getBlockPosition());
        return true;
    }

    public static BlockState createBlockState(Vector3i position, BlockFace face, ItemInstance item) {
        Preconditions.checkNotNull(item, "item");

        if (!item.getItemType().isBlock()) {
            throw new IllegalArgumentException("Item type " + item.getItemType().getName() + " is not a block type.");
        }

        // Consult block behavior for the relevant block.
        Optional<BlockState> overrideOptional = BlockBehaviors.getBlockBehavior((BlockType) item.getItemType())
                .overridePlacement(position, face, item);
        if (overrideOptional.isPresent()) {
            return overrideOptional.get();
        }

        BlockType blockType = (BlockType) item.getItemType();
        Optional<Metadata> itemData = item.getItemData();
        Metadata blockData = itemData.orElse(null);
        return new NukkitBlockState(blockType, blockData, null);
    }
}
