package dev.tocraft.walkers.impl.variant;

import dev.tocraft.walkers.Walkers;
import dev.tocraft.walkers.api.variant.TypeProvider;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class ZombieVillagerTypeProvider extends TypeProvider<ZombieVillager> {

    @Override
    public int getVariantData(@NotNull ZombieVillager entity) {
        return BuiltInRegistries.VILLAGER_PROFESSION.getId(entity.getVillagerData().profession().value());
    }

    @Override
    public ZombieVillager create(EntityType<ZombieVillager> type, Level level, @NotNull Player player, int data) {
        ZombieVillager villager = new ZombieVillager(type, level);
        if (Walkers.CONFIG.multiVectorVariants > 0) {
            Holder<VillagerType> villagerType;
            if (Walkers.CONFIG.multiVectorVariants == 2) {
                villagerType = BuiltInRegistries.VILLAGER_TYPE.get(VillagerType.byBiome(level.getBiome(player.blockPosition()))).orElseThrow();
            } else {
                villagerType = BuiltInRegistries.VILLAGER_TYPE.get(new Random().nextInt(0, BuiltInRegistries.VILLAGER_TYPE.size() - 1)).orElseThrow();
            }
            villager.setVillagerData(villager.getVillagerData().withProfession(BuiltInRegistries.VILLAGER_PROFESSION.get(data).orElseThrow()).withType(villagerType));
        } else {
            villager.setVillagerData(villager.getVillagerData().withProfession(BuiltInRegistries.VILLAGER_PROFESSION.get(data).orElseThrow()));
        }
        return villager;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int size(Level level) {
        return BuiltInRegistries.VILLAGER_PROFESSION.size();
    }

    @Override
    public Component modifyText(@NotNull ZombieVillager entity, MutableComponent text) {
        return Component.literal(formatTypePrefix(entity.getVillagerData().profession().toString()) + " ").append(text);
    }
}
