package cn.nukkit.command.defaults.vanilla;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.command.defaults.VanillaCommand;
import cn.nukkit.level.Level;
import cn.nukkit.level.gamerule.GameRule;
import cn.nukkit.level.gamerule.GameRuleMap;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.registry.GameRuleRegistry;

import java.util.Arrays;
import java.util.StringJoiner;

import static cn.nukkit.command.args.builder.OptionalArgumentBuilder.optionalArg;

public class GameruleCommand extends VanillaCommand {
    private static final GameRuleRegistry registry = GameRuleRegistry.get();

    public GameruleCommand(String name) {
        super(name, "commands.gamerule.description", "/gamerule <gamerule> [value]");
        this.setPermission("nukkit.command.gamerule");

        registerOverload()
                .then(optionalArg("rule", registry.getRuleNames().toArray(new String[0])))
                .then(optionalArg("value", CommandParamType.STRING));
    }

    @Override
    public boolean execute(CommandSender sender, String aliasUsed, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        Level level = sender instanceof Player ? ((Player) sender).getLevel() : sender.getServer().getDefaultLevel();
        GameRuleMap rules = level.getGameRules();

        if(args.length == 0) {
            sender.sendMessage(String.join(", ", registry.getRuleNames()));
            return true;
        }

        if(args.length == 1) {
            GameRule gameRule = registry.fromString(args[0]);
            if(gameRule == null || !rules.contains(gameRule)) {
                sender.sendMessage(new TranslationContainer("commands.gamerule.norule", args[0]));
                return true;
            }
            // noinspection unchecked
            sender.sendMessage(gameRule.getName() + " = " + rules.get(gameRule).toString());
            return true;
        }

        GameRule gameRule = registry.fromString(args[0]);

        if(gameRule == null) {
            sender.sendMessage(new TranslationContainer("commands.gamerule.norule", args[0]));
            return true;
        }

        try {
            // noinspection unchecked
            rules.put(gameRule, gameRule.parse(args[1]));
            sender.sendMessage(new TranslationContainer("commands.gamerule.success", gameRule.getName(), args[1]));
        } catch (NumberFormatException ex) {
            sender.sendMessage(new TranslationContainer("commands.gamerule.type.invalid", gameRule.getName()));
        }
        return true;
    }
}
