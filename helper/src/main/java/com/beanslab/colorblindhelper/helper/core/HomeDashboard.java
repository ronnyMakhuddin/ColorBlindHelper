package com.beanslab.colorblindhelper.helper.core;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.GradientDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.beanslab.colorblindhelper.helper.R;
import com.beanslab.colorblindhelper.helper.custom.MyImageView;
import com.beanslab.colorblindhelper.helper.gobjects.DataSet;
import com.beanslab.colorblindhelper.helper.gobjects.Point;
import com.beanslab.colorblindhelper.helper.utils.TYPEFACE;
import com.beanslab.colorblindhelper.helper.utils.Utils;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

/**
 * Created by andrew on 30/05/14.
 */
public class HomeDashboard extends Activity {

    private int TAKE_PICTURE = 0;
    private int GET_GALLERY = 1;

    private String pictureName;
    private String externalStorage;

    private MyImageView image;

    public static boolean opened = false;

    private static DataSet colorDataSet;
    private static View gradientColor;

    private static TextView colorName;
    private static Thread th1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_dashboard);

        externalStorage = Environment.getExternalStorageDirectory() + "/colorblindhelper/";
        pictureName = "current_color_blind_image";
        image = (MyImageView) findViewById(R.id.image);
        opened = !opened ? pickImage() : false;

        colorDataSet = Utils.getColorsDataSet(getApplicationContext(),
                "dataset.js");

        gradientColor = findViewById(R.id.gradient_color);
        colorName = (TextView) findViewById(R.id.color_name);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        openImage();
    }


    public static synchronized void setColorDataSet(final int pixel){

        if(pixel == -1){

            return;
        }

        if(th1 != null){
            Thread moribund = th1;
            th1 = null;
            moribund.interrupt();
        }

        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Point [] point = (Point[]) msg.obj;
                Point pi = point[0];
                Point pf = point[1];


                GradientDrawable gd = new GradientDrawable(
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        new int[] {Color.rgb((int)pi.x,(int)pi.y,(int)pi.z),Color.rgb((int)pf.x,(int)pf.y,(int)pf.z)});
                gd.setCornerRadius(0f);

                gradientColor.setBackgroundDrawable(gd);
                colorName.setText(pf.label);


                //colorLabel.setText("El nombre del color mas cercano es : " + point.label);

                /*colorBoard.setBackgroundColor(Color.rgb(
                        (int) point.x, (int) point.y,
                        (int) point.z));*/
            }
        };

         th1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int redValue = Color.red(pixel);
                    int blueValue = Color.blue(pixel);
                    int greenValue = Color.green(pixel);

                    Point selectedPoint = new Point();

                    selectedPoint.x = redValue;
                    selectedPoint.y = greenValue;
                    selectedPoint.z = blueValue;

                    Point returnedPoint = Utils.classifykNN(
                            selectedPoint, colorDataSet);

                    Point [] gradient = new Point[2];
                    gradient[0] = selectedPoint;
                    gradient[1] = returnedPoint;

                    Message msg = new Message();
                    msg.obj = gradient;
                    handler.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        th1.start();
    }




    private void openImage(){

        try {

            Bitmap myBitmap = Utils.decodeSampledBitmapFromPath(externalStorage + pictureName +".jpg",image.getWidth(), image.getHeight());

            ExifInterface exif = new ExifInterface(externalStorage + pictureName +".jpg");
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
            }
            else if (orientation == 3) {
                matrix.postRotate(180);
            }
            else if (orientation == 8) {
                matrix.postRotate(270);
            }
            myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), matrix, true);

            image.setImageBitmap(myBitmap);
            //image.setImageDrawable(new BitmapDrawable(myBitmap));
            image.showIcon();

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TAKE_PICTURE && resultCode == Activity.RESULT_OK){
            try {
                openImage();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                        .show();
            }

        }

        if(requestCode == GET_GALLERY && resultCode == Activity.RESULT_OK){

            try {

                Uri selectedImage = data.getData();

                File source= new File( Utils.getRealPathFromUri(getApplicationContext(), selectedImage));
                File destination= new File(externalStorage + pictureName +".jpg");

                FileChannel src = new FileInputStream(source).getChannel();
                FileChannel dst = new FileOutputStream(destination).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
                openImage();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load", Toast.LENGTH_SHORT)
                        .show();
            }

        }

    }

    private boolean pickImage(){
        final Dialog custom = new Dialog(HomeDashboard.this,R.style.CustomDialogTheme);
        custom.setContentView(R.layout.imagepicker_menu);
        TextView fromCam = (TextView) custom.findViewById(R.id.cammera_option);
        TextView fromGallery = (TextView) custom.findViewById(R.id.gallery_option);

        Utils.applyCustomFont(fromCam, TYPEFACE.AvantGarde(getApplicationContext()));
        Utils.applyCustomFont(fromGallery, TYPEFACE.AvantGarde(getApplicationContext()));

        fromCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File photo = new File(externalStorage,  pictureName + ".jpg");
                intent3.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photo));
                startActivityForResult(intent3, TAKE_PICTURE);
                custom.dismiss();
            }
        });

        fromGallery.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto , GET_GALLERY);
                custom.dismiss();
            }
        });

        custom.show();

        return true;
    }
}
