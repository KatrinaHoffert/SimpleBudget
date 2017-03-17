package katrinahoffert.simplebudget.util;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

public class CurrencyFormatter {
    public static String format(Context context, int value) {
        String symbol = PreferenceManager.getDefaultSharedPreferences(context).getString("currency_symbol", null);
        String placement = PreferenceManager.getDefaultSharedPreferences(context).getString("currency_symbol_placement", null);
        String separator = PreferenceManager.getDefaultSharedPreferences(context).getString("decimal_separator", null);

        String formatted = (value / 100) + separator + String.format("%02d", value % 100);
        if(placement.equals("left")) return symbol + formatted;
        else return formatted + symbol;
    }
}
