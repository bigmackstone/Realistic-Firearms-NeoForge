package com.bigmackstone.realisticfirearms.item;

import net.minecraft.world.item.Item;

public class AttachmentItem extends Item {
    public enum Type { RED_DOT, SCOPE_4X, SCOPE_8X, SUPPRESSOR, COMPENSATOR, GRIP, EXTENDED_MAG }

    private final Type type;

    public AttachmentItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    public Type type() {
        return type;
    }
}
