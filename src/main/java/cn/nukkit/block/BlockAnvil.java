package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BlockProperty;
import cn.nukkit.blockproperty.value.AnvilDamage;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Sound;
import cn.nukkit.math.AxisAlignedBB;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Faceable;

import javax.annotation.Nonnull;
import java.util.Collection;

import static cn.nukkit.blockproperty.CommonBlockProperties.DIRECTION;

/**
 * @author Pub4Game
 * @since 27.12.2015
 */
@PowerNukkitDifference(info = "Extends BlockFallableMeta instead of BlockFallable")
public class BlockAnvil extends BlockFallableMeta implements Faceable {
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperty<AnvilDamage> DAMAGE = new ArrayBlockProperty<>("damage", false, AnvilDamage.class);

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(
        DIRECTION.exportingToItems(true), DAMAGE.exportingToItems(true)
    );

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

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public AnvilDamage getAnvilDamage() {
        return getPropertyValue(DAMAGE);
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    public void setAnvilDamage(AnvilDamage anvilDamage) {
        setPropertyValue(DAMAGE, anvilDamage);
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
        return getAnvilDamage().getEnglishName();
    }

    @PowerNukkitDifference(info = "Just like sand, it can now be placed anywhere and removed the sound for the player who placed, was duplicated", since = "1.3.0.0-PN")
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        setBlockFace(player != null? player.getDirection().rotateY() : BlockFace.SOUTH);
        this.getLevel().setBlock(this, this, true);
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
    public int getToolTier() {
        return ItemTool.TIER_WOODEN;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.IRON_BLOCK_COLOR;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public void setBlockFace(BlockFace face) {
        setPropertyValue(DIRECTION, face);
    }

    @Override
    public BlockFace getBlockFace() {
        return getPropertyValue(DIRECTION);
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
