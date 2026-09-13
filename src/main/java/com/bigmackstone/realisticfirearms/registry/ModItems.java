package com.bigmackstone.realisticfirearms.registry;

import com.bigmackstone.realisticfirearms.RealisticFirearms;
import com.bigmackstone.realisticfirearms.item.AmmoItem;
import com.bigmackstone.realisticfirearms.item.AttachmentItem;
import com.bigmackstone.realisticfirearms.item.FirearmItem;
import com.bigmackstone.realisticfirearms.item.FirearmSpec;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RealisticFirearms.MOD_ID);

    // Firearms
    public static final DeferredItem<FirearmItem> PISTOL = gun("pistol", "9x19mm", 12, 5, 6, 42, 30);
    public static final DeferredItem<FirearmItem> REVOLVER = gun("revolver", ".357", 6, 8, 14, 48, 36);
    public static final DeferredItem<FirearmItem> MACHINE_PISTOL = gun("machine_pistol", "9x19mm", 20, 4, 3, 40, 34);
    public static final DeferredItem<FirearmItem> PDW = gun("pdw", "4.6x30mm", 30, 5, 2, 55, 38);
    public static final DeferredItem<FirearmItem> SMG = gun("smg", "9x19mm", 30, 5, 2, 52, 40);
    public static final DeferredItem<FirearmItem> CARBINE = gun("carbine", "5.56x45mm", 30, 7, 3, 75, 42);
    public static final DeferredItem<FirearmItem> ASSAULT_RIFLE = gun("assault_rifle", "5.56x45mm", 30, 7, 2, 82, 44);
    public static final DeferredItem<FirearmItem> BULLPUP = gun("bullpup_rifle", "5.56x45mm", 36, 7, 2, 82, 46);
    public static final DeferredItem<FirearmItem> BATTLE_RIFLE = gun("battle_rifle", "7.62x51mm", 20, 10, 5, 95, 48);
    public static final DeferredItem<FirearmItem> DMR = gun("dmr", "7.62x51mm", 15, 12, 9, 120, 52);
    public static final DeferredItem<FirearmItem> BOLT_RIFLE = gun("bolt_rifle", "7.62x51mm", 5, 16, 22, 150, 58);
    public static final DeferredItem<FirearmItem> HEAVY_RIFLE = gun("heavy_rifle", "12.7mm", 5, 22, 28, 170, 65);
    public static final DeferredItem<FirearmItem> SHOTGUN = gun("shotgun", "12ga", 8, 14, 16, 38, 50);
    public static final DeferredItem<FirearmItem> LMG = gun("lmg", "7.62x51mm", 75, 8, 3, 90, 80);

    // Individual ammunition types
    public static final DeferredItem<AmmoItem> AMMO_9MM = ammo("ammo_9mm", "9x19mm");
    public static final DeferredItem<AmmoItem> AMMO_357 = ammo("ammo_357", ".357");
    public static final DeferredItem<AmmoItem> AMMO_46 = ammo("ammo_46", "4.6x30mm");
    public static final DeferredItem<AmmoItem> AMMO_556 = ammo("ammo_556", "5.56x45mm");
    public static final DeferredItem<AmmoItem> AMMO_762 = ammo("ammo_762", "7.62x51mm");
    public static final DeferredItem<AmmoItem> AMMO_127 = ammo("ammo_127", "12.7mm");
    public static final DeferredItem<AmmoItem> AMMO_12GA = ammo("ammo_12ga", "12ga");

    // Attachments
    public static final DeferredItem<AttachmentItem> RED_DOT = attachment("red_dot", AttachmentItem.Type.RED_DOT);
    public static final DeferredItem<AttachmentItem> SCOPE_4X = attachment("scope_4x", AttachmentItem.Type.SCOPE_4X);
    public static final DeferredItem<AttachmentItem> SCOPE_8X = attachment("scope_8x", AttachmentItem.Type.SCOPE_8X);
    public static final DeferredItem<AttachmentItem> SUPPRESSOR = attachment("suppressor", AttachmentItem.Type.SUPPRESSOR);
    public static final DeferredItem<AttachmentItem> COMPENSATOR = attachment("compensator", AttachmentItem.Type.COMPENSATOR);
    public static final DeferredItem<AttachmentItem> GRIP = attachment("angled_grip", AttachmentItem.Type.GRIP);
    public static final DeferredItem<AttachmentItem> EXTENDED_MAG = attachment("extended_mag", AttachmentItem.Type.EXTENDED_MAG);

    private static DeferredItem<FirearmItem> gun(String id, String caliber, int mag, int damage, int cooldown, double range, int reload) {
        return ITEMS.registerItem(id, p -> new FirearmItem(p, new FirearmSpec(id, caliber, mag, damage, cooldown, range, reload)));
    }

    private static DeferredItem<AmmoItem> ammo(String id, String caliber) {
        return ITEMS.registerItem(id, p -> new AmmoItem(p.stacksTo(64), caliber));
    }

    private static DeferredItem<AttachmentItem> attachment(String id, AttachmentItem.Type type) {
        return ITEMS.registerItem(id, p -> new AttachmentItem(p.stacksTo(1), type));
    }

    private ModItems() {}
}
