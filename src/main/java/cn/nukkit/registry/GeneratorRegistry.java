package cn.nukkit.registry;

import cn.nukkit.level.generator.*;
import cn.nukkit.level.generator.impl.VoidGenerator;
import cn.nukkit.level.generator.impl.FlatGenerator;
import cn.nukkit.level.generator.standard.StandardGenerator;
import cn.nukkit.utils.Identifier;
import lombok.RequiredArgsConstructor;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

public class GeneratorRegistry implements Registry {
    private static final GeneratorRegistry INSTANCE = new GeneratorRegistry();

    private final Map<Identifier, GeneratorFactory> generators = new IdentityHashMap<>();
    private final List<WeightedIdentifier> fallback = new ArrayList<>();
    private volatile boolean closed;

    private GeneratorRegistry() {
        this.registerVanillaStorage();
    }

    public static GeneratorRegistry get() {
        return INSTANCE;
    }

    public synchronized void register(Identifier identifier, GeneratorFactory factory, int fallbackWeight) throws RegistryException {
        checkClosed();
        Objects.requireNonNull(identifier, "identifier");
        Objects.requireNonNull(factory, "factory");

        checkArgument(!this.generators.containsKey(identifier));
        this.generators.put(identifier, factory);
        this.fallback.add(new WeightedIdentifier(identifier, fallbackWeight));
    }

    public GeneratorFactory getGeneratorFactory(Identifier identifier) {
        Objects.requireNonNull(identifier, "identifier");

        GeneratorFactory factory = this.generators.get(identifier);
        if (factory == null) {
            factory = this.generators.get(this.fallback.get(0).identifier);
        }
        return factory;
    }

    public Identifier getFallback() {
        return fallback.get(0).identifier;
    }

    public boolean isRegistered(Identifier identifier) {
        return this.generators.containsKey(identifier);
    }

    @Override
    public synchronized void close() {
        checkClosed();
        this.closed = true;

        this.fallback.sort(Comparator.naturalOrder());
    }

    private void checkClosed() {
        checkArgument(!closed, "Registry has closed");
    }

    private void registerVanillaStorage() throws RegistryException {
        this.register(StandardGenerator.ID, StandardGenerator.FACTORY, 1000);
        this.register(FlatGenerator.ID, FlatGenerator::new, 500);
        this.register(VoidGenerator.ID, VoidGenerator::new, 1);
    }

    @RequiredArgsConstructor
    private static class WeightedIdentifier implements Comparable<WeightedIdentifier> {
        private final Identifier identifier;
        private final int weight;

        @Override
        public int compareTo(@Nonnull WeightedIdentifier o) {
            return Integer.compare(this.weight, o.weight);
        }
    }
}
