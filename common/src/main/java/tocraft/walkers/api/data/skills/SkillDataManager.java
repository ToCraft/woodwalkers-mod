package tocraft.walkers.api.data.skills;

import com.google.gson.*;
import com.mojang.serialization.Codec;
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
import tocraft.walkers.api.data.util.SynchronizedJsonReloadListener;
import tocraft.walkers.skills.ShapeSkill;
import tocraft.walkers.skills.SkillRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SkillDataManager extends SynchronizedJsonReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    public SkillDataManager() {
        super(GSON, Walkers.MODID + "/skills");
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onApply(Map<ResourceLocation, JsonElement> map) {
        // prevent duplicates and the registration of removed entries
        SkillRegistry.clearAll();
        SkillRegistry.registerDefault();

        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            SkillList skillList = skillListFromJson(mapEntry.getValue().getAsJsonObject());

            if (!skillList.isEmpty()) {
                if (skillList.requiredMod() == null || Platform.isModLoaded(skillList.requiredMod())) {
                    // entity types
                    for (EntityType<LivingEntity> entityType : skillList.entityTypes()) {
                        SkillRegistry.registerByType(entityType, skillList.skillList().stream().map(skill -> (ShapeSkill<LivingEntity>) skill).toList());
                    }

                    if (!skillList.entityTypes().isEmpty())
                        Walkers.LOGGER.info("{}: {} registered for {}", getClass().getSimpleName(), skillList.entityTypes(), skillList.skillList().stream().map(skill -> skill.getClass().getSimpleName()).toArray(String[]::new));

                    // entity tags
                    for (TagKey<EntityType<?>> entityTag : skillList.entityTags()) {
                        SkillRegistry.registerByTag(entityTag, skillList.skillList().stream().map(skill -> (ShapeSkill<LivingEntity>) skill).toList());
                    }

                    if (!skillList.entityTags().isEmpty())
                        Walkers.LOGGER.info("{}: {} registered for {}", getClass().getSimpleName(), skillList.entityTags(), skillList.skillList().stream().map(skill -> skill.getClass().getSimpleName()).toArray(String[]::new));
                }
            }
        }
    }

    public static Codec<SkillList> SKILL_LIST_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.STRING.optionalFieldOf("required_mod", "").forGetter(SkillList::requiredMod),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_types", new ArrayList<>()).forGetter(SkillList::entityTypeKeys),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_tags", new ArrayList<>()).forGetter(SkillList::entityTagKeys),
            Codec.list(SkillRegistry.getSkillCodec()).fieldOf("skills").forGetter(SkillList::skillList)
    ).apply(instance, instance.stable(SkillList::new)));

    protected static SkillList skillListFromJson(JsonObject json) {
        return Util.getOrThrow(SKILL_LIST_CODEC.parse(JsonOps.INSTANCE, json), JsonParseException::new);
    }

    @SuppressWarnings("unused")
    public record SkillList(String requiredMod, List<ResourceLocation> entityTypeKeys,
                            List<ResourceLocation> entityTagKeys,
                            List<ShapeSkill<?>> skillList) {

        public SkillList(List<EntityType<?>> entityTypeKeys, List<TagKey<EntityType<?>>> entityTagKeys, List<ShapeSkill<?>> skillList, String requiredMod) {
            this(requiredMod, entityTypeKeys.stream().map(EntityType::getKey).toList(), entityTagKeys.stream().map(TagKey::location).toList(), skillList);
        }

        @SuppressWarnings("unchecked")
        public List<EntityType<LivingEntity>> entityTypes() {
            return entityTypeKeys.stream().filter(BuiltInRegistries.ENTITY_TYPE::containsKey).map(type -> (EntityType<LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(type)).toList();
        }

        public List<TagKey<EntityType<?>>> entityTags() {
            return entityTagKeys().stream().map(tag -> TagKey.create(Registries.ENTITY_TYPE, tag)).toList();
        }

        public boolean isEmpty() {
            return (entityTypeKeys().isEmpty() && entityTagKeys().isEmpty()) || skillList().isEmpty();
        }
    }
}
