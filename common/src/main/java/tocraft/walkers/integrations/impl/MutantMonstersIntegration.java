package tocraft.walkers.integrations.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import tocraft.craftedcore.patched.Identifier;
import tocraft.walkers.Walkers;
import tocraft.walkers.ability.AbilityRegistry;
import tocraft.walkers.ability.impl.generic.ExplosionAbility;
import tocraft.walkers.ability.impl.generic.JumpAbility;
import tocraft.walkers.ability.impl.generic.ShootSnowballAbility;
import tocraft.walkers.ability.impl.generic.TeleportationAbility;
import tocraft.walkers.integrations.AbstractIntegration;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.BurnInDaylightTrait;
import tocraft.walkers.traits.impl.ClimbBlocksTrait;
import tocraft.walkers.traits.impl.TemperatureTrait;

@SuppressWarnings("unchecked")
public class MutantMonstersIntegration extends AbstractIntegration {
    public static final String MODID = "mutantmonsters";
    private static final ResourceLocation CREEPER_MINION = Identifier.parse(MODID, "creeper_minion");
    private static final ResourceLocation MUTANT_CREEPER = Identifier.parse(MODID, "mutant_creeper");
    private static final ResourceLocation MUTANT_ENDERMAN = Identifier.parse(MODID, "mutant_enderman");
    private static final ResourceLocation MUTANT_SKELETON = Identifier.parse(MODID, "mutant_skeleton");
    private static final ResourceLocation MUTANT_SNOW_GOLEM = Identifier.parse(MODID, "mutant_snow_golem");
    private static final ResourceLocation MUTANT_ZOMBIE = Identifier.parse(MODID, "mutant_zombie");
    private static final ResourceLocation SPIDER_PIG = Identifier.parse(MODID, "spider_pig");


    @Override
    public void registerTraits() {
        TraitRegistry.registerByType((EntityType<? extends LivingEntity>) Walkers.getEntityTypeRegistry().get(MUTANT_SKELETON), new BurnInDaylightTrait<>());
        TraitRegistry.registerByType(getType(MUTANT_SNOW_GOLEM), new TemperatureTrait<>());
        TraitRegistry.registerByType(getType(MUTANT_ZOMBIE), new BurnInDaylightTrait<>());
        TraitRegistry.registerByType(getType(SPIDER_PIG), new ClimbBlocksTrait<>());
    }

    @Override
    public void registerAbilities() {
        AbilityRegistry.registerByType(getType(CREEPER_MINION), new ExplosionAbility<>());
        AbilityRegistry.registerByType(getType(MUTANT_CREEPER), new ExplosionAbility<>(4));
        AbilityRegistry.registerByType(getType(MUTANT_ENDERMAN), new TeleportationAbility<>());
        AbilityRegistry.registerByType(getType(MUTANT_SNOW_GOLEM), new ShootSnowballAbility<>());
        AbilityRegistry.registerByType(getType(SPIDER_PIG), new JumpAbility<>());
    }

    private static EntityType<? extends Mob> getType(ResourceLocation id) {
        return (EntityType<? extends Mob>) Walkers.getEntityTypeRegistry().get(id);
    }
}
