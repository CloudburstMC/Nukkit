package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.BlockFace.Axis;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

//Block state information: https://hastebin.com/emuvawasoj.js
public class BlockWood extends BlockSolid {
    private static final int STRIPPED_BIT = 0b1000;
    public static final int OAK = 0;
    public static final int SPRUCE = 1;
    public static final int BIRCH = 2;
    public static final int JUNGLE = 3;
    public static final int ACACIA = 4;
    public static final int DARK_OAK = 5;
    private static final int AXIS_Y = 0;
    private static final int AXIS_X = 1 << 4;
    private static final int AXIS_Z = 2 << 4;

    public BlockWood(Identifier id) {
        super(id);
    }

    @Override
    public double getHardness() {
        return 2;
    }

    @Override
    public double getResistance() {
        return 15;
    }

    @Override
    public int getBurnChance() {
        return 5;
    }

    @Override
    public int getBurnAbility() {
        return 20;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_AXE;
    }

    @Override
    public Item toItem() {
        return Item.get(id, getDamage() & 0xF);
    }


    @Override
    public boolean canBeActivated() {
        return !isStripped();
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (!item.isAxe() || !player.isCreative() && !item.useOn(this)) {
            return false;
        }

        setDamage(getDamage() | STRIPPED_BIT);  // adds the offset for stripped woods
        getLevel().setBlock(this, this, true);
        return true;
    }

    public void setStripped(boolean stripped) {
        setDamage((getDamage() & ~STRIPPED_BIT) | (stripped ? STRIPPED_BIT : 0));
    }

    public boolean isStripped() {
        return (getDamage() & STRIPPED_BIT) != 0;
    }

    public Axis getAxis() {
        switch (getDamage() & 0x30) {
            default:
            case AXIS_Y:
                return Axis.Y;
            case AXIS_X:
                return Axis.X;
            case AXIS_Z:
                return Axis.Z;
        }
    }

    public void setAxis(Axis axis) {
        int axisProp;
        switch (axis) {
            default:
            case Y:
                axisProp = AXIS_Y;
                break;
            case X:
                axisProp = AXIS_X;
                break;
            case Z:
                axisProp = AXIS_Z;
                break;
        }
        setDamage((getDamage() & ~0x30) | axisProp);
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        setAxis(face.getAxis());
        return super.place(item, block, target, face, clickPos, player);
    }

    public void setWoodType(int woodType) {
        if (woodType < OAK || woodType > DARK_OAK) {
            woodType = OAK;
        }
        setDamage((getDamage() & -0b1000) | woodType);
    }

    public int getWoodType() {
        return getDamage() & 0b111;
    }

    @Override
    public BlockColor getColor() {
        switch (getWoodType()) {
            default:
            case OAK:
                return BlockColor.WOOD_BLOCK_COLOR;
            case SPRUCE:
                return BlockColor.SPRUCE_BLOCK_COLOR;
            case BIRCH:
                return BlockColor.SAND_BLOCK_COLOR;
            case JUNGLE:
                return BlockColor.DIRT_BLOCK_COLOR;
            case ACACIA:
                return BlockColor.ORANGE_BLOCK_COLOR;
            case DARK_OAK: //DARK OAK
                return BlockColor.BROWN_BLOCK_COLOR;
        }
    }

}
