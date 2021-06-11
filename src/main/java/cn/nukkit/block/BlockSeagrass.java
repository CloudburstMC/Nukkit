package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.blockproperty.ArrayBlockProperty;
import cn.nukkit.blockproperty.BlockProperties;
import cn.nukkit.blockproperty.value.SeaGrassType;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;

import javax.annotation.Nonnull;

@PowerNukkitOnly
public class BlockSeagrass extends BlockFlowable {

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final ArrayBlockProperty<SeaGrassType> SEA_GRASS_TYPE = new ArrayBlockProperty<>("sea_grass_type", false, SeaGrassType.class);

    @PowerNukkitOnly
    @Since("1.5.0.0-PN")
    public static final BlockProperties PROPERTIES = new BlockProperties(SEA_GRASS_TYPE);

    @PowerNukkitOnly
    public BlockSeagrass() {
        this(0);
    }

    @PowerNukkitOnly
    public BlockSeagrass(int meta) {
        super(meta);
    }
    
    @Override
    public int getId() {
        return SEAGRASS;
    }

    @Since("1.4.0.0-PN")
    @PowerNukkitOnly
    @Nonnull
    @Override
    public BlockProperties getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getName() {
        return "Seagrass";
    }
    
    @Override
    public boolean place(@Nonnull Item item, @Nonnull Block block, @Nonnull Block target, @Nonnull BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = down();
        Block layer1Block = block.getLevelBlockAtLayer(1);
        int waterDamage;
        if (down.isSolid() && down.getId() != MAGMA && down.getId() != SOUL_SAND &&
                (layer1Block instanceof BlockWater && ((waterDamage = (block.getDamage())) == 0 || waterDamage == 8))
        ) {
            if (waterDamage == 8) {
                this.getLevel().setBlock(this, 1, new BlockWater(), true, false);
            }
            this.getLevel().setBlock(this, 0, this, true, true);
            return true;
        }
        return false;
    }
    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            Block blockLayer1 = getLevelBlockAtLayer(1);
            int damage;
            if (!(blockLayer1 instanceof BlockIceFrosted)
                    && (!(blockLayer1 instanceof BlockWater) || ((damage = blockLayer1.getDamage()) != 0 && damage != 8))) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            
            Block down = down();
            damage = getDamage();
            if (damage == 0 || damage == 2) {
                if (!down.isSolid() || down.getId() == MAGMA || down.getId() == SOUL_SAND) {
                    this.getLevel().useBreakOn(this);
                    return Level.BLOCK_UPDATE_NORMAL;
                }
                
                if (damage == 2) {
                    Block up = up();
                    if (up.getId() != getId() || up.getDamage() != 1) {
                        this.getLevel().useBreakOn(this);
                    }
                }
            } else if (down.getId() != getId() || down.getDamage() != 2) {
                this.getLevel().useBreakOn(this);
            }
            
            return Level.BLOCK_UPDATE_NORMAL;
        }
        
        return 0;
    }
    
    @Override
    public boolean canBeActivated() {
        return true;
    }
    
    @Override
    public boolean onActivate(@Nonnull Item item, Player player) {
        if (getDamage() == 0 && item.isFertilizer()) {
            Block up = this.up();
            int damage;
            if (up instanceof BlockWater && ((damage = up.getDamage()) == 0 || damage == 8)) {
                if (player != null && (player.gamemode & 0x01) == 0) {
                    item.count--;
                }

                this.level.addParticle(new BoneMealParticle(this));
                this.level.setBlock(this, new BlockSeagrass(2), true, false);
                this.level.setBlock(up, 1, up, true, false);
                this.level.setBlock(up, 0, new BlockSeagrass(1), true);
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public Item[] getDrops(Item item) {
        if (item.isShears()) {
            return new Item[] { toItem() };
        } else {
            return Item.EMPTY_ARRAY;
        }
    }
    
    @Override
    public boolean canBeReplaced() {
        return true;
    }
    
    @PowerNukkitOnly
    @Override
    public int getWaterloggingLevel() {
        return 2;
    }
    
    @Override
    public BlockColor getColor() {
        return BlockColor.WATER_BLOCK_COLOR;
    }
    
    @Override
    public int getToolType() {
        return ItemTool.TYPE_SHEARS;
    }
    
    @Override
    public Item toItem() {
        return new ItemBlock(new BlockSeagrass(), 0);
    }
}
