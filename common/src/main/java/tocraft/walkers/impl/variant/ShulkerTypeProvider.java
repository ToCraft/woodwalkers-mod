package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import tocraft.craftedcore.patched.TComponent;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.ShulkerAccessor;

import java.util.Optional;

public class ShulkerTypeProvider extends TypeProvider<Shulker> {

    @Override
    public int getVariantData(Shulker entity) {
        //#if MC>1182
        Optional<DyeColor> color = entity.getVariant();
        //#else
        //$$ Optional<DyeColor> color = Optional.ofNullable(entity.getColor());
        //#endif
        return color.map(DyeColor::getId).orElse(16);
    }

    @Override
    public Shulker create(EntityType<Shulker> type, Level world, int data) {
        Shulker shulker = new Shulker(type, world);
        if (data < 16) {
            //#if MC>1182
            shulker.setVariant(Optional.of(DyeColor.byId(data)));
            //#else
            //$$ ((ShulkerAccessor) shulker).callSetColor(DyeColor.byId(data));
            //#endif
        }
        return shulker;
    }

    @Override
    public int getFallbackData() {
        return 16;
    }

    @Override
    public int getRange() {
        return 16;
    }

    @Override
    public Component modifyText(Shulker entity, MutableComponent text) {
        int data = getVariantData(entity);
        String prefix = data < 16 ? formatTypePrefix(DyeColor.byId(getVariantData(entity)).getName()) : "Natural";
        return TComponent.literal(prefix + " ").append(text);
    }
}
