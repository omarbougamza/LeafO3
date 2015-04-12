/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.android.leafClassifier;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.InputMismatchException;

import static java.lang.Math.abs;


/**
 * @author WareHouse
 */

public class PixelClassifier {

    Bitmap img, goodImg, badImg;

    int bad[], good[];

    File file;
    String path;
    String absolute;
    long countG, countB;

    public PixelClassifier(String path, String absolute) throws IOException {
        file = new File(absolute);
        this.path = path;
        this.absolute = absolute;

        FileInputStream fis= new FileInputStream(new File(absolute));
        Log.i("--------------- PATH", absolute);
        img = BitmapFactory.decodeStream(fis);



        bad = new int[img.getWidth() * img.getHeight()];
        Log.i("--------------- HEIGHT", Integer.valueOf(img.getHeight()).toString());
        good = new int[img.getWidth() * img.getHeight()];

        Log.i("--------------- HEIGHT", Integer.valueOf(img.getHeight()).toString());
        countG = countB = 0;

        //badImg = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        //goodImg = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

    }


    public String process() throws IOException {
        int index = 0;

        for (int i = 0; i < img.getHeight(); i++)
            for (int j = 0; j < img.getWidth(); j++) {
                int col = img.getPixel(j, i);
                int type = classify(col);

                if (type == 1) {
                    // Good
                    //goodImg.setPixel(j, i, col);
                    //badImg.setPixel(j, i, 0xffffffff);
                    good[index] = col;
                    bad[index] = 0xffffffff;
                    countG++;
                } else if (type == 2) {
                    //badImg.setPixel(j, i, col);
                    //goodImg.setPixel(j, i, 0xffffffff);
                    bad[index] = col;
                    good[index] = 0xffffffff;

                    countB++;
                } else {
                    //goodImg.setPixel(j, i, 0xffffffff);
                    //badImg.setPixel(j, i, 0xffffffff);
                    good[index] = bad[index] = 0xffffffff;

                }
                    index++;
            }


        badImg = Bitmap.createBitmap(bad, img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        Log.i(" BEFORE SAVE"," BADIMG SAVE");

//        goodImg = Bitmap.createBitmap(good, img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);

//        saveBitmap(goodImg, "goodleaf.jpg");
        String path = saveBitmap(badImg, "badleaf.jpg");
        Log.i(" AFTER SAVE"," AFTER SAVE");
        return path;
    }

    public int classify(int pix) {
        int ans;
        final int r = (pix & 0x00ff0000) >> 16;
        final int g = (pix & 0x0000ff00) >> 8;
        final int b = pix & 0x000000ff;

        if (isBackground(r, g, b)) ans = 0;
        else {
            if (isGood(r, g, b)) ans = 1;
            else ans = 2;
        }
        
        /*
        0 : BACKGROUND
        1 : GOOD
        2 : BAD
        */

        return ans;
    }

    public String saveBitmap(Bitmap img, String name) {
        File dir = Environment.getExternalStorageDirectory();
        File f = new File(dir, name);
        Log.i(" IN SAVE"," AFTER SAVE");
        try {
            FileOutputStream out = new FileOutputStream(f);
            img.compress(Bitmap.CompressFormat.JPEG, 70, out);
            Log.i(" IN COMPRESS"," AFTER SAVE");
            out.flush();
            out.close();

        } catch (Exception e) {
            Log.i("EXCEPTION saving img", "EXCEPTION in Saving the Img");

        }

        return f.getAbsolutePath();
    }


    public boolean isBackground(int r, int g, int b) {
        return abs(r - g) < 10 && abs(g - b) < 10;
    }

    public boolean isGood(int r, int g, int b) {
        return g > r;
    }

    public long getBad() {
        return this.countB;
    }

    public long getGood() {
        return this.countG;
    }

    public long getArea() {
        return this.getGood() + this.getBad();
    }

    public double getDamagePercent() {
        return this.getBad() / (double) this.getArea() * 100.0;
    }




}

