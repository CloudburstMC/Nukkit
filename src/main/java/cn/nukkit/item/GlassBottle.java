package cn.nukkit.item;


import cn.nukkit.block.Block;
import cn.nukkit.level.Level;
import cn.nukkit.Player;

public class GlassBottle extends Item{

    public GlassBottle(){
        this(0, 1);
    }

    public GlassBottle(int meta){
        this(meta, 1);
    }

    public GlassBottle(int meta, int count){
        super(GLASS_BOTTLE, meta, count, "Glass Bottle");
    }

    @Override
    public boolean onActivate(Level level, Player player, Block block, Block target, int face, double fx, double fy, double fz){
        boolean isWater = false;

        if(target.getId() == WATER || target.getId() == STILL_WATER){
            isWater = true;
        }

        if(isWater){
            if(this.getCount() > 1){
                if(player.getInventory().canAddItem(Item.get(Item.POTION))){
                    this.setCount(this.getCount() - 1);
                    player.getInventory().addItem(Item.get(Item.POTION));
                    return true;
                }
            }else{
                player.getInventory().setItemInHand(Item.get(Item.POTION));
            }
        }

        return false;
    }
}