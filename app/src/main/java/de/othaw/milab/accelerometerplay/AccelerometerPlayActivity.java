/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package com.example.android.accelerometerplay;
package de.othaw.milab.accelerometerplay;

import android.annotation.TargetApi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.BitmapFactory.Options;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;


import java.time.Duration;
import java.time.Instant;
import java.util.Random;

/**
 * This is an example of using the accelerometer to integrate the device's
 * acceleration to a position using the Verlet method. This is illustrated with
 * a very simple particle system comprised of a few iron balls freely moving on
 * an inclined wooden table. The inclination of the virtual table is controlled
 * by the device's accelerometer.
 *
 * @see SensorManager
 * @see SensorEvent
 * @see Sensor
 */

public class AccelerometerPlayActivity extends AppCompatActivity {

    private static final int LEVELS_TO_PLAY = 1;
    private SimulationView mSimulationView;
    private SensorManager mSensorManager;
    private PowerManager mPowerManager;
    private WindowManager mWindowManager;
    private Display mDisplay;
    private WakeLock mWakeLock;

    private static int levelcount = 0;
    private static Duration duration;

    private Instant starttime;

    HighScoreEntryDBHelper dbHelper = new HighScoreEntryDBHelper(this);

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get an instance of the SensorManager
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        // Get an instance of the PowerManager
        mPowerManager = (PowerManager) getSystemService(POWER_SERVICE);

        // Get an instance of the WindowManager
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mDisplay = mWindowManager.getDefaultDisplay();

