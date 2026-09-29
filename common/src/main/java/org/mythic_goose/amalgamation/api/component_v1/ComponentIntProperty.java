package org.mythic_goose.amalgamation.api.component_v1;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public record ComponentIntProperty(DataComponentType<Integer> component, int defaultValue)
        implements RangeSelectItemModelProperty {

    public static final MapCodec<ComponentIntProperty> MAP_CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    BuiltInRegistries.DATA_COMPONENT_TYPE.byNameCodec()
                            .xmap(c -> (DataComponentType<Integer>) c, c -> c)
                            .fieldOf("component")
                            .forGetter(ComponentIntProperty::component),
                    Codec.INT.optionalFieldOf("default", 0)
                            .forGetter(ComponentIntProperty::defaultValue)
            ).apply(instance, ComponentIntProperty::new));

    @Override
    public float get(ItemStack item, @Nullable ClientLevel level, @Nullable ItemOwner owner, int seed) {
        return item.getOrDefault(component, defaultValue).floatValue();
    }

    @Override
    public MapCodec<? extends RangeSelectItemModelProperty> type() {
        return MAP_CODEC;
    }
}