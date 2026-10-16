package com.mrmelon54.DraggableLists.mixin.server;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.input.MouseButtonInfo;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin extends Screen {
    @Shadow
    protected ServerSelectionList serverSelectionList;

    protected JoinMultiplayerScreenMixin(Component component) {
        super(component);
    }

    // A "mouse released at 0,0 with button 0" event, used to cancel a drag that is still in progress
    @Unique
    private static MouseButtonEvent draggable_lists$releaseEvent() {
        return new MouseButtonEvent(0, 0, new MouseButtonInfo(0, 0));
    }

    @Inject(method = "join", at = @At("HEAD"))
    private void injectedConnect(CallbackInfo info) {
        if (serverSelectionList.isDragging()) serverSelectionList.mouseReleased(draggable_lists$releaseEvent());
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (serverSelectionList.isDragging()) serverSelectionList.mouseReleased(event);
        return super.mouseReleased(event);
    }

    @Override
    public void onClose() {
        if (serverSelectionList.isDragging()) serverSelectionList.mouseReleased(draggable_lists$releaseEvent());
        super.onClose();
    }
}

