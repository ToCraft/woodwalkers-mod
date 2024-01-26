package tocraft.walkers.integrations.friendsandfoes;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.ability.impl.CowAbility;

public class FriendsAndFoesIntegration {
    public void initialize(String modid) {
        // Abilities
        AbilityRegistry.register((EntityType<? extends Mob>) Registry.ENTITY_TYPE.get(new ResourceLocation(modid, "moobloom")), new CowAbility<>());
    }
}
