package tocraft.walkers.impl.variant;

import com.google.common.collect.ImmutableMap;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.level.Level;
import tocraft.walkers.api.variant.TypeProvider;

import java.util.Map;

public class RabbitTypeProvider extends TypeProvider<Rabbit> {
	
	private static final Map<Integer, String> PREFIX_BY_ID = ImmutableMap
            .<Integer, String>builder()
            .put(0, "Brown")
            .put(1, "White")
            .put(2, "Black")
            .put(3, "White Splotched")
            .put(4, "Gold")
            .put(5, "Salt")
            .put(6, "Evil")
            .build();

    @Override
    public int getVariantData(Rabbit entity) {
        int id = entity.getRabbitType();
        if (id == 99) return 6;
        else return id;
    }

    @Override
    public Rabbit create(EntityType<Rabbit> type, Level level, int data) {
        Rabbit rabbit = new Rabbit(type, level);
        if (data == 6) data = 99;
        rabbit.setRabbitType(data);
        return rabbit;
    }

    @Override
    public int getFallbackData() {
        return 0;
    }

    @Override
    public int getRange() {
        return 6;
    }

    @Override
    public Component modifyText(Rabbit entity, MutableComponent text) {
    	int variant = getVariantData(entity);
        return new TextComponent(PREFIX_BY_ID.containsKey(variant) ? PREFIX_BY_ID.get(variant) + " " : "").append(text);
    }
}