package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.Anvil2LevelDBConverter;
import cn.nukkit.level.format.leveldb.LevelDBProvider;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConvertCommand extends VanillaCommand {

    private static final Set<String> conversionInProgress = ConcurrentHashMap.newKeySet();

    public ConvertCommand(String name) {
        super(name, "%nukkit.command.world.convert.description", "%nukkit.command.world.convert.usage");
        this.setPermission("nukkit.command.world.convert");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("world", CommandParamType.STRING, false)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (sender instanceof Player) {
            sender.sendMessage("§cThis command can be used only via console");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        String worldName = String.join(" ", args);
        Level level = Server.getInstance().getLevelByName(worldName);
        if (level == null) {
            sender.sendMessage("Unknown level: " + worldName);
            return true;
        }

        if (level.getProvider() instanceof LevelDBProvider) {
            sender.sendMessage(worldName + " is already in LevelDB format");
            return true;
        }

        if (!level.getPlayers().isEmpty()) {
            sender.sendMessage(worldName + " has players in it! Make sure the world is not used while it's being converted");
            return true;
        }

        if (conversionInProgress.contains(worldName)) {
            sender.sendMessage(worldName + " is already being converted");
            return true;
        }

        conversionInProgress.add(worldName);

        Anvil2LevelDBConverter converter = new Anvil2LevelDBConverter(level);
        converter.convert().whenComplete((ignore, error) -> {
           if (error != null) {
               sender.sendMessage("Error during conversion!");
               Server.getInstance().getLogger().logException(error);
           }

           conversionInProgress.remove(worldName);
        });
        return true;
    }
}
