package tocraft.walkers.api.data.blacklist;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import tocraft.craftedcore.data.SynchronizedJsonReloadListener;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.blacklist.EntityBlacklist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityBlacklistDataManager extends SynchronizedJsonReloadListener {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(ResourceLocation.class, new ResourceLocation.Serializer()).create();

    public EntityBlacklistDataManager() {
        super(GSON, Walkers.MODID);
    }

    @Override
    protected void onApply(@NotNull Map<ResourceLocation, JsonElement> map) {
        // prevent duplicates and the registration of removed entries
        /*EntityBlacklist.clearAll();
        EntityBlacklist.registerDefault();*/

        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            if (mapEntry.getKey().getPath().equals("blacklist")) {
                Pair<List<ResourceLocation>, List<ResourceLocation>> someBlacklist = blacklistFromJson(mapEntry.getValue().getAsJsonObject());
                for (ResourceLocation resourceLocation : someBlacklist.getFirst()) {
                    if (BuiltInRegistries.ENTITY_TYPE.containsKey(resourceLocation)) {
                        EntityBlacklist.registerByType(BuiltInRegistries.ENTITY_TYPE.get(resourceLocation).orElseThrow().value());
                    }
                }
                for (ResourceLocation resourceLocation : someBlacklist.getSecond()) {
                    EntityBlacklist.registerByTag(TagKey.create(Registries.ENTITY_TYPE, resourceLocation));
                }
            }
        }
    }

    public static final Codec<Pair<List<ResourceLocation>, List<ResourceLocation>>> BLACKLIST_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_types", new ArrayList<>()).forGetter(Pair::getFirst),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_tags", new ArrayList<>()).forGetter(Pair::getSecond)
    ).apply(instance, instance.stable(Pair::new)));

    protected static Pair<List<ResourceLocation>, List<ResourceLocation>> blacklistFromJson(JsonObject json) {
        return BLACKLIST_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
    }
}
