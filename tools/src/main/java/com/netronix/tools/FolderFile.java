package com.netronix.tools;

import android.content.Context;
import android.util.Log;

import com.netronix.tools.FileUtility.AssetCopyMode;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import static com.netronix.tools.FileUtility.copyAssetFile;

public abstract class FolderFile {
    private static final String TAG = FolderFile.class.getSimpleName();
    private String mFileName;
    private String mChildFolder;
    private Object mUserTag;

    public abstract String getDefaultFolder(Context context); // eg. defaultAppFolder, defaultDownloadFolder.

    public FolderFile(String filename) {
        // eg. filename="test.png". getDefaultFolder()="/mnt/sdcard/Download" => the folderFile should be "/mnt/sdcard/Download/test.png".
        this(null, filename);
    }

    public FolderFile(String childFolder, String filename) {
        // eg. childFolder="imgs", filename="test.png". getDefaultFolder()="/mnt/sdcard/Download" => the folderFile should be "/mnt/sdcard/Download/imgs/test.png".
        Log.i(TAG, "test process FolderFile: ");

        mFileName = filename;
        if (null==childFolder || childFolder.length()==0) {
            childFolder = null;
        }
        mChildFolder = childFolder;
    }

    Object getUserTag () { return mUserTag; }
    void setUserTag (Object tag) { mUserTag=tag; }

    public boolean isFileExist (Context context) {
        if (null==context) {
            Log.e(TAG, "isFileExist - context==null !!");
            return false;
        }
        String path = getPath(context);
        File f = new File(path);
        if ( f.isFile() && f.exists() ) {
            return true;
        }
        return false;
    }

    public String getFolderPath (Context context) {
        String url = getDefaultFolder(context);
        if (url!=null) {
            url += File.separator;

            if (null != mChildFolder) {
                url += mChildFolder;
            }
        }
        return url;
    }

    public String getPath (Context context) {
        String url = getDefaultFolder(context);
        if (url!=null) {
            url+= File.separator;

            if (null!= mChildFolder) {
                url+= mChildFolder;
                url+= File.separator;
            }
            url+=mFileName;
        }
        return url;
    }

    public File getFile (Context context, boolean hasDefaultCopyInAssets) {
        String url = getPath(context);
        File f = new File(url);
        if (hasDefaultCopyInAssets) {
            copyAssetFile (context, f, mFileName, AssetCopyMode.skipWhileFileExist);
        }
        return f;
    }

    public boolean touch (Context context) {
        String url = getPath(context);
        File f = new File(url);
        if (f.exists()) {
            f.setLastModified(System.currentTimeMillis());
        }
        return false;
    }

    public Date lastModifiedDate (Context context) {
        String url = getPath(context);
        File f = new File(url);
        Date lastModified = null;
        if (f.exists()) {
            lastModified = new Date(f.lastModified());
        }
        return lastModified;
    }

    public byte[] readFile (Context context) {
        String path = getPath(context);
        byte[] ret = FileUtility.readFile(path);
        return ret;
    }

    /**
     * Get file name without leading file path.
     * @return file name.
     */
    public String getFilename () {
        return mFileName;
    }

    public String getFileExt () {
        String ext = mFileName.substring(mFileName.lastIndexOf("."));
        return ext;
    }

    public boolean delete (Context context) {
        String url = getPath(context);
        File f = new File(url);
        Log.e(TAG, "delete: url = "+url);
        Log.e(TAG, "delete: f = "+f.getName());
        boolean ret = false;
        if (f.exists() && f.isFile()) {
            ret = f.delete();
        }
        return ret;
    }

    public boolean writeFile (Context context, String data) {
        String url = getPath(context);
        File fout = new File(url);
        boolean isSuccess = false;
        Log.e(TAG, "writeFile: url = "+url);
        Log.e(TAG, "writeFile: fout = "+fout.getName());

        try {
            fout.getParentFile().mkdir();
            fout.createNewFile();
            FileOutputStream fos = new FileOutputStream(fout, false);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            osw.write(data);
            osw.close();
            isSuccess = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public boolean writeFile (Context context, byte[] data) {
        String url = getPath(context);
        File fout = new File(url);
        boolean isSuccess = false;

        try {
            fout.getParentFile().mkdir();
            fout.createNewFile();
            FileOutputStream fos = new FileOutputStream(fout, false);
            fos.write(data);
            fos.close();
            isSuccess = true;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }


}
