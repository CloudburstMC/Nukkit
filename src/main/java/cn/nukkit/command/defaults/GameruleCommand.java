package cn.nukkit.command.defaults;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.gamerule.GameRule;
import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.player.Player;
import cn.nukkit.registry.GameRuleRegistry;

import java.util.Arrays;
import java.util.StringJoiner;

public class GameruleCommand extends VanillaCommand {
    private final GameRuleRegistry registry;

    public GameruleCommand(String name, Server server) {
        super(name, "%nukkit.command.gamerule.description", "%nukkit.command.gamerule.usage");
        this.registry = server.getGameRuleRegistry();
        this.setPermission("nukkit.command.gamerule");
        this.commandParameters.clear();
        this.commandParameters.put("byString", new CommandParameter[]{
                new CommandParameter("gamerule", true, this.registry.getRules().toArray(new String[0])),
                new CommandParameter("value", CommandParamType.STRING, true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (!sender.isPlayer()) {
            sender.sendMessage(new TranslationContainer("%commands.generic.ingame"));
            return true;
        }
        GameRuleMap rules = ((Player) sender).getLevel().getGameRules();

        switch (args.length) {
            case 0:
                StringJoiner rulesJoiner = new StringJoiner(", ");
                for (String rule : this.registry.getRules()) {
                    rulesJoiner.add(rule.toLowerCase());
                }
                sender.sendMessage(rulesJoiner.toString());
                return true;
            case 1:
                GameRule gameRule = this.registry.fromString(args[0]);
                if (gameRule == null || !rules.contains(gameRule)) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/gamerule", args[0]));
                    return true;
                }

                sender.sendMessage(gameRule.getName() + " = " + rules.get(gameRule).toString());
                return true;
            default:
                gameRule = this.registry.fromString(args[0]);

                if (gameRule == null) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax",
                            "/gamerule ", args[0], " " + String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                    return true;
                }

                try {
                    rules.put(gameRule, gameRule.parse(args[1]));
                    sender.sendMessage(new TranslationContainer("commands.gamerule.success", gameRule.getName(), args[1]));
                } catch (NumberFormatException e) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/gamerule "  + args[0] + " ", args[1], " " + String.join(" ", Arrays.copyOfRange(args, 2, args.length))));
                }
                return true;
        }
    }
}
