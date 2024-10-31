package tocraft.walkers.api.data.variants;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.*;

// this is amazing
public class NBTTypeProvider<T extends LivingEntity> extends TypeProvider<T> {
    public static final Codec<NBTTypeProvider<?>> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            Codec.INT.optionalFieldOf("fallback", 0).forGetter(NBTTypeProvider::getFallbackData),
            Codec.INT.optionalFieldOf("range", -1).forGetter(NBTTypeProvider::getRange),
            Codec.either(Codec.list(NBTEntry.CODEC), AdvancedNBTEntries.CODEC).fieldOf("nbt").forGetter(o -> o.nbtEntryList),
            Codec.unboundedMap(Codec.STRING, Codec.STRING).optionalFieldOf("names", new HashMap<>()).forGetter(o -> o.nameMap)
    ).apply(instance, instance.stable(NBTTypeProvider::new)));

    private final int fallback;
    private final int range;
    private final Either<List<NBTEntry<?>>, AdvancedNBTEntries> nbtEntryList;
    private final Map<String, String> nameMap;

    public NBTTypeProvider(int fallback, int range, Either<List<NBTEntry<?>>, AdvancedNBTEntries> nbtEntryList, Map<String, String> nameMap) {
        this.fallback = fallback;
        this.nbtEntryList = nbtEntryList;
        this.nameMap = nameMap;
        if (range >= 0 && fallback <= range) {
            this.range = range;
        } else if (nbtEntryList.left().isPresent()) {
            switch (nbtEntryList.left().get().getFirst().nbtType().toUpperCase()) {
                case "BOOL", "BOOLEAN" -> this.range = 1;
                default -> this.range = fallback;
            }
        } else if (nbtEntryList.right().isPresent()) {
            this.range = nbtEntryList.right().get().highestId();
        } else {
            this.range = 0;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public int getVariantData(T entity) {
        CompoundTag tag = new CompoundTag();
        entity.save(tag);
        List<List<Integer>> validValues = new ArrayList<>();
        if (nbtEntryList.left().isPresent()) {
            for (NBTEntry<?> nbtEntry : nbtEntryList.left().get()) {
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
        }

        // support AdvancedNBTEntries
        else if (nbtEntryList.right().isPresent()) {
            validValues.add(List.of(nbtEntryList.right().get().getData(tag)));
        }

        // check if data applies to all nbt fields
        List<Integer> validData = getValidDataValues(validValues);
        if (!validData.isEmpty()) {
            if (validData.size() > 1) {
                Walkers.LOGGER.error("{}: found too much valid variant ids: {} for entity: {}", getClass().getSimpleName(), validData.toArray(Integer[]::new), entity.getType().getDescriptionId());
            }
            return validData.getFirst();
        } else {
            Walkers.LOGGER.error("{}: No Variant for entity type {} found.", getClass().getSimpleName(), entity.getType().getDescriptionId());
            return getFallbackData();
        }
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

        if (nbtEntryList.left().isPresent()) {
            for (NBTEntry<?> nbtEntry : nbtEntryList.left().get()) {
                Object value = nbtEntry.getValue(data);
                switch (value) {
                    case Integer intValue -> tag.putInt(nbtEntry.nbtField(), intValue);
                    case String stringValue -> tag.putString(nbtEntry.nbtField(), stringValue);
                    case Boolean booleanValue -> tag.putBoolean(nbtEntry.nbtField(), booleanValue);
                    case null ->
                            Walkers.LOGGER.error("{}: variant parameter for {} not found.", getClass().getSimpleName(), type.getDescriptionId());
                    default -> {
                    }
                }
            }
        }
        // support AdvancedNBTEntries
        else if (nbtEntryList.right().isPresent()) {
            nbtEntryList.right().get().fromData(tag, data);
        }

        CompoundTag compoundTag = tag.copy();
        compoundTag.putString("id", Objects.requireNonNull(EntityType.getKey(type)).toString());
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
}
