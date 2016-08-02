package cn.nukkit.command.defaults;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.command.CommandSender;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.particle.*;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;

import java.util.Random;

/**
 * Created on 2015/11/12 by xtypr.
 * Package cn.nukkit.command.defaults in project Nukkit .
 */
public class ParticleCommand extends VanillaCommand {
    public ParticleCommand(String name) {
        super(name, "%nukkit.command.particle.description", "%nukkit.command.particle.usage");
        this.setPermission("nukkit.command.particle");
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }

        if (args.length < 7) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));

            return true;
        }

        Level level;
        if (sender instanceof Player) {
            level = ((Player) sender).getLevel();
        } else {
            level = sender.getServer().getDefaultLevel();
        }

        String name = args[0].toLowerCase();

        float[] floats = new float[6];
        for (int i = 0; i < floats.length; i++) {
            try {
                double d = Double.valueOf(args[i + 1]);
                floats[i] = (float) d;
            } catch (Exception e) {
                return false;
            }
        }

        Vector3 pos = new Vector3(floats[0], floats[1], floats[2]);
        float xd = floats[3];
        float yd = floats[4];
        float zd = floats[5];

        int count = 1;
        if (args.length > 7) {
            try {
                double c = Double.valueOf(args[7]);
                count = (int) c;
            } catch (Exception e) {
                //ignore
            }
        }
        count = Math.max(1, count);

        Integer data = null;
        if (args.length > 8) {
            try {
                double d = Double.valueOf(args[8]);
                data = (int) d;
            } catch (Exception e) {
                //ignore
            }
        }

        Particle particle = this.getParticle(name, pos, xd, yd, zd, data);

        if (particle == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.particle.notFound", name));
            return true;
        }

        sender.sendMessage(new TranslationContainer("commands.particle.success", new String[]{name, String.valueOf(count)}));

        Random random = new Random(System.currentTimeMillis());

        for (int i = 0; i < count; i++) {
            particle.setComponents(
                    pos.x + (random.nextFloat() * 2 - 1) * xd,
                    pos.y + (random.nextFloat() * 2 - 1) * yd,
                    pos.z + (random.nextFloat() * 2 - 1) * zd
            );
            level.addParticle(particle);
        }

        return true;
    }

    private Particle getParticle(String name, Vector3 pos, float xd, float yd, float zd, Integer data) {
        switch (name) {
            case "explode":
                return new ExplodeParticle(pos);
            case "largeexplode":
                return new LargeExplodeParticle(pos);
            case "hugeexplosion":
                return new HugeExplodeParticle(pos);
            case "bubble":
                return new BubbleParticle(pos);
            case "splash":
                return new SplashParticle(pos);
            case "wake":
            case "water":
                return new WaterParticle(pos);
            case "crit":
                return new CriticalParticle(pos);
            case "smoke":
                return new SmokeParticle(pos, data != null ? data : 0);
            case "spell":
                return new EnchantParticle(pos);
            case "instantspell":
                return new InstantEnchantParticle(pos);
            case "dripwater":
                return new WaterDripParticle(pos);
            case "driplava":
                return new LavaDripParticle(pos);
            case "townaura":
            case "spore":
                return new SporeParticle(pos);
            case "portal":
                return new PortalParticle(pos);
            case "flame":
                return new FlameParticle(pos);
            case "lava":
                return new LavaParticle(pos);
            case "reddust":
                return new RedstoneParticle(pos, data != null ? data : 1);
            case "snowballpoof":
                return new ItemBreakParticle(pos, Item.get(Item.SNOWBALL));
            case "slime":
                return new ItemBreakParticle(pos, Item.get(Item.SLIMEBALL));
            case "itembreak":
                if (data != null && data != 0) {
                    return new ItemBreakParticle(pos, Item.get(data));
                }
                break;
            case "terrain":
                if (data != null && data != 0) {
                    return new TerrainParticle(pos, Block.get(data));
                }
                break;
            case "heart":
                return new HeartParticle(pos, data != null ? data : 0);
            case "ink":
                return new InkParticle(pos, data != null ? data : 0);
            case "droplet":
                return new RainSplashParticle(pos);
            case "enchantmenttable":
                return new EnchantmentTableParticle(pos);
            case "happyvillager":
                return new HappyVillagerParticle(pos);
            case "angryvillager":
                return new AngryVillagerParticle(pos);

        }

        if (name.startsWith("iconcrack_")) {
            String[] d = name.split("_");
            if (d.length == 3) {
                return new ItemBreakParticle(pos, Item.get(Integer.valueOf(d[1]), Integer.valueOf(d[2])));
            }
        } else if (name.startsWith("blockcrack_")) {
            String[] d = name.split("_");
            if (d.length == 2) {
                return new TerrainParticle(pos, Block.get(Integer.valueOf(d[1]) & 0xff, Integer.valueOf(d[1]) >> 12));
            }
        } else if (name.startsWith("blockdust_")) {
            String[] d = name.split("_");
            if (d.length >= 4) {
                return new DustParticle(pos, Integer.valueOf(d[1]) & 0xff, Integer.valueOf(d[2]) & 0xff, Integer.valueOf(d[3]) & 0xff, d.length >= 5 ? Integer.valueOf(d[4]) & 0xff : 255);
            }
        }

        return null;
    }
}
