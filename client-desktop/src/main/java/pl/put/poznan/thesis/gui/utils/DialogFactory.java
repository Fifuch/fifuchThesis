package pl.put.poznan.thesis.gui.utils;

import javafx.scene.control.Dialog;
import pl.put.poznan.thesis.gui.utils.builder.DialogBuilder;

public class DialogFactory {
    public static Dialog get(DialogBuilder dialogBuilder) {
        return dialogBuilder.construct();
    }
}
