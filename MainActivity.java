package com.example.idekatp;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements SensorEventListener {



    SensorManager sensorManager;

    Sensor accelerometer, gycroscopeSensor;

    GyroscopeSensorEventListener innerClassListener;

    ConstraintLayout constraintL;

    MediaPlayer mediaPlayer;

    ArrayList<Projectiles> projectilesArrayList;

    SoundPool soundPool;

    ArrayList<Bullet> bullets;

    double xSpeed, ySpeed;

    GameSurface gameSurface;

    int sound1, sound2;

    boolean collided;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        gameSurface = new GameSurface(this);
        setContentView(gameSurface);

        sensorManager= (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gycroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        projectilesArrayList = new ArrayList<>();
        context = this;
        constraintL = findViewById(R.id.main);
        bullets = new ArrayList<>();
        mediaPlayer = MediaPlayer.create(this, R.raw.gamesong);
        mediaPlayer.start();
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        sound1 = soundPool.load(this,R.raw.bulletsoundeffect,1);
        sound2 = soundPool.load(this,R.raw.crashsoundeffect,1);
        //constraintL.setOnClickListener();
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        xSpeed = -1*sensorEvent.values[0];
        if(Math.abs(sensorEvent.values[0]) > 3.75)
            xSpeed = -1*sensorEvent.values[0]*2;
        ySpeed = sensorEvent.values[1]/4;
        if(gameSurface.getBallX() <= 0 && xSpeed < 0)
            xSpeed = 0;
        if(gameSurface.getBallX()+200 >= gameSurface.getScreenWidth() && xSpeed > 0)
            xSpeed = 0;


        //Log.d("check",sensorEvent.values[0]+"");
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {}

    public  class GyroscopeSensorEventListener implements SensorEventListener{

        @Override
        public void onSensorChanged(SensorEvent event) {

        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    }



    public class GameSurface extends SurfaceView implements Runnable{

        Thread gameThread;
        SurfaceHolder holder;
        volatile boolean running = false;
        Bitmap ball, background;

        int lives, score;
        int ballX, ballY;
        Paint paintProperty;
        int screenWidth, screenHeight;

        float ballImageHorizontalSpacing, ballImageVerticalSpacing;
        int width, height;
        float totalFlip = 0f;

        public GameSurface(Context context){
            super(context);
            holder = getHolder();

            background = BitmapFactory.decodeResource(getResources(), R.drawable.starbg);
            lives = 5;
            score = 0;
            width = 200;
            height = 274;
            collided = false;
            ball = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mfalcon),
                    width, height, false);

            Activity activity = (Activity) context;

//            activity.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    //new projectileSpawn().start();
//                    CountDownTimer timer = new CountDownTimer(5000, 1000) {
//                        @Override
//                        public void onTick(long l) {
//
//                        }
//
//                        @Override
//                        public void onFinish() {
//                            Projectiles projectiles = new Projectiles(R.drawable.laserbeam,getResources(),200, 250);
//                            projectilesArrayList.add(projectiles);
//                            new projectileSpawn().run();
//                        }
//                    };
//                    timer.start();
//                }
//            });

            new projectileSpawn().run();



            Display screenDisplay = getWindowManager().getDefaultDisplay();
            Point sizeOfScreen = new Point();
            screenDisplay.getSize(sizeOfScreen);
            screenWidth = sizeOfScreen.x;
            screenHeight = sizeOfScreen.y;

            paintProperty = new Paint();

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {Bullet bullet = new Bullet(R.drawable.bullet,getResources(),223/4, 646/4, gameSurface.getBallX()+71,gameSurface.getBallY(),-75);
                   bullets.add(bullet);
                   soundPool.play(sound1,1,1,1,0,1);
                }
            });
            /// MotionSensors can go here

//            constraintLayout.setOnClickListener(new OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Bullet bullet = new Bullet(R.drawable.bullet,getResources(),223, 646, gameSurface.getBallX(),gameSurface.getBallY(),-25);
//                    bullets.add(bullet);
//                }
//            });

            //constraintLayout.
        }

        @Override
        public void run() {
            Canvas canvas = null;
            Drawable d = getResources().getDrawable(R.drawable.starbg, null);


//            double flipX = 5;

            while(running && lives > 0){
                // if holder is null or invalid, exit loop

                //flipX += 0.1;
                //Log.d("SPEEDVAL",+xSpeed+"");

                if(!holder.getSurface().isValid())
                    continue;
                // lock canvas to make necessary changes
                canvas = holder.lockCanvas(null);


                // resize background drawable to the root View left/top/right/bottom
                d.setBounds(getLeft(), getTop(), getRight(), getBottom());

                // draw the Drawable onto the canvas
                d.draw(canvas);

                // Define the spacing required to accommodate the image and screen size so images don't exceed bounds
                ballImageHorizontalSpacing = (screenWidth / 2.0f) - (ball.getWidth() / 2.0f);
                ballImageVerticalSpacing = (screenHeight / 2.0f) - (ball.getHeight() / 2.0f);

                // draw ball onto Canvas
                canvas.drawBitmap(ball, ballImageHorizontalSpacing + ballX, ballImageVerticalSpacing+700, null);

                for(int i = 0; i < projectilesArrayList.size(); i++){
                    canvas.drawBitmap(projectilesArrayList.get(i).getProjectile(), projectilesArrayList.get(i).getProjectileHorSpace(), projectilesArrayList.get(i).getProjectileVerSpace(), null);
                    projectilesArrayList.get(i).MoveMethod();
                    if(CheckPlayerInteraction(projectilesArrayList.get(i))){
                        projectilesArrayList.remove(i);
                        lives--;
                        collided = true;
                        ball = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.explodeds), width, height, false);
                        postDelayed(() -> {
                            ball = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.mfalcon), width, height, false);
                        }, 3000);
                        soundPool.play(sound2,1,1,1,0,1);
                        //replacingPicture();
                    }
                    else if(projectilesArrayList.get(i).getProjectileVerSpace()+100 >= screenHeight) {
                        projectilesArrayList.remove(i);
                        score++;
                    }
                }
                for(int i = 0; i < bullets.size(); i++){
                    canvas.drawBitmap(bullets.get(i).getBullet(), bullets.get(i).getBulletHorSpace(),bullets.get(i).getBulletVerSpace(), null);
                    bullets.get(i).MoveMethod();
                    for(int j = 0; j < projectilesArrayList.size(); j++){
                        if(CheckBulletInteraction(bullets.get(i),projectilesArrayList.get(j))){
                            bullets.remove(i);
                            projectilesArrayList.remove(j);
                            score+=5;
                        }
                        else if(bullets.get(i).getBulletVerSpace() <= 0){
                            bullets.remove(i);
                        }
                    }
                }


                if(ballX == (int)ballImageHorizontalSpacing && xSpeed >= 0 || ballX == -1 * (int)ballImageHorizontalSpacing && xSpeed <= 0)
                    xSpeed = 0;

                //Log.d("CheckSpeed",xSpeed+"");

                ballX += xSpeed;
                ballY += ySpeed;
                paintProperty.setColor(Color.WHITE);
                paintProperty.setTextSize(64);
                canvas.drawText("Lives: "+lives,screenWidth/2-120,200,paintProperty);
                canvas.drawText("Score: "+score,screenWidth/2-120,400,paintProperty);

                //Log.d("BALLXVALUE",ballX+"");

                if(lives <= 0){
                    paintProperty.setTextSize(100);
                    canvas.drawText("GAME OVER",screenWidth/2-200,screenHeight/2,paintProperty);
                }

                holder.unlockCanvasAndPost(canvas);

            }

        }
        public void resume(){
            running = true;
            gameThread = new Thread(this);
            gameThread.start();

        }

        public void pause(){
            running = false;
            while(true){
                try {
                    gameThread.join();
                } catch (InterruptedException e){
                    e.printStackTrace();
                }

            }
        }
        public float getBallX(){
            return ballImageHorizontalSpacing+ballX;
        }
        public float getBallY(){
            return ballImageVerticalSpacing+700;
        }
        public float getScreenWidth(){
            return screenWidth;
        }
        public float getScreenHeight(){
            return screenHeight;
        }
        public int getBallWidth(){
            return width;
        }
        public int getBallHeight(){
            return  height;
        }

        public void setPlayerImage(int id){
            ball = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), id), width, height, false);
        }
    }
    public class projectileSpawn extends Thread{
        //Activity activity = (Activity) context;

        public void run() {
            CountDownTimer timer = new CountDownTimer(2500, 1000) {
                @Override
                public void onTick(long l) {
                    ///ADD THE THREAD TO THE COLLISION CODE
                }

                @Override
                public void onFinish() {
                    int astNumb = randomAsteroid();
                    int[]dimensions = getDimensions(astNumb);
                    Projectiles projectiles = new Projectiles(astNumb,getResources(),dimensions[0],dimensions[1],(float) (Math.random()*601.0f)+100.0f, 250, 25);
                    projectilesArrayList.add(projectiles);
                    new projectileSpawn().run();
                }
            };
            timer.start();
        }
    }

    public class replacePlayerImage extends Thread{
        //@Override
        public void run() {
                Log.d("TESTINGRUNTHREAD","THREADRUN");
                gameSurface.setPlayerImage(R.drawable.explodeds);
                CountDownTimer timer = new CountDownTimer(2000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        gameSurface.setPlayerImage(R.drawable.mfalcon);
                        collided = false;
                    }
                };
                timer.start();
        }
            //Log.d("TESTINGRUNTHREAD","THREADRUN");
    }

