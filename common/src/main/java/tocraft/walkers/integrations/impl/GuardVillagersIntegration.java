package tocraft.walkers.integrations.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import tocraft.walkers.Walkers;
import tocraft.walkers.integrations.AbstractIntegration;
import tocraft.walkers.skills.SkillRegistry;
import tocraft.walkers.skills.impl.PreySkill;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class GuardVillagersIntegration extends AbstractIntegration {
    public static final String MODID = "guardvillagers";
    public static final ResourceLocation GUARD_VILLAGER_TYPE = new ResourceLocation(MODID, "guard");

    @Override
    public void initialize() {
        SkillRegistry.registerByPredicate(entity -> entity instanceof Enemy && !getMobBlackList().contains(entity.getEncodeId()), new PreySkill<>(List.of(hunter -> EntityType.getKey(hunter.getType()).equals(GUARD_VILLAGER_TYPE))));
    }

    private static List<String> CACHED_MOB_BLACKLIST = new ArrayList<>();

    @SuppressWarnings("unchecked")
    private static List<String> getMobBlackList() {
        try {
            Class<?> configClass = Class.forName("tallestegg.guardvillagers.configuration.GuardConfig");
            Object mobBlackListObject = configClass.getField("MobBlackList").get(configClass.getDeclaredConstructor().newInstance());
            if (mobBlackListObject instanceof List) {
                CACHED_MOB_BLACKLIST = (List<String>) mobBlackListObject;
                return CACHED_MOB_BLACKLIST;
            }
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException |
                 NoSuchFieldException | InstantiationException e) {
            Walkers.LOGGER.error("{}: failed to get the mob blacklist for {}: {}", GuardVillagersIntegration.class.getSimpleName(), MODID, e);
        }

        return CACHED_MOB_BLACKLIST;
    }
}
