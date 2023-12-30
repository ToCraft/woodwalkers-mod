package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Slime;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.SlimeEntityAccessor;

public class SlimeTypeProvider extends TypeProvider<Slime> {

    @Override
    public int getVariantData(Slime entity) {
        return entity.getSize();
    }

    @Override
    public Slime create(EntityType<Slime> type, Level world, int data) {
        Slime slime = new Slime(type, world);
        ((SlimeEntityAccessor) slime).callSetSize(data + 1, true);
        return slime;
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
    public Component modifyText(Slime entity, MutableComponent text) {
        return new TextComponent(String.format("Size %d ", entity.getSize())).append(text);
    }
}
