package pl.put.poznan.whereismymoney.gui.utils;

import javafx.scene.control.Dialog;
import pl.put.poznan.whereismymoney.gui.utils.builder.DialogBuilder;

public class DialogFactory {
    public static Dialog get(DialogBuilder dialogBuilder) {
        return dialogBuilder.construct();
    }
}
