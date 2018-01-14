package cn.nukkit.server.block;

import cn.nukkit.server.Player;
import cn.nukkit.server.item.Item;
import cn.nukkit.server.item.ItemBlock;
import cn.nukkit.server.item.ItemTool;
import cn.nukkit.server.level.NukkitLevel;
import cn.nukkit.server.math.BlockFace;

/**
 * @author Nukkit Project Team
 */
public class BlockRedstoneLamp extends BlockSolid {

    public BlockRedstoneLamp(int meta) {
        super(meta);
    }

    public BlockRedstoneLamp() {
        this(0);
    }

    @Override
    public String getName() {
        return "Redstone Lamp";
    }

    @Override
    public int getId() {
        return REDSTONE_LAMP;
    }

    @Override
    public double getHardness() {
        return 0.3D;
    }

    @Override
    public double getResistance() {
        return 1.5D;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        if (this.level.isBlockPowered(this)) {
            this.level.setBlock(this, new BlockRedstoneLampLit(), false, true);
        } else {
            this.level.setBlock(this, this, false, true);
        }
        return true;
    }

    @Override
    public int onUpdate(int type) {
        if (type == NukkitLevel.BLOCK_UPDATE_NORMAL || type == NukkitLevel.BLOCK_UPDATE_REDSTONE) {
            if (this.level.isBlockPowered(this)) {
                this.level.setBlock(this, new BlockRedstoneLampLit(), false, false);
                return 1;
            }
        }

        return 0;
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                new ItemBlock(new BlockRedstoneLamp())
        };
    }

}
