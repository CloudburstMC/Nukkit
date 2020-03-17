package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityTypes;
import cn.nukkit.entity.misc.PrimedTnt;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Sound;
import cn.nukkit.player.Player;
import cn.nukkit.registry.EntityRegistry;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;

import java.util.concurrent.ThreadLocalRandom;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.item.ItemIds.FIREBALL;
import static cn.nukkit.item.ItemIds.FLINT_AND_STEEL;

/**
 * Created on 2015/12/8 by xtypr.
 * Package cn.nukkit.block in project Nukkit .
 */
public class BlockTNT extends BlockSolid {

    public BlockTNT(Identifier id) {
        super(id);
    }

    @Override
    public float getHardness() {
        return 0;
    }

    @Override
    public float getResistance() {
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

    public void prime(int fuse, Entity source) {
        this.getLevel().setBlock(this.getPosition(), Block.get(AIR), true);
        float mot = ThreadLocalRandom.current().nextFloat() * (float) Math.PI * 2f;

        PrimedTnt primedTnt = EntityRegistry.get().newEntity(EntityTypes.TNT,
                Location.from(this.getPosition().toFloat().add(0.5, 0, 0.5), this.getLevel()));
        primedTnt.setMotion(Vector3f.from(-Math.sin(mot) * 0.02, 0.2, -Math.cos(mot) * 0.02));
        primedTnt.setFuse(fuse);
        primedTnt.setSource(source);
        primedTnt.spawnToAll();
        this.level.addSound(this.getPosition(), Sound.RANDOM_FUSE);
    }

    @Override
    public int onUpdate(int type) {
        if ((type == Level.BLOCK_UPDATE_NORMAL || type == Level.BLOCK_UPDATE_REDSTONE) && this.level.isBlockPowered(this.getPosition())) {
            this.prime();
        }

        return 0;
    }

    @Override
    public boolean onActivate(Item item, Player player) {
        if (item.getId() == FLINT_AND_STEEL) {
            item.useOn(this);
            this.prime(80, player);
            return true;
        }
        if (item.getId() == FIREBALL) {
            if (!player.isCreative()) player.getInventory().removeItem(Item.get(FIREBALL, 0, 1));
            this.level.addSound(player.getPosition(), Sound.MOB_GHAST_FIREBALL);
            this.prime(80, player);
            return true;
        }
        return false;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.TNT_BLOCK_COLOR;
    }
}
