package com.netronix.tools;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;

/**
 * This class is designed to print text on the {@link Canvas} object.
 */
public class PrintText {
    private static final String TAG = PrintText.class.getSimpleName();
    TextPaint mTP;
    Typeface mTypeface;
    Paint.Style mPaintStlye;
    int mTextSize;
    int mTextColor;

    public enum Flag {
        anti_alias(Paint.ANTI_ALIAS_FLAG),
        filter_bitmap(Paint.FILTER_BITMAP_FLAG),
        dither(Paint.DITHER_FLAG),
        underline_text(Paint.UNDERLINE_TEXT_FLAG),
        strike_thru_text(Paint.STRIKE_THRU_TEXT_FLAG),
        fake_bold_text(Paint.FAKE_BOLD_TEXT_FLAG),
        linear_text(Paint.LINEAR_TEXT_FLAG),
        subpixel_text(Paint.SUBPIXEL_TEXT_FLAG),
        embedded_bitmap_text(Paint.EMBEDDED_BITMAP_TEXT_FLAG),
        ;

        private int id;
        Flag(int id) { this.id=id; }
        public int getId () { return id; }
    }

    public enum AlignFlag {
        Center_horizontal(0x01),
        Right(0x02),
        Left(0x04),
        Center_vertical(0x10),
        Bottom(0x20),
        Top(0x40),
        ;

        int id;
        AlignFlag (int id) { this.id=id; }
        int getId() { return id; }
    }

    PrintText(int textColor, int textSize) {
        this(Flag.anti_alias.getId() | Flag.linear_text.getId(),
                Typeface.DEFAULT,
                Paint.Style.FILL,
                textColor,
                textSize);
    }

    PrintText(int paintFlags, Typeface typeface, Paint.Style style, int textColor, int textSize) {
        mTP = new TextPaint(paintFlags);
        mPaintStlye=style;
        mTextColor = textColor;
        mTextSize = textSize;

        if (mTypeface!=null) {
            mTP.setTypeface(mTypeface);
        }
        mTP.setColor(mTextColor);
        mTP.setTextSize(mTextSize);
        if (mPaintStlye!=null) {
            mTP.setStyle(mPaintStlye);
        }
    }

    public static boolean drawOneLine (TextPaint tp, String str, Canvas c,
                                       int x, int y, int availableWidth, int availableHeight,
                                       AlignFlag alignH, AlignFlag alignV)
    {

        Rect bounds = new Rect();
        tp.getTextBounds(str, 0, str.length(), bounds);

        Paint.FontMetricsInt fontMatrics = tp.getFontMetricsInt();
        //int fontH = fontMatrics.descent-fontMatrics.ascent;
        int fontH = fontMatrics.bottom-fontMatrics.top+fontMatrics.leading;
        float baselineH = Math.abs(-fontMatrics.top);

        float minW = bounds.right-bounds.left;
        float minH = bounds.bottom-bounds.top;

        Log.i(TAG, "drawText bounds(left,top,right,bottom) - "+bounds.toString()+" minW="+minW+" minH="+minH);
        if (availableHeight<minH) {
            Log.e(TAG, "drawText - availableHeight is too small.");
            return false;
        }

        float adjX;
        if (AlignFlag.Right==alignH) {
            adjX = x+availableWidth-1; // adjX is the last right pixel.
            tp.setTextAlign(Paint.Align.RIGHT);
        }
        else if (AlignFlag.Center_horizontal==alignH) {
            adjX = x + availableWidth/2;
            tp.setTextAlign(Paint.Align.CENTER);
        }
        else { // default : AlignFlag.Left==alignH
            adjX = x; // adjX is the first left pixel.
            tp.setTextAlign(Paint.Align.LEFT);
        }

        float remainH = availableHeight-fontH;
        float adjY; // baseline position.
        if (AlignFlag.Bottom==alignV) {
            adjY = y+remainH+baselineH;
        }
        else if (AlignFlag.Center_vertical==alignV) {
            adjY = y+(remainH/2)+baselineH;
        }
        else {
            // default : AlignFlag.Top==alignV
            adjY = y+baselineH;
        }

        Log.i(TAG, "["+str+"]draw x="+adjX+" y="+adjY);
        c.drawText(str, adjX, adjY, tp);
        return true;

    }

