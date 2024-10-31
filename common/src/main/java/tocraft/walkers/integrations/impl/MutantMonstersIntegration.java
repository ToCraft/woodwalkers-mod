package tocraft.walkers.integrations.impl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
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
    private static final ResourceLocation CREEPER_MINION = ResourceLocation.fromNamespaceAndPath(MODID, "creeper_minion");
    private static final ResourceLocation MUTANT_CREEPER = ResourceLocation.fromNamespaceAndPath(MODID, "mutant_creeper");
    private static final ResourceLocation MUTANT_ENDERMAN = ResourceLocation.fromNamespaceAndPath(MODID, "mutant_enderman");
    private static final ResourceLocation MUTANT_SKELETON = ResourceLocation.fromNamespaceAndPath(MODID, "mutant_skeleton");
    private static final ResourceLocation MUTANT_SNOW_GOLEM = ResourceLocation.fromNamespaceAndPath(MODID, "mutant_snow_golem");
    private static final ResourceLocation MUTANT_ZOMBIE = ResourceLocation.fromNamespaceAndPath(MODID, "mutant_zombie");
    private static final ResourceLocation SPIDER_PIG = ResourceLocation.fromNamespaceAndPath(MODID, "spider_pig");


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

    private static EntityType<? extends Mob> getType(ResourceLocation id) {
        return (EntityType<? extends Mob>) BuiltInRegistries.ENTITY_TYPE.get(id).orElseThrow().value();
    }
}
