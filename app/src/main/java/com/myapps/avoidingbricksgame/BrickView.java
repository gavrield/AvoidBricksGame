package com.myapps.avoidingbricksgame;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.view.View;

import static android.graphics.Color.rgb;


public class BrickView extends View implements ChangeListener{

    public static long DUR = 6000;
    private ChangeListener changeListener;
    private ObjectAnimator brickAnimation;
    public BrickView(Context c) {
        super(c);
        setMeasuredDimension(1, 1);
        this.setBackgroundColor(rgb(128,0,0));

        brickAnimation = ObjectAnimator.ofFloat
                (this, "Y", -80f, 2000f);
        brickAnimation.setDuration(DUR);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 300;
        int desiredHeight = 200;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        //Measure Width
        if (widthMode == MeasureSpec.EXACTLY) {
            //Must be this size
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            width = Math.min(desiredWidth, widthSize);
        } else {
            //Be whatever you want
            width = desiredWidth;
        }
        //Measure Height
        if (heightMode == MeasureSpec.EXACTLY) {
            //Must be this size
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            //Can't be bigger than...
            height = Math.min(desiredHeight, heightSize);
        } else {
            //Be whatever you want
            height = desiredHeight;
        }

        //MUST CALL THIS
        setMeasuredDimension(width, height);
    }

    public ObjectAnimator getBrickAnimation() {
        return brickAnimation;
    }

    @Override
    public void stateChanged(LivesEvent e) {
        if (e.getNumLivesChange() == 0){
            this.setVisibility(INVISIBLE);
        }


    }
}
