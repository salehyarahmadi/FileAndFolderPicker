package ir.androidexception.filepicker.utility;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.os.StatFs;
import android.provider.MediaStore;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import ir.androidexception.filepicker.R;


public class Util {
    public static final int FOLDER_CATEGORY = 0;
    public static final int VIDEO_CATEGORY = 1;
    private static List<String> videoFormats = Arrays.asList(
            "mp4","webm", "mkv", "flv", "vob", "ogv", "mpg", "mp2", "avi",
            "mov", "wmv", "asf", "amv", "m4p", "m4v", "mpeg", "3gp"
    );
    public static final int MUSIC_CATEGORY = 2;
    private static List<String> musicFormats = Arrays.asList(
            "mp3", "aa", "aac", "aax", "amr", "awb", "m4a", "m4b", "ogg", "oga",
            "raw", "voc", "vox", "wav", "wma", "webm", "wv", "ogg"
    );
    public static final int IMAGE_CATEGORY = 3;
    private static List<String> imageFormats = Arrays.asList(
            "jpg", "jpeg", "png", "gif", "tif", "tiff", "bmp", "eps", "webp", "raw"
    );
    public static final int DOCUMENT_CATEGORY = 4;
    private static List<String> documentFormats = Arrays.asList(
            "pdf", "txt" , "ppt" , "pptx" , "xls" , "xlsx" , "dox" , "docx"
    );
    public static final int OTHER_CATEGORY = 5;



    public static int getFileCategory(File file) {
        if(file.isDirectory())
            return FOLDER_CATEGORY;
        String format = file.getName().substring(file.getName().lastIndexOf(".") + 1);
        if(videoFormats.contains(format))
            return VIDEO_CATEGORY;
        if(musicFormats.contains(format))
            return MUSIC_CATEGORY;
        if(imageFormats.contains(format))
            return IMAGE_CATEGORY;
        if(documentFormats.contains(format))
            return DOCUMENT_CATEGORY;

        return OTHER_CATEGORY;

    }

    public static String changePathFormat(Context context, String path){
        File internalStorage = Environment.getExternalStorageDirectory();
        String internalStoragePath = internalStorage.getPath();
        path = path.replace(internalStoragePath, "Internal Storage");
        return path.replaceAll("/", context.getString(R.string.arrow));
    }

    public static Bitmap fetchMusicCover(File file){
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(file.getPath());
        byte [] data = mmr.getEmbeddedPicture();
        if(data != null) {
            return BitmapFactory.decodeByteArray(data, 0, data.length);
        }
        else {
            return null;
        }
    }

    public static Bitmap fetchVideoPreview(File file){
        return ThumbnailUtils.createVideoThumbnail(file.getPath(), MediaStore.Images.Thumbnails.MINI_KIND);
    }





    public static long totalMemory() {
        StatFs internalStatFs = new StatFs( Environment.getRootDirectory().getAbsolutePath() );
        long internalTotal;

        StatFs externalStatFs = new StatFs( Environment.getExternalStorageDirectory().getAbsolutePath() );
        long externalTotal;

        internalTotal = ( internalStatFs.getBlockCountLong() * internalStatFs.getBlockSizeLong() );
        externalTotal = ( externalStatFs.getBlockCountLong() * externalStatFs.getBlockSizeLong() ) ;

        long total = internalTotal + externalTotal;

        return total;
    }

    public static Long freeMemory(){
        StatFs statFs = new StatFs(Environment.getExternalStorageDirectory().getAbsolutePath());
        long   free   = (statFs.getAvailableBlocksLong() * statFs.getBlockSizeLong());
        return free;
    }

    public static Long busyMemory(){
        StatFs statFs = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        long   total  = totalMemory();
        long   free   = freeMemory();
        long   busy   = total - free;
        return busy;
    }

    public static String floatForm (double d)
    {
        return new DecimalFormat("#.##").format(d);
    }

    public static String bytesToHuman (Long size) {
        long Kb = 1  * 1024;
        long Mb = Kb * 1024;
        long Gb = Mb * 1024;
        long Tb = Gb * 1024;
        long Pb = Tb * 1024;
        long Eb = Pb * 1024;

        if (size <  Kb)                 return floatForm(        size        ) + " Byte";
        if (size >= Kb && size < Mb)    return floatForm((double)size / Kb) + " KB";
        if (size >= Mb && size < Gb)    return floatForm((double)size / Mb) + " MB";
        if (size >= Gb && size < Tb)    return floatForm((double)size / Gb) + " GB";
        if (size >= Tb && size < Pb)    return floatForm((double)size / Tb) + " TB";
        if (size >= Pb && size < Eb)    return floatForm((double)size / Pb) + " PB";
        if (size >= Eb)                 return floatForm((double)size / Eb) + " EB";

        return "???";
    }


    public static boolean permissionGranted(Context context){
        return ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }


    public static void requestPermission(Activity activity){
        ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

}
