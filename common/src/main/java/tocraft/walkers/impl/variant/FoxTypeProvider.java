package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Fox;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.TComponent;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.FoxEntityAccessor;

public class FoxTypeProvider extends TypeProvider<Fox> {

    @Override
    public int getVariantData(Fox entity) {
        //#if MC>1182
        return entity.getVariant().getId();
        //#else
        //$$ return entity.getFoxType().getId();
        //#endif
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Fox create(EntityType<Fox> type, Level world, int data) {
        Fox fox = new Fox(type, world);
        //#if MC>1182
        ((FoxEntityAccessor) fox).callSetVariant(Fox.Type.byId(data));
        //#else
        //$$ ((FoxEntityAccessor) fox).callSetFoxType(Fox.Type.byId(data));
        //#endif
        return fox;
    }

    @Override
    public int getFallbackData() {
        return Fox.Type.RED.getId();
    }

    @Override
    public int getRange() {
        return Fox.Type.values().length - 1;
    }

    @Override
    public Component modifyText(Fox entity, MutableComponent text) {
        return TComponent.literal(formatTypePrefix(Fox.Type.byId(getVariantData(entity)).name()) + " ").append(text);
    }
}
