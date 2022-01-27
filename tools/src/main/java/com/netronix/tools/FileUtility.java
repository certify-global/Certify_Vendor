package com.netronix.tools;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class FileUtility {
    private static final String TAG = FileUtility.class.getSimpleName();

    enum AssetCopyMode {
        replaceIfSizeNotMatch,
        skipWhileFileExist,
        ;
    }

    static boolean copyAssetFile (Context context, File des, String assetUrl, AssetCopyMode mode) {
        boolean isCopied = false;
        long szCur = 0;
        long szAsset = 0;

        if (des.exists()) {
            if (mode== AssetCopyMode.skipWhileFileExist) {
                return isCopied;
            }
            szCur = des.length();
        }

        try {
            InputStream in = context.getAssets().open(assetUrl);
            szAsset = in.available();

            boolean doCopy = true;
            if (mode== AssetCopyMode.replaceIfSizeNotMatch) {
                if (szCur==szAsset) { // copy asset file to des.
                    doCopy = false;
                } // else { size not match ! }
            }

            if (doCopy) { // copy asset file to des.

                String des_parent_path = des.getParent();
                File des_parent = new File(des_parent_path);
                des_parent.mkdirs();
                des.createNewFile();
                OutputStream out = new FileOutputStream(des.getAbsolutePath());

                byte[] buffer = new byte[1024];
                int read;
                while((read = in.read(buffer)) != -1){
                    out.write(buffer, 0, read);
                }

                out.flush();
                out.close();
                isCopied = true;
            }

            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return isCopied;
    }

    public static byte[] readFile (String filePath) {
        String ret;
        File file = new File(filePath);
        return readFile(file);
    }

    public static byte[] readFile (File file) {
        if (null==file || false==file.isFile()) {
            Log.e(TAG, "readFile - error paramenter.");
            return null;
        }

        int size = (int) file.length();
        if (size<=0) {
            Log.e(TAG, "readFile("+file.getPath()+") empty file !");
            return null;
        }
        byte[] bytes = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static class DownloadFolderFile extends FolderFile {
        public DownloadFolderFile(String filename) {
            super(filename);
        }
        public DownloadFolderFile(String childFolder, String filename) { super(childFolder, filename); }

        @Override
        public String getDefaultFolder(Context context) {
            return getDefaultDownloadFolder();
        }
    }

    public static class AppFolderFile extends FolderFile {
        public AppFolderFile (String filename) {
            super(filename);
        }
        public AppFolderFile (String childFolder, String filename) {
            super (childFolder, filename);
            Log.i(TAG, "test process AppFolderFile");
        }

        @Override
        public String getDefaultFolder(Context context) {
            return getDefaultAppFolder(context);
        }
    }

    public static String getDefaultDownloadFolder () {
        File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);;
        return path.getPath();
    }

    public static String getDefaultAppFolder(Context context) {
        if (null==context) {
            Log.e(TAG, "getDefaultAppFolder() - error parameter context==null !");
            return null;
        }
        File path = context.getExternalFilesDir(null);
        return path.getPath();
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    public static File[] sortFilesByLastModifiedDate (File parent_directory) {
        File[] files = parent_directory.listFiles();
        File[] sortFiles = new File[files.length];

        String TEMPLATE_J1 = "json1.json";
        String TEMPLATE_J2 = "json2.json";
        String TEMPLATE_J3 = "json3.json";
        String TEMPLATE_J4 = "json4.json";

        File f1 = null;
        File f2 = null;
        File f3 = null;
        File f4 = null;

        for (int i=0; i<files.length; i++){
            sortFiles[i] = files[i];
        }
        return sortFiles;
    }

    public static ArrayList<File> filesToArrayList (File[] fs) {
        if (null==fs) {
            return new ArrayList<>();
        }
        ArrayList<File> ary = new ArrayList<>();
        for (File f : fs) {
            ary.add(f);
        }
        return ary;
    }

    public static ArrayList<String> fileArrayGetNames (ArrayList<File> fs) {
        ArrayList<String> names = new ArrayList<>();
        for (File f : fs) {
            names.add(f.getName());
        }
        return names;
    }

    public static ArrayList<String> fileArrayGetBaseNames (ArrayList<File> fs) {
        ArrayList<String> names = new ArrayList<>();
        for (File f : fs) {
            names.add(getBaseName(f.getName()));
        }
        return names;
    }

    public enum FileOrder {
        Normal,
        SortByDate,
        ;
    }

    public static File[] dirGetFiles (File fdir, FileOrder order) {
        if (null==fdir || false==fdir.isDirectory()) {
            return null;
        }
        order = (null==order)? FileOrder.Normal:order;

        File[] ret;
        if (FileOrder.SortByDate==order) {
            ret = sortFilesByLastModifiedDate(fdir);;
        }
        else {
            ret = fdir.listFiles();
        }
        return ret;
    }

    public static String getBaseName (String filename) {
        //String filename = "test_template.json";
        int idx_ext = filename.lastIndexOf(".");
        String basename = filename.substring(0, idx_ext);
        //System.out.println(filename + " baseName="+basename);
        return basename;
    }

}
