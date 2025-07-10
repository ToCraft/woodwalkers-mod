package dev.tocraft.walkers.api.variant;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.tocraft.walkers.Walkers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueOutput;
import org.jetbrains.annotations.NotNull;

import java.util.*;

// this is amazing
public class NBTTypeProvider<T extends LivingEntity> extends TypeProvider<T> {
    public static final MapCodec<List<CompoundTag>> VARIANT_DATA_CODEC = Codec.unboundedMap(Codec.STRING, CompoundTag.CODEC).fieldOf("variants").flatXmap(map -> {
        List<CompoundTag> variantData = new ArrayList<>();
        // sort map to prevent errors while creating list
        map.entrySet().stream().sorted(Comparator.comparingInt(entry -> Integer.parseInt(entry.getKey()))).forEach(entry -> variantData.add(entry.getValue()));
        return DataResult.success(variantData);
    }, list -> {
        Map<String, CompoundTag> map = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            map.put(String.valueOf(i), list.get(i));
        }
        return DataResult.success(map);
    });

    public static final Codec<NBTTypeProvider<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.optionalFieldOf("fallback", 0).forGetter(NBTTypeProvider::getFallbackData),
            VARIANT_DATA_CODEC.forGetter(o -> o.variantData),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("names", new HashMap<>()).forGetter(o -> o.nameMap)
    ).apply(instance, instance.stable(NBTTypeProvider::new)));

    private final int fallback;
    private final List<CompoundTag> variantData;
    private final Map<String, String> nameMap;

    public NBTTypeProvider(int fallback, List<CompoundTag> variantData, Map<String, String> nameMap) {
        this.fallback = fallback;
        this.variantData = variantData;
        this.nameMap = nameMap;
    }

    @Override
    public int getVariantData(@NotNull T entity) {
        TagValueOutput out = TagValueOutput.createWithContext(Walkers.PROBLEM_REPORTER, entity.registryAccess());
        entity.save(out);
        CompoundTag tag = out.buildResult();

        int i = getData(tag);
        if (i != -1) {
            return i;
        } else {
            Walkers.LOGGER.error("{}: No Variant for entity type {} found.", getClass().getSimpleName(), EntityType.getKey(entity.getType()));
            return getFallbackData();
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public T create(EntityType<T> type, Level world, @NotNull Player player, int data) {
        CompoundTag tag = new CompoundTag();

        fromData(tag, data);

        CompoundTag compoundTag = tag.copy();
        compoundTag.putString("id", Objects.requireNonNull(EntityType.getKey(type)).toString());
        return (T) EntityType.loadEntityRecursive(compoundTag, world, EntitySpawnReason.LOAD, entity -> entity);
    }

    public int getData(CompoundTag tag) {
        for (CompoundTag compoundTag : variantData) {
            boolean bool = true;
            for (String key : compoundTag.keySet()) {
                if (!tag.contains(key) || tag.get(key) != compoundTag.get(key)) {
                    bool = false;
                }
                if (!bool) {
                    break;
                }
            }
            if (bool) {
                return variantData.indexOf(compoundTag);
            }
        }

        return -1;
    }

    public void fromData(CompoundTag tag, int data) {
        if (data < variantData.size()) {
            for (String key : variantData.get(data).keySet()) {
                Tag value = variantData.get(data).get(key);
                if (value != null) {
                    tag.put(key, value);
                }
            }
        }
    }

    @Override
    public int getFallbackData() {
        return fallback;
    }

    @Override
    public int size(Level level) {
        return variantData.size();
    }

    @Override
    public Component modifyText(T entity, MutableComponent text) {
        if (nameMap.containsKey(String.valueOf(getVariantData(entity))))
            return Component.translatable(nameMap.get(String.valueOf(getVariantData(entity))), text);
        else
            return text;
    }
}
