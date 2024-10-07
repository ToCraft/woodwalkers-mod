package tocraft.walkers.neoforge;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.AquaticTrait;
import tocraft.walkers.traits.impl.FlyingTrait;

@SuppressWarnings("unused")
@Mod(Walkers.MODID)
public class WalkersNeoForge {
    public WalkersNeoForge() {
        new Walkers().initialize();

        if (FMLEnvironment.dist.isClient()) {
            new WalkersClient().initialize();
        }

        NeoForge.EVENT_BUS.addListener(this::event);
    }

    private void event(PlayerEvent.BreakSpeed event) {
        @SuppressWarnings("RedundantCast") Player player = (Player) event.getEntity();

        //#if MC>1194
        if (!player.onGround()) {
            //#else
            //$$ if (!player.isOnGround()) {
            //#endif
            if (TraitRegistry.has(PlayerShape.getCurrentShape(player), FlyingTrait.ID)) {
                event.setNewSpeed(event.getNewSpeed() * 5);
            }
            else if (player.isEyeInFluid(FluidTags.WATER)) {
                for (ShapeTrait<LivingEntity> aquaticTrait : TraitRegistry.get(PlayerShape.getCurrentShape(player), AquaticTrait.ID)) {
                    if (((AquaticTrait<LivingEntity>) aquaticTrait).isAquatic) {
                        event.setNewSpeed(event.getNewSpeed() * 5);
                        break;
                    }
                }
            }
        }

        //noinspection deprecation
        if (player.isEyeInFluid(FluidTags.WATER)) {
            //#if MC<1210
            //$$ if (!EnchantmentHelper.hasAquaAffinity(player))
            //#endif
            for (ShapeTrait<LivingEntity> aquaticTrait : TraitRegistry.get(PlayerShape.getCurrentShape(player), AquaticTrait.ID)) {
                if (((AquaticTrait<LivingEntity>) aquaticTrait).isAquatic) {
                    event.setNewSpeed(event.getNewSpeed() * 5);
                    break;
                }
            }
        }
    }
}
