package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.utils.TextFormat;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Created on 2015/11/19 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class TimeCommand extends VanillaCommand {

    public TimeCommand(String name) {
        super(name, "%nukkit.command.time.description", "%nukkit.command.time.usage");
        this.setPermission(
                "nukkit.command.time.add;" +
                "nukkit.command.time.set;" +
                "nukkit.command.time.start;" +
                "nukkit.command.time.stop;" +
                "nukkit.command.time.query"
        );
    }
    
    private static class TimeCommandRequest{

        private enum RequestType{START, STOP, QUERY, SET, ADD, INVALID}

        private RequestType requestType = RequestType.INVALID;

        RequestType getRequestType() {
            return requestType;
        }

        private int newTime = 0;

        int getNewTime() {
            return newTime;
        }

        private TimeCommandRequest(RequestType requestType){
            this(requestType, 0);
        }

        private TimeCommandRequest(RequestType requestType, int newTime){
            this.requestType = requestType;
            this.newTime = newTime;
        }

        boolean testPerm(CommandSender sender){
            String permString;
            switch (getRequestType()){
                case START:
                    permString = "nukkit.command.time.start";
                    break;
                case STOP:
                    permString = "nukkit.command.time.stop";
                    break;
                case QUERY:
                    permString = "nukkit.command.time.query";
                    break;
                case ADD:
                    permString = "nukkit.command.time.add";
                    break;
                case SET:
                    permString = "nukkit.command.time.set";
                    break;
                case INVALID:
                default:
                    return true;
            }
            return sender.hasPermission(permString);
        }

        static TimeCommandRequest of(String[] args){
            Pattern p = Pattern.compile("[0-9]*"); //是不是整数
            int timeInt;
            if(args.length == 1 && Objects.equals(args[0], "start")){
                return new TimeCommandRequest(RequestType.START);
            }else if(args.length == 1 && Objects.equals(args[0], "stop")){
                return new TimeCommandRequest(RequestType.STOP);
            }else if(args.length == 1 && Objects.equals(args[0], "query")){
                return new TimeCommandRequest(RequestType.QUERY);
            }else if(args.length == 2 && Objects.equals(args[0], "set") &&
                    ((timeInt = getTimeSetIntFromString(args[1])) != -1)){
                return new TimeCommandRequest(RequestType.SET, timeInt);
            }else if(args.length == 2 && Objects.equals(args[0], "add") &&
                    (p.matcher(args[1]).matches())){
                timeInt = Integer.parseInt(args[1]);
                return new TimeCommandRequest(RequestType.ADD, timeInt);
            }else{
                return new TimeCommandRequest(RequestType.INVALID);
            }
        }

        private static int getTimeSetIntFromString(String timeString){
            timeString = timeString.toLowerCase();
            Pattern p = Pattern.compile("[0-9]*");
            if(p.matcher(timeString).matches()){   //是不是整数
                return Integer.parseInt(timeString);
            }else if(Objects.equals(timeString, "day")){
                return Level.TIME_DAY;
            }else if(Objects.equals(timeString, "night")){
                return Level.TIME_NIGHT;
            }else if(Objects.equals(timeString, "sunrise")){
                return Level.TIME_SUNRISE;
            }else if(Objects.equals(timeString, "sunset")){
                return Level.TIME_SUNSET;
            }else{
                return -1;
            }
        }

    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        TimeCommandRequest request = TimeCommandRequest.of(args);
        if (!request.testPerm(sender)) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
            return false;
        }
        switch (request.getRequestType()) {
            case START:
                sender.getServer().getLevels().forEach((i, l) -> {
                    l.checkTime();
                    l.startTime();
                    l.checkTime();
                });
                Command.broadcastCommandMessage(sender, "Restarted the time");
                break;
            case STOP:
                sender.getServer().getLevels().forEach((i, l) -> {
                    l.checkTime();
                    l.stopTime();
                    l.checkTime();
                });
                Command.broadcastCommandMessage(sender, "Stopped the time");
                break;
            case QUERY:
                Level level;
                if (sender instanceof Player) {
                    level = ((Player) sender).getLevel();
                } else {
                    level = sender.getServer().getDefaultLevel();
                }
                String timeStringQuery = String.valueOf(level.getTime());
                sender.sendMessage(new TranslationContainer("commands.time.query", timeStringQuery));
                break;
            case SET:
                sender.getServer().getLevels().forEach((i, l) -> {
                    l.checkTime();
                    l.setTime(request.getNewTime());
                    l.checkTime();
                });
                String timeStringSet = String.valueOf(request.getNewTime());
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.time.set", timeStringSet));
                break;
            case ADD:
                sender.getServer().getLevels().forEach((i, l) -> {
                    l.checkTime();
                    l.setTime(l.getTime() + request.getNewTime());
                    l.checkTime();
                });
                String timeStringAdd = String.valueOf(request.getNewTime());
                Command.broadcastCommandMessage(sender, new TranslationContainer("commands.time.added", timeStringAdd));
                break;
            case INVALID:
            default:
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
        }
        return true;
    }

}
