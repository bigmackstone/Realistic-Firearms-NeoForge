package com.bigmackstone.realisticfirearms.item;

public record FirearmSpec(String name, String caliber, int magazineSize, int damage, int cooldownTicks, double range, int reloadTicks) {}
