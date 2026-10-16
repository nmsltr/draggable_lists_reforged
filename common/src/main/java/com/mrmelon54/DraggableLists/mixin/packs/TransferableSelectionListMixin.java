package com.mrmelon54.DraggableLists.mixin.packs;

import com.mrmelon54.DraggableLists.DragItem;
import com.mrmelon54.DraggableLists.DragList;
import com.mrmelon54.DraggableLists.DragManager;
import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.duck.AbstractPackDuckProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(TransferableSelectionList.class)
@Environment(EnvType.CLIENT)
public abstract class TransferableSelectionListMixin extends ObjectSelectionList<TransferableSelectionList.Entry> implements DragList<PackSelectionModel.Entry, TransferableSelectionList.Entry> {
    @Unique
    private final DragManager<PackSelectionModel.Entry, TransferableSelectionList.Entry> draggable_lists$dragManager = new DragManager<>(this);

    public TransferableSelectionListMixin(Minecraft minecraft, int i, int j, int k, int l, int m) {
        super(minecraft, i, j, k, l);
    }

    @Override
    protected void extractListItems(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        if (draggable_lists$dragManager.isDragging()) {
            draggable_lists$dragManager.renderListItems(guiGraphics, mouseX, mouseY, tickDelta);
        } else {
            super.extractListItems(guiGraphics, mouseX, mouseY, tickDelta);
        }
    }

    @Unique
    private boolean draggable_lists$isMouseOverScrollbar(double mouseX) {
        return scrollable() && mouseX >= scrollBarX();
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggable_lists$dragManager.mouseReleased(event.x(), event.y())) return true;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (DraggableLists.CONFIG.resourcePackDraggingEnabled.isEnabled() && !draggable_lists$isMouseOverScrollbar(event.x()) && draggable_lists$dragManager.mouseDragged(event.x(), event.y())) return true;
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (draggable_lists$dragManager.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount)) return true;
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }

    @Override
    public void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float delta) {
        super.extractWidgetRenderState(guiGraphics, mouseX, mouseY, delta);
        draggable_lists$dragManager.renderWidget(guiGraphics, mouseX, mouseY, delta);
    }

    @Override
    @SuppressWarnings("unchecked")
    public DragItem<PackSelectionModel.Entry, TransferableSelectionList.Entry> draggable_lists$getEntryAtPosition(double mouseX, double mouseY) {
        TransferableSelectionList.Entry entryAtPosition = getEntryAtPosition(mouseX, mouseY);
        // The header row is also an entry but it can't be dragged
        if (entryAtPosition instanceof DragItem<?, ?> item) {
            return (DragItem<PackSelectionModel.Entry, TransferableSelectionList.Entry>) item;
        }
        return null;
    }

    @Override
    public int draggable_lists$getIndexOfEntry(DragItem<PackSelectionModel.Entry, TransferableSelectionList.Entry> selectedItem) {
        return children().indexOf(selectedItem.draggable_lists$getUnderlyingEntry());
    }

    @Override
    public void draggable_lists$setDragging(boolean b) {
        super.setDragging(b);
    }

    @Override
    public int draggable_lists$getY() {
        return getY();
    }

    @Override
    public int draggable_lists$getBottom() {
        return getBottom();
    }

    @Override
    public int draggable_lists$getItemHeight() {
        return defaultEntryHeight;
    }

    @Override
    public int draggable_lists$getRowTop(int i) {
        return getRowTop(i);
    }

    @Override
    public int draggable_lists$getRowBottom(int i) {
        return getRowBottom(i);
    }

    @Override
    public double draggable_lists$getRowLeft() {
        return getRowLeft();
    }

    @Override
    public int draggable_lists$getRowWidth() {
        return getRowWidth();
    }

    @Override
    public double draggable_lists$getScrollAmount() {
        return scrollAmount();
    }

    @Override
    public void draggable_lists$setScrollAmount(double v) {
        setScrollAmount(v);
    }

    @Override
    public void draggable_lists$moveEntry(DragItem<PackSelectionModel.Entry, TransferableSelectionList.Entry> item, int n) {
        if (item.draggable_lists$getUnderlyingData() instanceof AbstractPackDuckProvider duckProvider) {
            // n counts the header row, the pack model doesn't
            duckProvider.draggable_lists$moveTo(Math.max(0, n - draggable_lists$getFirstDraggableIndex()));
        }
    }

    @Override
    public int draggable_lists$getItemCount() {
        return getItemCount();
    }

    @Override
    public int draggable_lists$getFirstDraggableIndex() {
        var entries = children();
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i) instanceof DragItem<?, ?>) return i;
        }
        return 0;
    }

    @Override
    public void draggable_lists$renderItem(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta, int index, int y) {
        TransferableSelectionList.Entry entry = children().get(index);
        // Rows draw themselves at their own position, so move the row for the duration of the draw
        int oldY = entry.getY();
        entry.setY(y);
        extractItem(guiGraphics, mouseX, mouseY, tickDelta, entry);
        entry.setY(oldY);
    }
}

