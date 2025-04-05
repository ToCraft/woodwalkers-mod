package tocraft.walkers.api.data.traits;

import com.google.gson.*;
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
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TraitDataManager extends SynchronizedJsonReloadListener {
    public static final Gson GSON = new GsonBuilder().create();

    public TraitDataManager() {
        super(GSON, Walkers.MODID + "/traits");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onApply(@NotNull Map<ResourceLocation, JsonElement> map) {
        // prevent duplicates and the registration of removed entries
        TraitRegistry.clearAll();
        TraitRegistry.registerDefault();

        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            TraitList traitList = traitListFromJson(mapEntry.getValue().getAsJsonObject());

            if (!traitList.isEmpty()) {
                if (traitList.requiredMod() == null || traitList.requiredMod().isBlank() || PlatformData.isModLoaded(traitList.requiredMod())) {
                    // entity types
                    for (EntityType<LivingEntity> entityType : traitList.entityTypes()) {
                        TraitRegistry.registerByType(entityType, traitList.traitList().stream().map(trait -> (ShapeTrait<LivingEntity>) trait).toList());
                    }

                    if (!traitList.entityTypes().isEmpty()) {
                        logRegistration(traitList.entityTypes(), traitList.traitList());
                    }

                    // entity tags
                    for (TagKey<EntityType<?>> entityTag : traitList.entityTags()) {
                        TraitRegistry.registerByTag(entityTag, traitList.traitList().stream().map(trait -> (ShapeTrait<LivingEntity>) trait).toList());
                    }

                    if (!traitList.entityTags().isEmpty()) {
                        logRegistration(traitList.entityTags(), traitList.traitList());
                    }
                }
            }
        }
    }

    private static void logRegistration(Object key, @NotNull List<ShapeTrait<?>> traitList) {
        Walkers.LOGGER.warn("{}: {} registered for {}", TraitDataManager.class.getSimpleName(), traitList.stream().map(trait -> trait.getClass().getSimpleName()).toArray(String[]::new), key);
    }

    public static final Codec<TraitList> TRAIT_LIST_CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.STRING.optionalFieldOf("required_mod", "").forGetter(TraitList::requiredMod), Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_types", new ArrayList<>()).forGetter(TraitList::entityTypeKeys), Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_tags", new ArrayList<>()).forGetter(TraitList::entityTagKeys), Codec.list(TraitRegistry.getTraitCodec()).fieldOf("traits").forGetter(TraitList::traitList)).apply(instance, instance.stable(TraitList::new)));

    protected TraitList traitListFromJson(JsonObject json) {
        return TRAIT_LIST_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(msg -> {
            throw new JsonParseException(msg);
        });
    }

    @SuppressWarnings("unused")
    public record TraitList(String requiredMod, List<ResourceLocation> entityTypeKeys,
                            List<ResourceLocation> entityTagKeys, List<ShapeTrait<?>> traitList) {

        public TraitList(@NotNull List<EntityType<?>> entityTypeKeys, @NotNull List<TagKey<EntityType<?>>> entityTagKeys, List<ShapeTrait<?>> traitList, String requiredMod) {
            this(requiredMod, entityTypeKeys.stream().map(EntityType::getKey).toList(), entityTagKeys.stream().map(TagKey::location).toList(), traitList);
        }

        @SuppressWarnings("unchecked")
        public List<EntityType<LivingEntity>> entityTypes() {
            return entityTypeKeys.stream().filter(BuiltInRegistries.ENTITY_TYPE::containsKey).map(type -> (EntityType<LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(type).orElseThrow().value()).toList();
        }

        public List<TagKey<EntityType<?>>> entityTags() {
            return entityTagKeys().stream().map(tag -> TagKey.create(Registries.ENTITY_TYPE, tag)).toList();
        }

        public boolean isEmpty() {
            return (entityTypeKeys().isEmpty() && entityTagKeys().isEmpty()) || traitList().isEmpty();
        }
    }
}
