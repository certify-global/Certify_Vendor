package com.netronix.tools;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageUtility {
    private static final String TAG = ImageUtility.class.getSimpleName();

    public static Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void bitmapSave (Bitmap.CompressFormat format, Bitmap bm, String path) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int quality = 90;  // 100 == no compress !
        bm.compress(format, quality, baos);
        while (baos.size() / 1024>100 ) { // bigger than 100K bytes.
            Log.i(TAG, "bitmapSave - quality="+quality+" size="+baos.size()+">100K - start image compress ...");
            quality -= 5;
            baos.reset();
            bm.compress(format, quality, baos);
        }
        Log.i(TAG, "bitmapSave - quality="+quality+" size="+baos.size());

        File fout = new File (path);
        if (fout.exists()) {
            fout.delete();
        }
        FileOutputStream out;
        try {
            out = new FileOutputStream(fout);
            baos.writeTo(out);
            out.flush();
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public interface RequestInputStream {
        InputStream getInputStream ();
    }

    public static Bitmap imageToBitmapRgb565 (RequestInputStream ris, int desW, int desH) {
        if (ris==null) {
            return null;
        }

        InputStream is;
        Bitmap src = null;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;

        is = ris.getInputStream();
        if (is==null) {
            return null;
        }
        BitmapFactory.decodeStream(is, null, opts);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orgSz = (opts.outWidth<opts.outHeight)?opts.outHeight:opts.outWidth;
        int desSz = (desW<desH)?desH:desW;
        Log.i(TAG, "OrgW="+opts.outWidth+" OrgH="+opts.outHeight+" orgSz="+orgSz+" fixSz="+desSz);

        BitmapFactory.Options opt = new BitmapFactory.Options();

        if (desSz<=orgSz) {
            opt.inSampleSize = orgSz / desSz;
        }
        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        is = ris.getInputStream();
        src = BitmapFactory.decodeStream(is, null, opt);
        try {
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG,"src w="+src.getWidth()+" h="+src.getHeight()+" "+src.getConfig()+ " sampleSize="+opt.inSampleSize);
        return src;
    }

    public static int bitmapToJpg_cutCenter (Bitmap bmSrc, String desPath, int desW, int desH, int bg_color) {

        // create destination bitmap.
        Bitmap bmDes = Bitmap.createBitmap(desW, desH, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmDes);

        // Draw background color.
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bg_color);
        c.drawPaint(paint);

        int cpW;
        int cpH;

        Rect rectSrc = new Rect();
        Rect rectDes = new Rect();


        if (bmDes.getWidth()<bmSrc.getWidth()) {
            cpW = bmDes.getWidth();

            // check  Rect.right / Rect.bottom are include or exclude in the rectangle.
            rectDes.left=0;
            rectDes.right=cpW;

            rectSrc.left = (bmSrc.getWidth()-cpW)/2;
            rectSrc.right = rectSrc.left+cpW;

        }
        else {
            cpW = bmSrc.getWidth();

            rectSrc.left=0;
            rectSrc.right=cpW;

            rectDes.left = (bmDes.getWidth()-cpW)/2;
            rectDes.right = rectDes.left+cpW;
        }

        if (bmDes.getHeight()<bmSrc.getHeight()) {
            cpH = bmDes.getHeight();

            // check  Rect.right / Rect.bottom are include or exclude in the rectangle.
            rectDes.top = 0;
            rectDes.bottom = cpH;

            rectSrc.top = (bmSrc.getHeight()-cpH)/2;
            rectSrc.bottom = rectSrc.top+cpH;
        }
        else {
            cpH = bmSrc.getHeight();

            rectSrc.top = 0;
            rectSrc.bottom = cpH;

            rectDes.top = (bmDes.getHeight()-cpH)/2;
            rectDes.bottom = rectDes.top+cpH;
        }

        c.drawBitmap(bmSrc, rectSrc, rectDes, paint);

        bitmapSave (Bitmap.CompressFormat.JPEG, bmDes, desPath);

        //bmSrc.recycle();
        bmDes.recycle();
        return 0;
    }

    public static Bitmap bitmap_scale (Bitmap bmSrc, int desW, int desH, boolean enableSetBgColor, int bg_color) {
        int srcW = bmSrc.getWidth();
        int srcH = bmSrc.getHeight();

        Bitmap bmScale = Bitmap.createScaledBitmap(bmSrc, desW, desH, false);
        if (false==enableSetBgColor) {
            return bmScale;
        }

        // create destination bitmap.
        Bitmap bmDes = Bitmap.createBitmap(desW, desH, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmDes);

        // Draw background color.
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bg_color);
        c.drawPaint(paint);
        Rect area = new Rect();
        area.left=0;
        area.right=desW;
        area.top=0;
        area.bottom=desH;

        c.drawBitmap(bmScale, area, area, paint);
        bmScale.recycle();
        return bmDes;
    }

    public static int bitmapToJpg_scale (Bitmap bmSrc, String desPath, int desW, int desH, boolean enableSetBgColor, int bg_color) {
        Bitmap bm = bitmap_scale (bmSrc, desW, desH, enableSetBgColor, bg_color);
        if (null==bm) {
            return -1;
        }

        bitmapSave (Bitmap.CompressFormat.JPEG, bm, desPath);
        bm.recycle();
        return 0;
    }

    public static Bitmap bitmap_fit (Boolean isFitWidth, Bitmap bmSrc, int desW, int desH, int bg_color) {
        int srcW = bmSrc.getWidth();
        int srcH = bmSrc.getHeight();

        float sw = ((float)desW)/srcW;
        float sh = ((float)desH)/srcH;
        //float s = (sh<sw)?sw:sh;

        float s;
        if (isFitWidth) {
            s = sw;
        }
        else { // Fit height.
            s = sh;
        }

        float adjW = srcW*s;
        float adjH = srcH*s;

        Log.i(TAG, "sw="+sw+" sh="+sh+" scale="+s+" adjW="+adjW+" adjH="+adjH);

        // create source scale bitmap.
        Bitmap temp = Bitmap.createScaledBitmap(bmSrc, (int)adjW, (int)adjH, false);
        bmSrc = temp;

        // create destination bitmap.
        Bitmap bmDes = Bitmap.createBitmap(desW, desH, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmDes);

        // Draw background color.
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bg_color);
        c.drawPaint(paint);

        int cpW;
        int cpH;

        Rect rectSrc = new Rect();
        Rect rectDes = new Rect();

        if (bmDes.getWidth()<bmSrc.getWidth()) {
            cpW = bmDes.getWidth();

            // check  Rect.right / Rect.bottom are include or exclude in the rectangle.
            rectDes.left=0;
            rectDes.right=cpW;

            rectSrc.left = (bmSrc.getWidth()-cpW)/2;
            rectSrc.right = rectSrc.left+cpW;

        }
        else {
            cpW = bmSrc.getWidth();

            rectSrc.left=0;
            rectSrc.right=cpW;

            rectDes.left = (bmDes.getWidth()-cpW)/2;
            rectDes.right = rectDes.left+cpW;
        }

        if (bmDes.getHeight()<bmSrc.getHeight()) {
            cpH = bmDes.getHeight();

            // check  Rect.right / Rect.bottom are include or exclude in the rectangle.
            rectDes.top = 0;
            rectDes.bottom = cpH;

            rectSrc.top = (bmSrc.getHeight()-cpH)/2;
            rectSrc.bottom = rectSrc.top+cpH;
        }
        else {
            cpH = bmSrc.getHeight();

            rectSrc.top = 0;
            rectSrc.bottom = cpH;

            rectDes.top = (bmDes.getHeight()-cpH)/2;
            rectDes.bottom = rectDes.top+cpH;
        }

        c.drawBitmap(bmSrc, rectSrc, rectDes, paint);
        bmSrc.recycle();
        return bmDes;

        //bitmapSave (Bitmap.CompressFormat.JPEG, bmDes, desPath);
        //bmDes.recycle();

        //return 0;
    }

    public static int bitmapToJpg_fit (Boolean isFitWidth, Bitmap bmSrc, String desPath, int desW, int desH, int bg_color) {

        Bitmap bm = bitmap_fit (isFitWidth, bmSrc, desW, desH, bg_color);
        if (null==bm) {
            return -1;
        }

        bitmapSave (Bitmap.CompressFormat.JPEG, bm, desPath);
        bm.recycle();
        return 0;
    }



    public static Bitmap bitmap_cut (Bitmap bmSrc, int desW, int desH, boolean enableBgColor, int bg_color) {
        int srcW = bmSrc.getWidth();
        int srcH = bmSrc.getHeight();

        float sw = ((float)desW)/srcW;
        float sh = ((float)desH)/srcH;
        float s = (sh<sw)?sw:sh;

        float adjW = srcW*s;
        float adjH = srcH*s;

        Log.i(TAG, "sw="+sw+" sh="+sh+" scale="+s+" adjW="+adjW+" adjH="+adjH);

        Bitmap temp = Bitmap.createScaledBitmap(bmSrc, (int)adjW, (int)adjH, false);
        bmSrc = temp;

        int srcX = (int)((adjW-desW)/2);
        int srcY = (int)((adjH-desH)/2);

        Log.i(TAG, "area x="+srcX+" y="+srcY+" w="+desW+" h="+desH);
        Bitmap bmCut = Bitmap.createBitmap (bmSrc, srcX, srcY, desW, desH);

        if (false==enableBgColor) {
            //bitmapSave (Bitmap.CompressFormat.JPEG, bmCut, desPath);
            return bmCut;
        }

        // create destination bitmap.
        Bitmap bmDes = Bitmap.createBitmap(desW, desH, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmDes);

        // Draw background color.
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.LINEAR_TEXT_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(bg_color);
        c.drawPaint(paint);

        Rect area = new Rect();
        area.left=0;
        area.right=desW;
        area.top=0;
        area.bottom=desH;

        c.drawBitmap(bmCut, area, area, paint);
        bmCut.recycle();
        bmSrc.recycle();
        return bmDes;
    }

    public static int bitmapToJpg_cut (Bitmap bmSrc, String desPath, int desW, int desH, boolean enableBgColor, int bg_color) {
        //public static Bitmap bitmap_cut (Bitmap bmSrc, int desW, int desH, boolean enableBgColor, int bg_color)
        Bitmap bm = bitmap_cut (bmSrc, desW, desH, enableBgColor, bg_color);
        if (null==bm) {
            return -1;
        }
        bitmapSave (Bitmap.CompressFormat.JPEG, bm, desPath);
        bm.recycle();
        return 0;
    }

    public static Bitmap bitmapInvert (Bitmap src)
    {
        int height = src.getHeight();
        int width = src.getWidth();

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();

        ColorMatrix matrixGrayscale = new ColorMatrix();
        matrixGrayscale.setSaturation(0);

        ColorMatrix matrixInvert = new ColorMatrix();
        matrixInvert.set(new float[]
                {
                        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                });
        matrixInvert.preConcat(matrixGrayscale);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
        paint.setColorFilter(filter);

        canvas.drawBitmap(src, 0, 0, paint);
        return bitmap;
    }

}
