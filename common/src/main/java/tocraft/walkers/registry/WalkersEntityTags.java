package tocraft.walkers.registry;

import net.minecraft.tags.Tag;
import net.minecraft.world.entity.EntityType;
import tocraft.walkers.Walkers;
import tocraft.walkers.mixin.accessor.EntityTypeTagsAccessor;

public class WalkersEntityTags {

	public static final Tag.Named<EntityType<?>> BURNS_IN_DAYLIGHT = register("burns_in_daylight");
	public static final Tag.Named<EntityType<?>> FLYING = register("flying");
	public static final Tag.Named<EntityType<?>> SLOW_FALLING = register("slow_falling");
	public static final Tag.Named<EntityType<?>> WOLF_PREY = register("wolf_prey");
	public static final Tag.Named<EntityType<?>> FOX_PREY = register("fox_prey");
	public static final Tag.Named<EntityType<?>> HURT_BY_HIGH_TEMPERATURE = register("hurt_by_high_temperature");
	public static final Tag.Named<EntityType<?>> RAVAGER_RIDING = register("ravager_riding");
	public static final Tag.Named<EntityType<?>> PIGLIN_FRIENDLY = register("piglin_friendly");
	public static final Tag.Named<EntityType<?>> LAVA_WALKING = register("lava_walking");
	public static final Tag.Named<EntityType<?>> CANT_SWIM = register("cant_swim");
	public static final Tag.Named<EntityType<?>> UNDROWNABLE = register("undrownable");
	public static final Tag.Named<EntityType<?>> BLACKLISTED = register("blacklisted");
	public static final Tag.Named<EntityType<?>> FALL_THROUGH_BLOCKS = register("fall_through_blocks");

	public static void init() {
        // NO-OP
    }
	
	private static Tag.Named<EntityType<?>> register(String id) {
		return EntityTypeTagsAccessor.callBind(Walkers.id(id).toString());
	}
}
