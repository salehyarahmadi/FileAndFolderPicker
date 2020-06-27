package ir.androidexception.filepicker.interfaces;

import java.io.File;

public interface OnConfirmDialogListener {
    void onConfirmed(File... files);
}
