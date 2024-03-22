package tocraft.walkers.impl.variant;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Random;

public class VillagerTypeProvider extends TypeProvider<Villager> {

    @Override
    public int getVariantData(Villager entity) {
        return Registry.VILLAGER_PROFESSION.getId(entity.getVillagerData().getProfession());
    }

    @Override
    public Villager create(EntityType<Villager> type, Level level, int data) {
        Villager villager = new Villager(type, level);
        villager.setVillagerData(villager.getVillagerData().setProfession(Registry.VILLAGER_PROFESSION.byId(data)));
        return villager;
    }

    @Override
    public Villager create(EntityType<Villager> type, Level level, int data, Player player) {
        if (player != null && Walkers.CONFIG.multiVectorVariants > 0) {
            Villager villager = new Villager(type, level);
            VillagerType villagerType;
            if (Walkers.CONFIG.multiVectorVariants == 2) {
                villagerType = VillagerType.byBiome(level.getBiome(player.blockPosition()));
            } else {
                villagerType = Registry.VILLAGER_TYPE.byId(new Random().nextInt(0, Registry.VILLAGER_TYPE.size() - 1));
            }
            villager.setVillagerData(villager.getVillagerData().setType(villagerType));
            villager.setVillagerData(villager.getVillagerData().setProfession(Registry.VILLAGER_PROFESSION.byId(data)));
            return villager;
        } else {
            return create(type, level, data);
        }
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return Registry.VILLAGER_PROFESSION.size() - 1;
    }

    @Override
    public Component modifyText(Villager entity, MutableComponent text) {
        return new TextComponent(formatTypePrefix(entity.getVillagerData().getType().toString()) + " ").append(text);
    }
}