package tocraft.walkers.impl.variant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import tocraft.walkers.Walkers;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Random;

public class ZombieVillagerTypeProvider extends TypeProvider<ZombieVillager> {

    @Override
    public int getVariantData(ZombieVillager entity) {
        return BuiltInRegistries.VILLAGER_PROFESSION.getId(entity.getVillagerData().getProfession());
    }

    @Override
    public ZombieVillager create(EntityType<ZombieVillager> type, Level level, int data) {
        ZombieVillager villager = new ZombieVillager(type, level);
        villager.setVillagerData(villager.getVillagerData().setProfession(BuiltInRegistries.VILLAGER_PROFESSION.byId(data)));
        return villager;
    }

    @Override
    public ZombieVillager create(EntityType<ZombieVillager> type, Level level, int data, Player player) {
        if (player != null && Walkers.CONFIG.multiVectorVariants > 0) {
            ZombieVillager villager = new ZombieVillager(type, level);
            VillagerType villagerType;
            if (Walkers.CONFIG.multiVectorVariants == 2) {
                villagerType = VillagerType.byBiome(level.getBiome(player.blockPosition()));
            } else {
                villagerType = BuiltInRegistries.VILLAGER_TYPE.byId(new Random().nextInt(0, BuiltInRegistries.VILLAGER_TYPE.size() - 1));
            }
            villager.setVariant(villagerType);
            villager.setVillagerData(villager.getVillagerData().setProfession(BuiltInRegistries.VILLAGER_PROFESSION.byId(data)));
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
        return BuiltInRegistries.VILLAGER_PROFESSION.size() - 1;
    }

    @Override
    public Component modifyText(ZombieVillager entity, MutableComponent text) {
        return Component.literal(formatTypePrefix(entity.getVariant().toString()) + " ").append(text);
    }
}
