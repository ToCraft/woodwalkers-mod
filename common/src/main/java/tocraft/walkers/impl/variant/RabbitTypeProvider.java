package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.TComponent;
import tocraft.walkers.api.variant.TypeProvider;

public class RabbitTypeProvider extends TypeProvider<Rabbit> {

    @Override
    public int getVariantData(Rabbit entity) {
        //#if MC>1182
        int id = entity.getVariant().id();
        //#else
        //$$ int id = entity.getRabbitType();
        //#endif
        if (id == 99) return 6;
        else return id;
    }

    @Override
    public Rabbit create(EntityType<Rabbit> type, Level level, int data) {
        Rabbit rabbit = new Rabbit(type, level);
        if (data == 6) data = 99;
        //#if MC>1182
        rabbit.setVariant(Rabbit.Variant.byId(data));
        //#else
        //$$ rabbit.setRabbitType(data);
        //#endif
        return rabbit;
    }

    @Override
    public int getFallbackData() {
        //#if MC>1182
        return Rabbit.Variant.BROWN.id();
        //#else
        //$$ return 0;
        //#endif
    }

    @Override
    public int getRange() {
        //#if MC>1182
        return Rabbit.Variant.values().length - 1;
        //#else
        //$$ return 6;
        //#endif
    }

    @Override
    public Component modifyText(Rabbit entity, MutableComponent text) {
        //#if MC>1182
        String variantName = entity.getVariant().getSerializedName();
        //#else
        //$$ String variantName = switch (entity.getRabbitType()) {
        //$$     case Rabbit.TYPE_BROWN -> "brown";
        //$$     case Rabbit.TYPE_WHITE -> "white";
        //$$     case Rabbit.TYPE_BLACK -> "black";
        //$$     case Rabbit.TYPE_WHITE_SPLOTCHED -> "white splotched";
        //$$     case Rabbit.TYPE_GOLD -> "gold";
        //$$     case Rabbit.TYPE_SALT -> "salt";
        //$$     case Rabbit.TYPE_EVIL -> "evil";
        //$$     default -> "";
        //$$ };
        //#endif
        return TComponent.literal(variantName + " ").append(text);
    }
}
