package com.bigmackstone.realisticfirearms.item;

import net.minecraft.world.item.Item;

public class AmmoItem extends Item {
    private final String caliber;

    public AmmoItem(Properties properties, String caliber) {
        super(properties);
        this.caliber = caliber;
    }

    public String caliber() {
        return caliber;
    }
}
