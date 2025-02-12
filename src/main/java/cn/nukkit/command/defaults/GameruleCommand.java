package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandEnum;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.GameRules;

import java.util.*;

public class GameruleCommand extends VanillaCommand {

    public GameruleCommand(String name) {
        super(name, "%nukkit.command.gamerule.description", "%commands.gamerule.usage");
        this.setPermission("nukkit.command.gamerule");
        this.commandParameters.clear();

        GameRules rules = GameRules.getDefault();
        List<String> boolGameRules = new ArrayList<>();
        List<String> intGameRules = new ArrayList<>();
        List<String> floatGameRules = new ArrayList<>();
        List<String> unknownGameRules = new ArrayList<>();

        rules.getGameRules().forEach((rule, value) -> {
            switch (value.getType()) {
                case BOOLEAN:
                    boolGameRules.add(rule.getName().toLowerCase(Locale.ROOT));
                    break;
                case INTEGER:
                    intGameRules.add(rule.getName().toLowerCase(Locale.ROOT));
                    break;
                case FLOAT:
                    floatGameRules.add(rule.getName().toLowerCase(Locale.ROOT));
                    break;
                case UNKNOWN:
                default:
                    unknownGameRules.add(rule.getName().toLowerCase(Locale.ROOT));
                    break;
            }
        });

        if (!boolGameRules.isEmpty()) {
            this.commandParameters.put("boolGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("BoolGameRule", boolGameRules)),
                    CommandParameter.newEnum("value", true, CommandEnum.ENUM_BOOLEAN)
            });
        }
        if (!intGameRules.isEmpty()) {
            this.commandParameters.put("intGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("IntGameRule", intGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.INT)
            });
        }
        if (!floatGameRules.isEmpty()) {
            this.commandParameters.put("floatGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("FloatGameRule", floatGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.FLOAT)
            });
        }
        if (!unknownGameRules.isEmpty()) {
            this.commandParameters.put("unknownGameRules", new CommandParameter[]{
                    CommandParameter.newEnum("rule", new CommandEnum("UnknownGameRule", unknownGameRules)),
                    CommandParameter.newType("value", true, CommandParamType.STRING)
            });
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (!sender.isPlayer()) {
            sender.sendMessage(new TranslationContainer("commands.generic.ingame"));
            return true;
        }
        GameRules rules = ((Player) sender).getLevel().getGameRules();

        switch (args.length) {
            case 0:
                int splitCounter = 0;
                StringJoiner rulesJoiner = new StringJoiner("\n");
                for (GameRule rule: rules.getRules()) {
                    rulesJoiner.add(rule.getName().toLowerCase(Locale.ROOT) + " = " + rules.getString(rule));

                    // 1.21 disconnects on too long message
                    // TODO: do this the same way as on vanilla
                    if (splitCounter++ > 15) {
                        rulesJoiner.add("");
                        sender.sendMessage(rulesJoiner.toString());
                        splitCounter = 0;
                        rulesJoiner = new StringJoiner("\n");
                    }
                }
                sender.sendMessage(rulesJoiner.toString());
                return true;
            case 1:
                Optional<GameRule> gameRule = GameRule.parseString(args[0]);
                if (!gameRule.isPresent() || !rules.hasRule(gameRule.get())) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/gamerule", args[0]));
                    return true;
                }

                sender.sendMessage(gameRule.get().getName().toLowerCase(Locale.ROOT) + " = " + rules.getString(gameRule.get()));
                return true;
            default:
                Optional<GameRule> optionalRule = GameRule.parseString(args[0]);

                if (!optionalRule.isPresent()) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/gamerule ", args[0], ' ' + String.join(" ", Arrays.copyOfRange(args, 1, args.length))));
                    return true;
                }

                try {
                    rules.setGameRules(optionalRule.get(), args[1]);
                    sender.sendMessage(new TranslationContainer("commands.gamerule.success", optionalRule.get().getName().toLowerCase(Locale.ROOT), args[1]));
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(new TranslationContainer("commands.generic.syntax", "/gamerule "  + args[0] + ' ', args[1], ' ' + String.join(" ", Arrays.copyOfRange(args, 2, args.length))));
                }
                return true;
        }
    }
}