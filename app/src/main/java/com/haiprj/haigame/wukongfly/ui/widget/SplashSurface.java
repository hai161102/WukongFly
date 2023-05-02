package com.haiprj.haigame.wukongfly.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

import androidx.annotation.RequiresApi;

import com.haiprj.haigame.wukongfly.R;
import com.haiprj.haigame.wukongfly.base.widget.BaseGameSurface;

public class SplashSurface extends BaseGameSurface {

    private float loadLineX = 0f;
    private float loadLineY = 0f;

    private Bitmap loadingBitmap;
    private Bitmap logoBitmap;
    private RectF lineShape;
    private Paint linePaint;
    private Paint textPaint;

    private String warnText;
    private String loadingText;
    private float maxWidth = 0f;

    private float speedLoading = 3f;
    private LineChangeListener lineChangeListener;
    private boolean isPortrait;

    public void setLineChangeListener(LineChangeListener lineChangeListener) {
        this.lineChangeListener = lineChangeListener;
    }

    public SplashSurface(Context context) {
        super(context);
    }

    public SplashSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SplashSurface(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public SplashSurface(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void init() {
        super.init();
        isPortrait = getContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        setBackgroundColor(getContext().getColor(R.color.background_color));
        if (isPortrait) {
            this.setBackgroundResource(R.drawable.app_bg_portrait);
        }else
            this.setBackgroundResource(R.drawable.app_bg);
        lineShape = new RectF();
        linePaint = new Paint();
        loadingBitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.cdv);
        logoBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo_splash);
        linePaint.setColor(Color.RED);
        linePaint.setStyle(Paint.Style.FILL);
        linePaint.setShadowLayer(5f, 1f, 1f, Color.BLACK);

        textPaint = new Paint();
        textPaint.setTypeface(getContext().getResources().getFont(R.font.poppins_medium));
        textPaint.setColor(getContext().getColor(R.color.white));
        textPaint.setTextSize(24);

        warnText = getContext().getString(R.string.this_game_can_have_ads);
        loadingText = getContext().getString(R.string.loadingText) + 0 + "%";
    }

    @Override
    protected void initView(SurfaceHolder surfaceHolder) {
        loadLineX = 0f;
        loadLineY = getHeight() - getHeight() / 20f;
        maxWidth = getWidth();
        lineShape.left = loadLineX;
        lineShape.top = loadLineY;
        lineShape.right = 0f;
        lineShape.bottom = loadLineY + 10f;
        loadingBitmap = Bitmap.createScaledBitmap(loadingBitmap, 50, 50, true);
        logoBitmap = Bitmap.createScaledBitmap(logoBitmap, getHeight() / 4, getHeight() / 4, true);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void update() {
        if (maxWidth != 0 && lineShape.right >= maxWidth){
            if (lineChangeListener != null) {
                lineChangeListener.onMax();
            }
            return;
        }
        if (lineChangeListener != null) {
            lineChangeListener.onChange(lineShape.right, maxWidth);
        }
        lineShape.right += speedLoading;
        loadingText = getContext().getString(R.string.loadingText) + String.format("%.2f", (lineShape.right * 100 / maxWidth)) + "%";
    }

    @Override
    protected void gameDraw(Canvas canvas) {
        canvas.drawRect(lineShape, linePaint);
        canvas.drawBitmap(loadingBitmap, lineShape.right - loadingBitmap.getWidth() / 2f, lineShape.centerY() - loadingBitmap.getHeight() / 2f, null);
        canvas.drawText(warnText, 20f, lineShape.top - 20f, textPaint);
        canvas.drawBitmap(logoBitmap, getWidth() / 2f - logoBitmap.getWidth() / 2f, getHeight() / 2f - logoBitmap.getHeight() / 2f, null);
        canvas.drawText(loadingText, 20f, lineShape.bottom + 20f, textPaint);

    }

    public interface LineChangeListener {
        void onChange(float current, float max);
        void onMax();
    }
}
