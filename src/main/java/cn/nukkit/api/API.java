package cn.nukkit.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static cn.nukkit.api.API.Definition.UNIVERSAL;
import static cn.nukkit.api.API.Usage.BLEEDING;

/**
 * Describes an API element.
 *
 * @author Lin Mulan, Nukkit Project
 * @see Usage
 * @see Definition
 */
@Retention(RetentionPolicy.SOURCE)
@Target({ElementType.CONSTRUCTOR, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.TYPE})
@API(usage = BLEEDING, definition = UNIVERSAL)
@SuppressWarnings("unused")
public @interface API {

    /**
     * Indicates the level of stability of an API element.
     * The stability also describes when to use this API element.
     *
     * @return The stability
     * @see Usage
     */
    Usage usage();

    /**
     * Indicates definition or the platforms this API element supports.
     *
     * @return The definition
     * @see Definition
     */
    Definition definition();

    /**
     * Enum constant for API usage. Indicates when to use this API element.
     *
     * @see #DEPRECATED
     * @see #INCUBATING
     * @see #BLEEDING
     * @see #EXPERIMENTAL
     * @see #MAINTAINED
     * @see #STABLE
     */
    enum Usage {

        /**
         * Should no longer be used, might disappear in the next minor release.
         */
        DEPRECATED,

        /**
         * Intended for features in drafts. Should only be used for tests.
         *
         * <p>Might contains notable new features, but will be moved to a new package before remarking to {@link #BLEEDING}.
         * Could be unsafe, might be removed without prior notice. Warnings will be send if used.
         */
        INCUBATING,

        /**
         * Intended for features in early development. Should only be used for tests.
         *
         * <p>Might be unwrapped, unsafe or have unchecked parameters.
         * Further contribution was demanded to enhance, strengthen or simplify before remarking to {@link #EXPERIMENTAL}.
         * Might be removed or modified without prior notice.
         */
        BLEEDING,

        /**
         * Intended for new, experimental features where we are looking for feedback.
         * At least stable for development.
         *
         * <p>Use with caution, might be remarked to {@link #MAINTAINED} or {@link #STABLE} in the future,
         * but also might be removed without prior notice.
         */
        EXPERIMENTAL,

        /**
         * Intended for features that was tested, documented and at least stable for production use.
         *
         * <p>These features will not be modified in a backwards-incompatible way for at least next minor release
         * of the current major version. Will be remarked to {@link #DEPRECATED} first if scheduled for removal.
         */
        MAINTAINED,

        /**
         * Intended for features that was tested, documented and is preferred in production use.
         *
         * <p>Will not be changed in a backwards-incompatible way in the current version.
         */
        STABLE
    }

    /**
     * Enum constant for API definition. Indicates which client platform this API element supports.
     *
     * @see #INTERNAL
     * @see #PLATFORM_NATIVE
     * @see #UNIVERSAL
     */
    enum Definition {

        /**
         * Intended for features should only be used by Nukkit itself.
         * Should not be used in production.
         */
        INTERNAL,

        /**
         * Intended for features only available on one or several client platforms.
         *
         * <p>By using {@code PLATFORM_NATIVE} features, program will lose some cross-platform features provided.
         * Might not available in some client platforms. Read the documents carefully before using this API element.
         */
        PLATFORM_NATIVE,

        /**
         * Intended for features implemented in all client platforms.
         *
         * <p>Preferred to use for production use, but sometimes be lack of platform-native features.
         */
        UNIVERSAL
    }
}
