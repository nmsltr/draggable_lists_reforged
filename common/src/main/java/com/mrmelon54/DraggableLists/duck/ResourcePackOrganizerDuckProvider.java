package com.mrmelon54.DraggableLists.duck;

import net.minecraft.client.gui.screens.packs.PackSelectionModel;

public interface ResourcePackOrganizerDuckProvider {
    /**
     * Tells the screen that the pack lists changed (the screen rebuilds both lists).
     * Since 26.x the callback needs the entry that was moved, so the list can focus it again.
     */
    void draggable_lists$updateSelectedList(PackSelectionModel.EntryBase movedEntry);
}