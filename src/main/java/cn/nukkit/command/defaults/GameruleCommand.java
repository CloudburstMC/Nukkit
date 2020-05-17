package cn.nukkit.command.defaults;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandData;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.level.gamerule.GameRule;
import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.registry.GameRuleRegistry;

import java.util.Arrays;
import java.util.StringJoiner;

public class GameruleCommand extends Command {
    private static final GameRuleRegistry registry = GameRuleRegistry.get();

    public GameruleCommand() {
        super("gamerule", CommandData.builder("gamerule")
                .setDescription("commands.gamerule.description")
                .setUsageMessage("/gamerule <gamerule> [value]")
                .setPermissions("nukkit.command.gamerule")
                .setParameters(new CommandParameter[]{
                        new CommandParameter("gamerule", true, registry.getRuleNames().toArray(new String[0])),
                        new CommandParameter("value", CommandParamType.STRING, true)
                })
                .build());
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (!sender.isPlayer()) {
            sender.sendMessage(new TranslationContainer("commands.locate.fail.noplayer"));
            return true;
        }
        GameRuleMap rules = ((Player) sender).getLevel().getGameRules();

        switch (args.length) {
            case 0:
                StringJoiner rulesJoiner = new StringJoiner(", ");
                for (String rule : registry.getRuleNames()) {
                    rulesJoiner.add(rule.toLowerCase());
                }
                sender.sendMessage(rulesJoiner.toString());
                return true;
            case 1:
                GameRule gameRule = registry.fromString(args[0]);
                if (gameRule == null || !rules.contains(gameRule)) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/gamerule", args[0]));
                    return true;
                }

                sender.sendMessage(gameRule.getName() + " = " + rules.get(gameRule).toString());
                return true;
            default:
                gameRule = registry.fromString(args[0]);

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
