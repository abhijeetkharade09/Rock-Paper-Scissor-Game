package ui.theme;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;

/**
 * AppTheme — Central design system for Rock Paper Scissors.
 * All colors, fonts, and UIManager setup live here.
 * To restyle the entire app, only edit this file.
 */
public class AppTheme {

    // ─── Background Colors ────────────────────────────────────────────────────
    public static final Color BG_DARK      = new Color(10,  12,  20);
    public static final Color BG_CARD      = new Color(18,  22,  38);
    public static final Color BG_CARD2     = new Color(24,  29,  50);

    // ─── Accent Colors ────────────────────────────────────────────────────────
    public static final Color ACCENT_BLUE  = new Color(64,  156, 255);
    public static final Color ACCENT_PINK  = new Color(255,  72, 150);
    public static final Color ACCENT_GREEN = new Color(50,  220, 140);
    public static final Color ACCENT_GOLD  = new Color(255, 210,  60);

    // ─── Text Colors ──────────────────────────────────────────────────────────
    public static final Color TEXT_PRIMARY = new Color(230, 235, 255);
    public static final Color TEXT_DIM     = new Color(120, 130, 160);

    // ─── State Colors ─────────────────────────────────────────────────────────
    public static final Color WIN_COLOR    = new Color(50,  220, 140);
    public static final Color LOSE_COLOR   = new Color(255,  80,  80);
    public static final Color DRAW_COLOR   = new Color(255, 180,  40);

    // ─── Fonts ────────────────────────────────────────────────────────────────
    public static final Font FONT_DISPLAY = new Font("Dialog",      Font.BOLD,  48);
    public static final Font FONT_TITLE   = new Font("Dialog",      Font.BOLD,  14);
    public static final Font FONT_BODY    = new Font("Dialog",      Font.PLAIN, 13);
    public static final Font FONT_MONO    = new Font("Monospaced",  Font.BOLD,  13);
    public static final Font FONT_SCORE   = new Font("Dialog",      Font.BOLD,  32);
    public static final Font FONT_RESULT  = new Font("Dialog",      Font.BOLD,  18);
    public static final Font FONT_HISTORY = new Font("Monospaced",  Font.PLAIN, 11);

    // ─── Apply Global UIManager Settings ─────────────────────────────────────
    public static void apply() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("ScrollBar.thumb",             new ColorUIResource(new Color(50,  60,  95)));
        UIManager.put("ScrollBar.track",             new ColorUIResource(new Color(20,  24,  42)));
        UIManager.put("ScrollBar.background",        new ColorUIResource(new Color(20,  24,  42)));
        UIManager.put("SplitPane.background",        new ColorUIResource(BG_DARK));
        UIManager.put("SplitPaneDivider.background", new ColorUIResource(new Color(30,  36,  60)));
    }
}