package cn.nukkit.api.event.player;

import cn.nukkit.api.Player;
import cn.nukkit.api.event.Cancellable;
import cn.nukkit.api.event.entity.EntityDeathEvent;
import cn.nukkit.api.item.ItemStack;
import cn.nukkit.api.message.GenericMessage;
import cn.nukkit.api.message.Message;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerDeathEvent extends EntityDeathEvent implements Cancellable {

    private boolean cancelled;

    private Message deathMessage;
    private boolean keepInventory = false;
    private boolean keepExperience = false;
    private int experience;

    public PlayerDeathEvent(Player player, ItemStack[] drops, Message deathMessage, int experience) {
        super(player, drops);

        this.deathMessage = deathMessage;
        this.experience = experience;
    }

    public PlayerDeathEvent(Player player, ItemStack[] drops, String deathMessage, int experience) {
        this(player, drops, new GenericMessage(deathMessage), experience);
    }

    @Override
    public Player getEntity() {
        return (Player) super.getEntity();
    }


}
