package katrinahoffert.simplebudget.util;

import android.content.Context;
import android.preference.PreferenceManager;
import android.util.Log;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;

public class CurrencyFormatter {
    /**
     * Formats an integer number of cents into the given format. Will use the currency symbol and
     * decimal separator from the settings. The placement of the currency symbol depends on the
     * settings, as well.
     * @param context
     * @param value
     * @return The formatted string. Eg, 124 will become "$1.24", -4140 will become "$-41.50", etc.
     */
    public static String format(Context context, int value) {
        String symbol = PreferenceManager.getDefaultSharedPreferences(context).getString("currency_symbol", null);
        String placement = PreferenceManager.getDefaultSharedPreferences(context).getString("currency_symbol_placement", null);
        String separator = PreferenceManager.getDefaultSharedPreferences(context).getString("decimal_separator", null);

        String formatted = (value / 100) + separator + String.format("%02d", Math.abs(value % 100));
        if(placement.equals("left")) return symbol + formatted;
        else return formatted + symbol;
    }

    /**
     * Parses an input dollar amount to an integer number of "cents" (or whatever). The decimal
     * separator will be used from settings. Negative numbers are allowed. There must be a maximum of
     * two decimal places.
     * @param context Application context.
     * @param value The value to parse.
     * @return The number of cents. Eg, "123.45" will become 12345, "-.05" will become -5, etc.
     * @throws NumberFormatException The input is invalid. Maybe not a number, maybe using the
     * wrong decimal separator, maybe too many digits after the decimal.
     */
    public static int parse(Context context, String value) throws NumberFormatException {
        String separator = PreferenceManager.getDefaultSharedPreferences(context).getString("decimal_separator", null);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator(separator.charAt(0));

        DecimalFormat format = new DecimalFormat("#0.00", symbols);
        format.setParseBigDecimal(true);
        format.setMaximumFractionDigits(2);

        try {
            ParsePosition position = new ParsePosition(0);
            BigDecimal bigDecimal = (BigDecimal) format.parse(value, position);

            // If the parse position isn't at the end of the string, something went wrong
            if(position.getIndex() != value.length()) throw new ParseException("Failed to parse " + value, position.getIndex());

            return bigDecimal.multiply(new BigDecimal(100)).intValueExact();
        }
        catch(ArithmeticException | ParseException e) {
            throw new NumberFormatException(e.getMessage());
        }
    }
}
