package tocraft.walkers.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntitySummonArgument;
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import tocraft.walkers.Walkers;

public class EntityBlacklistCommands {
    public static LiteralCommandNode<CommandSourceStack> getRootNode() {
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal("entityBlacklist").build();

        LiteralCommandNode<CommandSourceStack> addToList = Commands.literal("add")
                .then(Commands.argument("entity", EntitySummonArgument.id()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context -> {
                            addToList(context.getSource(), Registry.ENTITY_TYPE.get(EntitySummonArgument.getSummonableEntity(context, "entity")));
                            return 1;
                        }))
                .build();
        LiteralCommandNode<CommandSourceStack> removeFromList = Commands.literal("remove")
                .then(Commands.argument("entity", EntitySummonArgument.id()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context -> {
                            removeFromList(context.getSource(), Registry.ENTITY_TYPE.get(EntitySummonArgument.getSummonableEntity(context, "entity")));
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

        source.sendSuccess(new TranslatableComponent("walkers.entityBlacklist.clear"), true);

        return 1;
    }

    private static int listEntities(CommandSourceStack source) {
        for (String s : Walkers.CONFIG.entityBlacklist) {
            source.sendSuccess(new TranslatableComponent("walkers.entityBlacklist.list", s), true);
        }

        if (Walkers.CONFIG.entityBlacklist.isEmpty()) {
            source.sendSuccess(new TranslatableComponent("walkers.entityBlacklist.isEmpty"), true);
        }

        return 1;
    }

    private static void addToList(CommandSourceStack source, EntityType<?> type) {
        Walkers.CONFIG.entityBlacklist.add(EntityType.getKey(type).toString());
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        source.sendSuccess(new TranslatableComponent("walkers.entityBlacklist.add", EntityType.getKey(type).toString()), true);
    }

    private static void removeFromList(CommandSourceStack source, EntityType<?> type) {
        Walkers.CONFIG.entityBlacklist.remove(EntityType.getKey(type).toString());
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        source.sendSuccess(new TranslatableComponent("walkers.entityBlacklist.remove", EntityType.getKey(type).toString()), true);
    }
}
