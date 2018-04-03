package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;

import java.util.Arrays;
import java.util.Optional;
import java.util.StringJoiner;

/*
TODO: Add i18n
 */
public class GameruleCommand extends VanillaCommand {

    public GameruleCommand(String name) {
        super(name, "Sets or queries a game rule value.", "/gamerule <rule> [value]");
        this.setPermission("nukkit.command.gamerule");
        this.commandParameters.clear();
        this.commandParameters.put("byString", new CommandParameter[]{
                new CommandParameter("gamerule", true , GameRule.getNames()),
                new CommandParameter("value", CommandParameter.ARG_TYPE_STRING, true)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (!sender.isPlayer()) {
            sender.sendMessage("This command can only be executed by players");
            return true;
        }
        GameRules rules = ((Player) sender).getLevel().getGameRules();

        switch (args.length) {
            case 0:
                StringJoiner rulesJoiner = new StringJoiner(", ");
                for (GameRule rule: rules.getRules()) {
                    rulesJoiner.add(rule.getName().toLowerCase());
                }
                sender.sendMessage(rulesJoiner.toString());
                return true;
            case 1:
                Optional<GameRule> gameRule = GameRule.parseString(args[0]);
                if (!gameRule.isPresent() || !rules.hasRule(gameRule.get())) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/gamerule", args[0]));
                    return true;
                }

                sender.sendMessage(gameRule.get().getName() + " = " + rules.getString(gameRule.get()));
                return true;
            default:
                Optional<GameRule> optionalRule = GameRule.parseString(args[0]);

                if (!optionalRule.isPresent()) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax",
                            "/gamerule ", args[0], " " + String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                    return true;
                }

                try {
                    rules.setGameRules(optionalRule.get(), args[1]);
                    sender.sendMessage(new TranslationContainer("commands.gamerule.success", optionalRule.get().getName(), args[1]));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/gamerule "  + args[0] + " ", args[1], " " + String.join(" ", Arrays.copyOfRange(args, 2, args.length))));
                }
                return true;
        }
    }
}
