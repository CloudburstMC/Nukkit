package cn.nukkit.inventory.transaction.action;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAnvil;
import cn.nukkit.block.BlockID;
import cn.nukkit.event.block.AnvilDamageEvent;
import cn.nukkit.inventory.AnvilInventory;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import lombok.ToString;

@ToString(callSuper = true)
public class DamageAnvilAction extends InventoryAction {
    
    private final AnvilInventory anvil;
    private boolean shouldDamage;
    private CraftingTransaction transaction;
    
    public DamageAnvilAction(AnvilInventory anvil, boolean shouldDamage, CraftingTransaction transaction) {
        super(Item.get(0), Item.get(0));
        this.anvil = anvil;
        this.shouldDamage = shouldDamage;
        this.transaction = transaction;
    }
    
    @Override
    public boolean isValid(Player source) {
        return true;
    }
    
    @Override
    public boolean execute(Player source) {
        Block levelBlock = anvil.getHolder().getLevelBlock();
        if (!(levelBlock instanceof BlockAnvil)) {
            return false;
        }
        Block newState = levelBlock.clone();
        int damage = (newState.getDamage() >> 2 & 0x3) + 1;
        if (damage >= 3) {
            newState = Block.get(0, 0, newState, newState.layer);
        } else {
            newState.setDamage(newState.getDamage() & (Block.DATA_MASK ^ 0b1100) | (damage << 2));
        }
        AnvilDamageEvent ev = new AnvilDamageEvent(levelBlock, newState, source, transaction, AnvilDamageEvent.Cause.USE);
        ev.setCancelled(!shouldDamage);
        source.getServer().getPluginManager().callEvent(ev);
        if (ev.isCancelled()) {
            levelBlock.getLevel().addSound(levelBlock, Sound.RANDOM_ANVIL_USE);
            return true;
        } else {
            if (newState.getId() == BlockID.AIR) {
                levelBlock.getLevel().addSound(levelBlock, Sound.RANDOM_ANVIL_BREAK);
            } else {
                levelBlock.getLevel().addSound(levelBlock, Sound.RANDOM_ANVIL_USE);
            }
            return levelBlock.getLevel().setBlock(levelBlock, newState, true, true);
        }
    }
    
    @Override
    public void onExecuteSuccess(Player source) {
    
    }
    
    @Override
    public void onExecuteFail(Player source) {
    
    }
}
