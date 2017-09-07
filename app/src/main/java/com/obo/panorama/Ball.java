package com.obo.panorama;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;


public class Ball implements GLSurfaceView.Renderer {
    private static final String TAG = "Ball";

    private Context mContext;
    private int mUProjectMatrixHandler;
    private final float[] mProjectMatrix = new float[16];
    private int mSize;
    private FloatBuffer mVertexBuff;
    private FloatBuffer mTextureBuff;
    private int mTextrueID;
    private int mImgId;
    private final float[] mCurrMatrix = new float[16];
    private final float[] mMVPMatrix = new float[16];

    public float xAngle = 0.0F;
    public float yAngle = 90.0F;
    public float zAngle;

    public Ball(Context context, int drawableId) {
        this.mContext = context;
        this.mImgId = drawableId;
        init();
    }

    private void init() {
        double perVertex = 36;
        double perRadius = 2 * Math.PI / perVertex;
        double perW = 1.0 / perVertex;
        double perH = 1.0 / perVertex;

        List<Double> vetexList = new ArrayList<>();
        List<Double> textureList = new ArrayList<>();

        for(int texture = 0; texture < perVertex; ++texture) {
            for(int vetex = 0; vetex < perVertex; ++vetex) {
                double i = texture * perH;
                double h1 = vetex * perW;
                double w2 = (texture + 1) * perH;
                double h2 = vetex * perW;
                double w3 = (texture + 1) * perH;
                double h3 = (vetex + 1) * perW;
                double w4 = texture * perH;
                double h4 = (vetex + 1) * perW;

                textureList.add(h1);
                textureList.add(i);
                textureList.add(h2);
                textureList.add(w2);
                textureList.add(h3);
                textureList.add(w3);
                textureList.add(h3);
                textureList.add(w3);
                textureList.add(h4);
                textureList.add(w4);
                textureList.add(h1);
                textureList.add(i);

                double x1 = Math.sin(texture * perRadius / 2.0) * Math.cos(vetex * perRadius);
                double z1 = Math.sin(texture * perRadius / 2.0) * Math.sin(vetex * perRadius);
                double y1 = Math.cos(texture * perRadius / 2.0);
                double x2 = Math.sin((texture + 1) * perRadius / 2.0) * Math.cos(vetex * perRadius);
                double z2 = Math.sin((texture + 1) * perRadius / 2.0) * Math.sin(vetex * perRadius);
                double y2 = Math.cos((texture + 1) * perRadius / 2.0);
                double x3 = Math.sin((texture + 1) * perRadius / 2.0) * Math.cos((vetex + 1) * perRadius);
                double z3 = Math.sin((texture + 1) * perRadius / 2.0) * Math.sin((vetex + 1) * perRadius);
                double y3 = Math.cos((texture + 1) * perRadius / 2.0);
                double x4 = Math.sin(texture * perRadius / 2.0) * Math.cos((vetex + 1) * perRadius);
                double z4 = Math.sin(texture * perRadius / 2.0) * Math.sin((vetex + 1) * perRadius);
                double y4 = Math.cos(texture * perRadius / 2.0);

                vetexList.add(x1);
                vetexList.add(y1);
                vetexList.add(z1);
                vetexList.add(x2);
                vetexList.add(y2);
                vetexList.add(z2);
                vetexList.add(x3);
                vetexList.add(y3);
                vetexList.add(z3);

                vetexList.add(x3);
                vetexList.add(y3);
                vetexList.add(z3);
                vetexList.add(x4);
                vetexList.add(y4);
                vetexList.add(z4);
                vetexList.add(x1);
                vetexList.add(y1);
                vetexList.add(z1);
            }
        }

        this.mSize = vetexList.size() / 3;
        float[] textureFloats = new float[this.mSize * 2];

        for(int i = 0; i < textureFloats.length; ++i) {
            double d = textureList.get(i);
            textureFloats[i] = (float)d;
        }

        this.mTextureBuff = ByteBuffer.allocateDirect(textureFloats.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mTextureBuff.put(textureFloats);
        this.mTextureBuff.position(0);

        float[] vertexFloats = new float[this.mSize * 3];
        for(int  i= 0; i < vertexFloats.length; ++i) {
            double d = vetexList.get(i);
            vertexFloats[i] = (float) d;
        }

        this.mVertexBuff = ByteBuffer.allocateDirect(vertexFloats.length * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        this.mVertexBuff.put(vertexFloats);
        this.mVertexBuff.position(0);
    }

    private float[] getfinalMVPMatrix() {
        Matrix.multiplyMM(this.mMVPMatrix, 0, this.mProjectMatrix, 0, this.mCurrMatrix, 0);
        Matrix.setIdentityM(this.mCurrMatrix, 0);
        return this.mMVPMatrix;
    }

    @Override
    public void onDrawFrame(GL10 arg0) {
        Log.i(TAG, "onDrawFrame");
        Matrix.rotateM(this.mCurrMatrix, 0, -this.xAngle, 1.0F, 0.0F, 0.0F);
        Matrix.rotateM(this.mCurrMatrix, 0, -this.yAngle, 0.0F, 1.0F, 0.0F);
        Matrix.rotateM(this.mCurrMatrix, 0, -this.zAngle, 0.0F, 0.0F, 1.0F);
        GLES20.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
        GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, this.mTextrueID);
        GLES20.glUniformMatrix4fv(this.mUProjectMatrixHandler, 1, false, getfinalMVPMatrix(), 0);
        GLES20.glDrawArrays(4, 0, this.mSize);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);
        GLES20.glEnable(GLES20.GL_CULL_FACE);
        float ratio = (float)width / (float)height;
        Matrix.frustumM(this.mProjectMatrix, 0, -ratio, ratio, -1.0F, 1.0F, 1.0F, 20.0F);
        Matrix.setIdentityM(this.mCurrMatrix, 0);
        Matrix.setIdentityM(this.mMVPMatrix, 0);
        Matrix.translateM(this.mProjectMatrix, 0, 0.0F, 0.0F, -2.0F);
        Matrix.scaleM(this.mProjectMatrix, 0, 4.0F, 4.0F, 4.0F);
        int program = OGLUtils.getProgram(this.mContext);
        GLES20.glUseProgram(program);
        int aPositionHandler = GLES20.glGetAttribLocation(program, "aPosition");
        this.mUProjectMatrixHandler = GLES20.glGetUniformLocation(program, "uProjectMatrix");
        int aTextureCoordHandler = GLES20.glGetAttribLocation(program, "aTextureCoord");
        this.mTextrueID = OGLUtils.initTexture(this.mContext, this.mImgId);
        Log.i(TAG, "aPositionHandler:" + aPositionHandler);
        Log.i(TAG, "mUProjectMatrixHandler:" + this.mUProjectMatrixHandler);
        Log.i(TAG, "aTextureCoordHandler:" + aTextureCoordHandler);
        Log.i(TAG, "textureID:" + this.mTextrueID);
        GLES20.glVertexAttribPointer(aPositionHandler, 3, GLES20.GL_FLOAT, false, 0, this.mVertexBuff);
        GLES20.glVertexAttribPointer(aTextureCoordHandler, 2, GLES20.GL_FLOAT, false, 0, this.mTextureBuff);
        GLES20.glEnableVertexAttribArray(aPositionHandler);
        GLES20.glEnableVertexAttribArray(aTextureCoordHandler);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {}
}