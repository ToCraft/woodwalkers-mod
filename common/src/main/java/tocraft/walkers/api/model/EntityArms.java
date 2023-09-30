package tocraft.walkers.api.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.BlazeModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.IronGolemModel;
import net.minecraft.client.model.LlamaModel;
import net.minecraft.client.model.OcelotModel;
import net.minecraft.client.model.PandaModel;
import net.minecraft.client.model.PigModel;
import net.minecraft.client.model.PolarBearModel;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.RavagerModel;
import net.minecraft.client.model.SpiderModel;
import net.minecraft.client.model.SquidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import tocraft.walkers.math.math;
import tocraft.walkers.mixin.accessor.BlazeEntityModelAccessor;
import tocraft.walkers.mixin.accessor.IllagerEntityModelAccessor;
import tocraft.walkers.mixin.accessor.LlamaEntityModelAccessor;
import tocraft.walkers.mixin.accessor.OcelotEntityModelAccessor;
import tocraft.walkers.mixin.accessor.QuadrupedEntityModelAccessor;
import tocraft.walkers.mixin.accessor.RavagerEntityModelAccessor;
import tocraft.walkers.mixin.accessor.SpiderEntityModelAccessor;
import tocraft.walkers.mixin.accessor.SquidEntityModelAccessor;

@Environment(EnvType.CLIENT)
public class EntityArms {

	private static final Map<EntityType<? extends LivingEntity>, Tuple<EntityArmProvider<? extends LivingEntity>, ArmRenderingManipulator<?>>> DIRECT_PROVIDERS = new LinkedHashMap<>();
	private static final Map<Class<?>, Tuple<ClassArmProvider<?>, ArmRenderingManipulator<?>>> CLASS_PROVIDERS = new LinkedHashMap<>();

	public static <T extends LivingEntity> void register(EntityType<T> type, EntityArmProvider<T> provider,
			ArmRenderingManipulator<EntityModel<T>> manipulator) {
		DIRECT_PROVIDERS.put(type, new Tuple<>(provider, manipulator));
	}

	public static <T> void register(Class<T> modelClass, ClassArmProvider<T> provider,
			ArmRenderingManipulator<T> manipulator) {
		CLASS_PROVIDERS.put(modelClass, new Tuple<>(provider, manipulator));
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T extends LivingEntity> Tuple<ModelPart, ArmRenderingManipulator<?>> get(T entity,
			EntityModel<T> model) {
		// done to bypass type issues
		Tuple<EntityArmProvider<? extends LivingEntity>, ArmRenderingManipulator<?>> before = DIRECT_PROVIDERS
				.get(entity.getType());

		// Direct entity type provider was found, return it now
		if (before != null) {
			Tuple<EntityArmProvider<T>, ArmRenderingManipulator<?>> provider = new Tuple<>(
					(EntityArmProvider<T>) before.getA(), before.getB());
			return new Tuple<>(provider.getA().getArm(entity, model), provider.getB());
		} else {
			Optional<Tuple<ClassArmProvider<?>, ArmRenderingManipulator<?>>> beforeClassProvider = CLASS_PROVIDERS
					.entrySet().stream().filter(pair -> {
						return pair.getKey().isInstance(model);
					}).findFirst().map(entry -> new Tuple<>(entry.getValue().getA(), entry.getValue().getB()));

			// fall back to class providers
			if (beforeClassProvider.isPresent()) {
				Tuple<ClassArmProvider<EntityModel>, ArmRenderingManipulator<EntityModel<LivingEntity>>> classProvider = new Tuple<>(
						(ClassArmProvider<EntityModel>) beforeClassProvider.get().getA(),
						(ArmRenderingManipulator<EntityModel<LivingEntity>>) beforeClassProvider.get().getB());
				return new Tuple<>(classProvider.getA().getArm(entity, model), classProvider.getB());
			} else {
				return null;
			}
		}
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T extends LivingEntity> EntityArmProvider<T> get(EntityType<LivingEntity> type) {
		return (EntityArmProvider<T>) DIRECT_PROVIDERS.get(type);
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static <T extends LivingEntity> EntityArmProvider<T> get(
			Class<EntityModel<? extends LivingEntity>> modelClass) {
		return (EntityArmProvider<T>) CLASS_PROVIDERS.get(modelClass);
	}

	public static void init() {
		// specific
		register(LlamaModel.class, (llama, model) -> ((LlamaEntityModelAccessor) model).getRightFrontLeg(),
				(stack, model) -> {
				});
		register(PandaModel.class, (llama, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(),
				(stack, model) -> stack.translate(0, -0.5, 0));
		register(BlazeModel.class, (llama, model) -> ((BlazeEntityModelAccessor) model).getUpperBodyParts()[10],
				(stack, model) -> {
					stack.mulPose(math.getDegreesQuaternion(math.POSITIVE_Z(), 45));
					stack.mulPose(math.getDegreesQuaternion(math.POSITIVE_Y(), -15));
					stack.mulPose(math.getDegreesQuaternion(math.POSITIVE_X(), -25));
					stack.translate(0, 0, -.25);
				});
		register(OcelotModel.class, (ocelot, model) -> ((OcelotEntityModelAccessor) model).getRightFrontLeg(),
				(stack, model) -> {
				});
		register(SpiderModel.class, (spider, model) -> ((SpiderEntityModelAccessor) model).getRightFrontLeg(),
				(stack, model) -> {
					stack.mulPose(math.getDegreesQuaternion(math.POSITIVE_Y(), -15));
					stack.mulPose(math.getDegreesQuaternion(math.POSITIVE_X(), 15));
					stack.translate(0, 0, 0);
				});
		register(IronGolemModel.class, (golem, model) -> model.getFlowerHoldingArm(), (stack, model) -> {
			stack.translate(0, 0, -.5);
		});
		register(PigModel.class, (pig, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(),
				(stack, model) -> {
					stack.translate(0, 0, .6);
				});
		register(PolarBearModel.class, (bear, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(),
				(stack, model) -> {
					stack.translate(0, 0, .3);
				});
		register(RavagerModel.class, (bear, model) -> ((RavagerEntityModelAccessor) model).getRightFrontLeg(),
				(stack, model) -> {
				});
		register(SquidModel.class, (squid, model) -> ((SquidEntityModelAccessor) model).getTentacles()[0],
				(stack, model) -> {

				});

		// generic
		register(QuadrupedModel.class, (quad, model) -> ((QuadrupedEntityModelAccessor) model).getRightFrontLeg(),
				(stack, model) -> {
				});

		// types
		register(EntityType.PILLAGER, (pillager, model) -> ((IllagerEntityModelAccessor) model).getRightArm(),
				(stack, model) -> {
					stack.mulPose(math.getDegreesQuaternion(math.POSITIVE_X(), -10));
					stack.translate(0, .5, -.3);
				});
	}
}
