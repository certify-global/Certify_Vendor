package com.netronix.tools;

import android.graphics.Bitmap;
import android.graphics.Color;

// ref : "https://gist.github.com/naikrovek/643a9799171d20820cb9"
public class FloySteinbergDithering {

    public static class C3 {
        int r, g, b;

        public C3(int c) {
            r = Color.red(c);
            g = Color.green(c);
            b = Color.blue(c);
        }

        public C3(int r, int g, int b) {
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public C3 add(C3 o) {
            return new C3(r + o.r, g + o.g, b + o.b);
        }

        public int clamp(int c) {
            return Math.max(0, Math.min(255, c));
        }

        public int diff(C3 o) {
            int Rdiff = o.r - r;
            int Gdiff = o.g - g;
            int Bdiff = o.b - b;
            int distanceSquared = Rdiff * Rdiff + Gdiff * Gdiff + Bdiff * Bdiff;
            return distanceSquared;
        }

        public C3 mul(double d) {
            return new C3((int) (d * r), (int) (d * g), (int) (d * b));
        }

        public C3 sub(C3 o) {
            return new C3(r - o.r, g - o.g, b - o.b);
        }

        public int getColorValue () {
            return Color.rgb(r,g,b);
        }

        public boolean isSameColor (C3 cmp) {
            if (r==cmp.r && g==cmp.g && b==cmp.b) {
                return true;
            }
            return false;
        }

        //public Color toColor() {
        //    //return new Color(clamp(r), clamp(g), clamp(b));
        //    //Color valueOf(float r, float g, float b)
        //}

        //public int toRGB() {
        //    return toColor().getRGB();
        //}
    }

    private static C3 findClosestPaletteColor(C3 c, C3[] palette) {
        C3 closest = palette[0];

        for (C3 n : palette) {
            if (n.diff(c) < closest.diff(c)) {
                closest = n;
            }
        }

        return closest;
    }

    //public static Bitmap floydSteinbergDithering(Bitmap src, C3[] palette)
    public static Bitmap Dithering(Bitmap src, C3[] palette)
    {

        /*
        C3[] palette = new C3[] {
                new C3(  0,   0,   0), // black
                new C3(  0,   0, 255), // green
                new C3(  0, 255,   0), // blue
                new C3(  0, 255, 255), // cyan
                new C3(255,   0,   0), // red
                new C3(255,   0, 255), // purple
                new C3(255, 255,   0), // yellow
                new C3(255, 255, 255)  // white
        };
        */

        int w = src.getWidth();
        int h = src.getHeight();

        C3[][] d = new C3[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                d[y][x] = new C3(src.getPixel(x, y));
            }
        }

        Bitmap out = Bitmap.createBitmap(src.getWidth(), src.getHeight(), Bitmap.Config.ARGB_8888);

        for (int y = 0; y < src.getHeight(); y++) {
            for (int x = 0; x < src.getWidth(); x++) {

                C3 oldColor = d[y][x];
                C3 newColor = findClosestPaletteColor(oldColor, palette);
                //img.setRGB(x, y, newColor.toColor().getRGB());
                out.setPixel(x,y, newColor.getColorValue());

                C3 err = oldColor.sub(newColor);

                if (x + 1 < w) {
                    d[y][x + 1] = d[y][x + 1].add(err.mul(7. / 16));
                }

                if (x - 1 >= 0 && y + 1 < h) {
                    d[y + 1][x - 1] = d[y + 1][x - 1].add(err.mul(3. / 16));
                }

                if (y + 1 < h) {
                    d[y + 1][x] = d[y + 1][x].add(err.mul(5. / 16));
                }

                if (x + 1 < w && y + 1 < h) {
                    d[y + 1][x + 1] = d[y + 1][x + 1].add(err.mul(1. / 16));
                }
            }
        }

        return out;
    }
}
