package net.xelbayria.tarotboards.util;

import java.text.DecimalFormat;

public class StringHelper {

    public static String printCommas(long amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }
}

