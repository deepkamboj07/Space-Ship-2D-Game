package com.game;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.view.MotionEvent;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameView extends SurfaceView implements Runnable {

    private Thread thread;
    private boolean isPlaying,isGameOver=false;
    private int screenX,screenY,score=0;
    private BackGround backGround1,backGround2;
    private Paint paint;
    private Stone[] stones;
    private SharedPreferences preferences;
    private Random random;
    private SoundPool soundPool;
    private int sound;
    private Flight flight;
    private GameActivity gameActivity;
    private List<Bullet> bulletList;
    public static float screenRationX,screenRationY;
    public GameView(GameActivity gameActivity,int screenX,int screenY)
    {
        super(gameActivity);
        this.gameActivity=gameActivity;

        preferences=gameActivity.getSharedPreferences("game",Context.MODE_PRIVATE);

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            AudioAttributes audioAttributes= new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .build();

            soundPool=new SoundPool.Builder().setAudioAttributes(audioAttributes).build();
        }else
            soundPool=new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        sound=soundPool.load(gameActivity,R.raw.shoot,1);

        this.screenX=screenX;
        this.screenY=screenY;

        screenRationX=2340f/screenX;
        screenRationY=1080f/screenY;
        backGround1= new BackGround(screenX,screenY,getResources());
        backGround2= new BackGround(screenX,screenY,getResources());

        flight=new Flight(this,screenY,getResources());

        bulletList=new ArrayList<>();

        backGround2.x=screenX;
        paint=new Paint();
        paint.setTextSize(128);paint.setColor(Color.WHITE);

        stones=new Stone[4];

        for(int i=0;i<4;i++){
            Stone stone= new Stone(getResources());
            stones[i]=stone;
        }
        random= new Random();
    }


    @Override
    public void run() {
        while(isPlaying)
        {
            update();
            draw();
            try {
                sleep();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void update(){
        backGround1.x-=10*screenRationX;
        backGround2.x-=10*screenRationX;

        if(backGround1.x+backGround1.background.getWidth()<0){
            backGround1.x=screenX;

        }
        if(backGround2.x+backGround2.background.getWidth()<0){
            backGround2.x=screenX;

        }
        if(flight.isGoingUp){
            flight.y-=30*screenRationY;

        }
        else
            flight.y+=30*screenRationY;
        if(flight.y<0)
            flight.y=0;
        if(flight.y>screenY-flight.height)
            flight.y=screenY-flight.height;

        List<Bullet> trash= new ArrayList<>();

        for (Bullet bullet:bulletList){

            if(bullet.x>screenX)
                trash.add(bullet);

            bullet.x+=50*screenRationX;

            for(Stone stone:stones){
                if(Rect.intersects(stone.getCollisionShape(),bullet.getCollisionShape())){
                    score++;
                    stone.x=-500;
                    bullet.x=screenX+500;
//                    stone.wasShot=true;
                }
            }

        }
        for(Bullet bullet:trash){
            bulletList.remove(bullet);
        }

        for(Stone stone:stones){

            stone.x-=stone.speed;
            if(stone.x + stone.width < 0){

//                if(!stone.wasShot){
//                    isGameOver=true;
//                    return;
//                }

                int bound=(int)(30*screenRationX);
                stone.speed=random.nextInt(bound);

                if(stone.speed<10 * screenRationX)
                    stone.speed= (int) (10*screenRationX);

                stone.x=screenX;
                stone.y=random.nextInt(screenY-stone.height);

//                stone.wasShot=false;
            }

            if(Rect.intersects(stone.getCollisionShape(),flight.getCollisionShape())){
                isGameOver=true;
                return;
            }
        }

    }
    private void draw(){

        if(getHolder().getSurface().isValid()){
            Canvas canvas=getHolder().lockCanvas();
            canvas.drawBitmap(backGround1.background,backGround1.x,backGround1.y,paint);
            canvas.drawBitmap(backGround2.background,backGround2.x,backGround2.y,paint);

            for(Stone stone : stones){
                canvas.drawBitmap(stone.getStone(),stone.x,stone.y,paint);
            }

            canvas.drawText(score+ "",screenX/2f,164,paint);

            if(isGameOver){
                isPlaying=false;
                canvas.drawBitmap(flight.getDead(),flight.x,flight.y,paint);
                getHolder().unlockCanvasAndPost(canvas);

                saveIfHighScore();
                waitBeforeExist();

                return;
            }



            canvas.drawBitmap(flight.getFlight(),flight.x,flight.y,paint);

            for(Bullet bullet:bulletList){
                canvas.drawBitmap(bullet.bullet,bullet.x,bullet.y,paint);
            }

            getHolder().unlockCanvasAndPost(canvas);
        }

    }

    private void waitBeforeExist() {
        try {
            Thread.sleep(1000);
            gameActivity.startActivity(new Intent(gameActivity,MainActivity.class));
            gameActivity.finish();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void saveIfHighScore() {

        if(preferences.getInt("highscore",0) < score){
            SharedPreferences.Editor editor=preferences.edit();
            editor.putInt("highscore",score);
            editor.apply();

        }

    }

    private void sleep() throws InterruptedException {
        Thread.sleep(13);
    }

    public void resume()
    {
        isPlaying=true;
        thread= new Thread(this);
        thread.start();

    }

    public void pause() throws InterruptedException {
        isPlaying=false;
        thread.join();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                if(event.getX()<screenX/2) {
                  flight.isGoingUp=true;
                }
                break;
            case MotionEvent.ACTION_UP:
                flight.isGoingUp=false;

                if(event.getX() > screenX/2)
                    flight.toShoot++;
                break;
        }
        return true;
    }

    public void newBullet() {

        if(!preferences.getBoolean("isMute",false)){
            soundPool.play(sound,1,0,0,1,1);
        }

        Bullet bullet= new Bullet(getResources());
        bullet.x=flight.x+flight.width;
        bullet.y=flight.y+(flight.height/2);
        bulletList.add(bullet);
    }

}
