package tocraft.walkers.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import tocraft.walkers.Walkers;

import java.util.UUID;

public class BlacklistCommands {
    public static LiteralCommandNode<CommandSourceStack> getRootNode() {
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal("playerBlacklist").build();

        LiteralCommandNode<CommandSourceStack> isWhitelist = Commands.literal("isWhitelist")
                .executes(context -> isWhitelist(context.getSource()))
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> setIsWhitelist(context.getSource(), BoolArgumentType.getBool(context, "value"))))
                .build();

        LiteralCommandNode<CommandSourceStack> addToList = Commands.literal("add")
                .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> {
                            for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                addToList(context.getSource(), player.getUUID());
                            }
                            return 1;
                        }))
                .then(Commands.argument("playerUUID", UuidArgument.uuid())
                        .executes(context -> {
                            addToList(context.getSource(), UuidArgument.getUuid(context, "playerUUID"));
                            return 1;
                        }))
                .build();
        LiteralCommandNode<CommandSourceStack> removeFromList = Commands.literal("remove")
                .then(Commands.argument("players", EntityArgument.players())
                        .executes(context -> {
                            for (ServerPlayer player : EntityArgument.getPlayers(context, "players")) {
                                removeFromList(context.getSource(), player.getUUID());
                            }
                            return 1;
                        })
                        .then(Commands.argument("playerUUID", UuidArgument.uuid())
                                .executes(context -> {
                                    removeFromList(context.getSource(), UuidArgument.getUuid(context, "playerUUID"));
                                    return 1;
                                })))
                .build();

        LiteralCommandNode<CommandSourceStack> listList = Commands.literal("list")
                .executes(context -> listPlayers(context.getSource()))
                .build();


        rootNode.addChild(isWhitelist);
        rootNode.addChild(listList);
        rootNode.addChild(addToList);
        rootNode.addChild(removeFromList);
        return rootNode;
    }

    private static int isWhitelist(CommandSourceStack source) {
        source.sendSystemMessage(Component.translatable("walkers.isWhitelist", Walkers.CONFIG.playerBlacklistIsWhitelist));
        return 1;
    }

    private static int setIsWhitelist(CommandSourceStack source, boolean value) {
        Walkers.CONFIG.playerBlacklistIsWhitelist = value;
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        source.sendSystemMessage(Component.translatable("walkers.setIsWhitelist", String.valueOf(value)));
        return 1;
    }

    private static int listPlayers(CommandSourceStack source) {
        for (UUID uuid : Walkers.CONFIG.playerUUIDBlacklist) {
            ServerPlayer player = source.getServer().getPlayerList().getPlayer(uuid);
            Component name = player != null ? player.getDisplayName() : Component.literal(uuid.toString());
            source.sendSystemMessage(Component.translatable("walkers.blacklistListPlayer", name));
        }

        if (Walkers.CONFIG.playerUUIDBlacklist.isEmpty())
            source.sendSystemMessage(Component.translatable("walkers.blacklistIsEmpty"));

        return 1;
    }

    private static void addToList(CommandSourceStack source, UUID uuid) {
        Walkers.CONFIG.playerUUIDBlacklist.add(uuid);
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        ServerPlayer player = source.getServer().getPlayerList().getPlayer(uuid);
        Component name = player != null ? player.getDisplayName() : Component.literal(uuid.toString());
        source.sendSystemMessage(Component.translatable("walkers.addToList", name));
    }

    private static void removeFromList(CommandSourceStack source, UUID uuid) {
        Walkers.CONFIG.playerUUIDBlacklist.remove(uuid);
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        ServerPlayer player = source.getServer().getPlayerList().getPlayer(uuid);
        Component name = player != null ? player.getDisplayName() : Component.literal(uuid.toString());
        source.sendSystemMessage(Component.translatable("walkers.removeFromList", name));
    }
}
