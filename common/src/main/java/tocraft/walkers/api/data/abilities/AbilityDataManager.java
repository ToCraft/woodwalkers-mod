package tocraft.walkers.api.data.abilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import tocraft.craftedcore.data.SynchronizedJsonReloadListener;
import tocraft.craftedcore.platform.PlatformData;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.ability.GenericShapeAbility;
import tocraft.walkers.ability.ShapeAbility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AbilityDataManager extends SynchronizedJsonReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    public AbilityDataManager() {
        super(GSON, Walkers.MODID + "/abilities");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onApply(@NotNull Map<ResourceLocation, JsonElement> map) {
        // prevent duplicates and the registration of removed entries
        AbilityRegistry.clearAll();
        AbilityRegistry.registerDefault();

        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            AbilityList abilityList = ABILITY_LIST_CODEC.parse(JsonOps.INSTANCE, mapEntry.getValue().getAsJsonObject()).getOrThrow(msg -> {
                throw new JsonParseException(msg);
            });

            if (!abilityList.isEmpty()) {
                if (abilityList.requiredMod() == null || abilityList.requiredMod().isBlank() || PlatformData.isModLoaded(abilityList.requiredMod())) {
                    // entity types
                    for (EntityType<LivingEntity> entityType : abilityList.entityTypes()) {
                        AbilityRegistry.registerByType(entityType, (GenericShapeAbility<LivingEntity>) abilityList.ability());
                    }

                    if (!abilityList.entityTypes().isEmpty()) {
                        logRegistration(abilityList.entityTypes(), abilityList.ability());
                    }

                    // entity tags
                    for (TagKey<EntityType<?>> entityTag : abilityList.entityTags()) {
                        AbilityRegistry.registerByTag(entityTag, (GenericShapeAbility<LivingEntity>) abilityList.ability());
                    }

                    if (!abilityList.entityTags().isEmpty()) {
                        logRegistration(abilityList.entityTags(), abilityList.ability());
                    }
                }
            }
        }
    }

    private static void logRegistration(Object key, @NotNull ShapeAbility<?> ability) {
        Walkers.LOGGER.debug("{}: {} registered for {}", AbilityDataManager.class.getSimpleName(), ability.getClass().getSimpleName(), key);
    }

    public static final Codec<AbilityList> ABILITY_LIST_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.optionalFieldOf("required_mod", "").forGetter(AbilityList::requiredMod),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_types", new ArrayList<>()).forGetter(AbilityList::entityTypeKeys),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_tags", new ArrayList<>()).forGetter(AbilityList::entityTagKeys),
            AbilityRegistry.getAbilityCodec().fieldOf("ability").forGetter(AbilityList::ability)
    ).apply(instance, instance.stable(AbilityList::new)));

    @SuppressWarnings("unused")
    public record AbilityList(String requiredMod, List<ResourceLocation> entityTypeKeys,
                              List<ResourceLocation> entityTagKeys,
                              GenericShapeAbility<?> ability) {

        public AbilityList(@NotNull List<EntityType<?>> entityTypeKeys, @NotNull List<TagKey<EntityType<?>>> entityTagKeys, GenericShapeAbility<?> ability, String requiredMod) {
            this(requiredMod, entityTypeKeys.stream().map(EntityType::getKey).toList(), entityTagKeys.stream().map(TagKey::location).toList(), ability);
        }

        @SuppressWarnings("unchecked")
        public List<EntityType<LivingEntity>> entityTypes() {
            return entityTypeKeys.stream().filter(BuiltInRegistries.ENTITY_TYPE::containsKey).map(type -> (EntityType<LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(type).orElseThrow().value()).toList();
        }

        public List<TagKey<EntityType<?>>> entityTags() {
            return entityTagKeys().stream().map(tag -> TagKey.create(Registries.ENTITY_TYPE, tag)).toList();
        }

        public boolean isEmpty() {
            return entityTypeKeys().isEmpty() && entityTagKeys().isEmpty();
        }
    }
}
