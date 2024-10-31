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

public class PlayerBlacklistCommands {
    public static LiteralCommandNode<CommandSourceStack> getRootNode() {
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal("playerBlacklist").build();

        LiteralCommandNode<CommandSourceStack> isWhitelist = Commands.literal("isWhitelist")
                .executes(context -> isWhitelist(context.getSource()))
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> setIsWhitelist(context.getSource(), BoolArgumentType.getBool(context, "value"))))
                .build();

        LiteralCommandNode<CommandSourceStack> preventUnlocking = Commands.literal("preventUnlocking")
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> setPreventUnlocking(context.getSource(), BoolArgumentType.getBool(context, "value"))))
                .build();

        LiteralCommandNode<CommandSourceStack> preventMorphing = Commands.literal("preventMorphing")
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> setPreventMorphing(context.getSource(), BoolArgumentType.getBool(context, "value"))))
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
        rootNode.addChild(preventUnlocking);
        rootNode.addChild(preventMorphing);
        rootNode.addChild(listList);
        rootNode.addChild(addToList);
        rootNode.addChild(removeFromList);
        return rootNode;
    }

    private static int isWhitelist(CommandSourceStack source) {
        source.sendSuccess(() -> Component.translatable("walkers.getConfigEntry", "playerBlacklistIsWhitelist", Walkers.CONFIG.playerBlacklistIsWhitelist), false);
        return 1;
    }

    private static int setIsWhitelist(CommandSourceStack source, boolean value) {
        Walkers.CONFIG.playerBlacklistIsWhitelist = value;
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        source.sendSuccess(() -> Component.translatable("walkers.setConfigEntry", "playerBlacklistIsWhitelist", String.valueOf(value)), false);
        return 1;
    }

    private static int setPreventUnlocking(CommandSourceStack source, boolean value) {
        Walkers.CONFIG.blacklistPreventsUnlocking = value;
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        source.sendSuccess(() -> Component.translatable("walkers.setConfigEntry", "blacklistPreventsUnlocking", String.valueOf(value)), false);
        return 1;
    }

    private static int setPreventMorphing(CommandSourceStack source, boolean value) {
        Walkers.CONFIG.blacklistPreventsMorphing = value;
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        source.sendSuccess(() -> Component.translatable("walkers.setConfigEntry", "blacklistPreventsMorphing", String.valueOf(value)), false);
        return 1;
    }

    private static int listPlayers(CommandSourceStack source) {
        for (UUID uuid : Walkers.CONFIG.playerUUIDBlacklist) {
            ServerPlayer player = source.getServer().getPlayerList().getPlayer(uuid);
            Component name = player != null ? player.getDisplayName() : Component.literal(uuid.toString());
            source.sendSuccess(() -> Component.translatable("walkers.playerBlacklist.list", name), false);
        }

        if (Walkers.CONFIG.playerUUIDBlacklist.isEmpty())
            source.sendSuccess(() -> Component.translatable("walkers.playerBlacklist.isEmpty"), false);

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
        source.sendSuccess(() -> Component.translatable("walkers.playerBlacklist.add", name), false);
    }

    private static void removeFromList(CommandSourceStack source, UUID uuid) {
        Walkers.CONFIG.playerUUIDBlacklist.remove(uuid);
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        ServerPlayer player = source.getServer().getPlayerList().getPlayer(uuid);
        Component name = player != null ? player.getDisplayName() : Component.literal(uuid.toString());
        source.sendSuccess(() -> Component.translatable("walkers.playerBlacklist.remove", name), false);
    }
}
