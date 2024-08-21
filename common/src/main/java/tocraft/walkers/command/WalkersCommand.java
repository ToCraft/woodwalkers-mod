package tocraft.walkers.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
//#if MC>1182
import net.minecraft.commands.CommandBuildContext;
//#endif
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.CompoundTagArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import tocraft.craftedcore.event.common.CommandEvents;
import tocraft.craftedcore.patched.CEntity;
import tocraft.craftedcore.patched.CEntitySummonArgument;
import tocraft.craftedcore.patched.TComponent;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.PlayerAbilities;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import static tocraft.craftedcore.patched.CCommandSourceStack.sendSuccess;

public class WalkersCommand {
    //#if MC>1182
    public static void initialize() {
        CommandEvents.REGISTRATION.register((dispatcher, ctx, selection) -> register(dispatcher, ctx));
    }
    private static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext ctx) {
    //#else
    //$$ public static void initialize() {
    //$$     CommandEvents.REGISTRATION.register((dispatcher, ctx) -> register(dispatcher));
    //$$ }
    //$$
    //$$ private static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
    //$$     Object ctx = null;
    //#endif
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
                        .then(Commands.argument("shape", CEntitySummonArgument.id(ctx))
                                .suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                        change2ndShape(context.getSource(),
                                                player,
                                                CEntitySummonArgument.getEntityTypeId(context, "shape"),
                                                null);
                                    }
                                    return 1;
                                }).then(Commands.argument("nbt", CompoundTagArgument.compoundTag())
                                        .executes(context -> {
                                            CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");
                                            for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                                change2ndShape(context.getSource(),
                                                        player,
                                                        CEntitySummonArgument.getEntityTypeId(context, "shape"),
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
                                })).then(Commands.argument("shape", CEntitySummonArgument.id(ctx))
                        .suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes(context -> {
                                    for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                        switchShape(context.getSource(),
                                                player,
                                                CEntitySummonArgument.getEntityTypeId(context, "shape"),
                                                null);
                                    }
                            return 1;
                        }).then(Commands.argument("nbt", CompoundTagArgument.compoundTag()).executes(context -> {
                            CompoundTag nbt = CompoundTagArgument.getCompoundTag(context, "nbt");
                            for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                switchShape(context.getSource(),
                                        player,
                                        CEntitySummonArgument.getEntityTypeId(context, "shape"),
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
        //#if MC>1182
        rootNode.addChild(EntityBlacklistCommands.getRootNode(ctx));
        //#else
        //$$ rootNode.addChild(EntityBlacklistCommands.getRootNode());
        //#endif

        dispatcher.getRoot().addChild(rootNode);
    }

    private static void show2ndShape(CommandSourceStack source, ServerPlayer player) {
        if (((PlayerDataProvider) player).walkers$get2ndShape() != null) {
            ShapeType<?> type = ((PlayerDataProvider) player).walkers$get2ndShape();
            if (type != null) {
                sendSuccess(source, TComponent.translatable("walkers.show2ndShapeNot_positive",
                        player.getDisplayName(), ShapeType.createTooltipText(type.create(CEntity.level(player), player))), false);
            }
        } else {
            sendSuccess(source, TComponent.translatable("walkers.show2ndShapeNot_failed", player.getDisplayName()), false);
        }
    }

    private static void remove2ndShape(CommandSourceStack source, ServerPlayer player) {

        change2ndShape(player, null);

        player.displayClientMessage(TComponent.translatable("walkers.remove_entity"), true);
        sendSuccess(source, TComponent.translatable("walkers.deletion_success", player.getDisplayName()), false);
    }

    @SuppressWarnings("unchecked")
    private static void change2ndShape(CommandSourceStack source, ServerPlayer player, ResourceLocation id,
                                       @Nullable CompoundTag nbt) {
        ShapeType<LivingEntity> type = ShapeType.from((EntityType<LivingEntity>) Walkers.getEntityTypeRegistry().get(id));
        Component name = TComponent.translatable(type.getEntityType().getDescriptionId());

        // If the specified granting NBT is not null, change the ShapeType to reflect
        // potential variants.
        if (nbt != null) {
            CompoundTag copy = nbt.copy();
            copy.putString("id", id.toString());
            ServerLevel serverWorld = source.getLevel();
            Entity loaded = EntityType.loadEntityRecursive(copy, serverWorld, it -> it);
            if (loaded instanceof LivingEntity living) {
                type = ShapeType.from(living);
                name = ShapeType.createTooltipText(living);
            }
        }

        if (((PlayerDataProvider) player).walkers$get2ndShape() != type) {
            change2ndShape(player, type);

            player.displayClientMessage(TComponent.translatable("walkers.unlock_entity", name), false);
            sendSuccess(source, 
                    TComponent.translatable("walkers.grant_success", name, player.getDisplayName()), false);
        } else {
            sendSuccess(source, TComponent.translatable("walkers.already_has", player.getDisplayName(), name), false);
        }
    }

    private static void switchShape(CommandSourceStack source, ServerPlayer player, ResourceLocation shape, @Nullable CompoundTag nbt) {
        Entity created;

        if (nbt != null) {
            CompoundTag copy = nbt.copy();
            copy.putString("id", shape.toString());
            ServerLevel serverWorld = source.getLevel();
            created = EntityType.loadEntityRecursive(copy, serverWorld, it -> it);
        } else {
            EntityType<?> entity = Walkers.getEntityTypeRegistry().get(shape);
            created = entity.create(CEntity.level(player));
        }

        if (created instanceof LivingEntity) {
            ((PlayerDataProvider) player).walkers$updateShapes((LivingEntity) created);
            sendSuccess(source, TComponent.translatable("walkers.switchShape_success",
                    player.getDisplayName(), TComponent.translatable(created.getType().getDescriptionId())), false);
        }
    }

    private static void switchShapeToNormal(CommandSourceStack source, ServerPlayer player) {
        boolean result = PlayerShape.updateShapes(player, null);

        if (result) {
            sendSuccess(source,
                    TComponent.translatable("walkers.switchShape_human_success", player.getDisplayName()), false);
        }
    }

    private static void change2ndShape(ServerPlayer player, ShapeType<?> newShape) {
        ((PlayerDataProvider) player).walkers$set2ndShape(newShape);
        PlayerShapeChanger.sync(player);
        PlayerAbilities.sync(player);
    }
}
