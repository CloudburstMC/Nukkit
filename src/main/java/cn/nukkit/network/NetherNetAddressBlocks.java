package cn.nukkit.network;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

/**
 * Shares address blocks between the server thread and signaling.
 */
class NetherNetAddressBlocks {

    private final Map<InetAddress, Long> expirations = new ConcurrentHashMap<>();
    private final LongSupplier clock;

    NetherNetAddressBlocks() {
        this(System::nanoTime);
    }

    NetherNetAddressBlocks(LongSupplier clock) {
        this.clock = clock;
    }

    void block(InetSocketAddress address, long seconds) {
        InetAddress ip = address.getAddress();
        if (ip == null || seconds <= 0) {
            return;
        }

        long now = this.clock.getAsLong();
        this.expirations.entrySet().removeIf(entry -> entry.getValue() - now <= 0);
        long until = now + TimeUnit.SECONDS.toNanos(seconds);
        this.expirations.merge(ip, until, (previous, next) -> previous - next > 0 ? previous : next);
    }

    boolean isBlocked(InetSocketAddress address) {
        InetAddress ip = address == null ? null : address.getAddress();
        if (ip == null) {
            return false;
        }

        Long until = this.expirations.get(ip);
        if (until == null) {
            return false;
        }

        if (until - this.clock.getAsLong() > 0) {
            return true;
        }

        this.expirations.remove(ip, until);
        return false;
    }

    void unblock(InetSocketAddress address) {
        InetAddress ip = address.getAddress();
        if (ip != null) {
            this.expirations.remove(ip);
        }
    }
}
