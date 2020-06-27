package ir.androidexception.filepicker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import ir.androidexception.filepicker.R;
import ir.androidexception.filepicker.databinding.ItemFileBinding;
import ir.androidexception.filepicker.interfaces.OnPathChangeListener;
import ir.androidexception.filepicker.interfaces.OnSelectItemListener;
import ir.androidexception.filepicker.model.Item;
import ir.androidexception.filepicker.utility.GlideApp;
import ir.androidexception.filepicker.utility.Util;


public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {
    private Context context;
    private List<Item> items;
    private LayoutInflater inflater;
    private OnPathChangeListener onPathChangeListener;
    private OnSelectItemListener onSelectItemListener;
    private String currentPath = "";
    private boolean multiFileSelect = false;
    private boolean directorySelect = false;
    private List<File> files;

    public FileAdapter(Context context, List<Item> items, OnPathChangeListener onPathChangeListener, OnSelectItemListener onSelectItemListener) {
        this.context = context;
        this.items = items;
        this.onPathChangeListener = onPathChangeListener;
        this.onSelectItemListener = onSelectItemListener;
        files = new ArrayList<>();
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(inflater==null)
            inflater = LayoutInflater.from(parent.getContext());
        return new FileViewHolder(DataBindingUtil.inflate(inflater, R.layout.item_file,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, int position) {
        holder.bind(context, items.get(position));
    }

    @Override
    public int getItemCount() {
        if(items!=null)
            return items.size();
        else
            return 0;
    }

    public void setMultiFileSelect(boolean multiFileSelect) {
        this.multiFileSelect = multiFileSelect;
    }

    public void setDirectorySelect(boolean directorySelect) {
        this.directorySelect = directorySelect;
    }



    public void select(int position){
        if(this.multiFileSelect){
            if(items.get(position).isSelected()) {
                items.get(position).setSelected(false);
                for(int i=0; i<files.size(); i++)
                    if(files.get(i).getPath().equals(items.get(position).getFile().getPath()))
                        files.remove(i);
            }
            else {
                items.get(position).setSelected(true);
                files.add(items.get(position).getFile());
            }
            notifyItemChanged(position);
        }
        else{
            for(int i=0; i<items.size(); i++){
                if(items.get(i).isSelected()) {
                    items.get(i).setSelected(false);
                    notifyItemChanged(i);
                }
            }
            items.get(position).setSelected(true);
            notifyItemChanged(position);
        }
    }

    public void back(){
        File currentPathFile = new File(currentPath);
        File parent = currentPathFile.getParentFile();
        if(parent!=null) {
            File[] files = parent.listFiles();
            List<Item> itemList = new ArrayList<>();
            if (files != null) {
                if(directorySelect){
                    for(File f : files){
                        if(f.isDirectory())
                            itemList.add(new Item(f));
                    }
                }
                else{
                    for(File f : files){
                        itemList.add(new Item(f));
                    }
                }

                items = itemList;
                notifyDataSetChanged();
                currentPath = parent.getPath();
                onPathChangeListener.onChanged(currentPath);
                if(!this.multiFileSelect  && onSelectItemListener!=null)
                    onSelectItemListener.onSelected(null);
            }
        }
    }

    class FileViewHolder extends RecyclerView.ViewHolder{
        private ItemFileBinding itemFileBinding;
        public FileViewHolder(ItemFileBinding itemFileBinding) {
            super(itemFileBinding.getRoot());
            this.itemFileBinding = itemFileBinding;
        }

        public void bind(Context context, Item item){
            for(File f : files){
                if(f.getPath().equals(item.getFile().getPath())) item.setSelected(true);
            }

            switch (Util.getFileCategory(item.getFile())){
                case Util.FOLDER_CATEGORY:
                    GlideApp.with(context)
                            .applyDefaultRequestOptions(new RequestOptions()
                                .dontAnimate()
                                .centerCrop()
                                .override(200,200)
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                            )
                            .load(R.drawable.ic_folder)
                            .into(itemFileBinding.ivItemFile);
                    itemFileBinding.ivItemFileFileType.setVisibility(View.GONE);
                    break;
                case Util.VIDEO_CATEGORY:
                    Bitmap videoPreview = Util.fetchVideoPreview(item.getFile());
                    GlideApp.with(context).load(videoPreview).into(itemFileBinding.ivItemFile);
                    itemFileBinding.ivItemFileFileType.setVisibility(View.VISIBLE);
                    GlideApp.with(context).load(R.drawable.ic_play_video).into(itemFileBinding.ivItemFileFileType);
                    break;
                case Util.MUSIC_CATEGORY:
                    Bitmap coverImage = Util.fetchMusicCover(item.getFile());
                    if(coverImage!=null){
                        GlideApp.with(context).load(coverImage).into(itemFileBinding.ivItemFile);
                        itemFileBinding.ivItemFileFileType.setVisibility(View.VISIBLE);
                        GlideApp.with(context).load(R.drawable.ic_play_music).into(itemFileBinding.ivItemFileFileType);
                    }
                    else {
                        GlideApp.with(context).load(R.drawable.ic_music).into(itemFileBinding.ivItemFile);
                        itemFileBinding.ivItemFileFileType.setVisibility(View.GONE);
                    }
                    break;
                case Util.IMAGE_CATEGORY:
                    GlideApp.with(context).load(item.getFile()).into(itemFileBinding.ivItemFile);
                    itemFileBinding.ivItemFileFileType.setVisibility(View.GONE);
                    break;
                case Util.DOCUMENT_CATEGORY:
                    if(item.getFile().getName().endsWith(".pdf")){
                        GlideApp.with(context).load(R.drawable.ic_pdf).into(itemFileBinding.ivItemFile);
                    }
                    else if(item.getFile().getName().endsWith(".txt")){
                        GlideApp.with(context).load(R.drawable.ic_txt).into(itemFileBinding.ivItemFile);
                    }
                    else if(item.getFile().getName().endsWith(".ppt") || item.getFile().getName().endsWith(".pptx") ){
                        GlideApp.with(context).load(R.drawable.ic_ppt).into(itemFileBinding.ivItemFile);
                    }
                    else if(item.getFile().getName().endsWith(".xls") || item.getFile().getName().endsWith(".xlsx") ){
                        GlideApp.with(context).load(R.drawable.ic_xls).into(itemFileBinding.ivItemFile);
                    }
                    else if(item.getFile().getName().endsWith(".doc") || item.getFile().getName().endsWith(".docx") ){
                        GlideApp.with(context).load(R.drawable.ic_doc).into(itemFileBinding.ivItemFile);
                    }
                    else{
                        GlideApp.with(context).load(R.drawable.ic_unknown_format).into(itemFileBinding.ivItemFile);
                    }
                    itemFileBinding.ivItemFileFileType.setVisibility(View.GONE);
                    break;
                default:
                    GlideApp.with(context).load(R.drawable.ic_unknown_format).into(itemFileBinding.ivItemFile);
                    itemFileBinding.ivItemFileFileType.setVisibility(View.GONE);
            }
            itemFileBinding.setItem(item);
            itemFileBinding.getRoot().setOnClickListener( view -> {
                File file = item.getFile();
                if(file.isDirectory()){
                    // Click On Directory
                    List<Item> newItems = new ArrayList<>();
                    File[] children = file.listFiles();
                    if(children!=null){
                        if(directorySelect){
                            for (File f : children) {
                                if(f.isDirectory())
                                    newItems.add(new Item(f));
                            }
                        }
                        else{
                            for (File f : children) {
                                newItems.add(new Item(f));
                            }
                        }
                        items = newItems;
                        notifyDataSetChanged();
                    }


                    currentPath = file.getPath();
                    onPathChangeListener.onChanged(currentPath);
                    if(!multiFileSelect && onSelectItemListener!=null)
                        onSelectItemListener.onSelected(null);

                }
                else{
                    // Click On File
                    select(getAdapterPosition());
                    onSelectItemListener.onSelected(file);
                }
            });
//            setColors();
            itemFileBinding.executePendingBindings();
        }
    }
}
