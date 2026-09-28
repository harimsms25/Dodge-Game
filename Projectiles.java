package com.example.idekatp;

//import static androidx.appcompat.graphics.drawable.DrawableContainerCompat.Api21Impl.getResources;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

public class Projectiles {
    int image;
    Bitmap projectile;

    int width, height, speed;

    Resources resources;
    float projectileHorSpace;
    float projectileVerSpace;

    public Projectiles(int d, Resources r, int w ,int h, float pHS, float pVS, int s){
        image = d;
        resources = r;
        width = w;
        height = h;
        speed = s;
        projectile = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, image),width,height,false);
        projectileHorSpace = pHS;
        projectileVerSpace = pVS;
    }

    public Bitmap getProjectile(){
        return projectile;
    }

    public float getProjectileHorSpace() {
        return projectileHorSpace;
    }

    public float getProjectileVerSpace() {
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
