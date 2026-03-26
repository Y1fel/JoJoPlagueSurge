package com.Y1fel.JoJoPlagueSurge.client;

import com.Y1fel.JoJoPlagueSurge.ModEntrance;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class ModKeyMappings {
    public static final String CATEGORY = "key.categories." + ModEntrance.MODID;

    public static final KeyMapping DUWANG_SKILL_1 = new KeyMapping(
            "key." + ModEntrance.MODID + ".duwang_skill_1",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_V,
            CATEGORY
    );

    public static final KeyMapping DUWANG_SKILL_2 = new KeyMapping(
            "key." + ModEntrance.MODID + ".duwang_skill_2",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            CATEGORY
    );

    public static final KeyMapping TOGGLE_STAND = new KeyMapping(
            "key." + ModEntrance.MODID + ".toggle_stand",
            KeyConflictContext.IN_GAME,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_G,
            CATEGORY
    );

    private ModKeyMappings() {
    }
}
