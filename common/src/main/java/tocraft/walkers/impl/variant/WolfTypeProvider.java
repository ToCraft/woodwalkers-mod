package tocraft.walkers.impl.variant;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.animal.WolfVariant;
import net.minecraft.world.entity.animal.WolfVariants;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.platform.PlatformData;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Objects;

@SuppressWarnings("resource")
public class WolfTypeProvider extends TypeProvider<Wolf> {
    private static int range = 9;

    public WolfTypeProvider() {
        if (PlatformData.getEnv() == EnvType.CLIENT) {
            setClientRange();
        }
    }

    @Environment(EnvType.CLIENT)
    private static void setClientRange() {
        Level clientLevel = Minecraft.getInstance().level;
        if (clientLevel != null) {
            setRange(clientLevel);
        }
    }

    @Override
    public int getVariantData(Wolf entity) {
        setRange(entity.level());
        return entity.level().registryAccess().lookupOrThrow(Registries.WOLF_VARIANT).getId(entity.getVariant().value());
    }

    @Override
    public Wolf create(EntityType<Wolf> type, Level level, int data) {
        setRange(level);

        Wolf wolf = type.create(level, EntitySpawnReason.LOAD);
        if (wolf != null) {
            Registry<WolfVariant> wolfVariantRegistry = level.registryAccess().lookupOrThrow(Registries.WOLF_VARIANT);
            wolf.setVariant(wolfVariantRegistry.get(data).orElse(wolfVariantRegistry.get(WolfVariants.PALE).orElseThrow()));
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

    public static void setRange(Level level) {
        range = level.registryAccess().lookupOrThrow(Registries.WOLF_VARIANT).size() - 1;
    }

    @Override
    public Component modifyText(Wolf entity, MutableComponent text) {
        setRange(entity.level());
        return Component.literal(formatTypePrefix(Objects.requireNonNull(entity.level().registryAccess().lookupOrThrow(Registries.WOLF_VARIANT).getKey(entity.getVariant().value())).getPath()) + " ").append(text);
    }
}
