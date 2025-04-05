package tocraft.walkers.impl.variant;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import tocraft.walkers.api.variant.TypeProvider;
import tocraft.walkers.mixin.accessor.AxolotlEntityAccessor;

public class AxolotlTypeProvider extends TypeProvider<Axolotl> {

    @Override
    public int getVariantData(Axolotl entity) {
        return entity.getVariant().getId();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public Axolotl create(EntityType<Axolotl> type, Level world, @NotNull Player player, int data) {
        Axolotl axolotl = new Axolotl(type, world);
        ((AxolotlEntityAccessor) axolotl).callSetVariant(Axolotl.Variant.values()[data]);
        return axolotl;
    }

    @Override
    public int getFallbackData() {
        return Axolotl.Variant.LUCY.getId();
    }

    @Override
    public int size(Level level) {
        return Axolotl.Variant.values().length;
    }

    @Override
    public Component modifyText(Axolotl entity, MutableComponent text) {
        return Component.literal(formatTypePrefix(Axolotl.Variant.values()[getVariantData(entity)].getName()) + " ").append(text);
    }
}
