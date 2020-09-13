package cn.nukkit.level.biome.type;

import cn.nukkit.api.*;
import cn.nukkit.blockstate.BlockState;
import cn.nukkit.level.biome.Biome;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.generator.Normal;

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * @author DaPorkchop_ (Nukkit Project), joserobjr
 * <p>
 * A biome with ground covering
 * </p>
 */
public abstract class CoveredBiome extends Biome {
    private static final BlockState STATE_STONE = BlockState.of(STONE);
    ////// Backward compatibility flags //////
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected Boolean useNewRakNetCover;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected Boolean useNewRakNetSurfaceDepth;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected Boolean useNewRakNetSurface;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected Boolean useNewRakNetGroundDepth;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected Boolean useNewRakNetGroundBlock;
    
    @Deprecated
    @DeprecationDetails(since = "1.4.0.0-PN", reason = "Exposed lock object and removed from new-raknet and not used by PowerNukkit")
    @Since("1.4.0.0-PN")
    @RemovedFromNewRakNet
    public final Object synchronizeCover = new Object();

    /**
     * A single block placed on top of the surface blocks
     *
     * @return cover block
     */
    @NewRakNetOnly
    public int getCoverId(int x, int z) {
        useNewRakNetCover = false;
        return getCoverBlock() << 4;
    }

    /**
     * A single block placed on top of the surface blocks
     *
     * @return cover block
     * 
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    public int getCoverBlock() {
        if (useNewRakNetCover()) {
            return getCoverId(0, 0);
        } else {
            return AIR;
        }
    }

    /**
     * A single block placed on top of the surface blocks
     *
     * @return cover block
     */
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public BlockState getCoverState(int x, int z) {
        if (useNewRakNetCover()) {
            int fullId = getCoverId(x, z);
            int blockId = fullId >> 4;
            int storage = fullId & 0b1111;
            return BlockState.of(blockId, storage);
        } else {
            return BlockState.of(getCoverBlock());
        }
    }

    @NewRakNetOnly
    public int getSurfaceDepth(int x, int y, int z) {
        useNewRakNetSurfaceDepth = false;
        return getSurfaceDepth(y);
    }

    /**
     * The amount of times the surface block should be used	
     * <p>	
     * If &lt; 0 bad things will happen!	
     * </p>	
     *
     * @param y y	
     * @return surface depth
     *
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    public int getSurfaceDepth(int y) {
        if (useNewRakNetSurfaceDepth()) {
            return getSurfaceDepth(0, y, 0);
        } else {
            return 1;
        }
    }

    @NewRakNetOnly
    public int getSurfaceId(int x, int y, int z) {
        useNewRakNetSurface = false;
        return getSurfaceBlock(y) << 4 | (getSurfaceMeta(y) & 0xF);
    }

    /**
     * Between cover and ground	
     *
     * @param y y	
     * @return surface block
     * 
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    public int getSurfaceBlock(int y) {
        if (useNewRakNetSurface()) {
            return getSurfaceId(0, y, 0) >> 4;
        } else {
            return AIR;
        }
    }
    

    /**
     * The metadata of the surface block	
     *
     * @param y y	
     * @return surface meta	
     *
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    public int getSurfaceMeta(int y) {
        if (useNewRakNetSurface()) {
            return getSurfaceId(0, y, 0) & 0xF;
        } else {
            return 0;
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState getSurfaceState(int x, int y, int z) {
        if (useNewRakNetSurface()) {
            int fullId = getSurfaceId(x, y, z);
            int blockId = fullId >> 4;
            int storage = fullId & 0b1111;
            return BlockState.of(blockId, storage);
        } else {
            return BlockState.of(getSurfaceBlock(y), getSurfaceMeta(y));
        }
    }

    @NewRakNetOnly
    public int getGroundDepth(int x, int y, int z) {
        useNewRakNetGroundDepth = false;
        return getGroundDepth(y);
    }
    
    /**
     * The amount of times the ground block should be used	
     * <p>	
     * If &lt; 0 bad things will happen!	
     *
     * @param y y	
     * @return ground depth	
     * 
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    public int getGroundDepth(int y) {
        if (useNewRakNetGroundDepth()) {
            return getGroundDepth(0, y, 0);
        } else {
            return 4;
        }
    }

    @NewRakNetOnly
    public int getGroundId(int x, int y, int z) {
        useNewRakNetGroundBlock = false;
        return getGroundBlock(y) << 4 | (getGroundMeta(y) & 0xF);
    }

    /**
     * Between surface and stone	
     *
     * @param y y	
     * @return ground block
     * 
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    public int getGroundBlock(int y) {
        if (useNewRakNetGround()) {
            return getGroundId(0, y, 0) >> 4;
        } else {
            return AIR;
        }
    }

    /**
     * The metadata of the ground block
     * @param y y	
     * @return ground meta
     * 
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    public int getGroundMeta(int y) {
        if (useNewRakNetGround()) {
            return getGroundId(0, y, 0) & 0b1111;
        } else {
            return 0;
        }
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockState getGroundState(int x, int y, int z) {
        if (useNewRakNetGround()) {
            int fullId = getGroundId(x, y, z);
            int blockId = fullId >> 4;
            int storage = fullId & 0b1111;
            return BlockState.of(blockId, storage);
        } else {
            return BlockState.of(getGroundBlock(y), getGroundMeta(y));
        }
    }

    /**
     * The block used as stone/below all other surface blocks	
     *
     * @return stone block
     *
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    public int getStoneBlock() {
        return STONE;
    }

    /**
     * Called before a new block column is covered. Biomes can update any relevant variables here before covering.
     * <p>
     * Biome covering is synchronized on the biome, so thread safety isn't an issue.
     * </p>
     *
     * @param x x
     * @param z z
     *          
     * @implNote Removed from new-raknet branch
     */
    @RemovedFromNewRakNet
    @Since("1.4.0.0-PN")
    public void preCover(int x, int z) {

    }

