package dev.tocraft.walkers.integrations.impl;

import dev.tocraft.walkers.ability.AbilityRegistry;
import dev.tocraft.walkers.ability.impl.generic.ExplosionAbility;
import dev.tocraft.walkers.ability.impl.generic.JumpAbility;
import dev.tocraft.walkers.ability.impl.generic.ShootSnowballAbility;
import dev.tocraft.walkers.ability.impl.generic.TeleportationAbility;
import dev.tocraft.walkers.integrations.AbstractIntegration;
import dev.tocraft.walkers.traits.TraitRegistry;
import dev.tocraft.walkers.traits.impl.BurnInDaylightTrait;
import dev.tocraft.walkers.traits.impl.ClimbBlocksTrait;
import dev.tocraft.walkers.traits.impl.TemperatureTrait;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

@SuppressWarnings("unchecked")
public class MutantMonstersIntegration extends AbstractIntegration {
    public static final String MODID = "mutantmonsters";
    private static final Identifier CREEPER_MINION = Identifier.fromNamespaceAndPath(MODID, "creeper_minion");
    private static final Identifier MUTANT_CREEPER = Identifier.fromNamespaceAndPath(MODID, "mutant_creeper");
    private static final Identifier MUTANT_ENDERMAN = Identifier.fromNamespaceAndPath(MODID, "mutant_enderman");
    private static final Identifier MUTANT_SKELETON = Identifier.fromNamespaceAndPath(MODID, "mutant_skeleton");
    private static final Identifier MUTANT_SNOW_GOLEM = Identifier.fromNamespaceAndPath(MODID, "mutant_snow_golem");
    private static final Identifier MUTANT_ZOMBIE = Identifier.fromNamespaceAndPath(MODID, "mutant_zombie");
    private static final Identifier SPIDER_PIG = Identifier.fromNamespaceAndPath(MODID, "spider_pig");


    @Override
    public void registerTraits() {
        TraitRegistry.registerByType((EntityType<? extends LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(MUTANT_SKELETON).orElseThrow().value(), new BurnInDaylightTrait<>());
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

    private static EntityType<? extends Mob> getType(Identifier id) {
        return (EntityType<? extends Mob>) BuiltInRegistries.ENTITY_TYPE.get(id).orElseThrow().value();
    }
}
