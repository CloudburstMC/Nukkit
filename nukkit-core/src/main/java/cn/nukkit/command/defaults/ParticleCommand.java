package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.ExplodeParticle;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;

import java.util.regex.Pattern;

/**
 * Created on 2015/11/21 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class ParticleCommand extends VanillaCommand {
    public ParticleCommand(String name) {
        super(name, "%nukkit.command.particle.description", "%nukkit.command.particle.usage");
        this.setPermission("nukkit.command.particle");
    }

    private static class ParticleCommandRequest{

        private enum RequestType{OK, ILLEGAL_ARGUMENTS, UNKNOWN_PARTICLE, INVALID_POSITION}

        private RequestType requestType;

        private Particle particle;

        private double xd;
        private double yd;
        private double zd;

        private int count;

        public RequestType getRequestType() {
            return requestType;
        }

        public Particle getParticle() {
            return particle;
        }

        public double getXd() {
            return xd;
        }

        public double getYd() {
            return yd;
        }

        public double getZd() {
            return zd;
        }

        public int getCount() {
            return count;
        }

        private ParticleCommandRequest(RequestType requestType){
            this(requestType, null, 0, 0, 0, 0);
        }

        private ParticleCommandRequest(RequestType requestType, Particle particle, double xd, double yd, double zd, int count){
            this.requestType = requestType;
            this.particle = particle;
            this.xd = xd;
            this.yd = yd;
            this.zd = zd;
            this.count = count;
        }

        private static Particle getParticleFromString(String string, Vector3 pos, int data){
            string = string.toLowerCase().trim();
            switch (string){
                case "explode":
                    return new ExplodeParticle(pos);
                //todo a lot
                default:
                    return null;
            }
        }

        private static boolean isParticleExist(String string){
            return getParticleFromString(string, new Vector3(0, 0, 0), 0) != null;
        }

        //nukkit.command.particle.usage=/particle <粒子名称> <x> <y> <z> <xd> <yd> <zd> [数量] [数据值]
        static ParticleCommandRequest of(String[] args){
            if (args.length < 7 || args.length > 9){
                return new ParticleCommandRequest(RequestType.ILLEGAL_ARGUMENTS);
            } else if (!isAllNumeric(args[1], args[2], args[3], args[4], args[5], args[6])){
                return new ParticleCommandRequest(RequestType.ILLEGAL_ARGUMENTS);
            } else if (!isParticleExist(args[0])){
                return new ParticleCommandRequest(RequestType.UNKNOWN_PARTICLE);
            } else {
                Vector3 pos = new Vector3(
                        Double.parseDouble(args[1]),
                        Double.parseDouble(args[2]),
                        Double.parseDouble(args[3])
                );
                double xd = Double.parseDouble(args[4]);
                double yd = Double.parseDouble(args[5]);
                double zd = Double.parseDouble(args[6]);

                int count = 1;
                int data = 0;
                if (args.length == 8){
                    if(isAllInteger(args[7])) count = Integer.parseInt(args[7]);
                } else if (args.length == 9){
                    if(isAllInteger(args[7], args[8])) {
                        count = Integer.parseInt(args[7]);
                        data = Integer.parseInt(args[8]);
                    }
                } else if(args.length != 7){
                    return new ParticleCommandRequest(RequestType.ILLEGAL_ARGUMENTS);
                }
                Particle particle = getParticleFromString(args[0], pos, data);
                return new ParticleCommandRequest(RequestType.OK, particle, xd, yd, zd, count);
            }
        }

        static boolean isAllNumeric(String... strings){
            final String regDouble = "^[-\\+]?\\d+(\\.\\d+)?$";
            Pattern p = Pattern.compile(regDouble);
            boolean result = true;
            for (String string : strings) {
                if (!p.matcher(string).matches()) result = false;
            }
            return result;
        }

        static boolean isAllInteger(String... strings){
            final String regDouble = "^[-\\+]?[\\d]*$";
            Pattern p = Pattern.compile(regDouble);
            boolean result = true;
            for (String string : strings) {
                if (!p.matcher(string).matches()) result = false;
            }
            return result;
        }
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        ParticleCommandRequest request = ParticleCommandRequest.of(args);
        switch (request.getRequestType()){
            case INVALID_POSITION:
            case ILLEGAL_ARGUMENTS:
                sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                break;
            case UNKNOWN_PARTICLE:
                sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.particle.notFound", args[0]));
                break;
            case OK:
                Particle p = request.getParticle();
                Level level = sender.getServer().getDefaultLevel();
                if(sender instanceof Player){
                    level = ((Player) sender).getLevel();
                }
                for (int i = 0; i < request.getCount(); i++) {
                    p.setComponents(
                            p.getX() + Math.random() * request.getXd(),
                            p.getY() + Math.random() * request.getYd(),
                            p.getZ() + Math.random() * request.getZd()
                    );

                    level.addParticle(p);
                }
                String countString = String.valueOf(request.getCount());
                sender.sendMessage(new TranslationContainer("commands.particle.success", new String[]{args[0], countString}));
                break;
        }
        return true;
    }
}
