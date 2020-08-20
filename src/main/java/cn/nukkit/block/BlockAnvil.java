package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Pub4Game
 * @since 27.12.2015
 */
@PowerNukkitDifference(info = "Extends BlockFallableMeta instead of BlockFallable")
public class BlockAnvil extends BlockFallableMeta implements Faceable {

    private static final String[] NAMES = new String[]{
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
        // Does nothing
    }

    public BlockAnvil(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return ANVIL;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 1;
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

    @PowerNukkitDifference(info = "Just like sand, it can now be placed anywhere and removed the sound for the player who placed, was duplicated", since = "1.3.0.0-PN")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        int damage = this.getDamage();
        int[] faces = {1, 2, 3, 0};
        this.setDamage(faces[player != null ? player.getDirection().getHorizontalIndex() : 0]);
        if (damage >= 4 && damage <= 7) {
            this.setDamage(this.getDamage() | 0x04);
        } else if (damage >= 8 && damage <= 11) {
            this.setDamage(this.getDamage() | 0x08);
        }
        this.getLevel().setBlock(block, this, true);
        if (player == null) {
            this.getLevel().addSound(this, Sound.RANDOM_ANVIL_LAND, 1, 0.8F);
        } else {
            Collection<Player> players = getLevel().getChunkPlayers(getChunkX(), getChunkZ()).values();
            players.remove(player);
            if (!players.isEmpty()) {
                getLevel().addSound(this, Sound.RANDOM_ANVIL_LAND, 1, 0.8F, players);
            }
        }
        return true;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
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
        if (item.isPickaxe() && item.getTier() >= ItemTool.TIER_WOODEN) {
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
        return BlockFace.fromHorizontalIndex(this.getDamage() & 0x7);
    }

    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Fixed the returned bounding box")
    @Override
    protected AxisAlignedBB recalculateBoundingBox() {
        BlockFace face = getBlockFace().rotateY();
        double xOffset = Math.abs(face.getXOffset()) * (2/16.0);
        double zOffset = Math.abs(face.getZOffset()) * (2/16.0);
        return new SimpleAxisAlignedBB(x + xOffset, y, z + zOffset, x + 1 - xOffset, y + 1, z + 1 - zOffset);
    }
}
