package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.animal.Parrot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MushroomCow.class)
public interface MushroomCowAccessor {
    @Invoker
    void callSetVariant(MushroomCow.Variant variant);
}
