package tocraft.walkers.mixin.accessor;

import net.minecraft.world.entity.animal.horse.AbstractHorse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractHorse.class)
public interface AbstractHorseAccessor {
    @Accessor
    void setEatAnim(float eatAnim);

    @Accessor
    void setEatAnimO(float eatAnimO);

    @Accessor
    void setStandAnim(float standAnim);

    @Accessor
    void setStandAnimO(float standAnimO);

    @Accessor
    void setMouthAnim(float mouthAnim);

    @Accessor
    void setMouthAnimO(float mouthAnimO);
}
