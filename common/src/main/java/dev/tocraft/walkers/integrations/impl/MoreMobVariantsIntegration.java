package dev.tocraft.walkers.integrations.impl;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.variant.TypeProvider;
import dev.tocraft.walkers.api.variant.TypeProviderRegistry;
import dev.tocraft.walkers.integrations.AbstractIntegration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Reflections are simply great!
public class MoreMobVariantsIntegration extends AbstractIntegration {
    public static final String MODID = "moremobvariants";

    @Override
    public void registerTypeProvider() {
        registerMMVTypeProvider(EntityType.CAT);
        registerMMVTypeProvider(EntityType.CHICKEN);
        registerMMVTypeProvider(EntityType.COW);
        registerMMVTypeProvider(EntityType.SKELETON);
        registerMMVTypeProvider(EntityType.SPIDER);
        registerMMVTypeProvider(EntityType.WOLF);
        registerMMVTypeProvider(EntityType.ZOMBIE);
    }

    private static <L extends LivingEntity> void registerMMVTypeProvider(EntityType<L> type) {
        TypeProviderRegistry.register(type, new MMVTypeProvider<>(type));
    }

    @SuppressWarnings("unchecked")
    private static List<ResourceLocation> getVariants(EntityType<?> type) {
        try {
            Class<?> variantsClass = Class.forName("com.github.nyuppo.config.Variants");
            Method getVariants = variantsClass.getDeclaredMethod("getVariants", EntityType.class);
            List<Object> variants = (List<Object>) getVariants.invoke(null, type);
            List<ResourceLocation> variantIds = new ArrayList<>();
            for (Object variant : variants) {
                ResourceLocation id = (ResourceLocation) variant.getClass().getDeclaredMethod("getIdentifier").invoke(variant);
                variantIds.add(id);
            }
            return variantIds;
        } catch (ReflectiveOperationException e) {
            Walkers.LOGGER.error("{}: failed to get the mob blacklist for {}: {}", GuardVillagersIntegration.class.getSimpleName(), MODID, e);
        }

        return new ArrayList<>();
    }

    @SuppressWarnings("unused")
    private static class MMVTypeProvider<L extends LivingEntity> extends TypeProvider<L> {
        private final EntityType<L> type;

        public MMVTypeProvider(EntityType<L> type) {
            this.type = type;
        }

        @Override
        public int getVariantData(L entity) {
            List<ResourceLocation> variants = getVariants(type);
            TagValueOutput out = TagValueOutput.createWithContext(Walkers.PROBLEM_REPORTER, entity.level().registryAccess());
            entity.saveWithoutId(out);
            CompoundTag nbt = out.buildResult();
            Optional<String> str = nbt.getString("VariantID");
            if (str.isEmpty()) {
                return getFallbackData();
            }
            ResourceLocation id = ResourceLocation.parse(str.get());
            return variants.indexOf(id);
        }

        @SuppressWarnings("unchecked")
        @Override
        public L create(EntityType<L> type, Level world, @NotNull Player player, int data) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("id", EntityType.getKey(type).toString());
            ResourceLocation variantId = getVariants(type).get(data);
            nbt.putString("VariantID", variantId.toString());
            return (L) EntityType.loadEntityRecursive(nbt, world, EntitySpawnReason.LOAD, entity -> entity);
        }

        @Override
        public int getFallbackData() {
            return 0;
        }

        @Override
        public int size(Level level) {
            return getVariants(type).size() - 1;
        }

        @Override
        public @NotNull Component modifyText(@NotNull L entity, MutableComponent text) {
            List<ResourceLocation> variants = getVariants(type);
            TagValueOutput out = TagValueOutput.createWithContext(Walkers.PROBLEM_REPORTER, entity.level().registryAccess());
            entity.saveWithoutId(out);
            CompoundTag nbt = out.buildResult();
            String str = nbt.getString("VariantID").orElse("");
            return Component.literal(formatTypePrefix(str) + " ").append(text);
        }
    }
}
