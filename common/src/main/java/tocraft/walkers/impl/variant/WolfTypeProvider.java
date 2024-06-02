package tocraft.walkers.impl.variant;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Objects;

@SuppressWarnings("resource")
public class WolfTypeProvider extends TypeProvider<Wolf> {
    private int range = 9;

    @Override
    public int getVariantData(Wolf entity) {
        setRange(entity.level());
        return entity.level().registryAccess().registryOrThrow(Registries.WOLF_VARIANT).getId(entity.getVariant().value());
    }

    @Override
    public Wolf create(EntityType<Wolf> type, Level level, int data) {
        setRange(level);

        Wolf wolf = type.create(level);
        if (wolf != null) {
            wolf.setVariant(level.registryAccess().registryOrThrow(Registries.WOLF_VARIANT).getHolder(data).orElseThrow());
        }
        return wolf;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return range;
    }

    private void setRange(Level level) {
        range = level.registryAccess().registryOrThrow(Registries.WOLF_VARIANT).size() - 1;
    }

    @Override
    public Component modifyText(Wolf entity, MutableComponent text) {
        setRange(entity.level());
        return Component.literal(formatTypePrefix(Objects.requireNonNull(entity.level().registryAccess().registryOrThrow(Registries.WOLF_VARIANT).getKey(entity.getVariant().value())).getPath()) + " ").append(text);
    }
}
