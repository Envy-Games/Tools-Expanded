package com.styenvy.egtools;

import com.mojang.brigadier.Command;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientCommandsEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

import static net.minecraft.commands.Commands.literal;

@EventBusSubscriber(modid = EgTools.MODID, value = Dist.CLIENT)
public final class CoordHudClient {
    private static final ResourceLocation LAYER_ID = ResourceLocation.fromNamespaceAndPath(EgTools.MODID, "coordshud");
    private static final int BACKGROUND_COLOR = 0x7A1A0E04;
    private static final int GOLD = 0xFFFFAA00;
    private static final int COORDINATE_TEXT = 0xFFE6E6E6;
    private static final int PANEL_PADDING_X = 6;
    private static final int PANEL_PADDING_Y = 4;
    private static final int BOTTOM_OFFSET = 62;

    private static boolean enabled;

    private CoordHudClient() {}

    @SubscribeEvent
    public static void registerClientCommands(RegisterClientCommandsEvent event) {
        event.getDispatcher().register(literal("coordshud")
                .executes(context -> {
                    enabled = !enabled;
                    Minecraft minecraft = Minecraft.getInstance();
                    if (minecraft.player != null) {
                        minecraft.player.displayClientMessage(toggleMessage(), true);
                    } else {
                        context.getSource().sendSuccess(CoordHudClient::toggleMessage, false);
                    }
                    return Command.SINGLE_SUCCESS;
                }));
    }

    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAbove(VanillaGuiLayers.HOTBAR, LAYER_ID, CoordHudClient::render);
    }

    private static void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        Minecraft minecraft = Minecraft.getInstance();
        if (!enabled || minecraft.options.hideGui || minecraft.player == null || minecraft.level == null) {
            return;
        }

        Font font = minecraft.font;
        BlockPos pos = minecraft.player.blockPosition();
        String label = "XYZ: ";
        String coordinates = pos.getX() + " " + pos.getY() + " " + pos.getZ();
        String direction = "  " + directionLetter(minecraft.player.getDirection());
        String time = "  " + formatDayTime(minecraft.level.getDayTime());

        int width = font.width(label) + font.width(coordinates) + font.width(direction) + font.width(time);
        int panelWidth = width + PANEL_PADDING_X * 2;
        int panelHeight = font.lineHeight + PANEL_PADDING_Y * 2;
        int panelX = (guiGraphics.guiWidth() - panelWidth) / 2;
        int panelY = Math.max(2, guiGraphics.guiHeight() - BOTTOM_OFFSET);
        int textX = panelX + PANEL_PADDING_X;
        int textY = panelY + PANEL_PADDING_Y;

        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, BACKGROUND_COLOR);
        textX = drawText(guiGraphics, font, label, textX, textY, GOLD);
        textX = drawText(guiGraphics, font, coordinates, textX, textY, COORDINATE_TEXT);
        textX = drawText(guiGraphics, font, direction, textX, textY, GOLD);
        drawText(guiGraphics, font, time, textX, textY, GOLD);
    }

    private static int drawText(GuiGraphics guiGraphics, Font font, String text, int x, int y, int color) {
        guiGraphics.drawString(font, text, x, y, color, true);
        return x + font.width(text);
    }

    private static Component toggleMessage() {
        return Component.literal("Coordinate HUD " + (enabled ? "enabled" : "disabled"))
                .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.YELLOW);
    }

    private static String directionLetter(Direction direction) {
        return switch (direction) {
            case NORTH -> "N";
            case SOUTH -> "S";
            case EAST -> "E";
            case WEST -> "W";
            default -> "";
        };
    }

    private static String formatDayTime(long dayTime) {
        int dayMinutes = (int)((Math.floorMod(dayTime, 24000L) + 6000L) % 24000L * 1440L / 24000L);
        int hours = dayMinutes / 60;
        int minutes = dayMinutes % 60;
        return String.format("%02d:%02d", hours, minutes);
    }
}
