package com.mrmelon54.DraggableLists.mixin.server;

import com.mrmelon54.DraggableLists.DragItem;
import com.mrmelon54.DraggableLists.DragList;
import com.mrmelon54.DraggableLists.DragManager;
import com.mrmelon54.DraggableLists.DraggableLists;
import com.mrmelon54.DraggableLists.duck.ServerListDuckProvider;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerSelectionList.class)
@Environment(EnvType.CLIENT)
public abstract class ServerSelectionListMixin extends ObjectSelectionList<ServerSelectionList.Entry> implements DragList<ServerData, ServerSelectionList.OnlineServerEntry> {
    @Shadow
    @Final
    private JoinMultiplayerScreen screen;

    @Shadow
    public abstract void updateOnlineServers(ServerList serverList);

    @Unique
    private final DragManager<ServerData, ServerSelectionList.OnlineServerEntry> draggable_lists$dragManager = new DragManager<>(this);

    public ServerSelectionListMixin(Minecraft minecraftClient, int i, int j, int k, int l, int m) {
        super(minecraftClient, i, j, k, l);
    }

    @Override
    protected void extractListItems(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta) {
        if (draggable_lists$dragManager.isDragging()) {
            draggable_lists$dragManager.renderListItems(guiGraphics, mouseX, mouseY, tickDelta);
        } else {
            super.extractListItems(guiGraphics, mouseX, mouseY, tickDelta);
        }
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (draggable_lists$dragManager.mouseReleased(event.x(), event.y())) return true;
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (DraggableLists.CONFIG.serverDraggingEnabled.isEnabled() && draggable_lists$dragManager.mouseDragged(event.x(), event.y())) return true;
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
    public DragItem<ServerData, ServerSelectionList.OnlineServerEntry> draggable_lists$getEntryAtPosition(double mouseX, double mouseY) {
        ServerSelectionList.Entry entryAtPosition = getEntryAtPosition(mouseX, mouseY);
        if (entryAtPosition instanceof ServerSelectionList.OnlineServerEntry onlineServerEntry) {
            return (DragItem<ServerData, ServerSelectionList.OnlineServerEntry>) onlineServerEntry;
        }
        return null;
    }

    @Override
    public int draggable_lists$getIndexOfEntry(DragItem<ServerData, ServerSelectionList.OnlineServerEntry> selectedItem) {
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
    public void draggable_lists$moveEntry(DragItem<ServerData, ServerSelectionList.OnlineServerEntry> item, int n) {
        ServerList servers = screen.getServers();
        if (servers instanceof ServerListDuckProvider duckProvider) {
            duckProvider.draggable_lists$moveItem(item, n);
            servers.save();
            updateOnlineServers(servers);
        }
    }

    @Override
    public int draggable_lists$getItemCount() {
        return getItemCount();
    }

    @Override
    public int draggable_lists$getFirstDraggableIndex() {
        // Online servers come first in the list, the LAN header and LAN servers follow them
        return 0;
    }

    @Override
    public void draggable_lists$renderItem(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float tickDelta, int index, int y) {
        ServerSelectionList.Entry entry = children().get(index);
        // Rows draw themselves at their own position, so move the row for the duration of the draw
        int oldY = entry.getY();
        entry.setY(y);
        extractItem(guiGraphics, mouseX, mouseY, tickDelta, entry);
        entry.setY(oldY);
    }
}

