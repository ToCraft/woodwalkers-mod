package tocraft.walkers.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
//#if MC>1182
import net.minecraft.commands.CommandBuildContext;
//#endif
import net.minecraft.commands.synchronization.SuggestionProviders;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import tocraft.craftedcore.patched.CEntitySummonArgument;
import tocraft.craftedcore.patched.TComponent;
import tocraft.walkers.Walkers;
import static tocraft.craftedcore.patched.CCommandSourceStack.sendSuccess;

public class EntityBlacklistCommands {
    //#if MC>1182
    public static LiteralCommandNode<CommandSourceStack> getRootNode(CommandBuildContext ctx) {
        //#else
        //$$ public static LiteralCommandNode<CommandSourceStack> getRootNode() {
        //$$     // just 'coz I'm lazy
        //$$     Object ctx = null;
        //#endif
        LiteralCommandNode<CommandSourceStack> rootNode = Commands.literal("entityBlacklist").build();

        LiteralCommandNode<CommandSourceStack> addToList = Commands.literal("add")
                .then(Commands.argument("entity", CEntitySummonArgument.id(ctx)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context -> {
                            addToList(context.getSource(), CEntitySummonArgument.getEntityTypeId(context, "entity"));
                            return 1;
                        }))
                .build();
        LiteralCommandNode<CommandSourceStack> removeFromList = Commands.literal("remove")
                .then(Commands.argument("entity", CEntitySummonArgument.id(ctx)).suggests(SuggestionProviders.SUMMONABLE_ENTITIES)
                        .executes(context -> {
                            removeFromList(context.getSource(), CEntitySummonArgument.getEntityTypeId(context, "entity"));
                            return 1;
                        }))
                .build();

        LiteralCommandNode<CommandSourceStack> listList = Commands.literal("list")
                .executes(context -> listEntities(context.getSource()))
                .build();

        LiteralCommandNode<CommandSourceStack> clearList = Commands.literal("clear")
                .executes(context -> clearEntities(context.getSource()))
                .build();


        LiteralCommandNode<CommandSourceStack> isWhitelist = Commands.literal("isWhitelist")
                .executes(context -> {
                    isWhitelist(context.getSource());
                    return 1;
                })
                .then(Commands.argument("value", BoolArgumentType.bool())
                        .executes(context -> {
                            setIsWhitelist(context.getSource(), BoolArgumentType.getBool(context, "value"));
                            return 1;
                        }))
                .build();

        rootNode.addChild(listList);
        rootNode.addChild(clearList);
        rootNode.addChild(addToList);
        rootNode.addChild(removeFromList);
        rootNode.addChild(isWhitelist);
        return rootNode;
    }

    private static void isWhitelist(CommandSourceStack source) {
        sendSuccess(source, TComponent.translatable("walkers.getConfigEntry", "entityBlacklistIsWhitelist", Walkers.CONFIG.entityBlacklistIsWhitelist), false);
    }

    private static void setIsWhitelist(CommandSourceStack source, boolean value) {
        Walkers.CONFIG.entityBlacklistIsWhitelist = value;
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        sendSuccess(source, TComponent.translatable("walkers.setConfigEntry", "entityBlacklistIsWhitelist", String.valueOf(value)), false);
    }

    private static int clearEntities(CommandSourceStack source) {
        Walkers.CONFIG.entityBlacklist.clear();
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        sendSuccess(source, TComponent.translatable("walkers.entityBlacklist.clear"), false);

        return 1;
    }

    private static int listEntities(CommandSourceStack source) {
        for (String s : Walkers.CONFIG.entityBlacklist) {
            sendSuccess(source, TComponent.translatable("walkers.entityBlacklist.list", s), false);
        }

        if (Walkers.CONFIG.entityBlacklist.isEmpty()) {
            sendSuccess(source, TComponent.translatable("walkers.entityBlacklist.isEmpty"), false);
        }

        return 1;
    }

    private static void addToList(CommandSourceStack source, ResourceLocation type) {
        Walkers.CONFIG.entityBlacklist.add(type.toString());
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        sendSuccess(source, TComponent.translatable("walkers.entityBlacklist.add", type.toString()), false);
    }

    private static void removeFromList(CommandSourceStack source, ResourceLocation type) {
        Walkers.CONFIG.entityBlacklist.remove(type.toString());
        Walkers.CONFIG.save();

        for (ServerPlayer player : source.getServer().getPlayerList().getPlayers()) {
            Walkers.CONFIG.sendToPlayer(player);
        }

        sendSuccess(source, TComponent.translatable("walkers.entityBlacklist.remove", type.toString()), false);
    }
}
