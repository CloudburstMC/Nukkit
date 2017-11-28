package cn.nukkit.block;

import cn.nukkit.dispenser.DefaultDispenseBehavior;
import cn.nukkit.dispenser.DispenseBehavior;
import cn.nukkit.dispenser.ProjectileDispenseBehavior;
import cn.nukkit.item.Item;
import cn.nukkit.math.BlockFace;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by CreeperFace on 15.4.2017.
 */
public class BlockDispenser extends BlockSolid {

    private static Map<Integer, DispenseBehavior> behaviors = new HashMap<>();
    private static DispenseBehavior defaultBehavior = new DefaultDispenseBehavior();

    static {
        DispenseBehavior projectile = new ProjectileDispenseBehavior();

        registerBehavior(Item.ARROW, projectile);
        registerBehavior(Item.SNOWBALL, projectile);
        registerBehavior(Item.EGG, projectile);
        registerBehavior(Item.EXPERIENCE_BOTTLE, projectile);
        registerBehavior(Item.SPLASH_POTION, projectile);
        registerBehavior(Item.FIRE_CHARGE, projectile);
        registerBehavior(Item.LINGERING_POTION, projectile);
        /*registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);
        registerBehavior(Item., projectile);*/
    }

    public BlockDispenser() {
        this(0);
    }

    public BlockDispenser(int meta) {
        super(meta);
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public String getName() {
        return "Dispenser";
    }

    @Override
    public int getId() {
        return DISPENSER;
    }

    @Override
    public int getComparatorInputOverride() {
        /*BlockEntity blockEntity = this.level.getBlockEntity(this);

        if(blockEntity instanceof BlockEntityDispenser) {
            //return ContainerInventory.calculateRedstone(((BlockEntityDispenser) blockEntity).getInventory()); TODO: dispenser
        }*/

        return super.getComparatorInputOverride();
    }

    public BlockFace getFacing() {
        return BlockFace.fromIndex(this.meta & 7);
    }

    public boolean isTriggered() {
        return (this.meta & 8) > 0;
    }

    public void setTriggered(boolean value) {
        int i = 0;
        i |= getFacing().getIndex();

        if (value) {
            i |= 8;
        }

        this.meta = i;
    }

    @Override
    public boolean canHarvestWithHand() {
        return false;
    }

    public static void registerBehavior(int itemId, DispenseBehavior behavior) {
        behaviors.put(itemId, behavior);
    }

    public DispenseBehavior getBehavior(int id) {
        return behaviors.getOrDefault(id, defaultBehavior);
    }
}
