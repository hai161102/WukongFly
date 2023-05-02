package com.haiprj.haigame.wukongfly.gamemodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.RectF;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.annotation.RequiresApi;

import com.haiprj.haigame.wukongfly.Const;
import com.haiprj.haigame.wukongfly.R;
import com.haiprj.haigame.wukongfly.base.enums.MediaEnum;
import com.haiprj.haigame.wukongfly.base.utils.GameSharePreference;
import com.haiprj.haigame.wukongfly.base.widget.BaseEntity;
import com.haiprj.haigame.wukongfly.base.widget.GameMedia;

@SuppressWarnings("FieldCanBeLocal")
public class PlayerManager extends BaseEntity {

    @SuppressLint("StaticFieldLeak")
    private static PlayerManager instance;
    private final float ANi_FPS = 6;
    private final Runnable runnable = new Runnable() {
        @SuppressWarnings("BusyWait")
        @Override
        public void run() {
            int count = 0;
            double drawInterval = 1000000000f / ANi_FPS;
            double nextTime = System.nanoTime() + drawInterval;
            while (thread != null) {
                double remainTime = nextTime - System.nanoTime();
                remainTime /= 1000000;

                count ++;
                animationCount = count % bitmaps.length;
                if (count >= 999) {
                    count = 0;
                }

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
    protected Context context;
    protected float playerSpeed;
    protected float speedDown;
    public float score = 0;

    protected Bitmap[] bitmaps;

    private Thread thread;
    protected int animationCount = 0;
    protected float beforeWorldX = 0;

    protected int color;
    public Paint textPain;
    private Paint testPaint;

    private Paint playerPaint;

    private MediaPlayer flyMedia;
    private boolean isFlySoundReady = true;

    private final int GRAVITY = 1;
    private float jumpLength;

    @SuppressLint("NewApi")
    private PlayerManager(Context context, String name, int[] imageId, int width, int height, float x, float y, float playerSpeed, int color) {
        super(name, imageId, width, height, x, y);
        this.context = context;
        this.playerSpeed = playerSpeed;
        this.color = color;
        playerPaint = new Paint();
        ColorFilter colorFilter = null;
        if (this.color != -1) {
            colorFilter = new PorterDuffColorFilter(this.color, PorterDuff.Mode.SRC_ATOP);
        }
        if (colorFilter != null) {
            playerPaint.setColorFilter(colorFilter);
        }
        bitmaps = new Bitmap[imageId.length];
        for (int i = 0; i < imageId.length; i++) {
            Bitmap b = BitmapFactory.decodeResource(context.getResources(), imageId[i]);
            bitmaps[i] = Bitmap.createScaledBitmap(b, this.getWidth(), this.getHeight(), true);
        }
        startAnimation();
        beforeWorldX = this.worldX;
        textPain = new Paint();
        textPain.setColor(Color.WHITE);
        textPain.setTypeface(context.getResources().getFont(R.font.poppins_medium));
        textPain.setTextSize(50);
        textPain.setShadowLayer(10f, 10f, 10f, Color.BLUE);

        testPaint = new Paint();
        testPaint.setColor(Color.RED);
        testPaint.setStyle(Paint.Style.FILL);
    }
    private PlayerManager(Context context) {
        this.context = context;
    }

    public static PlayerManager getInstance(Context context, String name, int[] imageId, int width, int height, float x, float y, float playerSpeed, int color) {
        if (instance == null) instance = new PlayerManager(context, name, imageId, width, height, x, y, playerSpeed, color);
        return instance;
    }

    public static PlayerManager getInstance(Context context) {
        if (instance == null) instance = new PlayerManager(context);
        return instance;
    }

    private void startAnimation() {
        thread = new Thread(runnable);
        thread.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setMediaPlayer() {
        flyMedia = MediaPlayer.create(this.context, R.raw.fly_sound);
        flyMedia.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                flyMedia.seekTo(0);
                isFlySoundReady = true;
            }
        });
    }

    public float getPlayerSpeed() {
        return playerSpeed;
    }

    public void setPlayerSpeed(float playerSpeed) {
        this.playerSpeed = playerSpeed;
    }

    public float getJumpLength() {
        return jumpLength;
    }

    public void setJumpLength(float jumpLength) {
        this.jumpLength = jumpLength;
    }

    public void update(){
        this.worldX += playerSpeed;

        this.speedDown += GRAVITY;
        this.y += this.speedDown;
    }


    @Override
    public void setY(float y) {
        super.setY(y);

    }

    public void jump() {
        speedDown = -jumpLength;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //playFlySound();
            if (GameSharePreference.getInstance().getBoolean(Const.SOUND_CHECK_KEY, false) && isFlySoundReady) {
                isFlySoundReady = false;
                flyMedia.start();
            }
        }
    }

    public void goStrait() {
        playerSpeed *= 2f;
    }

    public void endFlash() {
        playerSpeed *= 2f;
    }
    @Override
    public void setImageId(int[] imageId) {
        super.setImageId(imageId);
        bitmaps = new Bitmap[imageId.length];
        for (int i = 0; i < imageId.length; i++) {
            Bitmap b = BitmapFactory.decodeResource(context.getResources(), imageId[i]);
            bitmaps[i] = Bitmap.createScaledBitmap(b, this.getWidth(), this.getHeight(), true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void playFlySound() {
        GameMedia.getInstance(context).playSong(MediaEnum.FLY_SONG);
    }
    public void resetSound(){
        GameMedia.getInstance(context).getMediaPlayerAt(MediaEnum.FLY_SONG).seekTo(0);
    }

    public void stopSound(){
        GameMedia.getInstance(context).stopSong(MediaEnum.FLY_SONG);
    }

    public void releaseSound(){
        flyMedia.release();
    }

    public Bitmap rotateBitmap(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, this.width, this.height, matrix, true);
    }

    @SuppressLint("DefaultLocale")
    public void draw(Canvas canvas) {
        //canvas.drawRect(getRectF(), testPaint);
        canvas.drawBitmap(bitmaps[animationCount], getX(), getY(), playerPaint);
        if (worldX - beforeWorldX >= 1000f) {
            playerSpeed += 0.2f;
            beforeWorldX = worldX;
        }
    }

    public void onGameOver() {
        float heightScore = GameSharePreference.getInstance().getFloat(Const.HIGHEST_SCORE_KEY, 0);
        if (heightScore < score) {
            GameSharePreference.getInstance().setFloat(Const.HIGHEST_SCORE_KEY, score);
        }
        instance = null;
    }

    @Override
    public RectF getRectF() {
        RectF rectF = super.getRectF();
        rectF.left += this.getWidth() / 4f;
        rectF.right -= this.getWidth() / 4f;
        return rectF;
    }
}
