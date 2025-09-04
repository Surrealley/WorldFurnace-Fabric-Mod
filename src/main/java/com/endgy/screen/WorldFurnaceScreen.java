package com.endgy.screen;

import com.endgy.WorldFurnace;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class WorldFurnaceScreen extends HandledScreen<WorldFurnaceScreenHandler> {
    private static final Identifier TEXTURE = new Identifier(WorldFurnace.MOD_ID, "textures/gui/world_furnace_gui.png");

    public WorldFurnaceScreen(WorldFurnaceScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
        titleY = 1000;
        playerInventoryTitleY = 1000;
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
        RenderSystem.setShaderTexture(0, TEXTURE);
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        context.drawTexture(TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight);

        renderProgressArrow(context, x, y);
        renderFire(context,x,y);
    }

    private void renderProgressArrow(DrawContext context, int x, int y) {
        if (handler.isLiting()) {
            int progress = handler.getScaledProgress();
            int fullHeight = 73; // full height of your arrow

            // draw from bottom to top
            int arrowX = x + 115;
            int arrowY = y + 7 + (fullHeight - progress); // shift starting point upward
            int textureV = fullHeight - progress;         // shift texture coordinates too

            context.drawTexture(TEXTURE, arrowX, arrowY,
                    176, textureV, // source X/Y in the texture
                    18, progress); // width, height to draw
        }
    }

    private void renderFire(DrawContext context,int x, int y){
        if(handler.isLiting()) {
            float percent = handler.getPercentProgress();

            if (percent >= 0.8)
                context.drawTexture(TEXTURE,x+144,y+45,
                        176,96,
                        24,22);
            else if (percent >= 0) {
                context.drawTexture(TEXTURE,x+144,y+45,
                        176,73,
                        24,22);
            }
        }
    }


    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        renderBackground(context
//                , mouseX, mouseY, delta
        );
        super.render(context, mouseX, mouseY, delta);
        drawMouseoverTooltip(context, mouseX, mouseY);
    }
}