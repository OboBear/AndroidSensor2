package com.obo.panorama;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by obo on 2017/9/6.
 */


public class OGLUtils {
    private static final String TAG = "OGLUtils";

    public static String SOURCE_DEFAULT_NAME_FRAGMENT = "fragment.glsl";
    public static String SOURCE_DEFAULT_NAME_VERTEX = "vertex.glsl";

    public static int getProgram(Context context) {
        String vertexStr = getShaderSource(context, SOURCE_DEFAULT_NAME_VERTEX);
        String fragmentStr = getShaderSource(context, SOURCE_DEFAULT_NAME_FRAGMENT);
        return getProgram(vertexStr, fragmentStr);
    }

    public static int getProgram(String vertexStr, String fragmentStr) {
        int program = GLES20.glCreateProgram();
        int fragmentShader = GLES20.glCreateShader(GLES20.GL_FRAGMENT_SHADER);
        int vertexShader = GLES20.glCreateShader(GLES20.GL_VERTEX_SHADER);
        GLES20.glShaderSource(vertexShader, vertexStr);
        GLES20.glShaderSource(fragmentShader, fragmentStr);
        GLES20.glCompileShader(vertexShader);
        GLES20.glCompileShader(fragmentShader);
        GLES20.glAttachShader(program, vertexShader);
        GLES20.glAttachShader(program, fragmentShader);
        GLES20.glLinkProgram(program);
        return program;
    }

    public static String getShaderSource(Context context, String sourseName) {
        StringBuffer shaderSource = new StringBuffer();

        try {
            BufferedReader e = new BufferedReader(new InputStreamReader(context.getAssets().open(sourseName)));
            String tempStr;
            while(null != (tempStr = e.readLine())) {
                shaderSource.append(tempStr);
            }
        } catch (IOException var5) {
            var5.printStackTrace();
        }

        return shaderSource.toString();
    }


    public static int initTexture(Context context, int drawableId) {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        int textureId = textures[0];
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, 9728.0F);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, 9729.0F);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, 33071.0F);
        GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, 33071.0F);
        InputStream is = context.getResources().openRawResource(drawableId);

        Bitmap bitmapTmp;
        try {
            bitmapTmp = BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            Log.i(TAG, e.getMessage());
            return textureId;
        } finally {
            try {
                is.close();
            } catch (IOException var12) {
                var12.printStackTrace();
            }
        }

        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmapTmp, 0);
        bitmapTmp.recycle();
        return textureId;
    }
}