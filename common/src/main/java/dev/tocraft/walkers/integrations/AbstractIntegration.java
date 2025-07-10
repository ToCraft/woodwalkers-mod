package dev.tocraft.walkers.integrations;

import dev.tocraft.walkers.ability.AbilityRegistry;
import dev.tocraft.walkers.api.blacklist.EntityBlacklist;
import dev.tocraft.walkers.api.variant.TypeProviderRegistry;
import dev.tocraft.walkers.traits.TraitRegistry;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public abstract class AbstractIntegration {
    /**
     * Called to load the integration (only when the required mod is loaded)
     */
    public void initialize() {

    }

    /**
     * Called to add custom abilities via {@link AbilityRegistry AbilityRegistry}
     */
    public void registerAbilities() {

    }

    /**
     * Called to add custom traits via {@link TraitRegistry TraitRegistry}
     */
    public void registerTraits() {

    }

    /**
     * Called to add custom variants via {@link TypeProviderRegistry TypeProviderRegistry}
     */
    public void registerTypeProvider() {

    }

    /**
     * Called to blacklist mods via {@link EntityBlacklist EntityBlacklist}
     */
    public void registerEntityBlacklist() {

    }


    /**
     * called to modify the way entities attack a morphed player
     *
     * @param mob    the mob that wants to attack the player
     * @param target the player that is morphed
     * @return whether the mob cannot attack the morphed player
     */
    public boolean mightAttackInnocent(Mob mob, Player target) {
        return true;
    }
}
