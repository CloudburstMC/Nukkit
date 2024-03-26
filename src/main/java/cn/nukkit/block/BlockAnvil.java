package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.network.protocol.LevelEventPacket;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

/**
 * Created by Pub4Game on 27.12.2015.
 */
public class BlockAnvil extends BlockFallableMeta implements Faceable {

    private static final int[] FACES = {1, 2, 3, 0};

    private static final String[] NAMES = {
            "Anvil",
            "Anvil",
            "Anvil",
            "Anvil",
            "Slighty Damaged Anvil",
            "Slighty Damaged Anvil",
            "Slighty Damaged Anvil",
            "Slighty Damaged Anvil",
            "Very Damaged Anvil",
            "Very Damaged Anvil",
            "Very Damaged Anvil",
            "Very Damaged Anvil"
    };

    public BlockAnvil() {
        this(0);
    }

    public BlockAnvil(int meta) {
        super(meta);
    }

    @Override
    public int getFullId() {
        return (getId() << DATA_BITS) + getDamage();
    }

    @Override
    public int getId() {
        return ANVIL;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean isTransparent() {
        return true;
    }

    @Override
    public double getHardness() {
        return 5;
    }

    @Override
    public double getResistance() {
        return 6000;
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_PICKAXE;
    }

    @Override
    public String getName() {
        return NAMES[this.getDamage() > 11 ? 0 : this.getDamage()];
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        int damage = this.getDamage();
        this.setDamage(FACES[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        if (damage >= 4 && damage <= 7) {
            this.setDamage(this.getDamage() | 0x04);
        } else if (damage >= 8 && damage <= 11) {
            this.setDamage(this.getDamage() | 0x08);
        }
        this.getLevel().setBlock(this, this, true, true);
        this.getLevel().addLevelEvent(this, LevelEventPacket.EVENT_SOUND_ANVIL_FALL);
        return true;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (player != null) {
            player.addWindow(new AnvilInventory(player.getUIInventory(), this), Player.ANVIL_WINDOW_ID);
        }
        return true;
    }

    @Override
    public Item toItem() {
        int damage = this.getDamage();
        if (damage >= 4 && damage <= 7) {
            return new ItemBlock(this, this.getDamage() & 0x04);
        } else if (damage >= 8 && damage <= 11) {
            return new ItemBlock(this, this.getDamage() & 0x08);
        } else {
            return new ItemBlock(this);
        }
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isPickaxe()) {
            return new Item[]{
                    this.toItem()
            };
        }
        return new Item[0];
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Override
    public BlockFace getBlockFace() {
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x3);
    }

    @Override
    public double getMinX() {
        return this.x + (this.getBlockFace().getAxis() == BlockFace.Axis.X ? 0 : 2 / 16.0);
    }

    @Override
    public double getMinZ() {
        return this.z + (this.getBlockFace().getAxis() == BlockFace.Axis.Z ? 0 : 2 / 16.0);
    }

    @Override
    public double getMaxX() {
        return this.x + (this.getBlockFace().getAxis() == BlockFace.Axis.X ? 1 : 1 - 2 / 16.0);
    }

    @Override
    public double getMaxZ() {
        return this.z + (this.getBlockFace().getAxis() == BlockFace.Axis.Z ? 1 : 1 - 2 / 16.0);
    }

    @Override
    public boolean isSolid() {
        return false;
    }

    @Override
    public WaterloggingType getWaterloggingType() {
        return WaterloggingType.WHEN_PLACED_IN_WATER;
    }
}
