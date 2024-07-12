package tocraft.walkers.api.data.variants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.*;

public record AdvancedNBTEntries(List<CompoundTag> variantData) {
    public static final Codec<AdvancedNBTEntries> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(Codec.STRING, CompoundTag.CODEC).fieldOf("variants").forGetter(o -> {
                Map<String, CompoundTag> map = new HashMap<>();
                for (int i = 0; i < o.variantData().size(); i++) {
                    map.put(String.valueOf(i), o.variantData().get(i));
                }
                return map;
            })
    ).apply(instance, instance.stable((variantDataMap) -> {
        List<CompoundTag> variantData = new ArrayList<>();
        // sort map to prevent errors while creating list
        variantDataMap.entrySet().stream().sorted(Comparator.comparingInt(entry -> Integer.parseInt(entry.getKey()))).forEach(entry -> variantData.add(entry.getValue()));
        return new AdvancedNBTEntries(variantData);
    })));

    public int getData(CompoundTag tag) {
        for (CompoundTag compoundTag : variantData()) {
            boolean bool = true;
            for (String key : compoundTag.getAllKeys()) {
                if (!tag.contains(key) || tag.get(key) != compoundTag.get(key)) {
                    bool = false;
                }
                if (!bool) {
                    break;
                }
            }
            if (bool) {
                return variantData().indexOf(compoundTag);
            }
        }

        return -1;
    }

    public void fromData(CompoundTag tag, int data) {
        if (data < variantData().size()) {
            for (String key : variantData().get(data).getAllKeys()) {
                Tag value = variantData().get(data).get(key);
                if (value != null) {
                    tag.put(key, value);
                }
            }
        }
    }

    public int highestId() {
        return variantData().size() - 1;
    }
}
