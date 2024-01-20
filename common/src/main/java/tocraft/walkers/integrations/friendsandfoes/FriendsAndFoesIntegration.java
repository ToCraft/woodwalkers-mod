package tocraft.walkers.integrations.friendsandfoes;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.ability.impl.CowAbility;
import tocraft.walkers.integrations.api.AbstractIntegration;

public class FriendsAndFoesIntegration extends AbstractIntegration {
    @Override
    public void initialize(String modid) {
        // Abilities
        AbilityRegistry.register((EntityType<? extends Mob>) BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(modid, "moobloom")), new CowAbility<>());
    }
}
