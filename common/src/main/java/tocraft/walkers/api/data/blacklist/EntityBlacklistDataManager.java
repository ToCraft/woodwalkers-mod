package tocraft.walkers.api.data.blacklist;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.blacklist.EntityBlacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityBlacklistDataManager extends SimpleJsonResourceReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    public EntityBlacklistDataManager() {
        super(GSON, Walkers.MODID);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager resourceManager, ProfilerFiller profiler) {
        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            if (mapEntry.getKey().getPath().equals("blacklist")) {
                Pair<List<ResourceLocation>, List<ResourceLocation>> someBlacklist = blacklistFromJson(mapEntry.getValue().getAsJsonObject());
                for (ResourceLocation resourceLocation : someBlacklist.getFirst()) {
                    if (BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation)) {
                        EntityBlacklist.registerByType(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation));
                    }
                }
                for (ResourceLocation resourceLocation : someBlacklist.getSecond()) {
                    EntityBlacklist.registerByTag(TagKey.create(Registries.ENTITY_TYPE, resourceLocation));
                }
            }
        }
    }

    protected static Pair<List<ResourceLocation>, List<ResourceLocation>> blacklistFromJson(JsonObject json) {
        Codec<Pair<List<ResourceLocation>, List<ResourceLocation>>> codec = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_types", new ArrayList<>()).forGetter(Pair::getFirst),
                Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_tags", new ArrayList<>()).forGetter(Pair::getSecond)
        ).apply(instance, instance.stable(Pair::new)));
        return Util.getOrThrow(codec.parse(JsonOps.INSTANCE, json), JsonParseException::new);
    }
}
