package cn.nukkit.locale;

import org.apache.logging.log4j.message.FormattedMessage;

import java.util.Locale;

public class NukkitMessage extends FormattedMessage {

    /**
     * Constructs with a locale, a pattern and a single parameter.
     *
     * @param locale         The locale
     * @param messagePattern The message pattern.
     * @param arg            The parameter.
     * @since 2.6
     */
    public NukkitMessage(final Locale locale, final String messagePattern, final Object arg) {
        super(locale, messagePattern, arg);
    }

    /**
     * Constructs with a locale, a pattern and two parameters.
     *
     * @param locale         The locale
     * @param messagePattern The message pattern.
     * @param arg1           The first parameter.
     * @param arg2           The second parameter.
     * @since 2.6
     */
    public NukkitMessage(final Locale locale, final String messagePattern, final Object arg1, final Object arg2) {
        super(locale, messagePattern, arg1, arg2);
    }

    /**
     * Constructs with a locale, a pattern and a parameter array.
     *
     * @param locale         The locale
     * @param messagePattern The message pattern.
     * @param arguments      The parameter.
     * @since 2.6
     */
    public NukkitMessage(final Locale locale, final String messagePattern, final Object... arguments) {
        super(locale, messagePattern, arguments);
    }

    /**
     * Constructs with a locale, a pattern, a parameter array, and a throwable.
     *
     * @param locale         The Locale
     * @param messagePattern The message pattern.
     * @param arguments      The parameter.
     * @param throwable      The throwable
     * @since 2.6
     */
    public NukkitMessage(final Locale locale, final String messagePattern, final Object[] arguments, final Throwable throwable) {
        super(locale, messagePattern, arguments, throwable);
    }

    /**
     * Constructs with a pattern and a single parameter.
     *
     * @param messagePattern The message pattern.
     * @param arg            The parameter.
     */
    public NukkitMessage(final String messagePattern, final Object arg) {
        super(messagePattern, arg);
    }

    /**
     * Constructs with a pattern and two parameters.
     *
     * @param messagePattern The message pattern.
     * @param arg1           The first parameter.
     * @param arg2           The second parameter.
     */
    public NukkitMessage(final String messagePattern, final Object arg1, final Object arg2) {
        super(messagePattern, arg1, arg2);
    }

    /**
     * Constructs with a pattern and a parameter array.
     *
     * @param messagePattern The message pattern.
     * @param arguments      The parameter.
     */
    public NukkitMessage(final String messagePattern, final Object... arguments) {
        super(messagePattern, arguments);
    }

    /**
     * Constructs with a pattern, a parameter array, and a throwable.
     *
     * @param messagePattern The message pattern.
     * @param arguments      The parameter.
     * @param throwable      The throwable
     */
    public NukkitMessage(final String messagePattern, final Object[] arguments, final Throwable throwable) {
        super(messagePattern, arguments, throwable);
    }

    @Override
    public String getFormattedMessage() {
        return null;
    }

    @Override
    public String getFormat() {
        return null;
    }

    @Override
    public Object[] getParameters() {
        return new Object[0];
    }

    @Override
    public Throwable getThrowable() {
        return null;
    }
}