        // Create a bright wake lock
        mWakeLock = mPowerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, getClass()
                .getName());

        // instantiate our simulation view and set it as the activity's content
        mSimulationView = new SimulationView(this);
        mSimulationView.setBackgroundResource(R.drawable.wood);
        setContentView(mSimulationView);

        starttime = Instant.now();

    }

    public static int getLevelCount(){
        return levelcount;
    }

    /**
     * finishes the current Activity and returns to the Main Menu
     */
    public void goToScoreMenu(){
        levelcount = 0;
        Instant endtime = Instant.now();
        duration = Duration.between(starttime, endtime);
        Intent intent = new Intent(this, gameend_menu.class);
        startActivity(intent);
    }

    /**
     * Handles the instantiating of the next level
     */
    public void newLevel() {
        // increase the level count
        levelcount += 1;

        //unregister
        mSimulationView.stopSimulation();

        // while levels to play
        if (levelcount < LEVELS_TO_PLAY) {
            // instantiate a new Level
            mSimulationView = new SimulationView(this);
            mSimulationView.setBackgroundResource(R.drawable.wood);
            setContentView(mSimulationView);
            //reregister the Accelerometer
            mSimulationView.startSimulation();
        } else {
            goToScoreMenu();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * when the activity is resumed, we acquire a wake-lock so that the
         * screen stays on, since the user will likely not be fiddling with the
         * screen or buttons.
         */
        mWakeLock.acquire();

        // Start the simulation
        mSimulationView.startSimulation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        /*
         * When the activity is paused, we make sure to stop the simulation,
         * release our sensor resources and wake locks
         */

        // Stop the simulation
        mSimulationView.stopSimulation();

        // and release our wake-lock
        mWakeLock.release();
    }

    public static Duration getDuration() {
        return duration;
    }

    /**
     * View handling the particle System and general game Logic
     */
    class SimulationView extends FrameLayout implements SensorEventListener {

        /**
         * Diameter of the ball in meters
         */
        private static final float sBallDiameter = 0.004f;
        /**
         * Diameter of the ball squared
         */
        private static final float sBallDiameter2 = sBallDiameter * sBallDiameter;

        /**
         * Width of the ball
         */
        private final int mDstWidth;
        /**
         * Height of the ball
         */
        private final int mDstHeight;

        /**
         * Accelerometer
         */
        private Sensor mAccelerometer;
        /**
         * Last timestamp when calculating dT
         */
        private long mLastT;


        /**
         * physical pixels of the screen along the x-axis
         */
        private float mXDpi;
        /**
         * physical pixels of the screen along the y-axis
         */
        private float mYDpi;


        private float mMetersToPixelsX;
        private float mMetersToPixelsY;

        private float mXOrigin;
        private float mYOrigin;
        private float mSensorX;
        private float mSensorY;
        /**
         * Horizontal bound of the screen
         */
        private float mHorizontalBound;

        /**
         * Vertical bound of the screen
         */
        private float mVerticalBound;

        /**
         * Particle System holding the ball, goal and falseFriends
         */
        private final ParticleSystem mParticleSystem;

        public class WinView extends View{

            public WinView(Context context) {
                super(context);
            }

            public WinView(Context context, @Nullable AttributeSet attrs) {
                super(context, attrs);
            }

            public WinView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
            }

            public WinView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
                super(context, attrs, defStyleAttr, defStyleRes);
            }

            @Override
            protected void onDraw(Canvas canvas){
                super.onDraw(canvas);


            }
        }

        /**
         * Particle is used as a base class for ball, goal and falseFriend. Each Particle has a
         * random position on the screen.
         */
        class Particle extends View {

            protected Random rand = new Random();

            protected float mPosX = -0.03f + rand.nextFloat() * 0.06f;
            protected float mPosY = -0.05f + rand.nextFloat() * 0.1f;

            public Particle(Context context) {
                super(context);

            }

            public Particle(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public Particle(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            public Particle(Context context, AttributeSet attrs, int defStyleAttr,
                            int defStyleRes) {
                super(context, attrs, defStyleAttr, defStyleRes);
            }


            /**
             * Resolves Collisions with the bounds of the screen
             * if a particle is outside of the screens view it gets moved back in
             */
            public void resolveCollisionWithBounds() {
                final float xmax = mHorizontalBound;
                final float ymax = mVerticalBound;
                final float x = mPosX;
                final float y = mPosY;
                if (x > xmax) {
                    mPosX = xmax;

                } else if (x < -xmax) {
                    mPosX = -xmax;

                }
                if (y > ymax) {
                    mPosY = ymax;

                } else if (y < -ymax) {
                    mPosY = -ymax;

                }
            }



        }

        /**
         * The goal object is the View that the ball has to intersect with to win the game / round
         */
        class Goal extends Particle{

            public Goal(Context context) {
                super(context);
            }

            public Goal(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public Goal(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
            }

            public Goal(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
                super(context, attrs, defStyleAttr, defStyleRes);
            }
        }

        /**
         * A FalseFriend looks just like the goal, but does not make the player win,
         * instead intersecting with a FalseFriend slows down the ball.
         */
        class FalseFriend extends Particle{

            public FalseFriend(Context context) {
                super(context);
            }

            public FalseFriend(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public FalseFriend(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
            }

            public FalseFriend(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
                super(context, attrs, defStyleAttr, defStyleRes);
            }

        }


        /**
         * A ball has a velocity aswell as a position in space. It is rolled around the screen by a player.
         */
        class Ball extends Particle{
            private float mVelX;
            private float mVelY;

            public Ball(Context context) {
                super(context);
            }

            public Ball(Context context, AttributeSet attrs) {
                super(context, attrs);
            }

            public Ball(Context context, AttributeSet attrs, int defStyleAttr) {
                super(context, attrs, defStyleAttr);
            }

            public Ball(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
                super(context, attrs, defStyleAttr, defStyleRes);
            }

            /** Computes the new coordinates and velocities
             *
             * @param sx mSensorX
             * @param sy mSensorY
             * @param dT deltaTime
             */
            public void computePhysics(float sx, float sy, float dT) {

                // increase the acceleration throughout the levels
               final float ax = -sx/5 * (levelcount + 1) * 0.5f;


                //increase the acceleration throughout the levels
                final float ay = -sy/5 * (levelcount + 1) * 0.5f;


                if (mParticleSystem.checkIfByFalseFriend()){
                    mPosX += mVelX / 3.5 * dT + ax * dT * dT / 2;
                    mPosY += mVelY / 3.5 * dT + ay * dT * dT / 2;
                }else{
                    mPosX += mVelX * dT + ax * dT * dT / 2;
                    mPosY += mVelY * dT + ay * dT * dT / 2;
                }

                mVelX += ax * dT;
                mVelY += ay * dT;
            }


            /**
             * Resolves Collisions with the bounds of the screen
             * if a particle is outside of the screens view it gets moved back in and its velocity
             * gets set to 0
             */
            @Override
            public void resolveCollisionWithBounds() {
                final float xmax = mHorizontalBound;
                final float ymax = mVerticalBound;
                final float x = mPosX;
                final float y = mPosY;
                if (x > xmax) {
                    mPosX = xmax;
                    mVelX = 0;
                } else if (x < -xmax) {
                    mPosX = -xmax;
                    mVelX = 0;
                }
                if (y > ymax) {
                    mPosY = ymax;
                    mVelY = 0;
                } else if (y < -ymax) {
                    mPosY = -ymax;
                    mVelY = 0;
                }
            }

        }

        /**
         * The Particle System holds a ball, a goal and n FalseFriends
         */
        class ParticleSystem {

            /**
             * The ball that gets rolled around the screen
             */
            private Ball ball= new Ball(getContext());

            /**
             * The number of FalseFriends in the Particle System
             */
            final int NUM_FF = 15 + 5 * getLevelCount();

            /**
             * An Array of n FalseFriends
             */
            private FalseFriend mFalseFriends[] = new FalseFriend[NUM_FF];

            /**
             * The goal a player has to roll the ball into
             */
            private Goal goal = new Goal(getContext());

            ParticleSystem() {
                /*
                 * Initially our particles have no speed or acceleration
                 */
                for (int i = 0; i < mFalseFriends.length; i++) {
                    mFalseFriends[i] = new FalseFriend(getContext());
                    mFalseFriends[i].setBackgroundResource(R.drawable.goal2);
                    mFalseFriends[i].setLayerType(LAYER_TYPE_HARDWARE, null);
                    addView(mFalseFriends[i], new ViewGroup.LayoutParams(mDstWidth, mDstHeight));
                }

                goal.setBackgroundColor(Color.MAGENTA);
                goal.setLayerType(LAYER_TYPE_HARDWARE, null);
                addView(goal, new ViewGroup.LayoutParams(mDstWidth, mDstHeight));



                ball.setBackgroundResource(R.drawable.ball);
                ball.setLayerType(LAYER_TYPE_HARDWARE, null);
                addView(ball, new ViewGroup.LayoutParams(mDstWidth, mDstHeight));


            }

            /**
             * Checks if the ball is intersecting with a FalseFriend
             *
             * @return boolean value: true if near a falseFriend, false if not
             */
            public boolean checkIfByFalseFriend(){

                float x;
                float y;
                float distance;
                for (FalseFriend ff: mFalseFriends) {
                     x = ball.mPosX - ff.mPosX;
                     y = ball.mPosY - ff.mPosY;

                    distance =  (float) Math.sqrt(x * x + y * y);

                    // we can use the diameter, as the falseFriend radius + the ball radius is just the balls diameter
                    if (distance <= sBallDiameter){
                        return true;
                    }
                }
                return false;
            }

            public boolean checkIfByGoal(){
                float x;
                float y;
                float distance;
                x = ball.mPosX - goal.mPosX;
                y = ball.mPosY - goal.mPosY;

                distance =  (float) Math.sqrt(x * x + y * y);

                // we can use the ball radius as at least the center of the ball should be intersecting
                if (distance <= sBallDiameter / 2){
                    return true;
                }

                return false;
            }

            /**
             * Update the position of each particle in the system using the
             * Verlet integrator.
             *
             * @param sx mSensorX
             * @param sy mSensorY
             * @param timestamp the current time
             */
            private void updatePositions(float sx, float sy, long timestamp) {
                final long t = timestamp;
                if (mLastT != 0) {
                    final float dT = (float) (t - mLastT) / 1000.f /** (1.0f / 1000000000.0f)*/;
                    ball.computePhysics(sx, sy, dT);
                }
                mLastT = t;
            }

            /**
             * Performs one iteration of the simulation. First updating the
             * position of all the particles and resolving the constraints and
             * collisions.
             *
             * @param sx mSensorX
             * @param sy mSensorY
             * @param now current system time
             */
            public void update(float sx, float sy, long now) {
                // update the system's positions
                updatePositions(sx, sy, now);

                if (checkIfByGoal()){
                    stopSimulation();

                    newLevel();

                }
                ball.resolveCollisionWithBounds();
                //goal.resolveCollisionWithBounds();

                removeOverlap();
            }

            /**
             * Resolves Collisions between FalseFriends so they don't overlap on screen
             */
            private  void removeOverlap(){

                // We do no more than a limited number of iterations
                final int NUM_MAX_ITERATIONS = 10;

                /*
                 * Resolve collisions, each particle is tested against every
                 * other particle for collision. If a collision is detected the
                 * particle is moved away using a virtual spring of infinite
                 * stiffness.
                 */
                boolean more = true;

                /*
                 * Keep FalseFriends from Overlapping each other
                 * */
                final int count = mFalseFriends.length;
                for (int k = 0; k < NUM_MAX_ITERATIONS && more; k++) {
                    more = false;
                    for (int i = 0; i < count; i++) {
                        FalseFriend curr = mFalseFriends[i];
                        for (int j = i + 1; j < count; j++) {
                            FalseFriend ff = mFalseFriends[j];
                            float dx = ff.mPosX - curr.mPosX;
                            float dy = ff.mPosY - curr.mPosY;
                            float dd = dx * dx + dy * dy;
                            // Check for collisions
                            if (dd <= sBallDiameter2) {
                                /*
                                 * add a little bit of entropy, after nothing is
                                 * perfect in the universe.
                                 */
                                dx += ((float) Math.random() - 0.5f) * 0.0001f;
                                dy += ((float) Math.random() - 0.5f) * 0.0001f;
                                dd = dx * dx + dy * dy;
                                // simulate the spring
                                final float d = (float) Math.sqrt(dd);
                                final float c = (0.5f * (sBallDiameter - d)) / d;
                                final float effectX = dx * c;
                                final float effectY = dy * c;
                                curr.mPosX -= effectX;
                                curr.mPosY -= effectY;
                                ff.mPosX += effectX;
                                ff.mPosY += effectY;
                                more = true;
                            }
                        }
                        curr.resolveCollisionWithBounds();
                    }
                }
            }

            /**
             *
             * @return X Coordinate of the ball
             */
            public float getBallPosX() {
                return ball.mPosX;

            }

            /**
             *
             * @return Y Coordinate of the ball
             */
            public float getBallPosY() {
                return ball.mPosY;
            }

            /**
             *
             * @param i the i-th FalseFriend in the FalseFriend Array
             * @return X Coordinate of the i-th FalseFriend
             */
            public float getFFPosX(int i) {
                return mFalseFriends[i].mPosX;

            }

            /**
             *
             * @param i the i-th FalseFriend in the FalseFriend Array
             * @return Y Coordinate of the i-th FalseFriend
             */
            public float getFFPosY(int i) {
                return mFalseFriends[i].mPosY;
            }

            /**
             *
             * @return number of falseFriends on screen
             */
            public int getFFCount() {
                return mFalseFriends.length;
            }

            /**
             *
             * @return X Coordinate of the goal
             */
            public float getGoalPosX() {
                return goal.mPosX;
            }

            /**
             *
             * @return Y Coordinate of the goal
             */
            public float getGoalPosY() {
                return goal.mPosY;
            }
        }

        /**
         * Starts the Physics Game
         */
        public void startSimulation() {
            /*
             * It is not necessary to get accelerometer events at a very high
             * rate, by using a slower rate (SENSOR_DELAY_UI), we get an
             * automatic low-pass filter, which "extracts" the gravity component
             * of the acceleration. As an added benefit, we use less power and
             * CPU resources.
             */
            mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
        }

        public void stopSimulation() {
            mSensorManager.unregisterListener(this);
        }

        public SimulationView(Context context) {
            super(context);
            mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            mXDpi = metrics.xdpi;
            mYDpi = metrics.ydpi;
            mMetersToPixelsX = mXDpi / 0.0254f;
            mMetersToPixelsY = mYDpi / 0.0254f;

            // rescale the ball so it's about 0.5 cm on screen
            mDstWidth = (int) (sBallDiameter * mMetersToPixelsX + 0.5f);
            mDstHeight = (int) (sBallDiameter * mMetersToPixelsY + 0.5f);
            mParticleSystem = new ParticleSystem();

            Options opts = new Options();
            opts.inDither = true;
            opts.inPreferredConfig = Bitmap.Config.RGB_565;
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            // compute the origin of the screen relative to the origin of
            // the bitmap
            mXOrigin = (w - mDstWidth) * 0.5f;
            mYOrigin = (h - mDstHeight) * 0.5f;
            mHorizontalBound = ((w / mMetersToPixelsX - sBallDiameter) * 0.5f);
            mVerticalBound = ((h / mMetersToPixelsY - sBallDiameter) * 0.5f);
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER)
                return;
            /*
             * record the accelerometer data, the event's timestamp as well as
             * the current time. The latter is needed so we can calculate the
             * "present" time during rendering. In this application, we need to
             * take into account how the screen is rotated with respect to the
             * sensors (which always return data in a coordinate space aligned
             * to with the screen in its native orientation).
             */

            switch (mDisplay.getRotation()) {
                case Surface.ROTATION_0:
                    mSensorX = event.values[0];
                    mSensorY = event.values[1];
                    break;
                case Surface.ROTATION_90:
                    mSensorX = -event.values[1];
                    mSensorY = event.values[0];
                    break;
                case Surface.ROTATION_180:
                    mSensorX = -event.values[0];
                    mSensorY = -event.values[1];
                    break;
                case Surface.ROTATION_270:
                    mSensorX = event.values[1];
                    mSensorY = -event.values[0];
                    break;
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            /*
             * Compute the new position of our object, based on accelerometer
             * data and present time.
             */
            final ParticleSystem particleSystem = mParticleSystem;
            final long now = System.currentTimeMillis();
            final float sx = mSensorX;
            final float sy = mSensorY;

            particleSystem.update(sx, sy, now);

            final float xc = mXOrigin;
            final float yc = mYOrigin;
            final float xs = mMetersToPixelsX;
            final float ys = mMetersToPixelsY;
            /*
             * We transform the canvas so that the coordinate system matches
             * the sensors coordinate system with the origin in the center
             * of the screen and the unit is the meter.
             */
            float x = xc + particleSystem.getBallPosX() * xs;
            float y = yc - particleSystem.getBallPosY() * ys;
            particleSystem.ball.setTranslationX(x);
            particleSystem.ball.setTranslationY(y);

            final int friends = particleSystem.getFFCount();
            for (int i = 0; i < friends; i++) {
                /*
                 * We transform the canvas so that the coordinate system matches
                 * the sensors coordinate system with the origin in the center
                 * of the screen and the unit is the meter.
                 */
                x = xc + particleSystem.getFFPosX(i) * xs;
                y = yc - particleSystem.getFFPosY(i) * ys;
                particleSystem.mFalseFriends[i].setTranslationX(x);
                particleSystem.mFalseFriends[i].setTranslationY(y);
            }

            x = xc + particleSystem.getGoalPosX() * xs;
            y = yc - particleSystem.getGoalPosY() * ys;
            particleSystem.goal.setTranslationX(x);
            particleSystem.goal.setTranslationY(y);

            // and make sure to redraw asap
            invalidate();
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    }
}