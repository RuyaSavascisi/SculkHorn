package net.anvian.sculkhornid.core.item.custom;

import net.anvian.sculkhornid.core.config.ModConfigs;
import net.anvian.sculkhornid.core.util.Helper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SculkHornDistance extends SculkHorn {
    int DISTANCE = ModConfigs.distanceDistance;
    int DISTANCE_USE_TIME = ModConfigs.distanceUseTime;

    public SculkHornDistance(Properties properties) {
        super(
                properties,
                (float) ModConfigs.distanceDamage,
                ModConfigs.distanceCooldown,
                ModConfigs.distanceExperienceLevel,
                ModConfigs.distanceRemoveExperience
        );
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tooltip.experience", Math.abs(REMOVE_EXPERIENCE))));
            list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tooltip.distance", DISTANCE)));
            list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tooltip.cooldown", Helper.ticksToSeconds(COOLDOWN))));
            list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tooltip.damage", DAMAGE)));
        } else {
            list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tooltip_info_item.sculkhorn_shif")));
        }
        list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("null")));
        list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tootip_sculkhorn_distance")));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (player.experienceLevel >= EXPERIENCE_LEVEL || player.isCreative()) {
            player.startUsingItem(hand);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack itemStack) {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack pStack, LivingEntity pEntity) {
        return DISTANCE_USE_TIME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack itemStack, int i) {
        super.onUseTick(level, entity, itemStack, i);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity user) {
        if (!level.isClientSide) {
            if (user instanceof Player player) {
                if (player.experienceLevel >= EXPERIENCE_LEVEL || player.isCreative()) {
                    if (!player.isCreative()) {
                        player.giveExperiencePoints(REMOVE_EXPERIENCE);
                        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                    }
                    if (ModConfigs.bothInCooldown) {
                        applyCooldownToBothHorns(player);
                    } else {
                        player.getCooldowns().addCooldown(this, COOLDOWN);
                    }
                    spawnSonicBoom(level, user);
                }
            }
        }
        return super.finishUsingItem(stack, level, user);
    }

    private void spawnSonicBoom(Level level, LivingEntity user) {
        level.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.WARDEN_SONIC_BOOM, SoundSource.PLAYERS, 3.0f, 1.0f);

        Vec3 target = user.position().add(user.getLookAngle().scale(DISTANCE));
        Vec3 source = user.position().add(0.0, 1.6f, 0.0);
        Vec3 offSetToTarget = target.subtract(source);
        Vec3 normalized = offSetToTarget.normalize();

        Set<Entity> hit = new HashSet<>();
        for (int particleIndex = 1; particleIndex < Mth.floor(offSetToTarget.length()) + 7; particleIndex++) {
            Vec3 particlePos = source.add(normalized.scale(particleIndex));
            ((ServerLevel) level).sendParticles(ParticleTypes.SONIC_BOOM, particlePos.x, particlePos.y, particlePos.z, 1, 0.0, 0.0, 0.0, 0.0);

            hit.addAll(level.getEntitiesOfClass(LivingEntity.class, new AABB(new BlockPos((int) particlePos.x, (int) particlePos.y, (int) particlePos.z)).inflate(2), it -> !(Helper.isAllOf(user, it))));

            hit.remove(user);

            for (Entity hitTarget : hit) {
                if (hitTarget instanceof LivingEntity living) {
                    living.hurt(level.damageSources().sonicBoom(user), DAMAGE);
                }
            }
        }
    }
}