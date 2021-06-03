package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.blockproperty.value.DoublePlantType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemID;
import cn.nukkit.item.ItemTool;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Angelic47 (Nukkit Project)
 */
public class BlockTallGrass extends BlockFlowable {

    public BlockTallGrass() {
        this(1);
    }

    public BlockTallGrass(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return TALL_GRASS;
    }

    @Override
    public String getName() {
        String[] names = new String[]{
                "Grass",
                "Grass",
                "Fern",
                "Fern"
        };
        return names[this.getDamage() & 0x03];
    }

    @Override
    public boolean canBeActivated() {
        return true;
    }

    @Override
    public boolean canBeReplaced() {
        return true;
    }

    @Override
    public int getBurnChance() {
        return 60;
    }

    @Override
    public int getBurnAbility() {
        return 100;
    }

    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        if (BlockSweetBerryBush.isSupportValid(down())) {
            this.getLevel().setBlock(block, this, true);
            return true;
        }
        return false;
    }
    
    @PowerNukkitDifference(since = "1.4.0.0-PN", info = "Will break on block update if the supporting block is invalid")
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            if (!BlockSweetBerryBush.isSupportValid(down(1, 0))) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
        }
        return 0;
    }

    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (item.isFertilizer()) {
            Block up = this.up();

            if (up.getId() == AIR) {
                DoublePlantType type;

                switch (this.getDamage()) {
                    case 0:
                    case 1:
                        type = DoublePlantType.TALL_GRASS;
                        break;
                    case 2:
                    case 3:
                        type = DoublePlantType.LARGE_FERN;
                        break;
                    default:
                        type = null;
                }

                if (type != null) {
                    if (player != null && !player.isCreative()) {
                        item.count--;
                    }

                    BlockDoublePlant doublePlant = (BlockDoublePlant) Block.get(BlockID.DOUBLE_PLANT);
                    doublePlant.setDoublePlantType(type);
                    doublePlant.setTopHalf(false);

                    this.level.addParticle(new BoneMealParticle(this));
                    this.level.setBlock(this, doublePlant, true, false);

                    doublePlant.setTopHalf(true);
                    this.level.setBlock(up, doublePlant, true);
                    this.level.updateAround(this);
                }
            }

            return true;
        }

        return false;
    }

    @Override
    public Item[] getDrops(Item item) {
        // https://minecraft.gamepedia.com/Fortune#Grass_and_ferns
        List<Item> drops = new ArrayList<>(2);
        if (item.isShears()) {
            drops.add(getCurrentState().asItemBlock());
        }

        ThreadLocalRandom random = ThreadLocalRandom.current();
        if (random.nextInt(8) == 0) {
            Enchantment fortune = item.getEnchantment(Enchantment.ID_FORTUNE_DIGGING);
            int fortuneLevel = fortune != null? fortune.getLevel() : 0;
            int amount = fortuneLevel == 0? 1 : 1 + random.nextInt(fortuneLevel * 2);
            drops.add(Item.get(ItemID.WHEAT_SEEDS, 0, amount));
        }
        
        return drops.toArray(Item.EMPTY_ARRAY);
    }

    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }

    @Override
    public BlockColor getColor() {
        return BlockColor.FOLIAGE_BLOCK_COLOR;
    }
}
