package cn.nukkit.command.defaults;

import cn.nukkit.command.BaseCommand;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.locale.TranslationContainer;
import cn.nukkit.player.Player;
import cn.nukkit.utils.TextFormat;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import lombok.extern.log4j.Log4j2;

import static cn.nukkit.block.BlockIds.AIR;
import static cn.nukkit.command.args.ItemArgument.getItem;
import static cn.nukkit.command.args.ItemArgument.item;
import static cn.nukkit.command.args.PlayerArgument.getPlayer;
import static cn.nukkit.command.args.PlayerArgument.player;
import static com.mojang.brigadier.arguments.IntegerArgumentType.getInteger;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

@Log4j2
public class GiveCommand extends BaseCommand {

    public GiveCommand(CommandDispatcher<CommandSource> dispatcher) {
        super("give", "%nukkit.command.give.description");

        dispatcher.register(literal("give")
                .requires(requirePermission("nukkit.command.give"))
                .then(argument("player", player())
                        .then(argument("itemName", item()).executes(ctx -> run(ctx, 1, 0))
                                .then(argument("amount", integer()).executes(ctx -> run(ctx, getInteger(ctx, "amount"), 0))
                                        .then(argument("data", integer()).executes(ctx -> run(ctx, getInteger(ctx, "amount"), getInteger(ctx, "data"))))))));
    }

    public int run(CommandContext<CommandSource> context, int amount, int data) {
        CommandSource source = context.getSource();
        Player target = getPlayer(context, "player");
        Item item = getItem(context, "itemName");

        item.setCount(amount);
        item.setMeta(data);

        if(item.getId() == AIR) {
            source.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.give.item.notFound", item.getName()));
            return 1;
        }
        target.getInventory().addItem(item.clone());

        sendAdminMessage(source, new TranslationContainer("%commands.give.success",
                item.getName(), item.getCount(), target.getName()));
        return 1;
    }
}
