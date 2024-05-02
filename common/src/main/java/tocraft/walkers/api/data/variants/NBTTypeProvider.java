package tocraft.walkers.api.data.variants;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// this is amazing
public class NBTTypeProvider<T extends LivingEntity> extends TypeProvider<T> {
    public static final Codec<NBTTypeProvider<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.optionalFieldOf("fallback", 0).forGetter(NBTTypeProvider::getFallbackData),
            Codec.INT.optionalFieldOf("range", -1).forGetter(NBTTypeProvider::getRange),
            Codec.list(NBTEntry.CODEC).fieldOf("nbt").forGetter(o -> o.nbtEntryList),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("names", new HashMap<>()).forGetter(o -> o.nameMap)
    ).apply(instance, instance.stable(NBTTypeProvider::new)));

    private final int fallback;
    private final int range;
    private final List<NBTEntry<?>> nbtEntryList;
    private final Map<String, String> nameMap;

    public NBTTypeProvider(int fallback, int range, List<NBTEntry<?>> nbtEntryList, Map<String, String> nameMap) {
        this.fallback = fallback;
        this.nbtEntryList = nbtEntryList;
        this.nameMap = nameMap;
        if (range >= 0 && fallback <= range) {
            this.range = range;
        } else {
            switch (nbtEntryList.get(0).nbtType.toUpperCase()) {
                case "BOOL", "BOOLEAN" -> this.range = 1;
                default -> this.range = fallback;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getVariantData(T entity) {
        CompoundTag tag = new CompoundTag();
        entity.save(tag);
        List<List<Integer>> validValues = new ArrayList<>();
        for (NBTEntry<?> nbtEntry : nbtEntryList) {
            if (tag.contains(nbtEntry.nbtField())) {
                switch (nbtEntry.nbtType().toUpperCase()) {
                    case "BOOL", "BOOLEAN" ->
                            validValues.add(((NBTEntry<Boolean>) nbtEntry).getIndex(tag.getBoolean(nbtEntry.nbtField())));
                    case "STRING" ->
                            validValues.add(((NBTEntry<String>) nbtEntry).getIndex(tag.getString(nbtEntry.nbtField())));
                    case "INT", "INTEGER" ->
                            validValues.add(((NBTEntry<Integer>) nbtEntry).getIndex(tag.getInt(nbtEntry.nbtField())));
                }
            }
        }

        // check if data applies to all nbt fields
        List<Integer> validData = getValidDataValues(validValues);
        if (!validData.isEmpty()) {
            if (validData.size() > 1) {
                Walkers.LOGGER.error("{}: found too much valid variant ids: {} for entity: {}", getClass().getSimpleName(), validData.toArray(Integer[]::new), entity.getType().getDescriptionId());
            }
            return validData.get(0);
        }
        Walkers.LOGGER.error("{}: No Variant for entity type {} found.", getClass().getSimpleName(), entity.getType().getDescriptionId());
        return getFallbackData();
    }

    @NotNull
    private static List<Integer> getValidDataValues(List<List<Integer>> validValues) {
        List<Integer> validData = new ArrayList<>();
        for (List<Integer> validValue : validValues) {
            for (Integer i : validValue) {
                boolean invalid = false;
                for (List<Integer> value : validValues) {
                    if (!value.contains(i)) {
                        invalid = true;
                        break;
                    }
                }
                if (!invalid) {
                    if (!validData.contains(i)) {
                        validData.add(i);
                    }
                }
            }
        }
        return validData;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T create(EntityType<T> type, Level world, int data) {
        CompoundTag tag = new CompoundTag();

        for (NBTEntry<?> nbtEntry : nbtEntryList) {
            Object value = nbtEntry.getValue(data);
            if (value instanceof Integer intValue) {
                tag.putInt(nbtEntry.nbtField(), intValue);
            } else if (value instanceof String stringValue) {
                tag.putString(nbtEntry.nbtField(), stringValue);
            } else if (value instanceof Boolean booleanValue) {
                tag.putBoolean(nbtEntry.nbtField(), booleanValue);
            } else if (value == null) {
                Walkers.LOGGER.error("{}: variant parameter for {} not found.", getClass().getSimpleName(), type.getDescriptionId());
            }
        }

        CompoundTag compoundTag = tag.copy();
        compoundTag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(type).toString());
        return (T) EntityType.loadEntityRecursive(compoundTag, world, entity -> entity);
    }

    @Override
    public int getFallbackData() {
        return fallback;
    }

    @Override
    public int getRange() {
        return range;
    }

    @Override
    public Component modifyText(T entity, MutableComponent text) {
        if (nameMap.containsKey(String.valueOf(getVariantData(entity))))
            return Component.translatable(nameMap.get(String.valueOf(getVariantData(entity))), text);
        else
            return text;
    }

    @SuppressWarnings("unchecked")
    public record NBTEntry<T>(String nbtType, String nbtField, Map<Integer, T> parameterList, boolean isMutable) {
        public static final Codec<NBTEntry<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                Codec.STRING.fieldOf("nbt_type").forGetter(NBTEntry::nbtType),
                Codec.STRING.fieldOf("nbt_field").forGetter(NBTEntry::nbtField),
                Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("parameters", new HashMap<>()).forGetter(o -> {
                    Map<String, String> parameters = new HashMap<>();
                    o.parameterList().forEach((key, value) -> parameters.put(String.valueOf(key), String.valueOf(value)));
                    return parameters;
                }),
                Codec.BOOL.optionalFieldOf("is_mutable", false).forGetter(NBTEntry::isMutable)
        ).apply(instance, instance.stable((nbtType, nbtField, parameters, isMutable) -> {
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
                    MutableComponent tagDataMutable = Component.Serializer.fromJsonLenient((String) value);
                    if (tagDataMutable != null) {
                        value = (T) tagDataMutable.getString();
                    }
                }
                for (int i : parameterList.keySet()) {
                    T parameterT = parameterList.get(i);
                    if (isMutable && parameterT instanceof String) {
                        MutableComponent parameterMutable = Component.Serializer.fromJsonLenient((String) parameterT);
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
}
