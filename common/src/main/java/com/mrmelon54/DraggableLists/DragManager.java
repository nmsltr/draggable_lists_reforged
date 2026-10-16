package com.mrmelon54.DraggableLists;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;

public class DragManager<T, E extends ObjectSelectionList.Entry<?>> {
    private final DragList<T, E> dragList;
    private DragItem<T, E> selectedItem;
    private double draggingStartX = 0;
    private double draggingStartY = 0;
    private double draggingOffsetX = 0;
    private double draggingOffsetY = 0;
    private double softScrollingTimer = 0;
    private double softScrollingOrigin = 0;

    public DragManager(DragList<T, E> dragList) {
        this.dragList = dragList;
    }

    public boolean isDragging() {
        return selectedItem != null;
    }

    public boolean mouseReleased(double mouseX, double mouseY) {
        dragList.draggable_lists$setDragging(false);
        if (selectedItem == null) return false;

        DragItem<T, E> selectedItem1 = selectedItem;
        selectedItem = null;
        softScrollingTimer = 0;

        Cursor.reset();
        double y = capYCoordinate((int) mouseY, true);
        selectedItem1.draggable_lists$setBeingDragged(false);

        DragItem<T, E> hoveredEntry = dragList.draggable_lists$getEntryAtPosition(mouseX, y);
        if (hoveredEntry == null) return false;

        T draggingData = selectedItem1.draggable_lists$getUnderlyingData();
        T hoveredData = hoveredEntry.draggable_lists$getUnderlyingData();

        // if the items are null, identical or a dragServerItem call fails then stop here
        return draggingData != null && hoveredData != null && draggingData != hoveredData && dragServerItem(selectedItem1, y);
    }

    public boolean mouseDragged(double mouseX, double mouseY) {
        if (selectedItem != null || !isCapMouseY((int) mouseY)) return selectedItem != null;

        selectedItem = dragList.draggable_lists$getEntryAtPosition(mouseX, mouseY);
        if (selectedItem == null) return false;

        // Save the mouse origin position and the offset for the top left corner of the widget
        draggingStartX = mouseX;
        draggingStartY = mouseY;
        draggingOffsetX = dragList.draggable_lists$getRowLeft() - draggingStartX;
        int entryIndex = dragList.draggable_lists$getIndexOfEntry(selectedItem);
        if (entryIndex < 0) {
            selectedItem = null;
            return false;
        }
        draggingOffsetY = dragList.draggable_lists$getRowTop(entryIndex) - draggingStartY;

        // Don't grab if inside the icon (32px icon + 2px entry padding)
        if (draggingOffsetX > -34f) {
            selectedItem = null;
            return false;
        }

        dragList.draggable_lists$setDragging(true);
        selectedItem.draggable_lists$getUnderlyingEntry().setFocused(true);
        selectedItem.draggable_lists$setBeingDragged(true);
        softScrollingTimer = 0;
        Cursor.setDragging();
        return true;
    }

    public boolean mouseScrolled(double mouseX, double mouseY, double deltaX, double deltaY) {
        return selectedItem != null;
    }

    /**
     * Draws every row except the dragged one. Rows between the original and the target
     * slot are shifted by one slot to leave a gap.
     */
    public void renderListItems(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        int n = dragList.draggable_lists$getItemCount();

        int draggingIndex = selectedItem == null ? -1 : dragList.draggable_lists$getIndexOfEntry(selectedItem);
        int draggedPosition = selectedItem == null ? -1 : getIndexFromMouseY(mouseY);

        for (int i = 0; i < n; i++) {
            if (i == draggingIndex) continue;

            int renderRow = otherItemIndexShift(i, draggingIndex, draggedPosition);
            int rowTop = dragList.draggable_lists$getRowTop(renderRow);
            int rowBottom = dragList.draggable_lists$getRowBottom(renderRow);
            if (rowBottom >= dragList.draggable_lists$getY() && rowTop <= dragList.draggable_lists$getBottom()) {
                dragList.draggable_lists$renderItem(guiGraphics, mouseX, mouseY, tickDelta, i, rowTop);
            }
        }
    }

