package tocraft.walkers.integrations.impl;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import tocraft.craftedcore.patched.Identifier;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.integrations.AbstractIntegration;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.FearedTrait;
import tocraft.walkers.traits.impl.PreyTrait;

import java.util.List;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
public class AlexMobsIntegration extends AbstractIntegration {
    public static final String MODID = "alexsmobs";

    @Override
    public void registerTraits() {
        // PreyTrait
        registerAlexMobsTargetTagTrait("anaconda");
        registerAlexMobsTargetTagTrait("bald_eagle");
        registerAlexMobsTargetTagTrait("cachalot_whale");
        registerAlexMobsTargetTagTrait("crimson-mosquito");
        registerAlexMobsTargetTagTrait("crocodile");
        registerAlexMobsTargetTagTrait("froststalker");
        registerAlexMobsTargetTagTrait("giant-squid");
        registerAlexMobsTargetTagTrait("komodo-dragon");
        registerAlexMobsTargetTagTrait("mantis_shrimp");
        registerAlexMobsTargetTagTrait("orca");
        registerAlexMobsTargetTagTrait("snow_leopard");
        registerAlexMobsTargetTagTrait("sunbird_scorch");
        registerAlexMobsTargetTagTrait("tiger");
        registerAlexMobsTargetTagTrait("warped_toad");
        TraitRegistry.registerByType(getType("bunfungus"), new PreyTrait<>(List.of(entity -> (entity instanceof Enemy && !(entity instanceof Creeper) && !(entity.getType().getCategory().getName().contains("water") && entity.isInWaterOrBubble()) && !entity.getType().is(getTag("bunfungus_ignores"))))));
        // FearedTrait
        registerAlexMobsFearTagTrait("mimic_octopus");
        registerAlexMobsFearTagTrait("skunk");
    }

    private void registerAlexMobsTargetTagTrait(String entityId) {
        TraitRegistry.registerByType(getType(entityId), (PreyTrait<Mob>) PreyTrait.ofHunterTag(getTag(entityId + "anaconda_targets")));
    }

    private void registerAlexMobsFearTagTrait(String entityId) {
        TraitRegistry.registerByType(getType(entityId), (FearedTrait<Mob>) FearedTrait.ofFearfulTag(getTag(entityId + "anaconda_targets")));
    }

    private static EntityType<Mob> getType(String entityId) {
        return (EntityType<Mob>) Walkers.getEntityTypeRegistry().get(Identifier.parse(MODID, entityId));
    }

    private static TagKey<EntityType<?>> getTag(String tagId) {
        return TagKey.create(Walkers.getEntityTypeRegistry().key(), Identifier.parse(MODID, tagId));
    }
}
