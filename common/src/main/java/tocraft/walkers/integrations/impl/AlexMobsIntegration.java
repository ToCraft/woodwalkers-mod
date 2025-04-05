package tocraft.walkers.integrations.impl;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import tocraft.walkers.integrations.AbstractIntegration;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.FearedTrait;
import tocraft.walkers.traits.impl.PreyTrait;

import java.util.List;

@SuppressWarnings("unchecked")
public class AlexMobsIntegration extends AbstractIntegration {
    public static final String MODID = "alexsmobs";

    @Override
    public void registerTraits() {
        // PreyTrait
        registerAlexMobsTargetTagTrait("anaconda", 1, 200);
        registerAlexMobsTargetTagTrait("bald_eagle", 4, 55);
        registerAlexMobsTargetTagTrait("cachalot_whale", 2, 30);
        registerAlexMobsTargetTagTrait("crimson-mosquito", 2, 50);
        registerAlexMobsTargetTagTrait("crocodile", 5, 180);
        TraitRegistry.registerByType(getType("fly"), new PreyTrait<>(List.of(), List.of(), List.of(), List.of(getTag("fly_hurt_targets")), 3, 5));
        registerAlexMobsTargetTagTrait("froststalker", 3, 80);
        registerAlexMobsTargetTagTrait("giant-squid", 3, 70);
        registerAlexMobsTargetTagTrait("komodo-dragon", 8, 180);
        registerAlexMobsTargetTagTrait("mantis_shrimp", 3, 120);
        registerAlexMobsTargetTagTrait("orca", 3, 200);
        registerAlexMobsTargetTagTrait("snow_leopard", 2, 10);
        registerAlexMobsTargetTagTrait("sunbird_scorch", 3, 5);
        registerAlexMobsTargetTagTrait("tiger", 4, 220);
        registerAlexMobsTargetTagTrait("warped_toad", 4, 50);
        TraitRegistry.registerByPredicate(entity -> (entity instanceof Enemy && !(entity instanceof Creeper) && !(entity.getType().getCategory().name().contains("water") && entity.isInWater()) && !entity.getType().is(getTag("bunfungus_ignores"))), new PreyTrait<>(List.of(), List.of(getType("bunfungus")), List.of(), List.of(), 3, 5));
        // FearedTrait
        registerAlexMobsFearTagTrait("mimic_octopus");
        registerAlexMobsFearTagTrait("skunk");
    }

    private void registerAlexMobsTargetTagTrait(String entityId, int priority, int randInt) {
        TraitRegistry.registerByTag(getTag(entityId + "_targets"), new PreyTrait<>(List.of(), List.of(getType(entityId)), List.of(), List.of(), priority, randInt));
    }

    private void registerAlexMobsFearTagTrait(String entityId) {
        TraitRegistry.registerByTag((getTag(entityId + "_fears")), (FearedTrait<Mob>) FearedTrait.ofFearfulType(getType(entityId)));
    }

    private static EntityType<Mob> getType(String entityId) {
        return (EntityType<Mob>) BuiltInRegistries.ENTITY_TYPE.get(ResourceLocation.fromNamespaceAndPath(MODID, entityId)).orElseThrow().value();
    }

    private static TagKey<EntityType<?>> getTag(String tagId) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(MODID, tagId));
    }
}
