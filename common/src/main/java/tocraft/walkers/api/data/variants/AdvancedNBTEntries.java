package tocraft.walkers.api.data.variants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import tocraft.walkers.Walkers;

import java.util.*;

public record AdvancedNBTEntries(List<CompoundTag> variantDataList) {
    public static final Codec<AdvancedNBTEntries> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(Codec.STRING, CompoundTag.CODEC).fieldOf("variants").forGetter(o -> {
                Map<String, CompoundTag> map = new HashMap<>();
                for (int i = 0; i < o.variantDataList().size(); i++) {
                    map.put(String.valueOf(i), o.variantDataList().get(i));
                }
                return map;
            })
    ).apply(instance, instance.stable((variantDataList) -> new AdvancedNBTEntries(new ArrayList<>() {
        {
            variantDataList.forEach((i, nbt) -> add(Integer.parseInt(i), nbt));
        }
    }))));

    public int getData(CompoundTag tag) {
        for (CompoundTag compoundTag : variantDataList()) {
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
                return variantDataList().indexOf(compoundTag);
            }
        }

        return -1;
    }

    public void fromData(CompoundTag tag, int data) {
        if (data < variantDataList().size()) {
            for (String key : variantDataList().get(data).getAllKeys()) {
                Tag value = variantDataList().get(data).get(key);
                if (value != null) {
                    tag.put(key, value);
                }
            }
        }
    }

    public int highestId() {
        return variantDataList().size() - 1;
    }
}