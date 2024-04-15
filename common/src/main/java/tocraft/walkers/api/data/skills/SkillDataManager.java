package tocraft.walkers.api.data.skills;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.platform.Platform;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.Walkers;
import tocraft.walkers.skills.ShapeSkill;
import tocraft.walkers.skills.SkillRegistry;

import java.util.*;
import java.util.function.Function;

public class SkillDataManager extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    public SkillDataManager() {
        super(GSON, Walkers.MODID + "/skills");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            Map<Pair<ResourceLocation, ResourceLocation>, List<? extends ShapeSkill<?>>> skillMap = skillEntryFromJson(mapEntry.getValue().getAsJsonObject());

            if (!skillMap.isEmpty()) {
                for (Map.Entry<Pair<ResourceLocation, ResourceLocation>, List<? extends ShapeSkill<?>>> entitySkills : skillMap.entrySet()) {
                    for (ShapeSkill<?> shapeSkill : entitySkills.getValue()) {
                        if (BuiltInRegistries.ENTITY_TYPE.containsKey(entitySkills.getKey().getFirst())) {
                            SkillRegistry.registerByType((EntityType<LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(entitySkills.getKey().getFirst()), (ShapeSkill<LivingEntity>) shapeSkill);
                        } else if (entitySkills.getKey().getSecond() != null) {
                            SkillRegistry.registerByTag(TagKey.create(Registries.ENTITY_TYPE, entitySkills.getKey().getSecond()), (ShapeSkill<LivingEntity>) shapeSkill);
                        }
                    }
                    Walkers.LOGGER.info("{}: {} registered for {}", getClass().getSimpleName(), entitySkills.getKey().toString().replaceAll("null", ""), entitySkills.getValue().stream().map(skill -> skill.getClass().getSimpleName()).toArray(String[]::new));
                }
            }
        }
    }

    protected static Map<Pair<ResourceLocation, ResourceLocation>, List<? extends ShapeSkill<?>>> skillEntryFromJson(JsonObject json) {
        Codec<Map<Pair<ResourceLocation, ResourceLocation>, List<? extends ShapeSkill<?>>>> codec = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_types", new ArrayList<>()).forGetter(o -> o.keySet().stream().map(Pair::getFirst).toList()),
                Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_tags", new ArrayList<>()).forGetter(o -> o.keySet().stream().map(Pair::getSecond).toList()),
                Codec.STRING.optionalFieldOf("required_mod", "").forGetter(o -> ""),
                Codec.list(byNameCodec()).fieldOf("skills").forGetter(o -> new ArrayList<>())
        ).apply(instance, instance.stable((entityTypes, entityTags, requiredMod, shapeSkills) -> {
            Map<Pair<ResourceLocation, ResourceLocation>, List<? extends ShapeSkill<?>>> skillMap = new HashMap<>();
            if (requiredMod.isBlank() || Platform.isModLoaded(requiredMod)) {
                for (ResourceLocation entityType : entityTypes) {
                    skillMap.put(new Pair<>(entityType, null), shapeSkills);
                }
                for (ResourceLocation entityTag : entityTags) {
                    skillMap.put(new Pair<>(null, entityTag), shapeSkills);
                }
            }
            return skillMap;
        })));
        return Util.getOrThrow(codec.parse(JsonOps.INSTANCE, json), JsonParseException::new);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static Codec<? extends ShapeSkill<?>> byNameCodec() {
        return ResourceLocation.CODEC.flatXmap(
                resourceLocation -> (DataResult) Optional.ofNullable(SkillRegistry.getSkillCodec(resourceLocation))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown shape skill: " + resourceLocation)),
                object -> Optional.ofNullable(SkillRegistry.getSkillId((ShapeSkill<?>) object))
                        .map(DataResult::success)
                        .orElseGet(() -> DataResult.error(() -> "Unknown shape skill:" + object))
        ).dispatchStable(object -> ((ShapeSkill<?>) object).codec(), Function.identity());
    }
}
