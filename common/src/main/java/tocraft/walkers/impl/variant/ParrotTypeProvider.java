package tocraft.walkers.impl.variant;

import com.google.common.collect.ImmutableMap;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

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
    public int getVariantData(Parrot entity) {
        return entity.getVariant();
    }

    @Override
    public Parrot create(EntityType<Parrot> type, Level world, int data) {
        Parrot parrot = new Parrot(type, world);
        parrot.setVariant(data);
        return parrot;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 4;
    }

    @Override
    public Component modifyText(Parrot parrot, MutableComponent text) {
        int variant = getVariantData(parrot);
        return new TextComponent(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}
