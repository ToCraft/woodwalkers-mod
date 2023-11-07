package tocraft.walkers.impl.variant;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

public class VillagerTypeProvider extends TypeProvider<Villager> {

    @Override
    public int getVariantData(Villager entity) {
        return BuiltInRegistries.VILLAGER_TYPE.getId(entity.getVariant());
    }

    @Override
    public Villager create(EntityType<Villager> type, Level level, int data) {
    	Villager villager = new Villager(type, level);
        villager.setVariant(BuiltInRegistries.VILLAGER_TYPE.byId(data));
        return villager;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return BuiltInRegistries.VILLAGER_TYPE.size() - 1;
    }

    @Override
    public Component modifyText(Villager entity, MutableComponent text) {
        return Component.literal(formatTypePrefix(entity.getVariant().toString()) + " ").append(text);
    }
}
