package tocraft.walkers.integrations.impl;

import com.mojang.datafixers.util.Either;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Wolf;
import tocraft.walkers.api.data.variants.NBTEntry;
import tocraft.walkers.api.data.variants.NBTTypeProvider;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.api.variant.TypeProviderRegistry;
import tocraft.walkers.integrations.AbstractIntegration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackportedWolvesIntegration extends AbstractIntegration {
    public static final String MODID = "backported_wolves";
    private final Map<String, String> WOLF_VARIANT_NAME_MAP = new HashMap<>() {
        {
            put("0", "Plae %s");
            put("1", "Woods %s");
            put("2", "Ashen %s");
            put("3", "Black %s");
            put("4", "Chestnut %s");
            put("5", "Rusty %s");
            put("6", "Spotted %s");
            put("7", "Striped %s");
            put("8", "Snowy %s");
        }
    };
    private final NBTEntry<Wolf> WOLF_NBT_ENTRY = new NBTEntry<>("INTEGER", "Variant", new HashMap<>(), false);
    private final TypeProvider<Wolf> WOLF_VARIANT_PROVIDER = new NBTTypeProvider<>(0, 8, Either.left(List.of(WOLF_NBT_ENTRY)), WOLF_VARIANT_NAME_MAP);

    @Override
    public void registerTypeProvider() {
        TypeProviderRegistry.register(EntityType.WOLF, WOLF_VARIANT_PROVIDER);
    }
}
