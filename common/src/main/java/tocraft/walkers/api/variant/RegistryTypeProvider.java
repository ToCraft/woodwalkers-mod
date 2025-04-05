package tocraft.walkers.api.variant;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.variant.VariantUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;

public class RegistryTypeProvider<T extends LivingEntity, V> extends TypeProvider<T> {
    protected final ResourceKey<Registry<V>> registry;

    public RegistryTypeProvider(ResourceKey<Registry<V>> registry) {
        this.registry = registry;
    }

    @Override
    public int getVariantData(T entity) {
        return getVariant(entity).map(v -> entity.registryAccess().lookupOrThrow(this.registry).getId(v.value())).orElse(getFallbackData());
    }

    private @NotNull Optional<Holder<V>> getVariant(@NotNull T entity) {
        CompoundTag tag = new CompoundTag();
        entity.save(tag);

        return VariantUtils.readVariant(tag, entity.registryAccess(), this.registry);
    }

    @Override
    public T create(@NotNull EntityType<T> type, @NotNull Level level, @NotNull Player player, int data) {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("id", Objects.requireNonNull(EntityType.getKey(type)).toString());
        level.registryAccess().lookupOrThrow(registry).get(data).ifPresent(v -> VariantUtils.writeVariant(compoundTag, v));

        //noinspection unchecked
        return (T) EntityType.loadEntityRecursive(compoundTag, level, EntitySpawnReason.LOAD, entity -> entity);
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int size(@NotNull Level level) {
        return level.registryAccess().lookupOrThrow(this.registry).size();
    }

    @Override
    public Component modifyText(@NotNull T entity, MutableComponent text) {
        Optional<MutableComponent> variant = getVariant(entity).flatMap(Holder::unwrapKey).map(key -> Component.literal(formatTypePrefix(key.location().getPath() + " ")));

        return variant.map(c -> c.append(text)).orElse(text);
    }
}
