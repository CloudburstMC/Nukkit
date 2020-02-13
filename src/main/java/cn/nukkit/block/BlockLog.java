package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.registry.BlockRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

/**
 * author: MagicDroidX
 * Nukkit Project
 */
public class BlockLog extends BlockSolid {
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;

    protected static final Identifier[] STRIPPED_IDS = new Identifier[] {
            BlockIds.STRIPPED_OAK_LOG,
            BlockIds.STRIPPED_SPRUCE_LOG,
            BlockIds.STRIPPED_BIRCH_LOG,
            BlockIds.STRIPPED_JUNGLE_LOG,
            BlockIds.STRIPPED_ACACIA_LOG,
            BlockIds.STRIPPED_DARK_OAK_LOG
    };

    protected static final short[] FACES = new short[]{
            0,
            0,
            0b1000,
            0b1000,
            0b0100,
            0b0100
    };

    public BlockLog(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 10;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 10;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        // Convert the old log bark to the new wood block
        if ((this.getDamage() & 0b1100) == 0b1100) {
            Block woodBlock = Block.get(BlockIds.WOOD, this.getDamage() & 0x03, this);
            return woodBlock.place(item, block, target, face, clickPos, player);
        }

        this.setDamage(((this.getDamage() & 0x03) | FACES[face.getIndex()]));
        this.getLevel().setBlock(block, this, true, true);

        return true;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (!item.isAxe() || !item.useOn(this)) {
            return false;
        }

        int log2Damage = this instanceof BlockLog2 ? 4 : 0;
        int damage = ( this.getDamage() & -0b100 ) ^ this.getDamage();
        Block strippedBlock = Block.get(STRIPPED_IDS[damage + log2Damage], ( this.getDamage() >> 2 ) );
        this.getLevel().setBlock(this.asVector3i(), strippedBlock, true, true);
        return true;
    }

    @Override
    public Item toItem() {
        if ((getDamage() & 0b1100) == 0b1100) {
            return Item.get(BlockIds.WOOD, this.getDamage() & 0x3);
        } else {
            return Item.get(id, this.getDamage() & 0x03);
        }
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public BlockColor getColor() {
        switch (getDamage() & 0x07) {
            default:
            case OAK:
                return BlockColor.WOOD_BLOCK_COLOR;
            case SPRUCE:
                return BlockColor.SPRUCE_BLOCK_COLOR;
            case BIRCH:
                return BlockColor.SAND_BLOCK_COLOR;
            case JUNGLE:
                return BlockColor.DIRT_BLOCK_COLOR;
        }
    }

    public static void upgradeLegacyBlock(int[] blockState) {
        if ((blockState[1] & 0b1100) == 0b1100) { // old full bark texture
            blockState[0] = BlockRegistry.get().getLegacyId(BlockIds.WOOD);
            blockState[1] = blockState[1] & 0x03; // gets only the log type and set pillar to y
        }
    }
}
