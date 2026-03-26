package com.Y1fel.JoJoPlagueSurge.compat;

import net.minecraft.server.level.ServerPlayer;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * JCraft 兼容层：
 * - 按用户要求优先调用 JCraft 内部技能方法；
 * - 使用反射，避免在 JCraft 未安装或 API 变化时直接崩溃；
 * - 如果没有匹配到方法，会返回 false，外层继续使用本模组的本地逻辑。
 */
public final class JCraftCompat {
    private static final String[] POSSIBLE_SKILL_API_CLASSES = {
            "com.jcraft.eyesofender.api.skill.SkillApi",
            "com.jcraft.eyesofender.api.skills.SkillApi",
            "com.jcraft.eyesofender.common.skill.SkillApi"
    };

    private JCraftCompat() {
    }

    public static boolean tryUseDuWangSkill(ServerPlayer player, int skillId) {
        for (String className : POSSIBLE_SKILL_API_CLASSES) {
            try {
                Class<?> api = Class.forName(className);
                if (tryInvoke(api, "useSkill", player, skillId)) {
                    return true;
                }
                if (tryInvoke(api, "castSkill", player, skillId)) {
                    return true;
                }
                if (tryInvoke(api, "triggerSkill", player, skillId)) {
                    return true;
                }
            } catch (ClassNotFoundException ignored) {
                // 该类不存在，继续尝试下一个候选类。
            }
        }
        return false;
    }

    private static boolean tryInvoke(Class<?> api, String methodName, ServerPlayer player, int skillId) {
        try {
            Method method = api.getMethod(methodName, ServerPlayer.class, int.class);
            Object result = method.invoke(null, player, skillId);
            return !(result instanceof Boolean bool) || bool;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            return false;
        }
    }
}
