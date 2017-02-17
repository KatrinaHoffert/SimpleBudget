package katrinahoffert.simplebudget.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import katrinahoffert.simplebudget.R;

public class GuiUtil {
    /**
     * Creates and shows a simple alert dialogue with just a message.
     * @param context The application context.
     * @param message The message to display.
     */
    public static void generateSimpleAlert(Context context, String message) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(R.string.generic_okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
