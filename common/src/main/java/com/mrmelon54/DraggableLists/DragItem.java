package com.mrmelon54.DraggableLists;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;

public interface DragItem<T, E extends ObjectSelectionList.Entry<?>> {
    T draggable_lists$getUnderlyingData();

    E draggable_lists$getUnderlyingEntry();

    /**
     * Draws the entry at its CURRENT position (entry.getX()/getY()).
     * The caller moves the entry with setX/setY before calling this.
     */
    void draggable_lists$render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovered, float tickDelta);

    void draggable_lists$setBeingDragged(boolean v);
}

