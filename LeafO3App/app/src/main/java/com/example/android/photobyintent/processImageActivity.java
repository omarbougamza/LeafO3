package com.example.android.photobyintent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.leafClassifier.PixelClassifier;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by BBC on 4/11/2015.
 */
public class processImageActivity extends Activity {


    private String mCurrentPhotoPath;
    private ImageView goodImageView;
    private TextView percentageView;
    private double damagePecentage;

    private String goodImagename;

    private static String IMAGE_PATH = "IMAGE_PATH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.imagepreview);

        goodImageView = (ImageView) findViewById(R.id.goodImg);
        percentageView = (TextView) findViewById(R.id.badImg);
        mCurrentPhotoPath = getIntent().getStringExtra(IMAGE_PATH);




        Log.i("Passed Photo Path", mCurrentPhotoPath.toString());
        File imageFile = new File(mCurrentPhotoPath);

        try {
            PixelClassifier pc = new PixelClassifier(imageFile.getParent(),imageFile.getAbsolutePath());
            mCurrentPhotoPath = pc.process();

            damagePecentage = pc.getDamagePercent();
            percentageView.setText(String.format("%.2f",damagePecentage)+"%");

            //mCurrentPhotoPath = imageFile.getParent()+File.separator+"badleaf.jpg";

            setPic();
            galleryAddPic();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void setPic() {
        /* There isn't enough memory to open up more than a couple camera photos */
        /* So pre-scale the target bitmap into which the file is decoded */
        Log.i(IMAGE_PATH+"----------PROCESSING--------", mCurrentPhotoPath);
		/* Get the size of the ImageView */
        int targetW = goodImageView.getWidth();
        int targetH = goodImageView.getHeight();

		/* Get the size of the image */
        /*BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

		*//* Figure out which way needs to be reduced less *//*
        int scaleFactor = 1;
        if ((targetW > 0) || (targetH > 0)) {
            scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        }

		*//* Set bitmap options to scale the image decode target *//*
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
*/
		/* Decode the JPEG file into a Bitmap */
        //mCurrentPhotoPath = "/storage/emulated/0/badleaf.jpg";
        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
		/* Associate the Bitmap to the ImageView */
        goodImageView.setImageBitmap(bitmap);
        goodImageView.setVisibility(View.VISIBLE);
    }


    private void galleryAddPic() {
        //Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_MOUNTED);
/*
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);*/
        this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED),
                Uri.parse("file://"+Environment.getExternalStorageDirectory()).toString());
    }
}