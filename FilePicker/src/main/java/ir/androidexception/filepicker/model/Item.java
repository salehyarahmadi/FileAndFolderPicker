package ir.androidexception.filepicker.model;

import android.graphics.drawable.Drawable;

import java.io.File;

public class Item {
    private File file;
    private Drawable imageResource;
    private boolean selected;

    public Item(File file, Drawable imageResource, boolean selected) {
        this.file = file;
        this.imageResource = imageResource;
        this.selected = selected;
    }

    public Item(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public Drawable getImageResource() {
        return imageResource;
    }

    public void setImageResource(Drawable imageResource) {
        this.imageResource = imageResource;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
