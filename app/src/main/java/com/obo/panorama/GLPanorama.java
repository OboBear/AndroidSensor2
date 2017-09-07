package com.obo.panorama;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.zph.three360panorama.R;


public class GLPanorama extends RelativeLayout implements SensorEventListener {
    private static final String TAG = "GLPanorama";

    private Context mContext;
    private IViews mGlSurfaceView;
    private ImageView mImg;
    private float mPreviousY;
    private float mPreviousYs;
    private float mPreviousX;
    private float mPreviousXs;
    private float mPredegrees = 0.0F;
    private Ball mBall;
    private SensorManager mSensorManager;
    private Sensor mGyroscopeSensor;
    private float mTimestamp;
    private float[] mAngle = new float[3];

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case 101:
                    Sensordt info = (Sensordt)msg.obj;
                    float y = info.getSensorY();
                    float x = info.getSensorX();
                    float dy = y - mPreviousY;
                    float dx = x - mPreviousX;
                    mBall.yAngle += dx * 2.0F;
                    mBall.xAngle += dy * 0.5F;
                    if(mBall.xAngle < -50.0F) {
                        mBall.xAngle = -50.0F;
                    } else if(mBall.xAngle > 50.0F) {
                        mBall.xAngle = 50.0F;
                    }

                    mPreviousY = y;
                    mPreviousX = x;
                    rotate();
                default:
            }
        }
    };
    private Handler mHandlers = new Handler();
    int yy = 0;

    public GLPanorama(Context context) {
        super(context);
        this.mContext = context;
        this.init();
    }

    public GLPanorama(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        this.init();
    }

    public GLPanorama(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        this.init();
    }

    private void init() {
        this.initView();
    }

    private void initView() {
        LayoutInflater.from(mContext).inflate(R.layout.panoramalayout, this);
        mGlSurfaceView = findViewById(R.id.m_views);
        mImg = findViewById(R.id.img);
        mImg.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                zero();
            }
        });
    }

    private void initSensor() {
        mSensorManager = (SensorManager) mContext.getSystemService(Context.SENSOR_SERVICE);
        mGyroscopeSensor = mSensorManager.getDefaultSensor(4);
        mSensorManager.registerListener(this, mGyroscopeSensor, 0);
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Log.i(TAG, "onSensorChanged");
        if(sensorEvent.sensor.getType() == 4) {
            if(mTimestamp != 0.0F) {
                float dT = ((float)sensorEvent.timestamp - mTimestamp) * 1.0E-9F;
                mAngle[0] += sensorEvent.values[0] * dT;
                mAngle[1] += sensorEvent.values[1] * dT;
                mAngle[2] += sensorEvent.values[2] * dT;
                float anglex = (float)Math.toDegrees((double)mAngle[0]);
                float angley = (float)Math.toDegrees((double)mAngle[1]);
                float anglez = (float)Math.toDegrees((double)mAngle[2]);
                Sensordt info = new Sensordt();
                info.setSensorX(angley);
                info.setSensorY(anglex);
                info.setSensorZ(anglez);
                Message msg = new Message();
                msg.what = 101;
                msg.obj = info;
                this.mHandler.sendMessage(msg);
            }
            mTimestamp = (float)sensorEvent.timestamp;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mSensorManager.unregisterListener(this);
        float y = event.getY();
        float x = event.getX();
        switch(event.getAction()) {
            case MotionEvent.ACTION_UP:
                mSensorManager.registerListener(this, mGyroscopeSensor, 0);
                break;
            case MotionEvent.ACTION_MOVE:
                float dy = y - mPreviousYs;
                float dx = x - mPreviousXs;
                mBall.yAngle += dx * 0.3F;
                mBall.xAngle += dy * 0.3F;
                if(mBall.xAngle < -50.0F) {
                    mBall.xAngle = -50.0F;
                } else if(mBall.xAngle > 50.0F) {
                    mBall.xAngle = 50.0F;
                }

                rotate();
        }

        mPreviousYs = y;
        mPreviousXs = x;
        return true;
    }

    public void setGLPanorama(int pimgid) {
        this.mGlSurfaceView.setEGLContextClientVersion(2);
        this.mBall = new Ball(this.mContext, pimgid);
        this.mGlSurfaceView.setRenderer(this.mBall);
        this.initSensor();
    }

    private void rotate() {
        RotateAnimation anim = new RotateAnimation(mPredegrees, - mBall.yAngle, 1, 0.5F, 1, 0.5F);
        anim.setDuration(200L);
        this.mImg.startAnimation(anim);
        this.mPredegrees = - mBall.yAngle;
    }

    private void zero() {
        this.yy = (int)((mBall.yAngle - 90.0F) / 10.0F);
        this.mHandlers.post(new Runnable() {
            public void run() {
                if(yy != 0) {
                    if(yy > 0) {
                        mBall.yAngle -= 10.0F;
                        mHandlers.postDelayed(this, 16L);
                        --yy;
                    }

                    if(yy < 0) {
                        mBall.yAngle += 10.0F;
                        mHandlers.postDelayed(this, 16L);
                        ++yy;
                    }
                } else {
                    mBall.yAngle = 90.0F;
                }

                mBall.xAngle = 0.0F;
            }
        });
    }

    private class Sensordt {
        float sensorX;
        float sensorY;
        float sensorZ;

        float getSensorX() {
            return this.sensorX;
        }

        void setSensorX(float sensorX) {
            this.sensorX = sensorX;
        }

        float getSensorY() {
            return this.sensorY;
        }

        void setSensorY(float sensorY) {
            this.sensorY = sensorY;
        }

        void setSensorZ(float sensorZ) {
            this.sensorZ = sensorZ;
        }
    }
}
