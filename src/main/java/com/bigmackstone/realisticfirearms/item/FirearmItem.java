package com.bigmackstone.realisticfirearms.item;

import com.bigmackstone.realisticfirearms.registry.ModComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class FirearmItem extends Item {
    private final FirearmSpec spec;

    public FirearmItem(Properties properties, FirearmSpec spec) {
        super(properties.stacksTo(1));
        this.spec = spec;
    }

    public FirearmSpec spec() { return spec; }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack gun = player.getItemInHand(hand);

        // Crouch + an attachment in the offhand installs the attachment on this gun.
        if (player.isCrouching() && player.getOffhandItem().getItem() instanceof AttachmentItem attachment) {
            installAttachment(player, gun, attachment);
            return InteractionResult.SUCCESS;
        }

        // Crouch + empty offhand reloads the magazine.
        if (player.isCrouching()) {
            reload(level, player, gun);
            return InteractionResult.SUCCESS;
        }

        if (player.getCooldowns().isOnCooldown(this)) return InteractionResult.PASS;

        int loaded = gun.getOrDefault(ModComponents.LOADED_AMMO.value(), 0);
        if (loaded <= 0) {
            if (!level.isClientSide()) {
                level.playSound(null, BlockPos.containing(player.position()), SoundEvents.DISPENSER_FAIL,
                        SoundSource.PLAYERS, 0.5f, 1.0f);
            }
            return InteractionResult.SUCCESS;
        }

        if (!level.isClientSide()) {
            gun.set(ModComponents.LOADED_AMMO.value(), loaded - 1);
            fire(level, player, gun);
            player.getCooldowns().addCooldown(this, adjustedCooldown(gun));
        }
        return InteractionResult.SUCCESS;
    }

    private void installAttachment(Player player, ItemStack gun, AttachmentItem attachment) {
        String id = attachment.type().name().toLowerCase();
        gun.set(ModComponents.ATTACHMENT.value(), id);
        if (!player.getAbilities().instabuild) player.getOffhandItem().shrink(1);
        player.displayClientMessage(net.minecraft.network.chat.Component.literal("Attached: " + id.replace('_', ' ')), true);
    }

    private void reload(Level level, Player player, ItemStack gun) {
        if (player.getCooldowns().isOnCooldown(this)) return;

        int capacity = magazineSize(gun);
        int loaded = gun.getOrDefault(ModComponents.LOADED_AMMO.value(), 0);
        int needed = capacity - loaded;
        if (needed <= 0 || !hasAmmo(player, spec.caliber())) return;

        int reserve = countAmmo(player, spec.caliber());
        int transfer = Math.min(needed, reserve);
        if (!player.getAbilities().instabuild) consumeAmmo(player, spec.caliber(), transfer);
        gun.set(ModComponents.LOADED_AMMO.value(), loaded + transfer);
        player.getCooldowns().addCooldown(this, spec.reloadTicks());

        if (!level.isClientSide()) {
            level.playSound(null, BlockPos.containing(player.position()), SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                    SoundSource.PLAYERS, 0.55f, 1.15f);
        }
    }

    private void fire(Level level, Player player, ItemStack gun) {
        Vec3 eye = player.getEyePosition();
        Vec3 direction = player.getViewVector(1.0f).normalize();
        double range = adjustedRange(gun);
        double gravity = 0.028; // gameplay-scale bullet drop

        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().inflate(range), e -> e != player && e.isAlive())) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            Vec3 toTarget = targetCenter.subtract(eye);
            double along = toTarget.dot(direction);
            if (along < 0 || along > range) continue;

            // Approximate a ballistic arc: farther targets require the crosshair to sit
            // slightly above the target, producing visible gameplay bullet drop behavior.
            double flightTicks = along / 2.6;
            Vec3 predicted = eye.add(direction.scale(along)).add(0, -0.5 * gravity * flightTicks * flightTicks, 0);
            double miss = targetCenter.distanceTo(predicted);
            if (miss <= 1.15 && along < bestDistance) {
                best = target;
                bestDistance = along;
            }
        }

        if (best != null && level instanceof ServerLevel serverLevel) {
            float damage = adjustedDamage(gun);
            int armor = best.getArmorValue();
            // Higher-power rounds retain more damage through armor.
            if (armor >= 15) {
                double penetration = penetrationMultiplier();
                damage *= (float) Math.max(0.25, 1.0 - armor * 0.025 * (1.0 - penetration));
            }
            best.hurtServer(serverLevel, player.damageSources().playerAttack(player), damage);
        }

        String attachment = gun.getOrDefault(ModComponents.ATTACHMENT.value(), "");
        float volume = attachment.equals("suppressor") ? 0.05f : 0.18f;
        level.playSound(null, BlockPos.containing(player.position()), SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS, volume, 2.0f);
    }

    private int magazineSize(ItemStack gun) {
        return spec.magazineSize() + (hasAttachment(gun, "extended_mag") ? 12 : 0);
    }

    private int adjustedCooldown(ItemStack gun) {
        int cooldown = spec.cooldownTicks();
        if (hasAttachment(gun, "compensator")) cooldown = Math.max(1, cooldown - 1);
        if (hasAttachment(gun, "grip")) cooldown = Math.max(1, cooldown - 1);
        return cooldown;
    }

    private double adjustedRange(ItemStack gun) {
        double range = spec.range();
        String a = gun.getOrDefault(ModComponents.ATTACHMENT.value(), "");
        if (a.equals("red_dot")) range *= 1.10;
        if (a.equals("scope_4x")) range *= 1.30;
        if (a.equals("scope_8x")) range *= 1.55;
        return range;
    }

    private float adjustedDamage(ItemStack gun) {
        float damage = spec.damage();
        if (hasAttachment(gun, "compensator")) damage *= 1.02f;
        return damage;
    }

    private double penetrationMultiplier() {
        if (spec.damage() >= 16) return 0.90;
        if (spec.damage() >= 10) return 0.72;
        if (spec.damage() >= 7) return 0.55;
        return 0.35;
    }

    private static boolean hasAttachment(ItemStack stack, String id) {
        return stack.getOrDefault(ModComponents.ATTACHMENT.value(), "").equals(id);
    }

    private static boolean hasAmmo(Player player, String caliber) {
        return player.getAbilities().instabuild || countAmmo(player, caliber) > 0;
    }

    private static int countAmmo(Player player, String caliber) {
        return player.getInventory().items.stream()
                .filter(s -> s.getItem() instanceof AmmoItem a && a.caliber().equals(caliber))
                .mapToInt(ItemStack::getCount).sum();
    }

    private static void consumeAmmo(Player player, String caliber, int amount) {
        int remaining = amount;
        for (ItemStack stack : player.getInventory().items) {
            if (remaining <= 0) break;
            if (stack.getItem() instanceof AmmoItem ammo && ammo.caliber().equals(caliber)) {
                int take = Math.min(remaining, stack.getCount());
                stack.shrink(take);
                remaining -= take;
            }
        }
    }
}
