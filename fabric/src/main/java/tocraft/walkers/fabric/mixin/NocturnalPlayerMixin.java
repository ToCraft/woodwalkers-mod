package tocraft.walkers.fabric.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.NocturnalSkill;

@Mixin(Player.class)
public abstract class NocturnalPlayerMixin {
    @WrapOperation(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;isDay()Z"))
    private boolean modifyIsDay(Level level, Operation<Boolean> original) {
        boolean isDay = original.call(level);
        if (SkillRegistry.has(PlayerShape.getCurrentShape((Player) (Object) this), NocturnalSkill.ID)) {
            return !isDay;
        }

        return isDay;
    }
}
