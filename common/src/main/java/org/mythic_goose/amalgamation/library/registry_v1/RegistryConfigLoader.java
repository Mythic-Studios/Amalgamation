package org.mythic_goose.amalgamation.library.registry_v1;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class RegistryConfigLoader<T, V> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final Path configDir;
    private final String fileName;
    private final Logger logger;
    private final Function<Identifier, Optional<T>> resolver;
    private final Function<JsonElement, V> valueParser;
    private final BiConsumer<T, V> setter;
    private final Supplier<JsonObject> defaultJsonSupplier;

    private RegistryConfigLoader(Builder<T, V> b) {
        this.configDir = b.configDir;
        this.fileName = b.fileName;
        this.logger = b.logger;
        this.resolver = b.resolver;
        this.valueParser = b.valueParser;
        this.setter = b.setter;
        this.defaultJsonSupplier = b.defaultJsonSupplier;
    }

    public void load() {
        Path file = configDir.resolve(fileName);

        try {
            Files.createDirectories(configDir);

            if (!Files.exists(file)) {
                writeDefault(file);
                return;
            }

            int loaded = 0;
            try (Reader reader = Files.newBufferedReader(file)) {
                JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();

                for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                    String key = entry.getKey();
                    if (key.startsWith("_")) continue;

                    Identifier id = Identifier.tryParse(key);
                    if (id == null) {
                        logger.warn("Config '{}': invalid id '{}', skipping", fileName, key);
                        continue;
                    }

                    Optional<T> resolved = resolver.apply(id);
                    if (resolved.isEmpty()) {
                        logger.warn("Config '{}': unknown entry '{}', skipping", fileName, key);
                        continue;
                    }

                    V value;
                    try {
                        value = valueParser.apply(entry.getValue());
                    } catch (RuntimeException e) {
                        logger.warn("Config '{}': bad value for '{}', skipping ({})", fileName, key, e.getMessage());
                        continue;
                    }

                    setter.accept(resolved.get(), value);
                    loaded++;
                }
            }
            logger.info("Config '{}': loaded {} override(s)", fileName, loaded);
        } catch (IOException | RuntimeException e) {
            logger.error("Config '{}': failed to load", fileName, e);
        }
    }

    private void writeDefault(Path file) throws IOException {
        JsonObject example = defaultJsonSupplier != null ? defaultJsonSupplier.get() : new JsonObject();
        Files.writeString(file, GSON.toJson(example));
        logger.info("Config '{}': wrote default file at {}", fileName, file);
    }

    public static <T, V> Builder<T, V> builder() {
        return new Builder<>();
    }

    public static final class Builder<T, V> {
        private Path configDir;
        private String fileName;
        private Logger logger;
        private Function<Identifier, Optional<T>> resolver;
        private Function<JsonElement, V> valueParser;
        private BiConsumer<T, V> setter;
        private Supplier<JsonObject> defaultJsonSupplier;

        public Builder<T, V> configDir(Path configDir) { this.configDir = configDir; return this; }
        public Builder<T, V> fileName(String fileName) { this.fileName = fileName; return this; }
        public Builder<T, V> logger(Logger logger) { this.logger = logger; return this; }
        public Builder<T, V> resolver(Function<Identifier, Optional<T>> resolver) { this.resolver = resolver; return this; }
        public Builder<T, V> valueParser(Function<JsonElement, V> valueParser) { this.valueParser = valueParser; return this; }
        public Builder<T, V> setter(BiConsumer<T, V> setter) { this.setter = setter; return this; }
        public Builder<T, V> defaultJson(Supplier<JsonObject> defaultJsonSupplier) { this.defaultJsonSupplier = defaultJsonSupplier; return this; }

        public RegistryConfigLoader<T, V> build() {
            Objects.requireNonNull(configDir, "configDir");
            Objects.requireNonNull(fileName, "fileName");
            Objects.requireNonNull(logger, "logger");
            Objects.requireNonNull(resolver, "resolver");
            Objects.requireNonNull(valueParser, "valueParser");
            Objects.requireNonNull(setter, "setter");
            return new RegistryConfigLoader<>(this);
        }
    }
}