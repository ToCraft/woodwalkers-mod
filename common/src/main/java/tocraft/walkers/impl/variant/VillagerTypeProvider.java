package tocraft.walkers.impl.variant;

import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class VillagerTypeProvider extends TypeProvider<Villager> {

    @Override
    public int getVariantData(Villager entity) {
    	VillagerType.byBiome(null);
        return Registry.VILLAGER_TYPE.getId(entity.getVillagerData().getType());
    }

    @Override
    public Villager create(EntityType<Villager> type, Level level, int data) {
    	Villager villager = new Villager(type, level);
        villager.getVillagerData().setType(Registry.VILLAGER_TYPE.byId(data));
        return villager;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return Registry.VILLAGER_TYPE.size() - 1;
    }

    @Override
    public Component modifyText(Villager entity, MutableComponent text) {
        return new TextComponent(formatTypePrefix(entity.getVillagerData().getType().toString()) + " ").append(text);
    }
}
