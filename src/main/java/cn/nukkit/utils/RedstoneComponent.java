package cn.nukkit.utils;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

/**
 * Interface, all redstone components implement, containing redstone related methods.
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public interface RedstoneComponent {

    //
    // DEFAULT METHODS
    //

    /**
     * Send a redstone update to all blocks around this block.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    default void updateAroundRedstone(@Nullable BlockFace... ignoredFaces) {
        if (ignoredFaces == null) ignoredFaces = new BlockFace[0];
        this.updateAroundRedstone(Arrays.asList(ignoredFaces));
    }

    /**
     * Send a redstone update to all blocks around this block.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    default void updateAroundRedstone(@Nonnull List<BlockFace> ignoredFaces) {
        if (this instanceof Position) updateAroundRedstone((Position) this, ignoredFaces);
    }

    /**
     * Send a redstone update to all blocks around this block.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    default void updateAllAroundRedstone(@Nullable BlockFace... ignoredFaces) {
        if (ignoredFaces == null) ignoredFaces = new BlockFace[0];
        this.updateAllAroundRedstone(Arrays.asList(ignoredFaces));
    }

    /**
     * Send a redstone update to all blocks around this block and also around the blocks of those updated blocks.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    default void updateAllAroundRedstone(@Nonnull List<BlockFace> ignoredFaces) {
        if (this instanceof Position) updateAllAroundRedstone((Position) this, ignoredFaces);
    }

    //
    // STATIC METHODS
    //

    /**
     * Send a redstone update to all blocks around the given position.
     * @param pos The middle of the blocks around.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    static void updateAroundRedstone(@Nonnull Position pos, @Nullable BlockFace... ignoredFaces) {
        if (ignoredFaces == null) ignoredFaces = new BlockFace[0];
        updateAroundRedstone(pos, Arrays.asList(ignoredFaces));
    }

    /**
     * Send a redstone update to all blocks around the given position.
     * @param pos The middle of the blocks around.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    static void updateAroundRedstone(@Nonnull Position pos, @Nonnull List<BlockFace> ignoredFaces) {
        for (BlockFace face : BlockFace.values()) {
            if (ignoredFaces.contains(face)) continue;

            pos.getLevelBlock().getSide(face).onUpdate(Level.BLOCK_UPDATE_REDSTONE);
        }
    }

    /**
     * Send a redstone update to all blocks around the given position and also around the blocks of those updated blocks.
     * @param pos The middle of the blocks around.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    static void updateAllAroundRedstone(@Nonnull Position pos, @Nullable BlockFace... ignoredFaces) {
        if (ignoredFaces == null) ignoredFaces = new BlockFace[0];
        updateAllAroundRedstone(pos, Arrays.asList(ignoredFaces));
    }

    /**
     * Send a redstone update to all blocks around the given position and also around the blocks of those updated blocks.
     * @param pos The middle of the blocks around.
     * @param ignoredFaces The faces, that shouldn't get updated.
     */
    static void updateAllAroundRedstone(@Nonnull Position pos, @Nonnull List<BlockFace> ignoredFaces) {
        updateAroundRedstone(pos, ignoredFaces);

        for (BlockFace face : BlockFace.values()) {
            if (ignoredFaces.contains(face)) continue;

            updateAroundRedstone(pos.getSide(face), face.getOpposite());
        }
    }
}