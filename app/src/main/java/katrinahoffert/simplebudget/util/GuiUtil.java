package katrinahoffert.simplebudget.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

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

    /**
     * Displays an alert that has a text input. You are given its value when the user presses
     * the positive button.
     * @param context The application context.
     * @param title The alert title.
     * @param positiveLabel The label of the positive button.
     * @param negativeLabel The label of the negative button.
     * @param positiveClickListener The action to perform when the positive button is pressed (will
     *                              be passed whatever the user entered with whitespace trimmed).
     * @param defaultValue A default value to set the text to. If null, no default.
     */
    public static void generateTextInputAlert(Context context, String title, String positiveLabel, String negativeLabel, final Functional.Action1<String> positiveClickListener, String defaultValue) {
        final EditText input = new EditText(context);
        if(defaultValue != null) input.setText(defaultValue);
        input.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // Just the text edit is too squished -- add some padding
        final LinearLayout layout = new LinearLayout(context);
        int padding = context.getResources().getDimensionPixelOffset(R.dimen.textAlertDialogHorizontalPadding);
        layout.setPadding(padding, 0, padding, 0);
        layout.addView(input);

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setView(layout)
                .setPositiveButton(positiveLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        positiveClickListener.action(input.getText().toString().trim());
                    }
                })
                .setNegativeButton(negativeLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .show();
    }

    /**
     * Displays a confirmation prompt, firing an event handler if the user selects the positive
     * option.
     * @param context The application context.
     * @param title The alert title.
     * @param message The message in the alert.
     * @param positiveLabel The label of the positive button.
     * @param negativeLabel The label of the negative button.
     * @param positiveClickListener The action to perform when the positive button is pressed.
     */
    public static void generateConfirmationPrompt(Context context, String title, String message, String positiveLabel, String negativeLabel, final Functional.Action positiveClickListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(positiveLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        positiveClickListener.action();
                    }
                })
                .setNegativeButton(negativeLabel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                })
                .show();
    }
}
