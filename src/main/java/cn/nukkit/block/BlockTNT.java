package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.BooleanBlockProperty;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.projectile.EntityArrow;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.NukkitRandom;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.RedstoneComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author xtypr
 * @since 2015/12/8
 */
@PowerNukkitDifference(info = "Implements RedstoneComponent.", since = "1.4.0.0-PN")
public class BlockTNT extends BlockSolid implements RedstoneComponent {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BooleanBlockProperty EXPLODE_ON_BREAK = new BooleanBlockProperty("explode_bit", false);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BooleanBlockProperty ALLOW_UNDERWATER = new BooleanBlockProperty("allow_underwater_bit", false);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(EXPLODE_ON_BREAK, ALLOW_UNDERWATER);

    public BlockTNT() {
    }

    @Override
    public String getName() {
        return "TNT";
    }

    @Override
    public int getId() {
        return TNT;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public double getHardness() {
        return 0;
    }

    @Override
    public double getResistance() {
        return 0;
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public int getBurnChance() {
        return 15;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    public void prime() {
        this.prime(80);
    }

    public void prime(int fuse) {
        prime(fuse, null);
    }

    @PowerNukkitDifference(info = "TNT Sound handled by EntityPrimedTNT", since = "1.4.0.0-PN")
    public void prime(int fuse, Entity source) {
        this.getLevel().setBlock(this, Block.get(BlockID.AIR), true);
        double mot = (new NukkitRandom()).nextSignedFloat() * Math.PI * 2;
        CompoundTag nbt = new CompoundTag()
                .putList(new ListTag<DoubleTag>("Pos")
                        .add(new DoubleTag("", this.x + 0.5))
                        .add(new DoubleTag("", this.y))
                        .add(new DoubleTag("", this.z + 0.5)))
                .putList(new ListTag<DoubleTag>("Motion")
                        .add(new DoubleTag("", -Math.sin(mot) * 0.02))
                        .add(new DoubleTag("", 0.2))
                        .add(new DoubleTag("", -Math.cos(mot) * 0.02)))
                .putList(new ListTag<FloatTag>("Rotation")
                        .add(new FloatTag("", 0))
                        .add(new FloatTag("", 0)))
                .putShort("Fuse", fuse);
        Entity tnt = Entity.createEntity("PrimedTnt",
                this.getLevel().getChunk(this.getFloorX() >> 4, this.getFloorZ() >> 4),
                nbt, source
        );
        if(tnt == null) {
            return;
        }
        tnt.spawnToAll();
    }

    @Override
    @PowerNukkitDifference(info = "Using new method for checking if powered", since = "1.4.0.0-PN")
    public int onUpdate(int type) {
        if (!this.level.getServer().isRedstoneEnabled()) {
            return 0;
        }

        if ((type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) && this.isGettingPower()) {
            this.prime();
        }

        return 0;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, @Nullable Player player) {
        if (item.getId() == Item.FLINT_STEEL) {
            item.useOn(this);
            this.prime(80, player);
            return true;
        } else if (item.getId() == Item.FIRE_CHARGE) {
            if (!player.isCreative()) item.count--;
            this.prime(80, player);
            return true;
        } else if (item.hasEnchantment(Enchantment.ID_FIRE_ASPECT)) {
            item.useOn(this);
            this.prime(80, player);
            return true;
        }
        return false;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Override
    public boolean onProjectileHit(@Nonnull Entity projectile, @Nonnull Position position, @Nonnull Vector3 motion) {
        if (projectile.isOnFire() && projectile instanceof EntityArrow) {
            prime(80, projectile);
            return true;
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TNT_BLOCK_COLOR;
    }
}
