package tocraft.walkers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;

// TODO: Morph into & set 2ndShape by variant id
public class WalkersCommand {
    public static void initialize() {
        CommandEvents.REGISTRATION.register((dispatcher, ctx, selection) -> register(dispatcher, ctx));
    }

    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal("walkers")
                .requires(source -> source.hasPermission(2)).build();

        /*
         * Used to remove the second shape of the specified Player.
         */
        LiteralCommandNode<CommandSourceStack> remove2ndShape = Commands.literal("remove2ndShape")
                .then(Commands.argument("players", EntityArgument.players()).executes(context -> {
                    for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                        remove2ndShape(context.getSource(), player);
                    }
                    return 1;
                })).build();

        /*
         * Used to give the specified shape to the specified Player.
         */
        LiteralCommandNode<CommandSourceStack> change2ndShape = Commands.literal("change2ndShape")
                .then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .executes(context -> {
                                    Entity entity = EntityArgument.getEntity(context, "entity");
                                    CompoundTag nbt = new CompoundTag();
                                    entity.saveWithoutId(nbt);
                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                        change2ndShape(context.getSource(),
                                                player,
                                                EntityType.getKey(entity.getType()),
                                                nbt);
                                    }

                                    return 1;
                                }))
                        .then(Commands.argument("shape", ResourceArgument.resource(ctx, Registries.ENTITY_TYPE))
                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                        change2ndShape(context.getSource(),
                                                player,
                                                ResourceArgument.getEntityType(context, "shape").key().location(),
                                                null);
                                    }
                                    return 1;
                                }).then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                        .executes(context -> {
                                            CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");
                                            for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                                change2ndShape(context.getSource(),
                                                        player,
                                                        ResourceArgument.getEntityType(context, "shape").key().location(),
                                                        nbt);
                                            }
                                            return 1;
                                        }))))
                .build();

        LiteralCommandNode<CommandSourceStack> switchShape = Commands.literal("switchShape").then(Commands.argument("players", EntityArgument.players())
                        .then(Commands.literal("normal").executes(context -> {
                            for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                switchShapeToNormal(context.getSource(), player);
                            }
                            return 1;
                        }))
                        .then(Commands.argument("entity", EntityArgument.entity())
                                .executes(context -> {
                                    Entity entity = EntityArgument.getEntity(context, "entity");
                                    CompoundTag nbt = new CompoundTag();
                                    entity.saveWithoutId(nbt);
                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                        switchShape(context.getSource(),
                                                player,
                                                EntityType.getKey(entity.getType()),
                                                nbt);
                                    }
                                    return 1;
                                })).then(Commands.argument("shape", ResourceArgument.resource(ctx, Registries.ENTITY_TYPE))
                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                        switchShape(context.getSource(),
                                                player,
                                                ResourceArgument.getEntityType(context, "shape").key().location(),
                                                null);
                                    }
                                    return 1;
                                }).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes(context -> {
                                    CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");
                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                        switchShape(context.getSource(),
                                                player,
                                                ResourceArgument.getEntityType(context, "shape").key().location(),
                                                nbt);
                                    }
                                    return 1;
                                }))))
                .build();

        LiteralCommandNode<CommandSourceStack> show2ndShape = Commands.literal("show2ndShape")
                .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> {
                            for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                show2ndShape(context.getSource(), player);
                            }
                            return 1;
                        }))
                .build();

        rootNode.addChild(remove2ndShape);
        rootNode.addChild(change2ndShape);
        rootNode.addChild(switchShape);
        rootNode.addChild(show2ndShape);

        rootNode.addChild(PlayerBlacklistCommands.getRootNode());
        rootNode.addChild(EntityBlacklistCommands.getRootNode(ctx));

        dispatcher.getRoot().addChild(rootNode);
    }

    private static void show2ndShape(CommandSourceStack source, ServerPlayer player) {
        if (((PlayerDataProvider) player).walkers$get2ndShape() != null) {
            ShapeType<?> type = ((PlayerDataProvider) player).walkers$get2ndShape();
            if (type != null) {
                source.sendSuccess(() -> Component.translatable("walkers.show2ndShapeNot_positive",
                        player.getDisplayName(), ShapeType.createTooltipText(type.create(player.level(), player))), false);
            }
        } else {
            source.sendSuccess(() -> Component.translatable("walkers.show2ndShapeNot_failed", player.getDisplayName()), false);
        }
    }

    private static void remove2ndShape(CommandSourceStack source, ServerPlayer player) {

        change2ndShape(player, null);

        player.displayClientMessage(Component.translatable("walkers.remove_entity"), true);
        source.sendSuccess(() -> Component.translatable("walkers.deletion_success", player.getDisplayName()), false);
    }

    @SuppressWarnings("unchecked")
    private static void change2ndShape(CommandSourceStack source, ServerPlayer player, ResourceLocation id,
                                       @Nullable CompoundTag nbt) {
        ShapeType<LivingEntity> type = ShapeType.from((EntityType<LivingEntity>) BuiltInRegistries.ENTITY_TYPE.get(id).orElseThrow().value());
        Component name = Component.translatable(type.getEntityType().getDescriptionId());

        // If the specified granting NBT is not null, change the ShapeType to reflect
        // potential variants.
        if (nbt != null) {
            CompoundTag copy = nbt.copy();
            copy.putString("id", id.toString());
            ServerLevel serverWorld = source.getLevel();
            Entity loaded = EntityType.loadEntityRecursive(copy, serverWorld, EntitySpawnReason.LOAD, it -> it);
            if (loaded instanceof LivingEntity living) {
                type = ShapeType.from(living);
                name = ShapeType.createTooltipText(living);
            }
        }

        if (((PlayerDataProvider) player).walkers$get2ndShape() != type) {
            change2ndShape(player, type);

            player.displayClientMessage(Component.translatable("walkers.unlock_entity", name), false);
            final Component n = name;
            source.sendSuccess(() ->
                    Component.translatable("walkers.grant_success", n, player.getDisplayName()), false);
        } else {
            final Component n = name;
            source.sendSuccess(() -> Component.translatable("walkers.already_has", player.getDisplayName(), n), false);
        }
    }

    private static void switchShape(CommandSourceStack source, ServerPlayer player, ResourceLocation shape, @Nullable CompoundTag nbt) {
        Entity created;

        if (nbt != null) {
            CompoundTag copy = nbt.copy();
            copy.putString("id", shape.toString());
            ServerLevel serverWorld = source.getLevel();
            created = EntityType.loadEntityRecursive(copy, serverWorld, EntitySpawnReason.LOAD, it -> it);
        } else {
            EntityType<?> entity = BuiltInRegistries.ENTITY_TYPE.get(shape).orElseThrow().value();
            created = entity.create(player.level(), EntitySpawnReason.LOAD);
        }

        if (created instanceof LivingEntity) {
            ((PlayerDataProvider) player).walkers$updateShapes((LivingEntity) created);
            source.sendSuccess(() -> Component.translatable("walkers.switchShape_success",
                    player.getDisplayName(), Component.translatable(created.getType().getDescriptionId())), false);
        }
    }

    private static void switchShapeToNormal(CommandSourceStack source, ServerPlayer player) {
        boolean result = PlayerShape.updateShapes(player, null);

        if (result) {
            source.sendSuccess(() ->
                    Component.translatable("walkers.switchShape_human_success", player.getDisplayName()), false);
        }
    }

    private static void change2ndShape(ServerPlayer player, ShapeType<?> newShape) {
        ((PlayerDataProvider) player).walkers$set2ndShape(newShape);
        PlayerShapeChanger.sync(player);
        PlayerAbilities.sync(player);
    }
}
