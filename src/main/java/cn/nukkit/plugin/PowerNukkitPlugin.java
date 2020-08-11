package cn.nukkit.plugin;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.api.PowerNukkitOnly;
import cn.nukkit.api.Since;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.inventory.InventoryOpenEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.event.server.DataPacketSendEvent;
import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.network.protocol.ContainerClosePacket;
import com.google.common.collect.MapMaker;

import java.util.Map;
import java.util.UUID;

@PowerNukkitOnly
@Since("1.3.0.0-PN")
public class PowerNukkitPlugin extends PluginBase {
    private static final PowerNukkitPlugin INSTANCE = new PowerNukkitPlugin();
    
    @PowerNukkitOnly
    @Since("1.3.0.0-PN")
    public static PowerNukkitPlugin getInstance() {
        return INSTANCE;
    }

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(new HotFixes(), this);
    }

    /**
     * These are temporary workaround to issues with great impact that wasn't fixed the right way yet.
     */
    private class HotFixes implements Listener {
        private final Map<UUID, Boolean> cancelNextSend = new MapMaker().weakKeys().makeMap();

        /**
         * Hotfix from MR.CLEAN: https://discordapp.com/channels/728280425255927879/728284727748067455/734449925596774410
         * PowerNukkit#365, PowerNukkit#339  
         */
        @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
        public void onFixCrash(DataPacketSendEvent e) {
            if(e.getPacket().pid() == ContainerClosePacket.NETWORK_ID && cancelNextSend.getOrDefault(e.getPlayer().getUniqueId(), true)) {
                e.setCancelled();
            }
        }

        /**
         * Hotfix from MR.CLEAN: https://discordapp.com/channels/728280425255927879/728284727748067455/734449925596774410
         * PowerNukkit#365, PowerNukkit#339  
         */
        @EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
        public void onDataPacketReceive(DataPacketReceiveEvent e) {
            if(e.getPacket().pid() == ContainerClosePacket.NETWORK_ID){
                cancelNextSend.put(e.getPlayer().getUniqueId(), false);
                e.getPlayer().dataPacket(e.getPacket());
                cancelNextSend.put(e.getPlayer().getUniqueId(), true);
            }
        }

        /**
         * Hotfix from MR.CLEAN: https://discordapp.com/channels/728280425255927879/728284727748067455/734488813774307329
         */
        @EventHandler
        public void onOpenEC(InventoryOpenEvent e) {
            if (!e.isCancelled() && e.getInventory() instanceof PlayerEnderChestInventory) {
                final Player p = e.getPlayer();

                Server.getInstance().getScheduler().scheduleDelayedTask(PowerNukkitPlugin.this, () -> {
                    if (p != null && p.isOnline() && p.getTopWindow().isPresent() && p.getTopWindow().get() instanceof PlayerEnderChestInventory) {
                        PlayerEnderChestInventory ec = (PlayerEnderChestInventory) p.getTopWindow().get();
                        ec.sendContents(p);
                    }
                }, 3);
            }
        }
    }
}
