package cn.nukkit.block;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.item.ItemTool;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.BoneMealParticle;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import cn.nukkit.utils.DyeColor;

public class BlockSeagrass extends BlockFlowable {
    
    public BlockSeagrass() {
        this(0);
    }
    
    public BlockSeagrass(int meta) {
        super(meta % 3);
    }
    
    @Override
    public int getId() {
        return SEAGRASS;
    }
    
    @Override
    public String getName() {
        return "Seagrass";
    }
    
    @Override
    public void setDamage(int meta) {
        super.setDamage(meta % 3);
    }
    
    @Override
    public boolean place(Item item, Block block, Block target, BlockFace face, double fx, double fy, double fz, Player player) {
        Block down = down();
        boolean waterAtLayer1 = false;
        if ((block.getId() == Block.STILL_WATER || (waterAtLayer1 = block.getLevelBlockAtLayer(1).getId() == Block.STILL_WATER))
                && down.isSolid() && down.getId() != Block.MAGMA) {
            if (!waterAtLayer1) {
                this.getLevel().setBlock(this, 1, block, true, false);
            }
            this.getLevel().setBlock(this, 0, this, true, true);
            return true;
        }
        return false;
    }
    
    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_NORMAL) {
            int blockLayer1 = getLevelBlockAtLayer(1).getId();
            if (blockLayer1 != Block.STILL_WATER) {
                this.getLevel().useBreakOn(this);
                return Level.BLOCK_UPDATE_NORMAL;
            }
            
            Block down = down();
            int damage = getDamage();
            if (damage == 0 || damage == 2) {
                if (!down.isSolid() || down.getId() == Block.MAGMA) {
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
    public boolean onActivate(Item item, Player player) {
        if (getDamage() == 0 && item.getId() == Item.DYE && item.getDamage() == DyeColor.WHITE.getDyeData()) {
            Block up = this.up();
    
            if (up.getId() == WATER || up.getId() == STILL_WATER) {
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
            return new Item[0];
        }
    }
    
    @Override
    public boolean canBeReplaced() {
        return true;
    }
    
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
