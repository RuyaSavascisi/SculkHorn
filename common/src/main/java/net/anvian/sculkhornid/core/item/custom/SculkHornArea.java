package net.anvian.sculkhornid.core.item.custom;

import net.anvian.sculkhornid.core.config.ModConfigs;
import net.anvian.sculkhornid.core.util.Helper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class SculkHornArea extends Item {
    public SculkHornArea(Properties properties) {
        super(properties);
    }

    float DAMAGE = ModConfigs.AREA_DAMAGE.get().floatValue();
    int COOLDOWN = ModConfigs.AREA_COOLDOWN.get();
    float RADIUS = ModConfigs.AREA_RADIUS.get().floatValue();
    int EXPERIENCE_LEVEL = ModConfigs.AREA_EXPERIENCE_LEVEL.get();
    int REMOVE_EXPERIENCE = ModConfigs.AREA_REMOVE_EXPERIENCE.get();

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        if (Screen.hasShiftDown()) {
            list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tooltip.experience", Math.abs(REMOVE_EXPERIENCE))));
            list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tooltip.radius", RADIUS)));
            list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tooltip.cooldown", Helper.ticksToSeconds(COOLDOWN))));
            list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tooltip.damage", DAMAGE)));
        } else {
            list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tooltip_info_item.sculkhorn_shif")));
        }
        list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("null")));
        list.add(Math.min(1, list.size()), Component.nullToEmpty(I18n.get("tootip_sculkhorn_area")));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemstack = player.getItemInHand(interactionHand);

        if (!level.isClientSide) {
            if (player.experienceLevel >= EXPERIENCE_LEVEL || player.isCreative()) {
                if (!player.isCreative()) {
                    player.giveExperiencePoints(REMOVE_EXPERIENCE);
                    itemstack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
                }
                sonicBoom(player, player, RADIUS);
                Helper.causeMagicExplosionAttack(level, player, player, DAMAGE, RADIUS);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30, 0));
                player.getCooldowns().addCooldown(this, COOLDOWN);
            }
        }
        if (level.isClientSide) {
            if (player.experienceLevel >= EXPERIENCE_LEVEL || player.isCreative()) {
                level.playSound(player, player, SoundEvents.WARDEN_SONIC_BOOM, SoundSource.RECORDS, 1.0f, 1.0f);
            }
        }

        if (player.experienceLevel < EXPERIENCE_LEVEL && !player.isCreative()) {
            return super.use(level, player, interactionHand);
        } else {
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
        }
    }

    private static void sonicBoom(LivingEntity attacker, LivingEntity victim, float radius) {
        AreaEffectCloud areaEffectCloud = new AreaEffectCloud(victim.level(), victim.getX(), victim.getY() + 0.25f, victim.getZ());
        areaEffectCloud.setOwner(attacker);
        areaEffectCloud.setParticle(ParticleTypes.SONIC_BOOM);
        areaEffectCloud.setRadius(radius);
        areaEffectCloud.setDuration(0);
        attacker.level().addFreshEntity(areaEffectCloud);
    }
}
