package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.skill.DuWangSkillCatalog;
import net.arna.jcraft.client.gui.hud.JCraftAbilityHud;
import net.arna.jcraft.common.util.CooldownType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 杜王技能 HUD（直接复用 JCraft HUD 绘制方法）。
 *
 * 接口方法（方便后续调试/替换）：
 * - JCraftAbilityHud.getHudX(...)           // 使用 JCraft 的 UI 定位规则
 * - JCraftAbilityHud.renderBorder(...)      // 使用 JCraft 图标边框
 * - JCraftAbilityHud.renderAbsIcon(...)     // 使用 JCraft 图标渲染 + fallback 逻辑
 * - JCraftAbilityHud.renderCooldown(...)    // 使用 JCraft 冷却遮罩渲染
 */
@Mod.EventBusSubscriber(modid = ModEntrance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class JCraftDuWangSkillOverlay {
    private static final int SLOT_SIZE = 22;
    private static final int SLOT_GAP = 6;
    private static final int TOP = 18;

    private static long skill1LastTriggerTick = Long.MIN_VALUE;
    private static long skill2LastTriggerTick = Long.MIN_VALUE;

    private JCraftDuWangSkillOverlay() {
    }

    public static void markSkillTriggered(int skillId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return;
        }
        long now = mc.level.getGameTime();
        if (skillId == DuWangSkillCatalog.STAND_ASSAULT_ID) {
            skill1LastTriggerTick = now;
        } else if (skillId == DuWangSkillCatalog.HURRICANE_BARRIER_ID) {
            skill2LastTriggerTick = now;
        }
    }

    @SubscribeEvent
    public static void onRenderHud(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null || mc.level == null) {
            return;
        }

        LocalPlayer player = mc.player;
        if (!hasOwnedStand(player)) {
            return;
        }

        GuiGraphics gui = event.getGuiGraphics();
        int baseX = JCraftAbilityHud.getHudX(event.getWindow().getGuiScaledWidth(), 32);
        int y1 = TOP;
        int y2 = TOP + SLOT_SIZE + SLOT_GAP;

        renderSkill(gui, baseX, y1, DuWangSkillCatalog.STAND_ASSAULT_ID,
                skill1LastTriggerTick, DuWangSkillCatalog.STAND_ASSAULT_COOLDOWN_TICKS, "special1", CooldownType.STAND_SP1);
        renderSkill(gui, baseX, y2, DuWangSkillCatalog.HURRICANE_BARRIER_ID,
                skill2LastTriggerTick, DuWangSkillCatalog.HURRICANE_BARRIER_COOLDOWN_TICKS, "special2", CooldownType.STAND_SP2);
    }

    private static void renderSkill(
            GuiGraphics gui,
            int x,
            int y,
            int skillId,
            long lastTriggerTick,
            int cooldownTicks,
            String fallback,
            CooldownType cooldownType
    ) {
        // [JCraft 接口] 边框渲染
        JCraftAbilityHud.renderBorder(gui, x, y);

        ResourceLocation icon = ResourceLocation.tryParse(DuWangSkillCatalog.getSkillIconPath(skillId));
        if (icon != null) {
            // [JCraft 接口] 图标渲染（含 JCraft fallback 机制）
            JCraftAbilityHud.renderAbsIcon(gui, x, y, icon, fallback);
        }

        double remainRatio = getCooldownRemainRatio(lastTriggerTick, cooldownTicks);
        if (remainRatio > 0) {
            // [JCraft 接口] 冷却遮罩渲染（参数是 0~1）
            JCraftAbilityHud.renderCooldown(gui, remainRatio, x, y);
            String remainText = Integer.toString(Mth.ceil((float) cooldownTicks * (float) remainRatio / 20.0F));
            int textX = x + (SLOT_SIZE - Minecraft.getInstance().font.width(remainText)) / 2;
            gui.drawString(Minecraft.getInstance().font, remainText, textX, y + 7, 0xFFE7E7E7, true);
        }

        // [JCraft 接口] 直接复用 JCraft 的按键文本映射
        String keyText = JCraftAbilityHud.cooldownTypeToKeybind(cooldownType, true);
        gui.drawString(Minecraft.getInstance().font, keyText, x + SLOT_SIZE + 4, y + 7, 0xFFDEE6EF, false);
    }

    private static boolean hasOwnedStand(LocalPlayer player) {
        List<DuWangEntity> stands = player.level().getEntitiesOfClass(
                DuWangEntity.class,
                player.getBoundingBox().inflate(64.0D),
                stand -> stand.isOwnedBy(player) && stand.isAlive()
        );
        return !stands.isEmpty();
    }

    private static double getCooldownRemainRatio(long lastTriggerTick, int cooldownTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (lastTriggerTick == Long.MIN_VALUE || cooldownTicks <= 0 || mc.level == null) {
            return 0.0D;
        }
        long elapsed = mc.level.getGameTime() - lastTriggerTick;
        if (elapsed >= cooldownTicks) {
            return 0.0D;
        }
        return Mth.clamp((cooldownTicks - elapsed) / (double) cooldownTicks, 0.0D, 1.0D);
    }
}
