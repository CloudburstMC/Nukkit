package cn.nukkit.event.blockstate;

import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.block.Block;
import cn.nukkit.blockstate.BlockStateRepair;
import cn.nukkit.event.HandlerList;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author joserobjr
 */
@PowerNukkitOnly
@Since("1.4.0.0-PN")
public class BlockStateRepairFinishEvent extends BlockStateRepairEvent {
    @Nonnull
    private final List<BlockStateRepair> allRepairs;
    
    @Nonnull
    private Block result;

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public BlockStateRepairFinishEvent(@Nonnull List<BlockStateRepair> allRepairs, @Nonnull Block result) {
        super(allRepairs.get(allRepairs.size() - 1));
        this.allRepairs = Collections.unmodifiableList(new ArrayList<>(allRepairs));
        this.result = result;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public List<BlockStateRepair> getAllRepairs() {
        return allRepairs;
    }

    @Nonnull
    public Block getResult() {
        return result;
    }

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    public void setResult(@Nonnull Block result) {
        this.result = Preconditions.checkNotNull(result);
    }

    @Nonnull
    private static final HandlerList handlers = new HandlerList();

    @PowerNukkitOnly
    @Since("1.4.0.0-PN")
    @Nonnull
    public static HandlerList getHandlers() {
        return handlers;
    }
}
