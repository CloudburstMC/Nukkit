package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockID;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;

import java.util.Arrays;

public class SetBlockCommand extends VanillaCommand {
    public SetBlockCommand(String name) {
        super(name, "%nukkit.command.setblock.description", "%nukkit.command.setblock.usage");
        this.setPermission("nukkit.command.setblock");
        this.commandParameters.clear();
        this.commandParameters.put("default", new CommandParameter[]{
                new CommandParameter("position", CommandParamType.POSITION, false),
                new CommandParameter("tileName", false, Arrays.stream(BlockID.class.getDeclaredFields()).map(f-> f.getName().toLowerCase()).toArray(String[]::new)),
                new CommandParameter("tileData", CommandParamType.INT, true),
                new CommandParameter("oldBlockHandling", true, new String[]{"destroy", "keep", "replace"})
        });
    }
    
    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(new TranslationContainer("commands.setblock.outOfWorld"));
            return true;
        }
        Player player = (Player) sender;

        double x;
        double y;
        double z;
        int data = 0;
        try {
            x = parseTilde(args[0], player.x);
            y = parseTilde(args[1], player.y);
            z = parseTilde(args[2], player.z);

            if (args.length > 4) {
                data = Integer.parseInt(args[4]);
            }
        } catch (NumberFormatException|IndexOutOfBoundsException ignored) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return true;
        }

        String oldBlockHandling = "replace";
        if (args.length > 5) {
            oldBlockHandling = args[5].toLowerCase();
            switch (oldBlockHandling) {
                case "destroy":
                case "keep":
                case "replace":
                    break;
                default:
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return true;
            }
        }

        Block block;
        try {
            int blockId = Integer.parseInt(args[3]);
            block = Block.get(blockId, data);
        } catch (NullPointerException|NumberFormatException|IndexOutOfBoundsException ignored) {
            try {
                int blockId = BlockID.class.getField(args[3].toUpperCase()).getInt(null);
                block = Block.get(blockId, data);
            } catch (NullPointerException|IndexOutOfBoundsException|ReflectiveOperationException ignored2) {
                sender.sendMessage(new TranslationContainer("commands.setblock.notFound", args[3]));
                return true;
            }
        }

        if (y < 0 || y > 255) {
            sender.sendMessage(new TranslationContainer("commands.setblock.outOfWorld"));
            return true;
        }

        Level level = player.getLevel();

        Position position = new Position(x, y, z, player.getLevel());
        Block current = level.getBlock(position);
        if (current.getId() != Block.AIR) {
            switch (oldBlockHandling) {
                case "destroy":
                    level.useBreakOn(position, null, Item.get(Item.AIR), player, true, true);
                    current = level.getBlock(position);
                    break;
                case "keep":
                    sender.sendMessage(new TranslationContainer("commands.setblock.noChange"));
                    return true;
            }
        }

        if (current.getId() == block.getId() && current.getDamage() == block.getDamage()) {
            sender.sendMessage(new TranslationContainer("commands.setblock.noChange"));
            return true;
        }

        Item item = block.toItem();
        block.position(position);
        if(block.place(item, block, block.down(), BlockFace.UP, 0.5, 0.5, 0.5, player)) {
            if (args.length > 4) {
                level.setBlockDataAt((int) x, (int) y, (int) z, data);
            }
        //if (level.setBlock(position, block, true, true)) {
            sender.sendMessage(new TranslationContainer("commands.setblock.success"));
        } else {
            sender.sendMessage(new TranslationContainer("commands.setblock.failed"));
        }
        return true;
    }
}
