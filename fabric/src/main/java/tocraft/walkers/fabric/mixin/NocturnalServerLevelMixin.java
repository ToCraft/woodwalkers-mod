package tocraft.walkers.fabric.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.NocturnalSkill;

import java.util.List;

@Mixin(ServerLevel.class)
public abstract class NocturnalServerLevelMixin {
    @Shadow
    @Final
    List<ServerPlayer> players;

    @ModifyArg(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setDayTime(J)V"))
    private long modifyIsDay(long original) {
        if (((Level) (Object) this).isDay() && this.players.stream().anyMatch(player -> player.isSleeping() && SkillRegistry.has(PlayerShape.getCurrentShape(player), NocturnalSkill.ID))) {
            return original + ((ServerLevel) (Object) this).getDayTime() % 24000L > 12000L ? 13000 : -11000;
        } else {
            return original;
        }
    }
}
