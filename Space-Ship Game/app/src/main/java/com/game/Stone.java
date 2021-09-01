package com.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import static com.game.GameView.screenRationX;
import static com.game.GameView.screenRationY;

public class Stone {
    public int speed=20;
    public boolean wasShot=true;
    int x=0,y,width,height,stoneCounter=1;
    Bitmap stone1,stone2,stone3,stone4;

    Stone(Resources res){

        stone1= BitmapFactory.decodeResource(res,R.drawable.stone1);
        stone2= BitmapFactory.decodeResource(res,R.drawable.stone1);
        stone3= BitmapFactory.decodeResource(res,R.drawable.stone1);
        stone4= BitmapFactory.decodeResource(res,R.drawable.stone1);

        width=stone1.getWidth();
        height=stone1.getHeight();

        width/=6;
        height/=6;

        width=(int)(width*screenRationX);
        height=(int)(height*screenRationY);

        stone1=Bitmap.createScaledBitmap(stone1,width,height,false);
        stone2=Bitmap.createScaledBitmap(stone2,width,height,false);
        stone3=Bitmap.createScaledBitmap(stone3,width,height,false);
        stone4=Bitmap.createScaledBitmap(stone4,width,height,false);

        y=-height;

    }

    Bitmap getStone(){

        if(stoneCounter==1){
            stoneCounter++;
            return stone1;
        }
        if(stoneCounter==2){
            stoneCounter++;
            return stone2;
        }
        if(stoneCounter==3){
            stoneCounter++;
            return stone3;
        }
        stoneCounter=1;
        return  stone4;
    }

    Rect getCollisionShape(){
        return new Rect(x,y,x+width,y+height);
    }
}
