package dev.tocraft.walkers.api.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.walkers.Walkers;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class RegistryTypeProvider<T extends LivingEntity, V> extends TypeProvider<T> {
    public static final Codec<RegistryTypeProvider<?, ?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            ResourceLocation.CODEC.fieldOf("registry").forGetter(o -> o.registry.location()),
            Codec.INT.optionalFieldOf("fallback", 0).forGetter(RegistryTypeProvider::getFallbackData)
    ).apply(instance, instance.stable((r, f) -> new RegistryTypeProvider<>(ResourceKey.createRegistryKey(r), f))));

    protected final ResourceKey<Registry<V>> registry;
    private final int fallback;

    public RegistryTypeProvider(ResourceKey<Registry<V>> registry) {
        this(registry, 0);
    }

    public RegistryTypeProvider(ResourceKey<Registry<V>> registry, int fallback) {
        this.registry = registry;
        this.fallback = fallback;
    }

    @Override
    public int getVariantData(T entity) {
        return getVariant(entity).flatMap(v -> getRegistry(entity.level()).map(reg -> reg.getId(v.value()))).orElse(getFallbackData());
    }

    private @NotNull Optional<Holder<V>> getVariant(@NotNull T entity) {
        TagValueOutput out = TagValueOutput.createWithContext(Walkers.PROBLEM_REPORTER, entity.registryAccess());
        entity.save(out);
        CompoundTag nbt = out.buildResult();
        ValueInput in = TagValueInput.create(Walkers.PROBLEM_REPORTER, entity.registryAccess(), nbt);

        return VariantUtils.readVariant(in, this.registry);
    }

    @Override
    public T create(@NotNull EntityType<T> type, @NotNull Level level, @NotNull Player player, int data) {
        TagValueOutput out = TagValueOutput.createWithContext(Walkers.PROBLEM_REPORTER, level.registryAccess());
        out.putString("id", Objects.requireNonNull(EntityType.getKey(type)).toString());
        getRegistry(level).flatMap(reg -> reg.get(data)).ifPresent(v -> VariantUtils.writeVariant(out, v));

        //noinspection unchecked
        return (T) EntityType.loadEntityRecursive(out.buildResult(), level, EntitySpawnReason.LOAD, entity -> entity);
    }

    @Override
    public int getFallbackData() {
        return fallback;
    }

    @Override
    public int size(@NotNull Level level) {
        return getRegistry(level).map(Registry::size).orElse(getFallbackData());
    }

    @Override
    public Component modifyText(@NotNull T entity, MutableComponent text) {
        Optional<MutableComponent> variant = getVariant(entity).flatMap(Holder::unwrapKey).map(key -> Component.literal(formatTypePrefix(key.location().getPath() + " ")));

        return variant.map(c -> c.append(text)).orElse(text);
    }

    private @NotNull Optional<Registry<V>> getRegistry(@NotNull Level level) {
        Optional<Registry<V>> reg = level.registryAccess().lookup(this.registry);
        if (reg.isEmpty()) {
            Walkers.LOGGER.error("{}: Could not find registry {}", RegistryTypeProvider.class.getSimpleName(), this.registry);
        }
        return reg;
    }
}
