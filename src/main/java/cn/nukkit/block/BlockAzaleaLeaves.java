package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.BlockFace;

import java.util.concurrent.ThreadLocalRandom;

public class BlockAzaleaLeaves extends BlockLeaves {

    public BlockAzaleaLeaves() {
        this(0);
    }

    public BlockAzaleaLeaves(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return AZALEA_LEAVES;
    }

    @Override
    public String getName() {
        return "Azalea Leaves";
    }

    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        this.setPersistent(true);
        return super.place(item, block, target, face, fx, fy, fz, player);
    }

    @Override
    public Item toItem() {
        return new ItemBlock(this, 0, 1);
    }

    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears() || item.hasEnchantment(Enchantment.ID_SILK_TOUCH)) {
            return new Item[]{ this.toItem() };
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextInt(20) != 0) {
            return new Item[0];
        }

        int chance = random.nextInt(4);
        if (chance == 0 || chance == 1) {
            return new Item[]{ Item.get(random.nextBoolean() ? Item.AZALEA : Item.FLOWERING_AZALEA, 0, 1) };
        } else if (chance == 2) {
            return new Item[]{ Item.get(Item.SAPLING, random.nextInt(0, 6), 1) };
        } else {
            return new Item[]{ Item.get(Item.STICK, 0, random.nextInt(1, 2)) };
        }
    }

    @Override
    protected void setOnDecayDamage() {
        this.setCheckDecay(false);
    }

    @Override
    protected boolean canDropApple() {
        return false;
    }

    @Override
    public boolean isPersistent() {
        return (this.getDamage() & 2) >>> 1 == 1;
    }

    @Override
    public void setPersistent(boolean persistent) {
        int value = (persistent ? 1 : 0) << 1;
        this.setDamage(this.getDamage() & ~1 | (value & 2));
    }

    @Override
    public boolean isCheckDecay() {
        return (this.getDamage() & 1) == 1;
    }

    @Override
    public void setCheckDecay(boolean updateBit) {
        this.setDamage(this.getDamage() & ~1 | ((updateBit ? 1 : 0) & 1));
    }
}
