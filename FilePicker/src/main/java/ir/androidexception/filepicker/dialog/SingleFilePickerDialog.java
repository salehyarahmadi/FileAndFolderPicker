package ir.androidexception.filepicker.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import ir.androidexception.filepicker.R;
import ir.androidexception.filepicker.adapter.FileAdapter;
import ir.androidexception.filepicker.databinding.DialogPickerBinding;
import ir.androidexception.filepicker.interfaces.OnCancelPickerDialogListener;
import ir.androidexception.filepicker.interfaces.OnConfirmDialogListener;
import ir.androidexception.filepicker.interfaces.OnPathChangeListener;
import ir.androidexception.filepicker.interfaces.OnSelectItemListener;
import ir.androidexception.filepicker.model.Item;
import ir.androidexception.filepicker.utility.Util;


public class SingleFilePickerDialog extends Dialog implements OnPathChangeListener, OnSelectItemListener {
    private RecyclerView recyclerViewDirectories;
    private FloatingActionButton fab;
    private ImageView close;
    private ImageView shorting;
    private final Context context;
    private DialogPickerBinding binding;
    private FileAdapter adapter;
    private final OnCancelPickerDialogListener onCancelPickerDialogListener;
    private final OnConfirmDialogListener onConfirmDialogListener;
    private File file;
    private List<String> formats = new ArrayList<>();

    public SingleFilePickerDialog(@NonNull Context context, OnCancelPickerDialogListener onCancelPickerDialogListener,
                                  OnConfirmDialogListener onConfirmDialogListener) {
        super(context);
        this.context = context;
        this.onCancelPickerDialogListener = onCancelPickerDialogListener;
        this.onConfirmDialogListener = onConfirmDialogListener;
    }

    public SingleFilePickerDialog(@NonNull Context context, OnCancelPickerDialogListener onCancelPickerDialogListener,
                                  OnConfirmDialogListener onConfirmDialogListener, List<String> formats) {
        super(context);
        this.context = context;
        this.onCancelPickerDialogListener = onCancelPickerDialogListener;
        this.onConfirmDialogListener = onConfirmDialogListener;
        this.formats = formats;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.dialog_picker, null, false);
        setContentView(binding.getRoot());
        getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        recyclerViewDirectories = binding.rvDialogPickerDirectories;
        fab = binding.fab;
        close = binding.ivClose;
        shorting = binding.ivShorting;

        if (Util.permissionGranted(context)) {
            binding.setPath("Internal Storage" + context.getString(R.string.arrow));
            binding.setBusySpace(Util.bytesToHuman(Util.busyMemory()));
            binding.setTotalSpace(Util.bytesToHuman(Util.totalMemory()));
            int busySpacePercent = (int) (((float) Util.busyMemory() / Util.totalMemory()) * 100);
            binding.setBusySpacePercent(busySpacePercent + "%");
            binding.progressView.setProgress(busySpacePercent);

            setupDirectoriesListRecyclerView();
            setupClickListener();
        }
    }


    private void setupClickListener() {
        close.setOnClickListener(v -> {
            onCancelPickerDialogListener.onCanceled();
            this.cancel();
        });

        fab.setOnClickListener(v -> {
            onConfirmDialogListener.onConfirmed(file);
            this.cancel();
        });

        shorting.setOnClickListener(v -> {
            if (adapter.getSorting()) {
                shorting.setImageResource(R.drawable.ic_ascending);
            } else {
                shorting.setImageResource(R.drawable.ic_descending);
            }
            adapter.sorting();
        });
    }

    private void setupDirectoriesListRecyclerView() {
        List<Item> items = new ArrayList<>();
        File internalStorage = Environment.getExternalStorageDirectory();
        List<File> children = new ArrayList<>(Arrays.asList(Objects.requireNonNull(internalStorage.listFiles())));
        Log.e("File", String.valueOf(children.size()));
        for (File file : children) {
            if (!formats.isEmpty()) {
                if (formats.contains(Util.getFileExtension(file)) || file.isDirectory()) {
                    items.add(new Item(file));
                }
            } else {
                items.add(new Item(file));
            }
        }
        if (formats.isEmpty()) {
            adapter = new FileAdapter(context, items, this, this);
        } else {
            adapter = new FileAdapter(context, items, this, this, formats);
        }
        recyclerViewDirectories.setAdapter(adapter);
        recyclerViewDirectories.setNestedScrollingEnabled(false);
        adapter.sorting();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override
    public void onBackPressed() {
        adapter.back();
    }

    @Override
    public void onChanged(String path) {
        binding.setPath(Util.changePathFormat(context, path));
    }

    @Override
    public void onSelected(File f) {
        file = f;
        if (f == null) fab.setVisibility(View.GONE);
        else fab.setVisibility(View.VISIBLE);
    }
}
