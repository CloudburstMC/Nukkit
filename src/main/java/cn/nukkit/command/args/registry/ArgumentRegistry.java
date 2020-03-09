package cn.nukkit.command.args.registry;

import cn.nukkit.command.args.*;

import com.nukkitx.protocol.bedrock.data.CommandParamData.Type;

import com.mojang.brigadier.arguments.*;

import java.util.HashMap;
import java.util.Map;

public class ArgumentRegistry {
    private final Map<Class<? extends ArgumentType>, ArgumentData> arguments = new HashMap<>();

    public ArgumentRegistry() {
        // Brigadier
        register(StringArgumentType.class, new ArgumentData("string", Type.STRING));
        register(LongArgumentType.class, new ArgumentData("long", Type.INT)); // TODO: int??
        register(FloatArgumentType.class, new ArgumentData("float", Type.FLOAT));
        register(IntegerArgumentType.class, new ArgumentData("int", Type.INT));
        register(DoubleArgumentType.class, new ArgumentData("double", Type.FLOAT));
        register(BoolArgumentType.class, new ArgumentData("bool", Type.STRING)); // TODO: theres no bool type? really?

        // Nukkit
        register(PlayerArgument.class, new ArgumentData("target", Type.TARGET));
        register(EffectArgument.class, new EnumArgumentData("Effect", Type.STRING));
        register(CommandArgument.class, new EnumArgumentData("CommandName", Type.STRING));
        register(ParticleArgument.class, new EnumArgumentData("Particle", Type.STRING));
        register(ItemArgument.class, new EnumArgumentData("Item", Type.STRING));
        register(GameRuleArgument.class, new EnumArgumentData("GameRule", Type.STRING));
        register(DifficultyArgument.class, new EnumArgumentData("Difficulty", Type.STRING));
        register(GameModeArgument.class, new EnumArgumentData("GameMode", Type.STRING));
        register(OfflinePlayerArgument.class, new EnumArgumentData("OfflinePlayer", Type.STRING));
    }

    public void register(Class<? extends ArgumentType> argumentClass, ArgumentData data) {
        arguments.put(argumentClass, data);
    }

    public ArgumentData getArgumentData(Class<? extends ArgumentType> argumentClass) {
        return arguments.get(argumentClass);
    }
}
