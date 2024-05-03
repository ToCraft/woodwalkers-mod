package tocraft.walkers.api.data.variants;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;

import java.util.HashMap;
import java.util.Map;

public record AdvancedNBTEntries(Map<Integer, Map<String, AdvancedNBT>> variantDataList) {
    public static final Codec<AdvancedNBTEntries> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.unboundedMap(Codec.STRING, Codec.unboundedMap(Codec.STRING, Codec.either(Codec.STRING, Codec.either(Codec.INT, Codec.BOOL)))).fieldOf("variants").forGetter(o -> {
                Map<String, Map<String, Either<String, Either<Integer, Boolean>>>> map = new HashMap<>();
                o.variantDataList().forEach((key, value) -> {
                    Map<String, Either<String, Either<Integer, Boolean>>> m = new HashMap<>();
                    value.forEach((k, v) -> m.put(k, v.toEither()));
                    map.put(String.valueOf(key), m);
                });
                return map;
            })
    ).apply(instance, instance.stable((variantDataList) -> new AdvancedNBTEntries(new HashMap<>() {
        {
            variantDataList.forEach((id, map) -> map.forEach((nbtField, data) -> {
                Map<String, AdvancedNBT> list = containsKey(Integer.valueOf(id)) ? get(Integer.valueOf(id)) : new HashMap<>();
                list.put(nbtField, new AdvancedNBT(data));
                put(Integer.valueOf(id), list);
            }));
        }
    }))));

    public int getData(CompoundTag tag) {
        for (Map.Entry<Integer, Map<String, AdvancedNBT>> variantData : variantDataList.entrySet()) {
            boolean bool = false;
            for (Map.Entry<String, AdvancedNBT> variantNBT : variantData.getValue().entrySet()) {
                if (tag.contains(variantNBT.getKey())) {
                    switch (variantNBT.getValue().nbtType().toUpperCase()) {
                        case "BOOL", "BOOLEAN" ->
                                bool = tag.getBoolean(variantNBT.getKey()) == (boolean) variantNBT.getValue().value();
                        case "INT", "INTEGER" ->
                                bool = tag.getInt(variantNBT.getKey()) == (int) variantNBT.getValue().value();
                        default -> bool = tag.getString(variantNBT.getKey()).equals(variantNBT.getValue().value());
                    }
                } else {
                    bool = false;
                }
                if (!bool) {
                    break;
                }
            }
            if (bool) {
                return variantData.getKey();
            }
        }


        // if this point is reached, no valid variant could be found. Check wherever there is an "empty" variant
        for (Map.Entry<Integer, Map<String, AdvancedNBT>> variantData : variantDataList.entrySet()) {
            if (variantData.getValue().isEmpty()) {
                return variantData.getKey();
            }
        }

        return -1;
    }

    public void fromData(CompoundTag tag, int data) {
        if (variantDataList.containsKey(data)) {
            variantDataList.get(data).forEach((key, value) -> {
                switch (value.nbtType().toUpperCase()) {
                    case "BOOL", "BOOLEAN" -> tag.putBoolean(key, (boolean) value.value());
                    case "INT", "INTEGER" -> tag.putInt(key, (int) value.value());
                    default -> tag.putString(key, (String) value.value());
                }
            });
        }
    }

    public int highestId() {
        return variantDataList().size() - 1;
    }

    public record AdvancedNBT(String nbtType, Object value) {
        public AdvancedNBT(Either<String, Either<Integer, Boolean>> either) {
            this(either.left().isPresent() ? "STRING" : either.right().orElseThrow().left().isPresent() ? "INTEGER" : "BOOLEAN", either.left().isPresent() ? either.left().get() : either.right().orElseThrow().left().isPresent() ? either.right().orElseThrow().left().get() : either.right().orElseThrow().right().orElseThrow());
        }

        public Either<String, Either<Integer, Boolean>> toEither() {
            return switch (nbtType().toUpperCase()) {
                case "BOOL", "BOOLEAN" -> Either.right(Either.right((boolean) value()));
                case "INT", "INTEGER" -> Either.right(Either.left((int) value()));
                default -> Either.left((String) value());
            };
        }
    }
}