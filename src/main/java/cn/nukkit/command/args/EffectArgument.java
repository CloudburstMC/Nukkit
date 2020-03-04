package cn.nukkit.command.args;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSource;
import cn.nukkit.command.TranslationMessage;
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
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;

import java.util.concurrent.CompletableFuture;

public class EffectArgument implements ArgumentType<Effect> {
    public static final DynamicCommandExceptionType NOT_FOUND = new DynamicCommandExceptionType((object) ->
            new LiteralMessage("Effect not found"));

    public static EffectArgument effect() {
        return new EffectArgument();
    }

    public static Effect getEffect(CommandContext<CommandSource> context, String name) {
        return context.getArgument(name, Effect.class);
    }

    @Override
    public Effect parse(StringReader reader) throws CommandSyntaxException {
        Identifier identifier = Identifier.from(reader);
        Effect effect = Effect.getEffectByName(identifier.toString());

        if(effect == null) {
            throw NOT_FOUND.create(identifier.toString());
        } else {
            return effect;
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return builder.suggest("TEST EFFECT!!!").buildFuture();
    }
}
