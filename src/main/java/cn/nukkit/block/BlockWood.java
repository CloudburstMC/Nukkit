package cn.nukkit.block;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3f;
import cn.nukkit.player.Player;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;

public class BlockWood extends BlockSolid {

    public BlockWood(Identifier id) { super(id); }

    @Override
    public double getHardness() { return 2; }

    @Override
    public double getResistance() { return 15; }

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
        return Item.get(id, this.getDamage() & 0xF);
    }


    @Override
    public boolean canBeActivated() {
        return ( 0x8 & getDamage() ) == 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (!item.isAxe()) return false;

        if ((this.getDamage() & 0b1000) == 0b1000 ) {
            this.setDamage( ( this.getDamage() ^ 0b1000 ) | 0b1101 );
        }
        Block replace = Block.get(getId(), this.getDamage() | 0x8); // adds the offset for stripped woods
        level.setBlock(x, y, z, layer, replace, true, true);
        return true;
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, Vector3f clickPos, Player player) {
        short[] faces = new short[]{
                0,    //y
                0,    //y
                0x20, //z
                0x20, //z
                0x10, //x
                0x10  //x
        };
        this.setDamage(this.getDamage() | faces[face.getIndex()]);
        return super.place(item, block, target, face, clickPos, player);
    }

    @Override
    public BlockColor getColor() {
        switch (this.getDamage() & 0x07) {
            default:
            case 1: //OAK
                return BlockColor.WOOD_BLOCK_COLOR;
            case 2: //SPRUCE
                return BlockColor.SPRUCE_BLOCK_COLOR;
            case 3: //BIRCH
                return BlockColor.SAND_BLOCK_COLOR;
            case 4: //JUNGLE
                return BlockColor.DIRT_BLOCK_COLOR;
            case 5: //ACACIA
                return BlockColor.ORANGE_BLOCK_COLOR;
            case 6: //DARK OAK
                return BlockColor.BROWN_BLOCK_COLOR;
        }
    }

}
