package com.wanching.birthdayreminder.Others;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;

/**
 * Created by WanChing on 6/8/2017.
 */

/**
 * Class for handling encoding and decoding format of an image
 */

public class Conversion {

    /**
     * Encode an image from Bitmap format to String format
     * @param image Bitmap format of an image
     * @return String Encoded String format
     */
    public static String encodeToBase64(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        Log.d("Image Log:", imageEncoded);
        return imageEncoded;
    }

    /**
     * Decode an image from String format to Bitmap format
     * @param input String format of an image
     * @return Bitmap Decoded Bitmap format
     */
    public static Bitmap decodeToBase64(String input) {
        byte[] decodeByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodeByte, 0, decodeByte.length);
    }
}
