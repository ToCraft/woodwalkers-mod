package tocraft.walkers.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.events.common.CommandEvents;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;

public class WalkersCommand {

	public static void register() {
		CommandEvents.REGISTRATION.register(((dispatcher, b) -> {
			LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal("walkers")
					.requires(source -> source.hasPermission(2)).build();

			/*
			 * Used to remove the second shape of the specified Player.
			 */
			LiteralCommandNode<CommandSourceStack> remove2ndShape = Commands.literal("remove2ndShape")
					.then(Commands.argument("player", EntityArgument.players()).executes(context -> {
						remove2ndShape(context.getSource(), EntityArgument.getPlayer(context, "player"));
						return 1;
					})).executes(context -> {
						remove2ndShape(context.getSource(), context.getSource().getPlayerOrException());
						return 1;
					}).build();

			/*
			 * Used to give the specified shape to the specified Player.
			 */
			LiteralCommandNode<CommandSourceStack> change2ndShape = Commands.literal("change2ndShape")
					.then(Commands.argument("shape", EntitySummonArgument.id())
							.suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
								change2ndShape(context.getSource(), context.getSource().getPlayerOrException(),
										EntitySummonArgument.getSummonableEntity(context, "shape"),
										null);
								return 1;
							}).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes(context -> {
								CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");

								change2ndShape(context.getSource(), context.getSource().getPlayerOrException(),
										EntitySummonArgument.getSummonableEntity(context, "shape"),
										nbt);

								return 1;
							})))
					.then(Commands.argument("player", EntityArgument.players())
							.then(Commands.argument("shape", EntitySummonArgument.id())
									.suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
										change2ndShape(context.getSource(), EntityArgument.getPlayer(context, "player"),
												EntitySummonArgument.getSummonableEntity(context, "shape"),
												null);
										return 1;
									}).then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
											.executes(context -> {
												CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");

												change2ndShape(context.getSource(),
														EntityArgument.getPlayer(context, "player"),
														EntitySummonArgument.getSummonableEntity(context, "shape"),
														nbt);

												return 1;
											}))))
					.build();

			LiteralCommandNode<CommandSourceStack> switchShape = Commands.literal("switchShape")
					.then(Commands.argument("shape", EntitySummonArgument.id())
							.suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
								switchShape(context.getSource(), context.getSource().getPlayerOrException(),
												EntitySummonArgument.getSummonableEntity(context, "shape"),
										null);

								return 1;
							}).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes(context -> {
								CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");

								switchShape(context.getSource(), context.getSource().getPlayerOrException(),
												EntitySummonArgument.getSummonableEntity(context, "shape"),
										nbt);

								return 1;
							})))
					.then(Commands.argument("player", EntityArgument.players()).executes(context -> {
						switchShape(context.getSource(), EntityArgument.getPlayer(context, "player"));
						return 1;
					}).then(Commands.argument("shape", EntityArgument.entity())
							.suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
								switchShape(context.getSource(), EntityArgument.getPlayer(context, "player"),
												EntitySummonArgument.getSummonableEntity(context, "shape"),
										null);

								return 1;
							}).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes(context -> {
								CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");

								switchShape(context.getSource(), EntityArgument.getPlayer(context, "player"),
											EntitySummonArgument.getSummonableEntity(context, "shape"),
										nbt);

								return 1;
							}))))
					.build();

			LiteralCommandNode<CommandSourceStack> show2ndShape = Commands.literal("show2ndShape")
					.executes(context -> show2ndShape(context.getSource(), context.getSource().getPlayerOrException()))
					.then(Commands.argument("player", EntityArgument.player())
							.executes(context -> show2ndShape(context.getSource(), EntityArgument.getPlayer(context, "player"))))
					.build();

			rootNode.addChild(remove2ndShape);
			rootNode.addChild(change2ndShape);
			rootNode.addChild(switchShape);
			rootNode.addChild(show2ndShape);

			dispatcher.getRoot().addChild(rootNode);
		}));
	}

	private static int show2ndShape(CommandSourceStack source, ServerPlayer player) {

		if (((PlayerDataProvider) player).walkers$get2ndShape() != null) {
			if (Walkers.CONFIG.logCommands) {
				source.sendSuccess(new TranslatableComponent("walkers.show2ndShapeNot_positive",
						player.getDisplayName(),
								((PlayerDataProvider) player).walkers$get2ndShape().getEntityType().getDescriptionId()), true);
			}

			return 1;
		} else if (Walkers.CONFIG.logCommands) {
			source.sendSuccess(new TranslatableComponent("walkers.show2ndShapeNot_failed", player.getDisplayName()), true);
		}

		return 0;
	}

	private static void remove2ndShape(CommandSourceStack source, ServerPlayer player) {

		boolean result = PlayerShapeChanger.change2ndShape(player, null);

		if (result && Walkers.CONFIG.logCommands) {
			player.displayClientMessage(new TranslatableComponent("walkers.remove_entity"), true);
			source.sendSuccess(new TranslatableComponent("walkers.deletion_success", player.getDisplayName()), true);
		}
	}

	private static void change2ndShape(CommandSourceStack source, ServerPlayer player, ResourceLocation id,
			@Nullable CompoundTag nbt) {
		ShapeType<LivingEntity> type = new ShapeType(Registry.ENTITY_TYPE.get(id));
		Component name = new TranslatableComponent(type.getEntityType().getDescriptionId());

		// If the specified granting NBT is not null, change the ShapeType to reflect
		// potential variants.
		if (nbt != null) {
			CompoundTag copy = nbt.copy();
			copy.putString("id", id.toString());
			ServerLevel serverWorld = source.getLevel();
			Entity loaded = EntityType.loadEntityRecursive(copy, serverWorld, it -> it);
			if (loaded instanceof LivingEntity living) {
				type = new ShapeType<>(living);
				name = type.createTooltipText(living);
			}
		}

		if (((PlayerDataProvider) player).walkers$get2ndShape() != type) {
			boolean result = PlayerShapeChanger.change2ndShape(player, type);

			if (result && Walkers.CONFIG.logCommands) {
				player.displayClientMessage(new TranslatableComponent("walkers.unlock_entity", name), false);
				source.sendSuccess(
						new TranslatableComponent("walkers.grant_success", name, player.getDisplayName()), true);
			}
		} else {
			if (Walkers.CONFIG.logCommands) {
				source.sendSuccess(new TranslatableComponent("walkers.already_has", player.getDisplayName(), name), true);
			}
		}
	}

	private static void switchShape(CommandSourceStack source, ServerPlayer player, ResourceLocation shape,
			@Nullable CompoundTag nbt) {
		Entity created;

		if (nbt != null) {
			CompoundTag copy = nbt.copy();
			copy.putString("id", shape.toString());
			ServerLevel serverWorld = source.getLevel();
			created = EntityType.loadEntityRecursive(copy, serverWorld, it -> it);
		} else {
			EntityType<?> entity = Registry.ENTITY_TYPE.get(shape);
			created = entity.create(player.level);
		}

		if (created instanceof LivingEntity living) {
			@Nullable
			ShapeType<?> defaultType = ShapeType.from(living);

			if (defaultType != null) {
				boolean result = PlayerShape.updateShapes(player, (LivingEntity) created);
				if (result && Walkers.CONFIG.logCommands) {
					source.sendSuccess(new TranslatableComponent("walkers.switchShape_success",
							player.getDisplayName(), new TranslatableComponent(created.getType().getDescriptionId())), true);
				}
			}
		}
	}

	private static void switchShape(CommandSourceStack source, ServerPlayer player) {
		boolean result = PlayerShape.updateShapes(player, null);

		if (result && Walkers.CONFIG.logCommands) {
			source.sendSuccess(
					new TranslatableComponent("walkers.switchShape_human_success", player.getDisplayName()), true);
		}
	}
}