    @NewRakNetOnly
    public void doCover(int x, int z, @Nonnull FullChunk chunk) {
        final int fullX = (chunk.getX() << 4) | x;
        final int fullZ = (chunk.getZ() << 4) | z;
        preCover(fullX, fullZ);

        final BlockState coverState = this.getCoverState(fullX, fullZ);

        boolean hasCovered = false;
        int realY;
        //start one below build limit in case of cover blocks
        for (int y = 254; y > 32; y--) {
            if (chunk.getBlockState(x, y, z).equals(STATE_STONE)) {
                COVER:
                if (!hasCovered) {
                    if (y >= Normal.seaHeight) {
                        chunk.setBlockState(x, y + 1, z, coverState);
                        int surfaceDepth = this.getSurfaceDepth(fullX, y, fullZ);
                        for (int i = 0; i < surfaceDepth; i++) {
                            realY = y - i;
                            if (chunk.getBlockState(x, realY, z).equals(STATE_STONE)) {
                                chunk.setBlockState(x, realY, z, this.getSurfaceState(fullX, realY, fullZ));
                            } else break COVER;
                        }
                        y -= surfaceDepth;
                    }
                    int groundDepth = this.getGroundDepth(fullX, y, fullZ);
                    for (int i = 0; i < groundDepth; i++) {
                        realY = y - i;
                        if (chunk.getBlockState(x, realY, z).equals(STATE_STONE)) {
                            chunk.setBlockState(x, realY, z, this.getGroundState(fullX, realY, fullZ));
                        } else break COVER;
                    }
                    //don't take all of groundDepth away because we do y-- in the loop
                    y -= groundDepth - 1;
                }
                hasCovered = true;
            } else {
                if (hasCovered) {
                    //reset it if this isn't a valid stone block (allows us to place ground cover on top and below overhangs)
                    hasCovered = false;
                }
            }
        }
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean useNewRakNetCover() {
        Boolean useNewRakNet = useNewRakNetCover;
        if (useNewRakNet != null) {
            return useNewRakNet;
        }
        return attemptToUseNewRakNet(
                ()-> getCoverId(0,0),
                ()-> useNewRakNetCover,
                val-> useNewRakNetCover = val
        );
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean useNewRakNetSurfaceDepth() {
        Boolean useNewRakNet = useNewRakNetSurfaceDepth;
        if (useNewRakNet != null) {
            return useNewRakNet;
        }
        return attemptToUseNewRakNet(
                ()-> getSurfaceDepth(0,0, 0),
                ()-> useNewRakNetSurfaceDepth,
                val-> useNewRakNetSurfaceDepth = val
        );
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean useNewRakNetSurface() {
        Boolean useNewRakNet = useNewRakNetSurface;
        if (useNewRakNet != null) {
            return useNewRakNet;
        }
        return attemptToUseNewRakNet(
                ()-> getSurfaceId(0,0, 0),
                ()-> useNewRakNetSurface,
                val-> useNewRakNetSurface = val
        );
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean useNewRakNetGroundDepth() {
        Boolean useNewRakNet = useNewRakNetGroundDepth;
        if (useNewRakNet != null) {
            return useNewRakNet;
        }
        return attemptToUseNewRakNet(
                ()-> getGroundDepth(0,0, 0),
                ()-> useNewRakNetGroundDepth,
                val-> useNewRakNetGroundDepth = val
        );
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean useNewRakNetGround() {
        Boolean useNewRakNet = useNewRakNetGroundBlock;
        if (useNewRakNet != null) {
            return useNewRakNet;
        }
        return attemptToUseNewRakNet(
                ()-> getGroundId(0,0, 0),
                ()-> useNewRakNetGroundBlock,
                val-> useNewRakNetGroundBlock = val
        );
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    protected boolean attemptToUseNewRakNet(Runnable method, Supplier<Boolean> flagGetter, Consumer<Boolean> flagSetter) {
        method.run();
        Boolean useNewRak = flagGetter.get();
        if (useNewRak != null) {
            return useNewRak;
        }
        flagSetter.accept(true);
        return true;
    }
}
