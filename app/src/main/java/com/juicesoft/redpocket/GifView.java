package com.juicesoft.redpocket;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by jting on 2017/1/23.
 */

public class GifView extends View {
    private static final int DEFAULT_MOVIEW_DURATION = 1000;

    private int mMovieResourceId;
    private Movie mMovie;

    private long mMovieStart = 0;
    private int mCurrentAnimationTime = 0;

    @SuppressLint("NewApi")
    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);

        /**
         * Starting from HONEYCOMB have to turn off HardWare acceleration to draw
         * Movie on Canvas.
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setImageResource(int mvId){
        this.mMovieResourceId = mvId;
        mMovie = Movie.decodeStream(getResources().openRawResource(mMovieResourceId));
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        Log.e("140117", "onMeasure()"+widthMeasureSpec+"||"+heightMeasureSpec);
//        if(mMovie != null){
//            setMeasuredDimension(mMovie.width(), mMovie.height());
//        }else{
//            setMeasuredDimension(getSuggestedMinimumWidth(), getSuggestedMinimumHeight());
//
//        }
        super.onMeasure(
                MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(heightMeasureSpec, MeasureSpec.EXACTLY));


    }




    @Override
    protected void onDraw(Canvas canvas) {
        canvas.scale((float)this.getWidth() / (float)mMovie.width(),(float)this.getHeight() /      (float)mMovie.height());
        if (mMovie != null){
            updateAnimtionTime();
            drawGif(canvas);
            invalidate();
        }else{
            drawGif(canvas);
        }
    }

    private void updateAnimtionTime() {
        long now = android.os.SystemClock.uptimeMillis();

        if (mMovieStart == 0) {
            mMovieStart = now;
        }
        int dur = mMovie.duration();
        if (dur == 0) {
            dur = DEFAULT_MOVIEW_DURATION;
        }
        mCurrentAnimationTime = (int) ((now - mMovieStart) % dur);
    }

    private void drawGif(Canvas canvas) {
        mMovie.setTime(mCurrentAnimationTime);
        mMovie.draw(canvas, 0, 0);
        canvas.restore();
    }

}
