package com.bigmackstone.realisticfirearms.item;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
        ItemStack stack = player.getItemInHand(hand);

        if (player.isCrouching()) {
            reload(level, player, stack);
            return InteractionResult.SUCCESS;
        }

        if (!player.getCooldowns().isOnCooldown(this) && hasAmmo(player, spec.caliber())) {
            if (!level.isClientSide()) {
                consumeAmmo(player, spec.caliber());
                fire(level, player, stack);
                player.getCooldowns().addCooldown(this, spec.cooldownTicks());
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    private void reload(Level level, Player player, ItemStack gun) {
        // Reload timing is represented by the item cooldown. The client animation hook can
        // use the same tick duration without storing legacy NBT on the stack.
        if (hasAmmo(player, spec.caliber()) && !player.getCooldowns().isOnCooldown(this)) {
            player.getCooldowns().addCooldown(this, spec.reloadTicks());
            if (!level.isClientSide()) {
                level.playSound(null, BlockPos.containing(player.position()), SoundEvents.ITEM_ARMOR_EQUIP_IRON,
                        SoundSource.PLAYERS, 0.55f, 1.15f);
            }
        }
    }

    private void fire(Level level, Player player, ItemStack gun) {
        Vec3 eye = player.getEyePosition();
        Vec3 direction = player.getViewVector(1.0f).normalize();
        Vec3 end = eye.add(direction.scale(spec.range()));

        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class,
                player.getBoundingBox().expandTowards(direction.scale(spec.range())).inflate(1.25),
                e -> e != player && e.isAlive())) {
            Vec3 targetCenter = target.getBoundingBox().getCenter();
            Vec3 toTarget = targetCenter.subtract(eye);
            double along = toTarget.dot(direction);
            if (along < 0 || along > spec.range()) continue;
            Vec3 closest = eye.add(direction.scale(along));
            double distance = targetCenter.distanceTo(closest);
            if (distance <= 1.0 && along < bestDistance) {
                best = target;
                bestDistance = along;
            }
        }

        if (best != null && level instanceof ServerLevel serverLevel) {
            float damage = spec.damage();
            // Simple gameplay armor penetration: high-caliber rifles retain more damage.
            if (best.getArmorValue() >= 15) damage *= spec.damage() >= 8 ? 0.78f : 0.55f;
            best.hurtServer(serverLevel, player.damageSources().playerAttack(player), damage);
        }

        level.playSound(null, BlockPos.containing(player.position()), SoundEvents.GENERIC_EXPLODE,
                SoundSource.PLAYERS, 0.18f, 2.0f);
    }

    private static boolean hasAmmo(Player player, String caliber) {
        if (player.getAbilities().instabuild) return true;
        return player.getInventory().items.stream().anyMatch(s -> s.getItem() instanceof AmmoItem a && a.caliber().equals(caliber) && !s.isEmpty());
    }

    private static void consumeAmmo(Player player, String caliber) {
        if (player.getAbilities().instabuild) return;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.getItem() instanceof AmmoItem ammo && ammo.caliber().equals(caliber) && !stack.isEmpty()) {
                stack.shrink(1);
                return;
            }
        }
    }
}
