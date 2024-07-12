package tocraft.walkers.api.data.blacklist;

import com.google.gson.*;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import tocraft.craftedcore.data.SynchronizedJsonReloadListener;
import tocraft.craftedcore.patched.CRegistries;
import tocraft.craftedcore.patched.Identifier;
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
    protected void onApply(Map<ResourceLocation, JsonElement> map) {
        // prevent duplicates and the registration of removed entries
        EntityBlacklist.clearAll();
        EntityBlacklist.registerDefault();

        for (Map.Entry<ResourceLocation, JsonElement> mapEntry : map.entrySet()) {
            if (mapEntry.getKey().getPath().equals("blacklist")) {
                Pair<List<ResourceLocation>, List<ResourceLocation>> someBlacklist = blacklistFromJson(mapEntry.getValue().getAsJsonObject());
                for (ResourceLocation resourceLocation : someBlacklist.getFirst()) {
                    if (Walkers.getEntityTypeRegistry().containsKey(resourceLocation)) {
                        EntityBlacklist.registerByType((EntityType<?>) Walkers.getEntityTypeRegistry().get(resourceLocation));
                    }
                }
                for (ResourceLocation resourceLocation : someBlacklist.getSecond()) {
                    //noinspection unchecked
                    EntityBlacklist.registerByTag(TagKey.create((ResourceKey<? extends Registry<EntityType<?>>>) Walkers.getEntityTypeRegistry().key(), resourceLocation));
                }
            }
        }
    }

    public static Codec<Pair<List<ResourceLocation>, List<ResourceLocation>>> BLACKLIST_CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_types", new ArrayList<>()).forGetter(Pair::getFirst),
            Codec.list(ResourceLocation.CODEC).optionalFieldOf("entity_tags", new ArrayList<>()).forGetter(Pair::getSecond)
    ).apply(instance, instance.stable(Pair::new)));

    protected static Pair<List<ResourceLocation>, List<ResourceLocation>> blacklistFromJson(JsonObject json) {
        //#if MC>=1205
        return BLACKLIST_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(JsonParseException::new);
        //#else
        //$$ return BLACKLIST_CODEC.parse(JsonOps.INSTANCE, json).getOrThrow(false, msg -> {
        //$$     throw new JsonParseException(msg);
        //$$ });
        //#endif
    }
}
