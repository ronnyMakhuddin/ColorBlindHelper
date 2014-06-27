package com.beanslab.colorblindhelper.helper.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.format.DateUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.beanslab.colorblindhelper.helper.gobjects.DataSet;
import com.beanslab.colorblindhelper.helper.gobjects.Point;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

/**
 * Created by andrew on 2/01/14.
 */

public class Utils {

    public static void applyCustomFont(ViewGroup list, Typeface customTypeface) {
        for (int i = 0; i < list.getChildCount(); i++) {
            View view = list.getChildAt(i);
            if (view instanceof ViewGroup) {
                applyCustomFont((ViewGroup) view, customTypeface);
            } else if (view instanceof TextView) {
                ((TextView) view).setTypeface(customTypeface);
            }
        }
    }

    public static void applyCustomFont(View view, Typeface customTypeface) {
        if (view instanceof ViewGroup) {
            applyCustomFont((ViewGroup) view, customTypeface);
        } else if (view instanceof TextView) {
            ((TextView) view).setTypeface(customTypeface);
        }

    }

    public static Spannable paintSpanableText(String text, String[] words, int color){

        text = text.replaceAll("\n ","\n");//.replaceAll("_","-");
        Spannable spanRange = new SpannableString(text);

        for( String word:words ){
            int startSpan = 0, endSpan = 0;
            while (true) {
                startSpan = text.indexOf(word, endSpan);
                ForegroundColorSpan foreColour = new ForegroundColorSpan(color);
                // Need a NEW span object every loop, else it just moves the span
                if (startSpan < 0)
                    break;
                endSpan = startSpan + word.length();
                spanRange.setSpan(foreColour, startSpan, endSpan,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spanRange;
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    public static Spannable paintSpanableText(Spannable text, String[] words, int color){

        //SpannableStringBuilder builder = new SpannableStringBuilder();

        //builder.append(text);
        String aux =  text.toString();

        //text = text.replaceAll("\n ","\n");//.replaceAll("_","-");
        Spannable spanRange = new SpannableString(text);


        for( String word:words ){
            int startSpan = 0, endSpan = 0;
            while (true) {
                startSpan = aux.indexOf(word, endSpan);
                ForegroundColorSpan foreColour = new ForegroundColorSpan(color);
                // Need a NEW span object every loop, else it just moves the span
                if (startSpan < 0)
                    break;
                endSpan = startSpan + word.length();
                spanRange.setSpan(foreColour, startSpan, endSpan,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return spanRange;
    }


    public static Spannable paintDrawableOnText(Context context, Spannable text, String word, int drawable){
        SpannableStringBuilder builder = new SpannableStringBuilder();
        builder.append(text);
        String aux =  text.toString();
        int startSpan = 0, endSpan = 0;
        while (true) {
            startSpan = aux.indexOf(word, endSpan);
            if(startSpan < 0)
                break;
            endSpan = startSpan + word.length();
            builder.setSpan(new ImageSpan(context, drawable),
                    startSpan, endSpan,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }


    public static Bitmap decodeSampledBitmapFromPath(String path, int reqWidth, int reqHeight) {

        Bitmap bitmap = null;

        try {
            FileInputStream istr = new FileInputStream(path);
            FileInputStream istr2 = new FileInputStream(path);

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(istr, null, options);


            options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
            options.inJustDecodeBounds = false;
            istr.close();

            bitmap = BitmapFactory.decodeStream(istr2, null, options);
            istr2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap decodeSampledBitmapFromAssets(Context ctx,
                                                       String path, int reqWidth, int reqHeight) {

        Bitmap bitmap = null;

        try {
            AssetManager assetManager = ctx.getAssets();
            InputStream istr = assetManager.open(path);

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(istr, null, options);

            options.inSampleSize = calculateInSampleSize(options, reqWidth,
                    reqHeight);

            options.inJustDecodeBounds = false;
            bitmap = BitmapFactory.decodeStream(istr, null, options);
            istr.close();
            //putBitmapInDiskCache(ctx, imageName, bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
        return bitmap;
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }


    public synchronized static double dist(Point point1, Point point2) {

        double x = Math.abs(point1.x - point2.x);
        double y = Math.abs(point1.y - point2.y);
        double z = Math.abs(point1.z - point2.z);

        return Math.sqrt((x * x) + (y * y) + (z * z));

    }

    public Point rgbFromHex(String triplet) {

        int x = Integer.valueOf(triplet.substring(1, 3), 16);
        int y = Integer.valueOf(triplet.substring(3, 5), 16);
        int z = Integer.valueOf(triplet.substring(5, 7), 16);

        Point point = new Point();
        point.x = x;
        point.y = y;
        point.z = z;
        return point;
    }

    public synchronized static Point classifykNN(Point point, DataSet points) {
        double min = Double.MAX_VALUE; // infinite
        int min_idx = -1;
        double dist;

        for (int i = 0; i < points.colorDataset.size(); ++i) {
            dist = dist(point, points.colorDataset.get(i));
            if (dist < min) {
                min = dist;
                min_idx = i;
            }
        }

        return points.colorDataset.get(min_idx);

    }

    public void learn() {

    }

    public String getClosestColorHex(Point point) {

        String hex = String.format("#%02x%02x%02x", point.x, point.y, point.z);
        return hex;
    }

    public static DataSet getColorsDataSet(Context context, String filename) {
        DataSet response = null;
        try {
            InputStream source = context.getResources().getAssets()
                    .open(filename);
            Gson gson = new Gson();
            Reader reader = new InputStreamReader(source);
            response = gson.fromJson(reader, DataSet.class);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return response;
    }


}
