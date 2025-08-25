package com.voc.utils;

public class AnsiColor {
    // Reset code
    public static final String RESET = "\u001b[0m";

    // Text colors
    public static final String BLACK = "\u001b[30m";
    public static final String RED = "\u001b[31m";
    public static final String GREEN = "\u001b[32m";
    public static final String YELLOW = "\u001b[33m";
    public static final String BLUE = "\u001b[34m";
    public static final String MAGENTA = "\u001b[35m";
    public static final String CYAN = "\u001b[36m";
    public static final String WHITE = "\u001b[37m";
    public static final String ORANGE = "\u001b[38;5;208m";

    // Background colors
    public static final String BG_BLACK = "\u001b[40m";
    public static final String BG_RED = "\u001b[41m";

    // Styles
    public static final String BOLD = "\u001b[1m";
    public static final String ITALIC = "\u001b[3m";
    public static final String UNDERLINE = "\u001b[4m";

    // Tags
    public static final String TAG_SUCCESS = "[" + BOLD + GREEN + "VO-CARD" + RESET + "] ";
    public static final String TAG_INFO = "[" + BOLD + BLUE + "VO-CARD" + RESET + "] ";
    public static final String TAG_WARNING = "[" + BOLD + YELLOW + "VO-CARD" + RESET + "] ";
    public static final String TAG_ERROR = "[" + BOLD + RED + "VO-CARD" + RESET + "] ";
    public static final String TAG_DEBUG = "[" + BOLD + MAGENTA + "VO-CARD" + RESET + "] ";
    public static final String TAG_ALERT = "[" + BOLD + ORANGE + "VO-CARD" + RESET + "] ";
}