package com.bigmackstone.realisticfirearms.registry;

import com.bigmackstone.realisticfirearms.RealisticFirearms;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModComponents {
    public static final DeferredRegister.DataComponents COMPONENTS =
            DeferredRegister.createDataComponents(RealisticFirearms.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> LOADED_AMMO =
            COMPONENTS.registerComponentType("loaded_ammo", builder -> builder
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<String>> ATTACHMENT =
            COMPONENTS.registerComponentType("attachment", builder -> builder
                    .persistent(Codec.STRING)
                    .networkSynchronized(ByteBufCodecs.STRING_UTF8));

    private ModComponents() {}
}
