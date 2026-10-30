package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.level.Level;
import cn.nukkit.level.format.generic.Anvil2LevelDBConverter;
import cn.nukkit.level.format.leveldb.LevelDBProvider;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConvertCommand extends VanillaCommand {

    private static final Set<String> CONVERSION_IN_PROGRESS = ConcurrentHashMap.newKeySet();

    public ConvertCommand(String name) {
        super(name, "%nukkit.command.world.convert.description", "%nukkit.command.world.convert.usage");
        this.setPermission("nukkit.command.world.convert");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                CommandParameter.newType("world", CommandParamType.STRING)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (sender instanceof Player) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%nukkit.command.world.convert.consoleOnly"));
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        String worldName = String.join(" ", args);
        Level level = Server.getInstance().getLevelByName(worldName);
        if (level == null) {
            sender.sendMessage(new TranslationContainer("nukkit.command.world.convert.unknownLevel", worldName));
            return true;
        }

        if (level.getProvider() instanceof LevelDBProvider) {
            sender.sendMessage(new TranslationContainer("nukkit.command.world.convert.alreadyLevelDB", worldName));
            return true;
        }

        if (!level.getPlayers().isEmpty()) {
            sender.sendMessage(new TranslationContainer("nukkit.command.world.convert.hasPlayers", worldName));
            return true;
        }

        if (CONVERSION_IN_PROGRESS.contains(worldName)) {
            sender.sendMessage(new TranslationContainer("nukkit.command.world.convert.inProgress", worldName));
            return true;
        }

        CONVERSION_IN_PROGRESS.add(worldName);

        Anvil2LevelDBConverter converter = new Anvil2LevelDBConverter(level);
        converter.convert().whenComplete((ignore, error) -> {
           if (error != null) {
               sender.sendMessage(new TranslationContainer("nukkit.command.world.convert.error"));
               Server.getInstance().getLogger().logException(error);
           }

           CONVERSION_IN_PROGRESS.remove(worldName);
        });
        return true;
    }
}