//    public void replacingPicture(){
//        gameSurface.setPlayerImage(R.drawable.explodeds);
//            CountDownTimer timer = new CountDownTimer(2000, 1000) {
//                @Override
//                public void onTick(long millisUntilFinished) {
//
//                }
//
//                @Override
//                public void onFinish() {
//                    gameSurface.setPlayerImage(R.drawable.mfalcon);
//                }
//            };
//            timer.start();
//    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        sensorManager.unregisterListener(innerClassListener);
        gameSurface.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_NORMAL);
        innerClassListener = new GyroscopeSensorEventListener();
        sensorManager.registerListener(innerClassListener, gycroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        gameSurface.resume();
    }

    public boolean CheckPlayerInteraction(Projectiles projectile){
//        if(projectile.getProjectileVerSpace()+projectile.getHeight() <  gameSurface.getBallY()+gameSurface.getBallWidth() && projectile.getProjectileVerSpace()+projectile.getHeight() > gameSurface.getBallY()){
//            if(projectile.getProjectileHorSpace()+projectile.getWidth() < gameSurface.getBallX()+gameSurface.getBallWidth() && projectile.getProjectileHorSpace()+projectile.getWidth()  > gameSurface.getBallX()){
//                return true;
//            }
//            else if(projectile.getProjectileHorSpace() < gameSurface.getBallX()+gameSurface.getBallWidth() && projectile.getProjectileHorSpace() > gameSurface.getBallX()){
//                return true;
//            }
//        }
        float legWidthMax = gameSurface.getBallWidth()/2.0f;
        float legHeightMax = gameSurface.getBallHeight()/2.0f;

        double maxDistanceBetweenCollision = Math.sqrt(legWidthMax*legWidthMax+legHeightMax*legHeightMax);

        float PlayerMidX = gameSurface.getBallX()+gameSurface.getBallWidth()/2.0f;
        float PlayerMidY = gameSurface.getBallY()+gameSurface.getBallHeight()/2.0f;

        float ProjectileMidX = projectile.getProjectileHorSpace()+projectile.getWidth()/2.0f;
        float ProjectileMidY = projectile.getProjectileVerSpace()+projectile.getHeight()/2.0f;

        float legWidthCurrent = Math.abs(PlayerMidX-ProjectileMidX);
        float legHeightCurrent = Math.abs(PlayerMidY-ProjectileMidY);

        double distanceBetween = Math.sqrt(legWidthCurrent*legWidthCurrent + legHeightCurrent*legHeightCurrent);

        return distanceBetween < maxDistanceBetweenCollision;
    }

    public boolean CheckBulletInteraction(Bullet bullet, Projectiles projectiles){
//        if(bullet.getBulletVerSpace() < projectiles.getProjectileVerSpace()+ projectiles.getHeight() && bullet.getBulletVerSpace() > projectiles.getProjectileVerSpace()){
//            if(bullet.getBulletHorSpace()+bullet.getWidth() < projectiles.getProjectileHorSpace()+projectiles.getWidth() && bullet.getBulletHorSpace()+bullet.getWidth() > projectiles.getProjectileHorSpace()){
//                return true;
//            }
//            if(bullet.getBulletHorSpace() < projectiles.getProjectileHorSpace()+projectiles.getWidth() && bullet.getBulletHorSpace() > projectiles.getProjectileHorSpace()){
//                return true;
//            }
//        }

        float legWidthMax = projectiles.getWidth()/2.0f;
        float legHeightMax = projectiles.getHeight()/2.0f;

        double maxDistanceBetweenCollision = Math.sqrt(legWidthMax*legWidthMax+legHeightMax*legHeightMax);

        float PlayerMidX = projectiles.getProjectileHorSpace()+projectiles.getWidth()/2.0f;
        float PlayerMidY = projectiles.getProjectileVerSpace()+projectiles.getHeight()/2.0f;

        float BulletMidX = bullet.getBulletHorSpace()+bullet.getWidth()/2.0f;
        float BulletMidY = bullet.getBulletVerSpace()+bullet.getHeight()/2.0f;

        float legWidthCurrent = Math.abs(PlayerMidX-BulletMidX);
        float legHeightCurrent = Math.abs(PlayerMidY-BulletMidY);

        double distanceBetween = Math.sqrt(legWidthCurrent*legWidthCurrent + legHeightCurrent*legHeightCurrent);

        return distanceBetween < maxDistanceBetweenCollision;
    }


    public int randomAsteroid(){
        int n = (int)(Math.random()*4)+1;
        if(n==1){
            return R.drawable.asteroid1simped;
        }
        if(n==2){
            return R.drawable.asteroid2simpled;
        }
        if(n==3){
            return R.drawable.asteroid3simpled;
        }
        if(n==4){
            return R.drawable.asteroid4simpled;
        }
        return 0;
    }
    public int[] getDimensions(int n){
        int[]array = new int[2];
        if(n == R.drawable.asteroid1simped){
            array[0] = 451/2;
            array[1] = 365/2;
        }
        if(n == R.drawable.asteroid2simpled){
            array[0] = 225/2;
            array[1] = 225/2;
        }
        if(n == R.drawable.asteroid3simpled){
            array[0] = 236/2;
            array[1] = 213/2;
        }
        if(n == R.drawable.asteroid4simpled){
            array[0] = 225/2;
            array[1] = 225/2;
        }
        return array;
    }

}