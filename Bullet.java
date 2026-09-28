package com.example.idekatp;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Bullet {
    int image;

    Bitmap bullet;

    int width, height, speed;
    Resources resources;
    float projectileHorSpace;
    float projectileVerSpace;

    public Bullet(int d, Resources r, int w, int h, float pHS, float pVS, int s){
        image = d;
        resources = r;
        width = w;
        height = h;
        speed = s;
        bullet = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, image),width,height,false);
        projectileHorSpace = pHS;
        projectileVerSpace = pVS;
    }
    public Bitmap getBullet(){
        return bullet;
    }

    public float getBulletHorSpace() {
        return projectileHorSpace;
    }

    public float getBulletVerSpace() {
        return projectileVerSpace;
    }
    public void MoveMethod(){
        projectileVerSpace += speed;
    }
    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }

}
