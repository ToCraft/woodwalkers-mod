package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class GoatTypeProvider extends TypeProvider<Goat> {

    @Override
    public int getVariantData(Goat entity) {
        return entity.isScreamingGoat() ? 1 : 0;
    }

    @Override
    public Goat create(EntityType<Goat> type, Level level, int data) {
        Goat goat = new Goat(type, level);
        goat.setScreamingGoat(data > 0);
        return goat;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 1;
    }

    @Override
    public Component modifyText(Goat entity, MutableComponent text) {
        if (entity.isScreamingGoat()) return Component.literal("Screaming ").append(text);
        else return text;
    }
}