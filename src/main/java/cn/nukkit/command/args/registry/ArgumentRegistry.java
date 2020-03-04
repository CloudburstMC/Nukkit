package cn.nukkit.command.args.registry;

import cn.nukkit.command.args.CommandArgument;
import cn.nukkit.command.args.EffectArgument;
import cn.nukkit.command.args.ParticleArgument;
import cn.nukkit.command.args.PlayerArgument;
import static cn.nukkit.network.protocol.AvailableCommandsPacket.*;

import static cn.nukkit.command.data.CommandParamType.*;
import cn.nukkit.network.protocol.AvailableCommandsPacket;
import com.mojang.brigadier.arguments.*;

import java.util.HashMap;
import java.util.Map;

public class ArgumentRegistry {
    private final Map<Class<? extends ArgumentType>, ArgumentData> arguments = new HashMap<>();

    public ArgumentRegistry() {
        // Brigadier
        register(StringArgumentType.class, new ArgumentData("string", STRING));
        register(LongArgumentType.class, new ArgumentData("long", INT)); // TODO: int??
        register(FloatArgumentType.class, new ArgumentData("float", FLOAT));
        register(IntegerArgumentType.class, new ArgumentData("int", INT));
        register(DoubleArgumentType.class, new ArgumentData("double", FLOAT));
        register(BoolArgumentType.class, new ArgumentData("bool", STRING)); // TODO: theres no bool type? really?

        // Nukkit
        register(EffectArgument.class, new ArgumentData("Effect", STRING));
        register(CommandArgument.class, new ArgumentData("CommandName", STRING));
        register(ParticleArgument.class, new ArgumentData("Particle", STRING));
        register(PlayerArgument.class, new ArgumentData("target", TARGET));
    }

    public void register(Class<? extends ArgumentType> argumentClass, ArgumentData data) {
        arguments.put(argumentClass, data);
    }

    public ArgumentData getArgumentData(Class<? extends ArgumentType> argumentClass) {
        return arguments.get(argumentClass);
    }
}
