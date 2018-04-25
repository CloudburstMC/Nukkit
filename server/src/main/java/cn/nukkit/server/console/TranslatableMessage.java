package cn.nukkit.server.console;

import cn.nukkit.api.message.TranslationMessage;
import cn.nukkit.server.locale.NukkitLocaleManager;
import com.google.common.base.Preconditions;
import org.apache.logging.log4j.message.Message;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;

public class TranslatableMessage implements Message {
    private static NukkitLocaleManager languageManager;

    private final String format;
    private final Object[] parameters;
    private transient String formatted = null;

    private TranslatableMessage(String format, Object[] parameters) {
        this.format = format;
        this.parameters = Arrays.copyOf(parameters, parameters.length);
    }

    public static void setLanguageManager(NukkitLocaleManager languageManager) {
        TranslatableMessage.languageManager = languageManager;
    }

    public static TranslatableMessage of(@Nonnull String format, @Nonnull Object... parameters) {
        return new TranslatableMessage(Preconditions.checkNotNull(format, "format"), Preconditions.checkNotNull(parameters, "parameters"));
    }

    public static TranslatableMessage of(@Nonnull String format, @Nonnull Collection parameters) {
        Preconditions.checkNotNull(parameters, "parameters");
        return new TranslatableMessage(Preconditions.checkNotNull(format, "format"), parameters.toArray());
    }

    public static TranslatableMessage of(@Nonnull TranslationMessage message) {
        Preconditions.checkNotNull(message, "message");
        return of(message.getMessage(), message.getParameters());
    }

    @Override
    public String getFormattedMessage() {
        if (formatted != null) {
            return formatted;
        }
        try {
            formatted = languageManager.replaceI18n(format, parameters);
        } catch (Exception e) {
            return format + ", " +
                    Arrays.toString(parameters);
        }
        return formatted;
    }

    @Override
    public String getFormat() {
        return format;
    }

    @Override
    public Object[] getParameters() {
        return Arrays.copyOf(parameters, parameters.length);
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }
}
