package com.bigmackstone.realisticfirearms;

import com.bigmackstone.realisticfirearms.registry.ModItems;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

@Mod(RealisticFirearms.MOD_ID)
public final class RealisticFirearms {
    public static final String MOD_ID = "realisticfirearms";

    public RealisticFirearms(IEventBus modEventBus) {
        ModItems.ITEMS.register(modEventBus);
    }
}