    private int otherItemIndexShift(int i, int draggingIndex, int draggedPosition) {
        if (i == draggingIndex) return draggedPosition;
        if (draggingIndex < draggedPosition) return i >= draggingIndex && i <= draggedPosition ? i - 1 : i;
        return i >= draggedPosition && i <= draggingIndex ? i + 1 : i;
    }

    /**
     * Draws the dragged row on top of everything, following the mouse vertically.
     */
    public void renderWidget(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        if (selectedItem == null) return;

        E entry = selectedItem.draggable_lists$getUnderlyingEntry();

        int z = Mth.floor(mouseY + draggingOffsetY);
        int x = Mth.floor(draggingStartX + draggingOffsetX);
        int y = capYCoordinate(z);
        int entryHeight = entry.getHeight();
        int entryWidth = entry.getWidth();

        // Temporarily move the entry to the dragged position; the entry draws itself at its own x/y
        int oldX = entry.getX();
        int oldY = entry.getY();
        entry.setX(x);
        entry.setY(y);

        guiGraphics.pose().pushMatrix();
        float shaderColorValue = 191f / 255f;
        guiGraphics.fill(x + 1, y + 1, x + entryWidth - 1, y + entryHeight - 1, ARGB.colorFromFloat(0.5f, shaderColorValue, shaderColorValue, shaderColorValue));
        selectedItem.draggable_lists$render(guiGraphics, mouseX, mouseY, false, delta);
        guiGraphics.pose().popMatrix();

        entry.setX(oldX);
        entry.setY(oldY);

        if (y < z) {
            if (softScrollingTimer == 0) {
                softScrollingTimer = Util.getMillis();
                softScrollingOrigin = dragList.draggable_lists$getScrollAmount();
            }
            float f = (float) (Util.getMillis() - softScrollingTimer) / 5f;
            dragList.draggable_lists$setScrollAmount(softScrollingOrigin + f);
        } else if (y > z) {
            if (softScrollingTimer == 0) {
                softScrollingTimer = Util.getMillis();
                softScrollingOrigin = dragList.draggable_lists$getScrollAmount();
            }
            float f = (float) (Util.getMillis() - softScrollingTimer) / 5f;
            dragList.draggable_lists$setScrollAmount(softScrollingOrigin - f);
        } else {
            softScrollingTimer = 0;
        }
    }

    private int capYCoordinate(int y, boolean useScreenSpace) {
        int scrollableTop = dragList.draggable_lists$getY() + 2;
        int scrollableHeight = dragList.draggable_lists$getBottom() - dragList.draggable_lists$getY() - (useScreenSpace ? 2 : dragList.draggable_lists$getItemHeight() + 2);
        if (y < scrollableTop) y = scrollableTop;
        if (y > scrollableTop + scrollableHeight) y = scrollableTop + scrollableHeight;
        return y;
    }

    private int capYCoordinate(int y) {
        return capYCoordinate(y, false);
    }

    private boolean isCapMouseY(int y) {
        return capYCoordinate(y, true) == y;
    }

    /**
     * Finds the slot under the given y using the real row positions
     * (rows can have different heights, and the header is a row too).
     */
    private int getIndexFromMouseY(double mouseY) {
        int count = dragList.draggable_lists$getItemCount();
        int first = dragList.draggable_lists$getFirstDraggableIndex();
        for (int i = first; i < count; i++) {
            if (mouseY < dragList.draggable_lists$getRowBottom(i)) return i;
        }
        return Math.max(first, count - 1);
    }

    private boolean dragServerItem(DragItem<T, E> item, double mouseY) {
        int n = getIndexFromMouseY(mouseY);
        dragList.draggable_lists$moveEntry(item, n);
        return true;
    }
}

