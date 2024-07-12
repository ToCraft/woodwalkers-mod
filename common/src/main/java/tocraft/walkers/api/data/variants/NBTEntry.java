package tocraft.walkers.api.data.variants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public record NBTEntry<T>(String nbtType, String nbtField, Map<Integer, T> parameterList, boolean isMutable) {
    public static final Codec<NBTEntry<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(Codec.STRING.fieldOf("nbt_type").forGetter(NBTEntry::nbtType), Codec.STRING.fieldOf("nbt_field").forGetter(NBTEntry::nbtField), Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("parameters", new HashMap<>()).forGetter(o -> {
        Map<String, String> parameters = new HashMap<>();
        o.parameterList().forEach((key, value) -> parameters.put(String.valueOf(key), String.valueOf(value)));
        return parameters;
    }), Codec.BOOL.optionalFieldOf("is_mutable", false).forGetter(NBTEntry::isMutable)).apply(instance, instance.stable((nbtType, nbtField, parameters, isMutable) -> {
        switch (nbtType.toUpperCase()) {
            case "INT", "INTEGER" -> {
                return new NBTEntry<>(nbtType, nbtField, new HashMap<>() {
                    {
                        parameters.forEach((key, value) -> put(Integer.valueOf(key), Integer.valueOf(value)));
                    }
                }, isMutable);
            }
            case "BOOL", "BOOLEAN" -> {
                return new NBTEntry<>(nbtType, nbtField, new HashMap<>() {
                    {
                        parameters.forEach((key, value) -> put(Integer.valueOf(key), Boolean.valueOf(value)));
                    }
                }, isMutable);
            }
            default -> {
                return new NBTEntry<>(nbtType, nbtField, new HashMap<>() {
                    {
                        parameters.forEach((key, value) -> put(Integer.valueOf(key), value));
                    }
                }, isMutable);
            }
        }
    })));

    public T getValue(int index) {
        if (parameterList.containsKey(index)) {
            return parameterList.get(index);
        }
        switch (nbtType.toUpperCase()) {
            case "INT", "INTEGER" -> {
                return (T) (Object) index;
            }
            case "BOOL", "BOOLEAN" -> {
                // check if index is odd
                if (index == 1) return (T) (Object) true;
                else return (T) (Object) false;
            }
        }
        return null;
    }

    public List<Integer> getIndex(T value) {
        List<Integer> index = new ArrayList<>();
        if (!parameterList.isEmpty()) {
            if (isMutable && value instanceof String) {
                //#if MC>=1205
                MutableComponent tagDataMutable = Component.Serializer.fromJsonLenient((String) value, RegistryAccess.EMPTY);
                //#else
                //$$ MutableComponent tagDataMutable = Component.Serializer.fromJsonLenient((String) value);
                //#endif
                if (tagDataMutable != null) {
                    value = (T) tagDataMutable.getString();
                }
            }
            for (int i : parameterList.keySet()) {
                T parameterT = parameterList.get(i);
                if (isMutable && parameterT instanceof String) {
                    //#if MC>=1205
                    MutableComponent parameterMutable = Component.Serializer.fromJsonLenient((String) parameterT, RegistryAccess.EMPTY);
                    //#else
                    //$$ MutableComponent parameterMutable = Component.Serializer.fromJsonLenient((String) parameterT);
                    //#endif
                    if (parameterMutable != null) {
                        parameterT = (T) parameterMutable.getString();
                    }
                }
                if (value.equals(parameterT)) {
                    index.add(i);
                }
            }
        }
        if (index.isEmpty()) {
            switch (nbtType.toUpperCase()) {
                case "BOOL", "BOOLEAN" -> {
                    if ((Boolean) value) {
                        index.add(1);
                    } else index.add(0);
                }
                case "INT", "INTEGER" -> index.add((Integer) value);
            }
        }
        return index;
    }
}
