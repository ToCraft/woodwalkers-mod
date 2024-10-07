package tocraft.walkers.forge;

import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;
import tocraft.walkers.Walkers;
import tocraft.walkers.WalkersClient;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.traits.ShapeTrait;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.AquaticTrait;
import tocraft.walkers.traits.impl.FlyingTrait;

@SuppressWarnings("unused")
@Mod(Walkers.MODID)
public class WalkersForge {

    public WalkersForge() {
        new Walkers().initialize();

        if (FMLEnvironment.dist.isClient()) {
            new WalkersClient().initialize();
        }

        MinecraftForge.EVENT_BUS.addListener(this::event);
    }

    @SuppressWarnings("RedundantSuppression")
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
        if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)) {
            for (ShapeTrait<LivingEntity> aquaticTrait : TraitRegistry.get(PlayerShape.getCurrentShape(player), AquaticTrait.ID)) {
                if (((AquaticTrait<LivingEntity>) aquaticTrait).isAquatic) {
                    event.setNewSpeed(event.getNewSpeed() * 5);
                    break;
                }
            }
        }
    }
}
