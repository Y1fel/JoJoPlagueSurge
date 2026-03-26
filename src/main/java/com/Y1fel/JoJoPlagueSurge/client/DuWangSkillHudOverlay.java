package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.Y1fel.JoJoPlagueSurge.entity.custom.duwang.DuWangEntity;
import com.Y1fel.JoJoPlagueSurge.item.ModItems;
import com.Y1fel.JoJoPlagueSurge.skill.DuWangSkillCatalog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

/**
 * JCraft 风格技能栏（简化版）：
 * - 只在替身召唤出来时显示；
 * - 用现有物品图标充当技能图片，先把“技能图 + 键位提示”这条链路跑通；
 * - 这里保留注释，后续你可以直接替换成独立 PNG 技能图。
 */
@Mod.EventBusSubscriber(modid = ModEntrance.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class DuWangSkillHudOverlay {
    private DuWangSkillHudOverlay() {
    }

    @SubscribeEvent
    public static void renderSkills(RenderGuiOverlayEvent.Post event) {
        // 优先直接走 JCraft 的 HUD 逻辑，成功则不再绘制本地兜底技能栏。
        if (JCraftClientCompat.tryRenderSkillHud(event)) {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null || mc.level == null) {
            return;
        }

        LocalPlayer player = mc.player;
        if (!hasOwnedStand(player)) {
            return;
        }

        GuiGraphics gui = event.getGuiGraphics();
        int baseX = 12;
        int baseY = event.getWindow().getGuiScaledHeight() - 54;

        gui.fill(baseX - 6, baseY - 6, baseX + 146, baseY + 30, 0x77000000);

        // 技能1图片（可替换成你自己的 skill_1.png）
        gui.renderItem(new ItemStack(ModItems.BLOODY_TOOTH.get()), baseX, baseY);
        gui.drawString(mc.font, "[V] " + DuWangSkillCatalog.displayNameZh(DuWangSkillCatalog.STAND_ASSAULT_ID),
                baseX + 22, baseY + 5, 0xFFFFFF, false);

        // 技能2图片（可替换成你自己的 skill_2.png）
        gui.renderItem(new ItemStack(ModItems.DUWANG_SPAWN_EGG.get()), baseX + 74, baseY);
        gui.drawString(mc.font, "[B] " + DuWangSkillCatalog.displayNameZh(DuWangSkillCatalog.HURRICANE_BARRIER_ID),
                baseX + 96, baseY + 5, 0xD6F3FF, false);
    }

    private static boolean hasOwnedStand(LocalPlayer player) {
        List<DuWangEntity> stands = player.level().getEntitiesOfClass(
                DuWangEntity.class,
                player.getBoundingBox().inflate(64.0D),
                stand -> stand.isOwnedBy(player) && stand.isAlive()
        );
        return !stands.isEmpty();
    }
}
