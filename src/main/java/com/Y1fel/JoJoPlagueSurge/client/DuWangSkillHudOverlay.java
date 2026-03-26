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
    private static final int SKILL_SLOT_SIZE = 20;
    private static final int PANEL_PADDING = 6;
    private static final int PANEL_HEIGHT = 42;
    private static final int SLOT_GAP = 8;
    private static final int PANEL_BORDER = 0xCC404040;
    private static final int PANEL_BG = 0x99000000;
    private static final int SLOT_BG = 0xCC111111;
    private static final int SELECTED_SLOT = 0xCC2D8CF0;
    private static final int NORMAL_SLOT = 0x88484848;
    private static final int KEYCAP_BG = 0xCC0A0A0A;
    private static final int KEYCAP_BORDER = 0xFFB7B7B7;
    private static final int ICON_Y_OFFSET = 1;

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
        int panelWidth = (SKILL_SLOT_SIZE * 2) + SLOT_GAP + (PANEL_PADDING * 2);
        int panelX = (event.getWindow().getGuiScaledWidth() - panelWidth) / 2;
        int panelY = event.getWindow().getGuiScaledHeight() - 58;
        int firstSlotX = panelX + PANEL_PADDING;
        int secondSlotX = firstSlotX + SKILL_SLOT_SIZE + SLOT_GAP;
        int slotY = panelY + PANEL_PADDING;

        gui.fill(panelX - 1, panelY - 1, panelX + panelWidth + 1, panelY + PANEL_HEIGHT + 1, PANEL_BORDER);
        gui.fill(panelX, panelY, panelX + panelWidth, panelY + PANEL_HEIGHT, PANEL_BG);

        renderSkillSlot(
                gui, mc, firstSlotX, slotY,
                DuWangSkillCatalog.STAND_ASSAULT_ID,
                new ItemStack(ModItems.BLOODY_TOOTH.get()),
                "V",
                skill1LastTriggerTick,
                DuWangSkillCatalog.STAND_ASSAULT_COOLDOWN_TICKS
        );
        renderSkillSlot(
                gui, mc, secondSlotX, slotY,
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
            int slotX,
            int slotY,
            int skillId,
            ItemStack icon,
            String keyText,
            long lastTriggerTick,
            int cooldownTicks
    ) {
        boolean selected = selectedSkillId == skillId;
        int borderColor = selected ? SELECTED_SLOT : NORMAL_SLOT;
        gui.fill(slotX - 1, slotY - 1, slotX + SKILL_SLOT_SIZE + 1, slotY + SKILL_SLOT_SIZE + 1, borderColor);
        gui.fill(slotX, slotY, slotX + SKILL_SLOT_SIZE, slotY + SKILL_SLOT_SIZE, SLOT_BG);

        RenderSystem.enableBlend();
        gui.renderItem(icon, slotX + 2, slotY + ICON_Y_OFFSET + 2);
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

        int keycapWidth = 12;
        int keycapHeight = 9;
        int keycapX = slotX + SKILL_SLOT_SIZE - keycapWidth;
        int keycapY = slotY + SKILL_SLOT_SIZE - keycapHeight;
        gui.fill(keycapX - 1, keycapY - 1, keycapX + keycapWidth + 1, keycapY + keycapHeight + 1, KEYCAP_BORDER);
        gui.fill(keycapX, keycapY, keycapX + keycapWidth, keycapY + keycapHeight, KEYCAP_BG);

        int keyTextX = keycapX + (keycapWidth - mc.font.width(keyText)) / 2;
        gui.drawString(mc.font, keyText, keyTextX, keycapY + 1, 0xFFFFFFFF, false);
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
