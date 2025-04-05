package tocraft.walkers.impl.variant;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.ParrotAccessor;

public class ParrotTypeProvider extends TypeProvider<Parrot> {

    private static final ImmutableMap<Integer, String> PREFIX_BY_ID = ImmutableMap
            .<Integer, String>builder()
            .put(0, "Red Blue")
            .put(1, "Blue")
            .put(2, "Green")
            .put(3, "Yellow Blue")
            .put(4, "Gray")
            .build();

    @Override
    public int getVariantData(@NotNull Parrot entity) {
        return entity.getVariant().getId();
    }

    @Override
    public Parrot create(EntityType<Parrot> type, Level world, @NotNull Player player, int data) {
        Parrot parrot = new Parrot(type, world);
        ((ParrotAccessor) parrot).callSetVariant(Parrot.Variant.byId(data));
        return parrot;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int size(Level level) {
        return Parrot.Variant.values().length;
    }

    @Override
    public Component modifyText(Parrot parrot, MutableComponent text) {
        int variant = getVariantData(parrot);
        return Component.literal(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}
