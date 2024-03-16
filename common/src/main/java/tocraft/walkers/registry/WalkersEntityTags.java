package tocraft.walkers.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import tocraft.walkers.Walkers;

@Deprecated
public class WalkersEntityTags {

    public static final TagKey<EntityType<?>> BURNS_IN_DAYLIGHT = register("burns_in_daylight");
    public static final TagKey<EntityType<?>> FLYING = register("flying");
    public static final TagKey<EntityType<?>> SLOW_FALLING = register("slow_falling");
    public static final TagKey<EntityType<?>> WOLF_PREY = register("wolf_prey");
    public static final TagKey<EntityType<?>> FOX_PREY = register("fox_prey");
    public static final TagKey<EntityType<?>> HURT_BY_HIGH_TEMPERATURE = register("hurt_by_high_temperature");
    public static final TagKey<EntityType<?>> RAVAGER_RIDING = register("ravager_riding");
    public static final TagKey<EntityType<?>> LAVA_WALKING = register("lava_walking");
    public static final TagKey<EntityType<?>> CANT_SWIM = register("cant_swim");
    public static final TagKey<EntityType<?>> UNDROWNABLE = register("undrownable");
    public static final TagKey<EntityType<?>> BLACKLISTED = register("blacklisted");
    public static final TagKey<EntityType<?>> FALL_THROUGH_BLOCKS = register("fall_through_blocks");

    private static TagKey<EntityType<?>> register(String id) {
        return TagKey.create(Registries.ENTITY_TYPE, Walkers.id(id));
    }
}
