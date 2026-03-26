package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.item.ModItems;
import com.Y1fel.JoJoPlagueSurge.skill.DuWangSkillCatalog;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * 技能栏（简化版）：
 * - 只在替身召唤出来时显示；
 * - 用现有物品图标充当技能图片，先把“技能图 + 键位提示”这条链路跑通；
 * - 这里保留注释，后续你可以直接替换成独立 PNG 技能图。
 */
@Mod.EventBusSubscriber(modid = ModEntrance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DuWangSkillHudOverlay {
    private static final int SKILL_SLOT_SIZE = 18;
    private static final int HUD_TOP = 22;
    private static final int HUD_RIGHT = 12;
    private static final int ROW_GAP = 4;
    private static final int KEY_TEXT_WIDTH = 16;
    private static final int ROW_BG = 0x66E2EAF3;
    private static final int ROW_BORDER = 0x88FFFFFF;
    private static final int SLOT_BG = 0x66AEB8C2;
    private static final int SELECTED_SLOT = 0xCC4A90E2;
    private static final int NORMAL_SLOT = 0x66FFFFFF;

    private static int selectedSkillId = DuWangSkillCatalog.STAND_ASSAULT_ID;
    private static long skill1LastTriggerTick = Long.MIN_VALUE;
    private static long skill2LastTriggerTick = Long.MIN_VALUE;

    private DuWangSkillHudOverlay() {
    }

    public static void markSkillTriggered(int skillId, int cooldownTicks) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || cooldownTicks <= 0) {
            return;
        }
        long now = mc.level.getGameTime();
        selectedSkillId = skillId;
        if (skillId == DuWangSkillCatalog.STAND_ASSAULT_ID) {
            skill1LastTriggerTick = now;
        } else if (skillId == DuWangSkillCatalog.HURRICANE_BARRIER_ID) {
            skill2LastTriggerTick = now;
        }
    }

    @SubscribeEvent
    public static void renderSkills(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null || mc.level == null) {
            return;
        }

        LocalPlayer player = mc.player;
        if (!hasOwnedStand(player)) {
            return;
        }

        GuiGraphics gui = event.getGuiGraphics();
        int rowWidth = KEY_TEXT_WIDTH + SKILL_SLOT_SIZE + 10;
        int rowHeight = SKILL_SLOT_SIZE + 4;
        int baseX = event.getWindow().getGuiScaledWidth() - HUD_RIGHT - rowWidth;
        int firstRowY = HUD_TOP;
        int secondRowY = firstRowY + rowHeight + ROW_GAP;

        renderSkillSlot(
                gui, mc, baseX, firstRowY,
                DuWangSkillCatalog.STAND_ASSAULT_ID,
                new ItemStack(ModItems.BLOODY_TOOTH.get()),
                "V",
                skill1LastTriggerTick,
                DuWangSkillCatalog.STAND_ASSAULT_COOLDOWN_TICKS
        );
        renderSkillSlot(
                gui, mc, baseX, secondRowY,
                DuWangSkillCatalog.HURRICANE_BARRIER_ID,
                new ItemStack(ModItems.DUWANG_SPAWN_EGG.get()),
                "B",
                skill2LastTriggerTick,
                DuWangSkillCatalog.HURRICANE_BARRIER_COOLDOWN_TICKS
        );
    }

    private static void renderSkillSlot(
            GuiGraphics gui,
            Minecraft mc,
            int rowX,
            int rowY,
            int skillId,
            ItemStack icon,
            String keyText,
            long lastTriggerTick,
            int cooldownTicks
    ) {
        int rowWidth = KEY_TEXT_WIDTH + SKILL_SLOT_SIZE + 10;
        int rowHeight = SKILL_SLOT_SIZE + 4;
        int slotX = rowX + rowWidth - SKILL_SLOT_SIZE - 3;
        int slotY = rowY + 2;
        boolean selected = selectedSkillId == skillId;
        gui.fill(rowX - 1, rowY - 1, rowX + rowWidth + 1, rowY + rowHeight + 1, ROW_BORDER);
        gui.fill(rowX, rowY, rowX + rowWidth, rowY + rowHeight, ROW_BG);

        int borderColor = selected ? SELECTED_SLOT : NORMAL_SLOT;
        gui.fill(slotX - 1, slotY - 1, slotX + SKILL_SLOT_SIZE + 1, slotY + SKILL_SLOT_SIZE + 1, borderColor);
        gui.fill(slotX, slotY, slotX + SKILL_SLOT_SIZE, slotY + SKILL_SLOT_SIZE, SLOT_BG);

        RenderSystem.enableBlend();
        gui.renderItem(icon, slotX + 1, slotY + 1);
        RenderSystem.disableBlend();

        float remainRatio = getCooldownRemainRatio(mc, lastTriggerTick, cooldownTicks);
        if (remainRatio > 0.0F) {
            int overlayHeight = Mth.ceil(SKILL_SLOT_SIZE * remainRatio);
            gui.fill(slotX, slotY + SKILL_SLOT_SIZE - overlayHeight, slotX + SKILL_SLOT_SIZE, slotY + SKILL_SLOT_SIZE, 0xAA000000);
            int remainSeconds = Mth.ceil((cooldownTicks * remainRatio) / 20.0F);
            String remainText = Integer.toString(remainSeconds);
            int textX = slotX + (SKILL_SLOT_SIZE - mc.font.width(remainText)) / 2;
            int textY = slotY + 6;
            gui.drawString(mc.font, remainText, textX, textY, 0xFFE7E7E7, true);
        }

        int keyTextX = rowX + 4;
        gui.drawString(mc.font, keyText, keyTextX, rowY + 6, 0xFFDEE6EF, false);
    }

    private static boolean hasOwnedStand(LocalPlayer player) {
        List<DuWangEntity> stands = player.level().getEntitiesOfClass(
                DuWangEntity.class,
                player.getBoundingBox().inflate(64.0D),
                stand -> stand.isOwnedBy(player) && stand.isAlive()
        );
        return !stands.isEmpty();
    }

    private static float getCooldownRemainRatio(Minecraft mc, long lastTriggerTick, int cooldownTicks) {
        if (lastTriggerTick == Long.MIN_VALUE || cooldownTicks <= 0 || mc.level == null) {
            return 0.0F;
        }
        long elapsed = mc.level.getGameTime() - lastTriggerTick;
        if (elapsed >= cooldownTicks) {
            return 0.0F;
        }
        return Mth.clamp((cooldownTicks - elapsed) / (float) cooldownTicks, 0.0F, 1.0F);
    }
}
