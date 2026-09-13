package com.bigmackstone.realisticfirearms.client;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

/** Client-friendly animation state shared by firearm renderers. */
public final class FirearmAnimationController {
    private static final Map<UUID, Long> RELOAD_END = new ConcurrentHashMap<>();
    private static final Map<UUID, Long> RECOIL_END = new ConcurrentHashMap<>();

    public static void startReload(UUID player, int ticks) {
        RELOAD_END.put(player, System.currentTimeMillis() + ticks * 50L);
    }

    public static void triggerRecoil(UUID player) {
        RECOIL_END.put(player, System.currentTimeMillis() + 90L);
    }

    public static float reloadProgress(UUID player, int ticks) {
        long end = RELOAD_END.getOrDefault(player, 0L);
        long duration = Math.max(1L, ticks * 50L);
        long remaining = Math.max(0L, end - System.currentTimeMillis());
        return 1.0f - Math.min(1.0f, remaining / (float) duration);
    }

    public static float recoilProgress(UUID player) {
        long remaining = Math.max(0L, RECOIL_END.getOrDefault(player, 0L) - System.currentTimeMillis());
        return Math.min(1.0f, remaining / 90.0f);
    }

    private FirearmAnimationController() {}
}
