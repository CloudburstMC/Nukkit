package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.utils.TextFormat;

/**
 * Created on 2015/11/13 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class GamemodeCommand extends VanillaCommand {

    public GamemodeCommand(String name) {
        super(name, "%nukkit.command.gamemode.description", "%commands.gamemode.usage");
        this.setPermission("nukkit.command.gamemode");
    }

    private static class GamemodeCommandRequest{
        int gamemode;
        Player player;

        enum Valid{VALID_SELF, VALID_OTHERS, INCORRECT_PARAM, INCORRECT_GAMEMODE, PLAYER_NOT_FOUND, PLAYER_EXECUTIONS_ONLY}
        Valid valid;

        public int getGamemode() {
            return gamemode;
        }

        public Player getPlayer() {
            return player;
        }

        public Valid getValid() {
            return valid;
        }

        private GamemodeCommandRequest(int gamemode, Player player, Valid valid){
            this.gamemode = gamemode;
            this.player = player;
            this.valid = valid;
        }

        static GamemodeCommandRequest of(String[] args, CommandSender sender){
            if(args.length < 1 || args.length > 2){
                return new GamemodeCommandRequest(-1, null, Valid.INCORRECT_PARAM);
            }
            int gamemode = Server.getGamemodeFromString(args[0]);
            if(gamemode == -1){
                return new GamemodeCommandRequest(-1, null, Valid.INCORRECT_GAMEMODE);
            }
            if(args.length == 1){
                if(!(sender instanceof Player))
                    return new GamemodeCommandRequest(gamemode, null, Valid.PLAYER_EXECUTIONS_ONLY);
                return new GamemodeCommandRequest(gamemode, (Player)sender, Valid.VALID_SELF);
            }else{ //args.length == 2
                Player player = sender.getServer().getPlayer(args[1]);
                if(player == null)
                    return new GamemodeCommandRequest(gamemode, null, Valid.PLAYER_NOT_FOUND);
                return new GamemodeCommandRequest(gamemode, player, Valid.VALID_OTHERS);
            }
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if(!this.testPermission(sender)){
            return true;
        }

        GamemodeCommandRequest request = GamemodeCommandRequest.of(args, sender);

        switch (request.valid){
            case VALID_SELF:
            case VALID_OTHERS:
                request.getPlayer().setGamemode(request.getGamemode());
                if(request.getPlayer().getGamemode() == request.getGamemode()) {
                    String gamemodeString = Server.getGamemodeString(request.getGamemode());
                    TranslationContainer t = (request.getValid() == GamemodeCommandRequest.Valid.VALID_SELF)?
                            new TranslationContainer("commands.gamemode.success.self",gamemodeString):
                            new TranslationContainer("commands.gamemode.success.other", new String[]{request.getPlayer().getName(), gamemodeString});
                    Command.broadcastCommandMessage(sender, t);
                }else
                    sender.sendMessage("Game mode change for " + request.getPlayer().getName() + " failed!");
                break;
            case PLAYER_NOT_FOUND:
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
                break;
            case INCORRECT_PARAM:
            case PLAYER_EXECUTIONS_ONLY:
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                break;
            case INCORRECT_GAMEMODE:
                sender.sendMessage("Unknown game mode");
                break;
        }
        return true;
    }
}
