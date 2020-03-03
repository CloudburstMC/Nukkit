package cn.nukkit.command.args;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSource;
import cn.nukkit.level.particle.Particle;
import cn.nukkit.player.Player;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.Identifier;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

public class ParticleArgument implements ArgumentType<Particle> {
    public static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType((object) ->
            new LiteralMessage("effect.effectNotFound"));

    public static ParticleArgument particle() {
        return new ParticleArgument();
    }

    public static Particle getParticle(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, Particle.class);
    }

    @Override
    public Particle parse(StringReader reader) throws CommandSyntaxException {
        Identifier identifier = Identifier.fromString(reader.getString());
//        //Particle effect = Particle.(identifier.toString());
//
//        if(effect == null) {
//            throw NOT_FOUND.create(identifier.toString());
//        } else {
//            return effect;
//        }
        return null;
    }
}
