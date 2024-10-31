package tocraft.walkers.integrations.impl;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Enemy;
import org.jetbrains.annotations.Nullable;
import tocraft.walkers.Walkers;
import tocraft.walkers.integrations.AbstractIntegration;
import tocraft.walkers.mixin.accessor.EntityAccessor;
import tocraft.walkers.traits.TraitRegistry;
import tocraft.walkers.traits.impl.PreyTrait;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

public class GuardVillagersIntegration extends AbstractIntegration {
    public static final String MODID = "guardvillagers";
    public static final ResourceLocation GUARD_VILLAGER_TYPE = ResourceLocation.fromNamespaceAndPath(MODID, "guard");

    @Override
    public void registerTraits() {
        List<String> mobBlacklist = getMobBlackList();
        TraitRegistry.registerByPredicate(entity -> entity instanceof Enemy && mobBlacklist != null && !mobBlacklist.contains(((EntityAccessor) entity).callGetEncodeId()), new PreyTrait<>(List.of(hunter -> EntityType.getKey(hunter.getType()).equals(GUARD_VILLAGER_TYPE))));
    }

    @SuppressWarnings("unchecked")
    @Nullable
    private List<String> getMobBlackList() {
        try {
            Class<?> configClass = Class.forName("tallestegg.guardvillagers.configuration.GuardConfig");
            Object commonConfig = configClass.getDeclaredField("COMMON").get(null);
            Field MobBlackListField = commonConfig.getClass().getDeclaredField("MobBlackList");
            Object MobBlacklist = MobBlackListField.get(commonConfig);
            Method getMobBlacklist = MobBlacklist.getClass().getDeclaredMethod("get");
            return (List<String>) getMobBlacklist.invoke(MobBlacklist);
        } catch (ReflectiveOperationException e) {
            Walkers.LOGGER.error("{}: failed to get the mob blacklist: {}", GuardVillagersIntegration.class, e);
            return null;
        }
    }
}
