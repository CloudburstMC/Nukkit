package cn.nukkit.event.block;

import cn.nukkit.Player;
import cn.nukkit.api.DeprecationDetails;
import cn.nukkit.api.PowerNukkitDifference;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.inventory.transaction.CraftingTransaction;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@PowerNukkitDifference(info = "Extends BlockFadeEvent instead of BlockEvent only in PowerNukkit")
@Deprecated @DeprecationDetails(since = "1.4.0.0-PN",
        reason = "This is only a warning, this event will change in 1.5.0.0-PN, " +
                "it will no longer extend BlockFadeEvent and the cause enum will be renamed!",
        toBeRemovedAt = "The class will have a breaking change in 1.5.0.0-PN"
)
@Since("1.1.1.0-PN")
public class AnvilDamageEvent extends BlockFadeEvent implements Cancellable {
    
    private static final HandlerList handlers = new HandlerList();

    @Since("1.1.1.0-PN")
    public static HandlerList getHandlers() {
        return handlers;
    }
    
    private final Player player;
    private final CraftingTransaction transaction;
    private final DamageCause cause;

    @Since("1.4.0.0-PN")
    public AnvilDamageEvent(Block block, int oldDamage, int newDamage, DamageCause cause, Player player) {
        this(adjustBlock(block, oldDamage), adjustBlock(block, newDamage), player, null, cause);
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public AnvilDamageEvent(Block block, Block newState, Player player, @Nullable CraftingTransaction transaction, DamageCause cause) {
        super(block, newState);
        this.player = player;
        this.transaction = transaction;
        this.cause = cause;
    }
    
    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public AnvilDamageEvent(Block block, Block newState, Player player, CraftingTransaction transaction, Cause cause) {
        this(block, newState, player, transaction, convert(cause));
    }


    @Since("1.1.1.0-PN")
    public Player getPlayer() {
        return player;
    }


    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    public CraftingTransaction getTransaction() {
        return transaction;
    }
    
    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public DamageCause getDamageCause() {
        return cause;
    }


    @Since("1.4.0.0-PN")
    public int getOldDamage() {
        return getBlock().getDamage();
    }

    @Since("1.4.0.0-PN")
    public int getNewDamage() {
        return getNewState().getDamage();
    }

    @Since("1.4.0.0-PN")
    public void setNewDamage(int newDamage) {
        getNewState().setDamage(newDamage);
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    @Deprecated @DeprecationDetails(
            since = "1.4.0.0-PN", by = "PowerNukkit",
            reason = "NukkitX added the class and made getCause() return an enum with a different name.",
            replaceWith = "getDamageCause()",
            toBeRemovedAt = "1.6.0.0-PN"
    )
    public Cause getCause() {
        return convert(cause);
    }

    @PowerNukkitOnly
    @Since("1.1.1.0-PN")
    @Deprecated @DeprecationDetails(
            since = "1.4.0.0-PN", by = "PowerNukkit",
            reason = "NukkitX added the class but with a different enum for the damage cause",
            replaceWith = "DamageCause",
            toBeRemovedAt = "1.6.0.0-PN"
    )
    @RequiredArgsConstructor
    public enum Cause {
        @PowerNukkitOnly
        @Since("1.1.1.0-PN")
        @Deprecated @DeprecationDetails(
                since = "1.4.0.0-PN", by = "PowerNukkit",
                reason = "NukkitX added the class but with a different enum for the damage cause",
                replaceWith = "DamageCause.USE",
                toBeRemovedAt = "1.6.0.0-PN"
        )
        USE,

        @PowerNukkitOnly
        @Since("1.1.1.0-PN")
        @Deprecated @DeprecationDetails(
                since = "1.4.0.0-PN", by = "PowerNukkit",
                reason = "NukkitX added the class but with a different enum for the damage cause",
                replaceWith = "DamageCause.FALL",
                toBeRemovedAt = "1.6.0.0-PN"
        )
        IMPACT;
        
        @PowerNukkitOnly
        @Since("1.4.0.0-PN")
        @Deprecated @DeprecationDetails(
                since = "1.4.0.0-PN", by = "PowerNukkit",
                reason = "This is method is only a temporary helper, it will also be removed in future",
                replaceWith = "Direct usage of DamageCause",
                toBeRemovedAt = "1.6.0.0-PN"
        )
        @Nonnull
        public DamageCause getDamageCause() {
            return DamageCause.valueOf(name());
        }
    }

    @Since("1.4.0.0-PN")
    public enum DamageCause {
        @Since("1.4.0.0-PN") USE,
        @Since("1.4.0.0-PN") FALL
    }

    private static Block adjustBlock(Block block, int damage) {
        Block adjusted = block.clone();
        adjusted.setDamage(damage);
        return adjusted;
    }

    private static DamageCause convert(Cause cause) {
        switch (cause) {
            case USE:
                return DamageCause.USE;
            case IMPACT:
                return DamageCause.FALL;
            default:
                return null;
        }
    }

    private static Cause convert(DamageCause cause) {
        switch (cause) {
            case USE:
                return Cause.USE;
            case FALL:
                return Cause.IMPACT;
            default:
                return null;
        }
    }
}
