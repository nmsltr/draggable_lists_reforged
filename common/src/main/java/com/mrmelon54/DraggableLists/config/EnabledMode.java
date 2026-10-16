package com.mrmelon54.DraggableLists.config;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public enum EnabledMode {
    DISABLED,
    REQUIRES_MODIFIER,
    ENABLED;

    public boolean isEnabled() {
        return switch (this) {
            case DISABLED -> false;
            case REQUIRES_MODIFIER -> isShiftDown();
            case ENABLED -> true;
        };
    }

    // Screen.hasShiftDown() no longer exists, so ask GLFW whether either Shift key is held
    private static boolean isShiftDown() {
        long window = Minecraft.getInstance().getWindow().handle();
        return GLFW.glfwGetKey(window, GLFW.GLFW_KEY_LEFT_SHIFT) == GLFW.GLFW_PRESS
                || GLFW.glfwGetKey(window, GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
    }
}

