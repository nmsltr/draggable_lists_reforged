package com.mrmelon54.DraggableLists.mixin.packs;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mrmelon54.DraggableLists.DragItem;
import com.mrmelon54.DraggableLists.DraggableLists;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.TransferableSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// PackEntry extends TransferableSelectionList.Entry, which is an inner class and can't be extended from here.
// Its own superclass ObjectSelectionList.Entry<TransferableSelectionList.Entry> is fine to extend.
@Mixin(TransferableSelectionList.PackEntry.class)
public abstract class TransferableSelectionList_PackEntryMixin extends ObjectSelectionList.Entry<TransferableSelectionList.Entry> implements DragItem<PackSelectionModel.Entry, TransferableSelectionList.Entry> {
    @Shadow
    @Final
    private TransferableSelectionList parent;

    @Shadow
    @Final
    private PackSelectionModel.Entry pack;

    @Unique
    private boolean draggable_lists$isBeingDragged = false;

    @WrapWithCondition(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 4))
    public boolean removeOnUpArrowButtons(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        return !DraggableLists.CONFIG.disableResourcePackArrows;
    }

    @WrapWithCondition(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 5))
    public boolean removeOffUpArrowButtons(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        return !DraggableLists.CONFIG.disableResourcePackArrows;
    }

    @WrapWithCondition(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 6))
    public boolean removeOnDownArrowButtons(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        return !DraggableLists.CONFIG.disableResourcePackArrows;
    }

    @WrapWithCondition(method = "extractContent", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/blaze3d/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V", ordinal = 7))
    public boolean removeOffDownArrowButtons(GuiGraphicsExtractor instance, RenderPipeline renderPipeline, Identifier identifier, int i, int j, int k, int l) {
        return !DraggableLists.CONFIG.disableResourcePackArrows;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    public void removeMoveTowardEnd(MouseButtonEvent event, boolean doubleClick, CallbackInfoReturnable<Boolean> cir) {
        if (!DraggableLists.CONFIG.disableResourcePackArrows) return;

        // Allow using the whole pack selection button
        if (pack.canSelect()) return;

        double f = event.x() - (double) getContentX();
        if (f < 16 || f > 32) return;

        cir.setReturnValue(true);
    }

    @Override
    public PackSelectionModel.Entry draggable_lists$getUnderlyingData() {
        return pack;
    }

    @Override
    public TransferableSelectionList.Entry draggable_lists$getUnderlyingEntry() {
        return (TransferableSelectionList.Entry) (Object) this;
    }

    @Override
    public void draggable_lists$render(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, boolean hovered, float tickDelta) {
        if (!draggable_lists$isBeingDragged) return;
        extractContent(guiGraphics, mouseX, mouseY, hovered, tickDelta);
    }

    @Override
    public void draggable_lists$setBeingDragged(boolean v) {
        draggable_lists$isBeingDragged = v;
    }
}

