package cn.nukkit.block;

import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.misc.FallingBlock;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.Identifier;

import static cn.nukkit.block.BlockIds.AIR;


/**
 * author: rcsuperman
 * Nukkit Project
 */
public abstract class BlockFallable extends BlockSolid {

    public BlockFallable(Identifier id) {
        super(id);
    }

    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block down = this.down();
            if (down.getId() == AIR || down instanceof BlockLiquid) {
                this.level.setBlock(this.getPosition(), Block.get(AIR), true, true);

                FallingBlock fallingBlock = EntityRegistry.get().newEntity(EntityTypes.FALLING_BLOCK,
                        Location.from(this.getPosition().toFloat().add(0.5, 0, 0.5), this.level));
                fallingBlock.setBlock(this.clone());
                fallingBlock.spawnToAll();
            }
        }
        return type;
    }
}
