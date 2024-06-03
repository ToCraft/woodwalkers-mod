package tocraft.walkers.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ResourceArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import tocraft.walkers.Walkers;

public class EntityBlacklistCommands {
    public static LiteralCommandNode<CommandSourceStack> getRootNode(CommandBuildContext ctx) {
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal("entityBlacklist").build();

        LiteralCommandNode<CommandSourceStack> addToList = Commands.literal("add")
                .then(Commands.argument("entity", ResourceArgument.resource(ctx, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context -> {
                            addToList(context.getSource(), ResourceArgument.getEntityType(context, "entity").value());
                            return 1;
                        }))
                .build();
        LiteralCommandNode<CommandSourceStack> removeFromList = Commands.literal("remove")
                .then(Commands.argument("entity", ResourceArgument.resource(ctx, Registries.ENTITY_TYPE)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context -> {
                            removeFromList(context.getSource(), ResourceArgument.getEntityType(context, "entity").value());
                            return 1;
                        }))
                .build();

        LiteralCommandNode<CommandSourceStack> listList = Commands.literal("list")
                .executes(context -> listEntities(context.getSource()))
                .build();

        LiteralCommandNode<CommandSourceStack> clearList = Commands.literal("clear")
                .executes(context -> clearEntities(context.getSource()))
                .build();

        rootNode.addChild(listList);
        rootNode.addChild(clearList);
        rootNode.addChild(addToList);
        rootNode.addChild(removeFromList);
        return rootNode;
    }

    private static int clearEntities(CommandSourceStack source) {
        Walkers.CONFIG.entityBlacklist.clear();
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        source.sendSystemMessage(Component.translatable("walkers.entityBlacklist.clear"));

        return 1;
    }

    private static int listEntities(CommandSourceStack source) {
        for (String s : Walkers.CONFIG.entityBlacklist) {
            source.sendSystemMessage(Component.translatable("walkers.entityBlacklist.list", s));
        }

        if (Walkers.CONFIG.entityBlacklist.isEmpty()) {
            source.sendSystemMessage(Component.translatable("walkers.entityBlacklist.isEmpty"));
        }

        return 1;
    }

    private static void addToList(CommandSourceStack source, EntityType<?> type) {
        Walkers.CONFIG.entityBlacklist.add(EntityType.getKey(type).toString());
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        source.sendSystemMessage(Component.translatable("walkers.entityBlacklist.add", EntityType.getKey(type).toString()));
    }

    private static void removeFromList(CommandSourceStack source, EntityType<?> type) {
        Walkers.CONFIG.entityBlacklist.remove(EntityType.getKey(type).toString());
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        source.sendSystemMessage(Component.translatable("walkers.entityBlacklist.remove", EntityType.getKey(type).toString()));
    }
}
