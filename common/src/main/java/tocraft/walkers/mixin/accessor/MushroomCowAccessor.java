package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.MushroomCow;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MushroomCow.class)
public interface MushroomCowAccessor {

    @Invoker
    void callSetMushroomType(MushroomCow.MushroomType variant);
}
