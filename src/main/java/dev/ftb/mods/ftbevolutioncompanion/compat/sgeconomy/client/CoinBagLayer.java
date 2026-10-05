package dev.ftb.mods.ftbevolutioncompanion.compat.sgeconomy.client;

import net.neoforged.neoforge.client.event.ScreenEvent;

import java.lang.reflect.Method;

public final class CoinBagLayer {
    private static final Method EVENT_HANDLER = findEventHandler();
    private static boolean drawing;

    private CoinBagLayer() {}

    public static boolean isDrawing() {
        return drawing;
    }

    public static void onRenderForeground(ScreenEvent.Render.Foreground event) {
        if (EVENT_HANDLER == null) {
            return;
        }
        drawing = true;
        try {
            EVENT_HANDLER.invoke(
                    null,
                    new ScreenEvent.Render.Post(
                            event.getScreen(),
                            event.getGuiGraphics(),
                            event.getMouseX(),
                            event.getMouseY(),
                            event.getPartialTick()));
        } catch (ReflectiveOperationException ignored) {
        } finally {
            drawing = false;
        }
    }

    private static Method findEventHandler() {
        try {
            return Class.forName("net.sirgrantd.sg_economy.internal.gui.CurrencyDisplay")
                    .getMethod("eventHandler", ScreenEvent.Render.Post.class);
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }
}
