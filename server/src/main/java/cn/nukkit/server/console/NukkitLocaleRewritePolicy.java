package cn.nukkit.server.console;

import cn.nukkit.api.message.TranslationMessage;
import cn.nukkit.server.lang.NukkitLocaleManager;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.rewrite.RewritePolicy;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.Log4jLogEvent;
import org.apache.logging.log4j.message.SimpleMessage;

@Plugin(name = "NukkitLocaleRewritePolicy", category = Core.CATEGORY_NAME, elementType = "rewritePolicy", printObject = true)
public class NukkitLocaleRewritePolicy implements RewritePolicy {
    private static NukkitLocaleManager languageManager;

    private NukkitLocaleRewritePolicy() {
    }

    public static void setLanguageManager(NukkitLocaleManager languageManager) {
        NukkitLocaleRewritePolicy.languageManager = languageManager;
    }

    @PluginFactory
    public static NukkitLocaleRewritePolicy createPolicy() {
        return new NukkitLocaleRewritePolicy();
    }

    @Override
    public LogEvent rewrite(final LogEvent event) {
        if (languageManager == null || event.getMessage().getFormat() == null || event.getMessage().getParameters().length != 1) {
            return event;
        }
        Object[] parameters = event.getMessage().getParameters();
        if (event.getMessage().getFormat() == null && parameters[0] instanceof TranslationMessage) {
            TranslationMessage message = (TranslationMessage) parameters[0];

            String l10n;
            try {
                l10n = languageManager.replaceI18n(message.getMessage(), message.getParameters().toArray());
            } catch (Exception e) {
                return event;
            }

            Log4jLogEvent.Builder builder = new Log4jLogEvent.Builder(event);
            builder.setMessage(new SimpleMessage(l10n));
            return builder.build();
        }
        return event;
    }
}
