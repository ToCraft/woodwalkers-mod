package tocraft.walkers.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import tocraft.walkers.api.PlayerShape;
import tocraft.walkers.api.PlayerShapeChanger;
import tocraft.walkers.api.platform.WalkersConfig;
import tocraft.walkers.api.variant.ShapeType;
import tocraft.walkers.impl.PlayerDataProvider;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.RegistryEntryArgumentType;
import net.minecraft.command.argument.NbtCompoundArgumentType;
import net.minecraft.command.suggestion.SuggestionProviders;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.Registries;

import org.jetbrains.annotations.Nullable;

public class WalkersCommand {

    public static void register() {
        CommandRegistrationEvent.EVENT.register((dispatcher, ctx, b) -> {
            LiteralCommandNode<ServerCommandSource> rootNode = CommandManager
                    .literal("walkers")
                    .requires(source -> source.hasPermissionLevel(2))
                    .build();

            /*
            Used to remove the second shape of the specified Player.
             */
            LiteralCommandNode<ServerCommandSource> remove2ndShape = CommandManager
            .literal("remove2ndShape")
            .then(CommandManager.argument("player", EntityArgumentType.players())
                        .executes(context -> {
                            remove2ndShape(
                                    context.getSource().getPlayer(),
                                    EntityArgumentType.getPlayer(context, "player")
                            );
                            return 1;
                        })
            )
            .executes(context -> {
                remove2ndShape(
                        context.getSource().getPlayer(),
                        context.getSource().getPlayer()
                );
                return 1;
            })
            .build();

            /*
            Used to give the specified shape to the specified Player.
             */
            LiteralCommandNode<ServerCommandSource> change2ndShape = CommandManager
                    .literal("change2ndShape")
                    .then(CommandManager.argument("shape", RegistryEntryArgumentType.registryEntry(ctx, RegistryKeys.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                            .executes(context -> {
                                change2ndShape(
                                        context.getSource().getPlayer(),
                                        context.getSource().getPlayer(),
                                        EntityType.getId(RegistryEntryArgumentType.getSummonableEntityType(context, "shape").value()),
                                        null
                                );
                                return 1;
                            })
                            .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                    .executes(context -> {
                                        NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");

                                        change2ndShape(
                                                context.getSource().getPlayer(),
                                                context.getSource().getPlayer(),
                                                EntityType.getId(RegistryEntryArgumentType.getSummonableEntityType(context, "shape").value()),
                                                nbt
                                        );

                                        return 1;
                                    })
                            )
                    )
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .then(CommandManager.argument("shape", RegistryEntryArgumentType.registryEntry(ctx, RegistryKeys.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        change2ndShape(
                                                context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                EntityType.getId(RegistryEntryArgumentType.getSummonableEntityType(context, "shape").value()),
                                                null
                                        );
                                        return 1;
                                    })
                                    .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                            .executes(context -> {
                                                NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");

                                                change2ndShape(
                                                        context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        EntityType.getId(RegistryEntryArgumentType.getSummonableEntityType(context, "shape").value()),
                                                        nbt
                                                );

                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> switchShape = CommandManager
                    .literal("switchShape")
                    .then(CommandManager.argument("shape", RegistryEntryArgumentType.registryEntry(ctx, RegistryKeys.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        switchShape(context.getSource().getPlayer(),
                                                context.getSource().getPlayer(),
                                                EntityType.getId(RegistryEntryArgumentType.getSummonableEntityType(context, "shape").value()),
                                                null);

                                        return 1;
                                    })
                                    .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                            .executes(context -> {
                                                NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");

                                                switchShape(context.getSource().getPlayer(),
                                                        context.getSource().getPlayer(),
                                                        EntityType.getId(RegistryEntryArgumentType.getSummonableEntityType(context, "shape").value()),
                                                        nbt);

                                                return 1;
                                            })
                                    )
                            )
                    .then(CommandManager.argument("player", EntityArgumentType.players())
                            .executes(context -> {
                                switchShape(
                                        context.getSource().getPlayer(),
                                        EntityArgumentType.getPlayer(context, "player")
                                );
                                return 1;
                            })
                            .then(CommandManager.argument("shape", RegistryEntryArgumentType.registryEntry(ctx, RegistryKeys.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                                    .executes(context -> {
                                        switchShape(context.getSource().getPlayer(),
                                                EntityArgumentType.getPlayer(context, "player"),
                                                EntityType.getId(RegistryEntryArgumentType.getSummonableEntityType(context, "shape").value()),
                                                null);

                                        return 1;
                                    })
                                    .then(CommandManager.argument("nbt", NbtCompoundArgumentType.nbtCompound())
                                            .executes(context -> {
                                                NbtCompound nbt = NbtCompoundArgumentType.getNbtCompound(context, "nbt");

                                                switchShape(context.getSource().getPlayer(),
                                                        EntityArgumentType.getPlayer(context, "player"),
                                                        EntityType.getId(RegistryEntryArgumentType.getSummonableEntityType(context, "shape").value()),
                                                        nbt);

                                                return 1;
                                            })
                                    )
                            )
                    )
                    .build();

            LiteralCommandNode<ServerCommandSource> show2ndShape = CommandManager
                    .literal("show2ndShape")
                    .executes(context -> {
                            return show2ndShape(
                                    context.getSource().getPlayer(),
                                    context.getSource().getPlayer()
                            );
                        })
                    .then(CommandManager.argument("player", EntityArgumentType.player())
                        .executes(context -> {
                            return show2ndShape(
                                    context.getSource().getPlayer(),
                                    EntityArgumentType.getPlayer(context, "player")
                            );
                        })
                    )
                    .build();

            rootNode.addChild(remove2ndShape);
            rootNode.addChild(change2ndShape);
            rootNode.addChild(switchShape);
            rootNode.addChild(show2ndShape);

            dispatcher.getRoot().addChild(rootNode);
        });
    }

    private static int show2ndShape(ServerPlayerEntity source, ServerPlayerEntity player) {

        if(((PlayerDataProvider) player).get2ndShape() != null) {
            if(WalkersConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("walkers.show2ndShapeNot_positive", player.getDisplayName(), Text.translatable(((PlayerDataProvider) player).get2ndShape().getEntityType().getTranslationKey())), true);
            }

            return 1;
        }
        else if(WalkersConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("walkers.show2ndShapeNot_failed", player.getDisplayName()), true);
        }

        return 0;
    }

    private static void remove2ndShape(ServerPlayerEntity source, ServerPlayerEntity player) {

        boolean result = PlayerShapeChanger.changeShape(player, null);

        if(result && WalkersConfig.getInstance().logCommands()) {
            player.sendMessage(Text.translatable("walkers.remove_entity"), true);
            source.sendMessage(Text.translatable("walkers.deletion_success", player.getDisplayName()), true);
        }
    };

    private static void change2ndShape(ServerPlayerEntity source, ServerPlayerEntity player, Identifier id, @Nullable NbtCompound nbt) {
        ShapeType<LivingEntity> type = new ShapeType(Registries.ENTITY_TYPE.get(id));
        Text name = Text.translatable(type.getEntityType().getTranslationKey());

        // If the specified granting NBT is not null, change the ShapeType to reflect potential variants.
        if(nbt != null) {
            NbtCompound copy = nbt.copy();
            copy.putString("id", id.toString());
            ServerWorld serverWorld = source.getServerWorld();
            Entity loaded = EntityType.loadEntityWithPassengers(copy, serverWorld, it -> it);
            if(loaded instanceof LivingEntity living) {
                type = new ShapeType<>(living);
                name = type.createTooltipText(living);
            }
        }

        if(((PlayerDataProvider) player).get2ndShape() != type) {
            boolean result = PlayerShapeChanger.changeShape(player, type);

            if(result && WalkersConfig.getInstance().logCommands()) {
                player.sendMessage(Text.translatable("walkers.unlock_entity", name), true);
                source.sendMessage(Text.translatable("walkers.grant_success", name, player.getDisplayName()), true);
            }
        } else {
            if(WalkersConfig.getInstance().logCommands()) {
                source.sendMessage(Text.translatable("walkers.already_has", player.getDisplayName(), name), true);
            }
        }
    };

    private static void switchShape(ServerPlayerEntity source, ServerPlayerEntity player, Identifier walkers, @Nullable NbtCompound nbt) {
        Entity created;

        if(nbt != null) {
            NbtCompound copy = nbt.copy();
            copy.putString("id", walkers.toString());
            ServerWorld serverWorld = source.getServerWorld();
            created = EntityType.loadEntityWithPassengers(copy, serverWorld, it -> it);
        } else {
            EntityType<?> entity = Registries.ENTITY_TYPE.get(walkers);
            created = entity.create(player.getWorld());
        }

        if(created instanceof LivingEntity living) {
            @Nullable ShapeType<?> defaultType = ShapeType.from(living);

            if(defaultType != null) {
                boolean result = PlayerShape.updateShapes(player, defaultType, (LivingEntity) created);
                if(result && WalkersConfig.getInstance().logCommands()) {
                    source.sendMessage(Text.translatable("walkers.switchShape_success", player.getDisplayName(), Text.translatable(created.getType().getTranslationKey())), true);
                }
            }
        }
    }

    private static void switchShape(ServerPlayerEntity source, ServerPlayerEntity player) {
        boolean result = PlayerShape.updateShapes(player, null, null);

        if(result && WalkersConfig.getInstance().logCommands()) {
            source.sendMessage(Text.translatable("walkers.switchShape_human_success", player.getDisplayName()), false);
        }
    }
}
