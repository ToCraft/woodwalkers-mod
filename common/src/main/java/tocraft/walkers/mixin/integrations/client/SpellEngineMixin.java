package tocraft.walkers.mixin.integrations.client;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import tocraft.walkers.api.PlayerShape;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;

@SuppressWarnings("UnresolvedMixinReference")
@Pseudo
@Environment(EnvType.CLIENT)
@Mixin(targets = "net.spell_engine.client.compatibility.FirstPersonModelCompatibility", remap = false)
public class SpellEngineMixin
{
    @WrapMethod(
        method = "isActive", require = 0
    )
    private static boolean wrap_isActive(Operation<Boolean> original)
    {
        Player player = Minecraft.getInstance().player;
        if (player != null &&
            PlayerShape.getCurrentShape(player) != null)
        {
            return true;
        }
        return original.call();
    }
}
