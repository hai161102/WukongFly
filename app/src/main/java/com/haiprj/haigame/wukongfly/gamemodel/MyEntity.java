package com.haiprj.haigame.wukongfly.gamemodel;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;

import com.haiprj.haigame.wukongfly.R;
import com.haiprj.haigame.wukongfly.base.interfaces.EntityListener;

public class MyEntity {

    private final Context context;
    private final Bitmap[] bitmaps;
    private final RectF[] doubleRectF;
    private final float worldX;
    private float drawWorldX;
    private final int width;
    private final int height;
    private final float spaceHeight;
    private final float centerSpawn;
    private float fakeCenterSpawn;
    private final int maxWidth;
    private final int maxHeight;
    private final PlayerManager playerManager;
    private final EntityListener listener;
    private final Paint testPaint;
    private boolean isDif;
    private Thread thread;
    private final int ANi_FPS = 6;
    private final Runnable runnable = new Runnable() {
        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            double drawInterval = 1000000000f / ANi_FPS;
            double nextTime = System.nanoTime() + drawInterval;
            while (thread != null) {
                double remainTime = nextTime - System.nanoTime();
                remainTime /= 1000000;
                updateHard();
//                if (viewRectF.get(0).getRectF().bottom < 0)
//                    viewRectF.setY(viewRectF.getY() - playerManager.playerSpeed / 2);
//                else viewRectF.setY(viewRectF.getY() + playerManager.playerSpeed / 2);

                try {
                    if (remainTime < 0) {
                        remainTime = 0;
                    }

                    Thread.sleep((long) remainTime);
                    nextTime += drawInterval;
                } catch (InterruptedException ignored) {
                    thread.interrupt();
                }
            }
        }
    };
    private boolean isHard;

    public void setHard(boolean hard) {
        isHard = hard;
        if (isHard) {
            if (thread == null) {
                thread = new Thread(runnable);
            }
            thread.start();
        }
        else {
            thread = null;
        }
    }

    public MyEntity(Context context, int width, int height, float spaceHeight, float centerSpawn, float x, int maxWidth, int maxHeight, PlayerManager playerManager, EntityListener entityListener) {
        this.context = context;
        this.width = width;
        this.height = height;
        this.spaceHeight = spaceHeight;
        this.centerSpawn = centerSpawn;
        this.fakeCenterSpawn = this.centerSpawn;
        this.worldX = x;
        this.maxWidth = maxWidth;
        this.maxHeight = maxHeight;
        this.playerManager = playerManager;
        this.listener = entityListener;
        this.drawWorldX = worldX;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.col);
        bitmap = Bitmap.createScaledBitmap(bitmap, this.width, this.height, true);
        bitmaps = new Bitmap[] {
                bitmap,
                bitmap
        };
        float divider = spaceHeight / 2f;
        doubleRectF = new RectF[2];
        doubleRectF[0] = new RectF();
        doubleRectF[1] = new RectF();

        setDataRect();

        testPaint = new Paint();
        testPaint.setStyle(Paint.Style.FILL);
        testPaint.setColor(Color.RED);

        thread = new Thread(runnable);
    }

    private void setDataRect() {
        doubleRectF[0].left = drawWorldX + 5f;
        doubleRectF[0].top = fakeCenterSpawn - spaceHeight / 2 - this.height + 5f;
        doubleRectF[0].right = doubleRectF[0].left + this.width - 10f;
        doubleRectF[0].bottom = doubleRectF[0].top + this.height - 10f;

        doubleRectF[1].left = doubleRectF[0].left;
        doubleRectF[1].top = doubleRectF[0].bottom + this.spaceHeight + 10f;
        doubleRectF[1].right = doubleRectF[0].right;
        doubleRectF[1].bottom = doubleRectF[1].top + this.height - 10f;
    }


    public void draw(Canvas canvas) {


        for (int i = 0; i < bitmaps.length; i++) {
            //canvas.drawRect(doubleRectF[i], testPaint);
            canvas.drawBitmap(bitmaps[i], doubleRectF[i].left, doubleRectF[i].top, null);
        }


    }

    public boolean hasIntersectionX() {
        return this.getEntityRectF().intersect(playerManager.getRectF());
    }

    public RectF getEntityRectF() {
        return new RectF(
                this.doubleRectF[0].left,
                this.doubleRectF[0].top,
                this.doubleRectF[0].right,
                this.doubleRectF[1].bottom
        );
    }
    public boolean hasIntersectionEntity() {
        RectF t = new RectF(doubleRectF[0]);
        RectF b = new RectF(doubleRectF[1]);

        if (t.intersect(playerManager.getRectF())) {
            Log.d("checkIntersection", "hasIntersectionEntity: on Top");
            return true;
        }
        if (b.intersect(playerManager.getRectF())) {
            Log.d("checkIntersection", "hasIntersectionEntity: on Bottom");
            return true;
        }
        if (playerManager.getRectF().bottom > maxHeight) {
            Log.d("checkIntersection", "drop down: on Bottom");
            return true;
        }

        if (playerManager.getRectF().top < 0) {
            Log.d("checkIntersection", "fly up: on Top");
            return true;
        }
        return false;
    }

    public float getCenterSpawn() {
        return this.fakeCenterSpawn;
    }
    public float getWorldX() {
        return worldX;
    }

    public float getGoodResumePlace() {
        return fakeCenterSpawn - spaceHeight / 2f + playerManager.getHeight();
    }

    public void update() {
        this.drawWorldX = this.worldX - playerManager.worldX;
        setDataRect();
        if (this.drawWorldX <= - (this.maxWidth / 4f + this.width + 20)) {
            listener.onRemove();
        }
    }

    float speedChangeHard;
    private void updateHard() {
        if (fakeCenterSpawn < this.centerSpawn + 60f){
            speedChangeHard = 2;
        }
        else if (fakeCenterSpawn > this.centerSpawn - 60f) {
            speedChangeHard = -2;
        }
        fakeCenterSpawn += speedChangeHard;
    }
}
