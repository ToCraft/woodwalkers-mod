package tocraft.walkers.api.data.blacklist;

import com.google.gson.*;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.blacklist.EntityBlacklist;

import java.util.List;
import java.util.Map;

public class EntityBlacklistDataManager extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    public EntityBlacklistDataManager() {
        super(GSON, Walkers.MODID);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            if (mapEntry.getKey().getPath().equals("blacklist")) {
                List<? extends EntityType<?>> someBlacklist = blacklistFromJson(mapEntry.getValue().getAsJsonObject());

                for (EntityType<?> entityType : someBlacklist) {
                    EntityBlacklist.registerByType(entityType);
                }
            }
        }
    }

    protected static List<? extends EntityType<?>> blacklistFromJson(JsonObject json) {
        Codec<List<? extends EntityType<?>>> codec = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.list(ResourceLocation.CODEC).fieldOf("entity_types").forGetter(o -> null)
        ).apply(instance, instance.stable((entityTypes) -> entityTypes.stream().filter(type -> BuiltInRegistries.ENTITY_TYPE.containsKey(type)).map(type -> BuiltInRegistries.ENTITY_TYPE.get(type)).toList())));
        return Util.getOrThrow(codec.parse(JsonOps.INSTANCE, json), JsonParseException::new);
    }
}
