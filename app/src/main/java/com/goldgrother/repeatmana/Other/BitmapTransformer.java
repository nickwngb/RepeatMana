package com.goldgrother.repeatmana.Other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

/**
 * Created by hao_jun on 2016/2/15.
 */

public class BitmapTransformer {
	private static final String plus = "+";
    private static final String plusUnicode = "%20";
	
    public static String BitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] byteArray = baos.toByteArray();
        String encode = Base64.encodeToString(byteArray, Base64.DEFAULT);
        return encode;
    }

    public static Bitmap Base64ToBitmap(String base64) {
        try {
			base64 = base64.replace(plusUnicode, plus);
            byte[] decodedString = Base64.decode(base64, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }catch (Exception ex){
            return null;
        }
    }

    public static Bitmap ResizeBitmap(Bitmap bmp, int newWidth, int newHeight) {
        // old w , h
        int oldWidth = bmp.getWidth();
        int oldHeight = bmp.getHeight();
        // scale %
        float scaleWidth = ((float) newWidth) / oldWidth;
        float scaleHeight = ((float) newHeight) / oldHeight;
        // scale matrix params
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(bmp, 0, 0, oldWidth, oldHeight, matrix, true);
        return newbmp;
    }
}
