package com.mrmelon54.DraggableLists;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;

public interface DragList<T, E extends ObjectSelectionList.Entry<?>> {
    DragItem<T, E> draggable_lists$getEntryAtPosition(double mouseX, double mouseY);

    int draggable_lists$getIndexOfEntry(DragItem<T, E> selectedItem);

    void draggable_lists$setDragging(boolean b);

    int draggable_lists$getY();

    int draggable_lists$getBottom();

    /** Height of a normal (draggable) row, e.g. defaultEntryHeight. */
    int draggable_lists$getItemHeight();

    int draggable_lists$getRowTop(int i);

    int draggable_lists$getRowBottom(int i);

    double draggable_lists$getRowLeft();

    int draggable_lists$getRowWidth();

    double draggable_lists$getScrollAmount();

    void draggable_lists$setScrollAmount(double v);

    /** n is the index in the list's children (header entries included). */
    void draggable_lists$moveEntry(DragItem<T, E> item, int n);

    int draggable_lists$getItemCount();

    /** Index of the first child that can be dragged (entries before it are headers etc.). */
    int draggable_lists$getFirstDraggableIndex();

    /** Draws the child at the given index with its top edge at y. */
    void draggable_lists$renderItem(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta, int index, int y);
}

