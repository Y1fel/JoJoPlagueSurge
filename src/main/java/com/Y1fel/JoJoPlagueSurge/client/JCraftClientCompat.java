package com.Y1fel.JoJoPlagueSurge.client;

import net.minecraftforge.client.event.RenderGuiOverlayEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 客户端 JCraft 兼容层：
 * 尝试直接调用 JCraft 的 HUD 渲染逻辑；若不存在对应类/方法则返回 false 并走本模组兜底 HUD。
 */
public final class JCraftClientCompat {
    private static final String[] POSSIBLE_HUD_CLASSES = {
            "com.jcraft.eyesofender.client.hud.SkillHudOverlay",
            "com.jcraft.eyesofender.client.gui.SkillHudOverlay",
            "com.jcraft.eyesofender.client.render.SkillHudRenderer"
    };

    private JCraftClientCompat() {
    }

    public static boolean tryRenderSkillHud(RenderGuiOverlayEvent.Post event) {
        for (String className : POSSIBLE_HUD_CLASSES) {
            try {
                Class<?> hudClass = Class.forName(className);
                if (invokeHud(hudClass, "render", event)) {
                    return true;
                }
                if (invokeHud(hudClass, "onRenderOverlay", event)) {
                    return true;
                }
                if (invokeHud(hudClass, "renderOverlay", event)) {
                    return true;
                }
            } catch (ClassNotFoundException ignored) {
                // 尝试下一个候选类。
            }
        }
        return false;
    }

    private static boolean invokeHud(Class<?> hudClass, String methodName, RenderGuiOverlayEvent.Post event) {
        try {
            Method method = hudClass.getMethod(methodName, RenderGuiOverlayEvent.Post.class);
            Object result = method.invoke(null, event);
            return !(result instanceof Boolean bool) || bool;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            return false;
        }
    }
}