    static boolean drawLines (TextPaint tp, String text, Canvas c,
                              int x, int y, int availableWidth, int availableHeight,
                              AlignFlag alignH, AlignFlag alignV)
    {

        float adjX;
        if (AlignFlag.Right==alignH) {
            adjX = x+availableWidth-1;
            tp.setTextAlign(Paint.Align.RIGHT);
        }
        else if (AlignFlag.Center_horizontal==alignH) {
            adjX = x+(availableWidth/2);
            tp.setTextAlign(Paint.Align.CENTER);
        }
        else { // AlignFlag.Left==AlignFlag
            adjX = x;
            tp.setTextAlign(Paint.Align.LEFT);
        }

        Layout.Alignment stl_align = Layout.Alignment.ALIGN_NORMAL;
        float spacingMultiplier = 1;
        float spacingAddition = 0;
        boolean includePadding = false;

        StaticLayout stl = new StaticLayout(text, tp, availableWidth, stl_align, spacingMultiplier, spacingAddition, includePadding);
        float stl_H = stl.getHeight();
        int lineCnt = stl.getLineCount();
        Log.i(TAG, "drawLines - lineCnt:"+lineCnt+" stl_H:"+stl_H);
        float line_H = stl_H/lineCnt;

        if (availableHeight<stl_H) {

            if (availableHeight<line_H) {
                Log.e(TAG, "drawText - available height less than line height ! availableH="+availableHeight+" lineH="+line_H+" stl_H="+stl_H);
                return false;
            }

            int avaliable_lineCnt = (int)(availableHeight/line_H);

            int offset = stl.getLineStart(avaliable_lineCnt);
            String str = text.substring(0, offset);

            Log.e(TAG, "drawText - available height not enough, only draw "+avaliable_lineCnt+" lines ! availableH="+availableHeight+" lineH="+line_H+" stl_H="+stl_H);
            return drawText(tp, str, c, x, y, availableWidth, availableHeight, alignH, alignV, true);
        }

        float adjY;
        float remainH = availableHeight-stl_H;
        if (AlignFlag.Bottom==alignV) {
            adjY = (y+remainH);
        }
        else if (AlignFlag.Center_vertical==alignV) {
            adjY = y+(remainH/2);
        }
        else {
            // AlignFlag.Top==alignV
            adjY = y;
        }
        c.translate(adjX, adjY);
        stl.draw(c);
        c.translate(-adjX, -adjY);

        return true;
    }

    boolean drawText (String text, Canvas c,
                      int x, int y, int availableWidth, int availableHeight,
                      AlignFlag alignH, AlignFlag alignV, boolean autoNewline)
    {
        return drawText(mTP, text, c, x, y, availableWidth, availableHeight, alignH, alignV, autoNewline);
    }

    public static boolean drawText (TextPaint tp, String text, Canvas c,
                                    int x, int y, int availableWidth, int availableHeight,
                                    AlignFlag alignH, AlignFlag alignV, boolean autoNewline)
    {

        int textSz=text.length();
        float[] measureWidthA = new float[text.length()];

        int breakSz = tp.breakText(text, true, availableWidth, measureWidthA);
        if (breakSz==0) {
            Log.e(TAG, "drawText - availableWidth is too small, not able to draw even one letter.");
            return false;
        }

        if (breakSz==textSz) {
            autoNewline = false; // text is able to print in one line.
        }
        else { // we need at least 2 line to print all text.

            if (autoNewline==false) { // draw only one line.
                ;
            }
            else {
                // using StaticLayout to print lines.
            }
        }

        if (false==autoNewline) {
            String str = text.substring(0, breakSz);
            return drawOneLine(tp, str, c, x, y, availableWidth, availableHeight, alignH, alignV);
        }

        // Draw more than one lines using "StaticLayout".
        return drawLines(tp, text, c, x, y, availableWidth, availableHeight, alignH, alignV);
    }
}
