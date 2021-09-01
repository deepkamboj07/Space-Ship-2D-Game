package com.game;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BackGround {
    int x=0,y=0;
    Bitmap background;

    BackGround(int screenx,int screeny, Resources res){
        background= BitmapFactory.decodeResource(res,R.drawable.background);
        background= Bitmap.createScaledBitmap(background,screenx,screeny,false);
    }
}
