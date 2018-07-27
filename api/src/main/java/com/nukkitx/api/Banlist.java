package com.nukkitx.api;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNullableByDefault;
import java.util.Date;
import java.util.UUID;

@ParametersAreNullableByDefault
public interface Banlist {

    boolean pardon(@Nonnull UUID uuid);

    boolean pardon(@Nonnull Player player);

    boolean pardon(@Nonnull String name);

    void ban(@Nonnull Player player, @Nullable Date expireDate, @Nullable String reason, String source);

    void ban(@Nonnull UUID uuid, @Nullable Date expireDate, @Nullable String reason, String source);

    void ban(@Nonnull String name, @Nullable Date expireDate, @Nullable String reason, String source);
}
