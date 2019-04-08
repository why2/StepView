package com.shida.stepview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.Nullable;


/**
 *
 */
public class StepView extends View {
    private int totalStep;//total step
    private int curStep;//current step
    private int cirRadius;//circle radius
    private int lineWidth;//line widthOrHeight
    private int lineHeight;//line height
    private int circleLineSpace;//Round wire spacing
    private boolean horizontal = true;
    private boolean commonOrder = true;// is true left to bottom or top to bottom
    private int widthOrHeight;
    private int pastStepColor;// past color
    private int unpPastStepColor;// un past color
    private Paint pastPaint, unPastPaint;
    private static final int HORIZONTAL_LEFT_TO_RIGHT = 0;
    private static final int VERTICAL_TOP_TO_BOTTOM = 1;
    private static final int HORIZONTAL_RIGHT_TO_LEFT = 2;
    private static final int VERTICAL_BOTTOM_TO_TOP = 3;

    public StepView(Context context) {
        this(context, null);
    }

    public StepView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getValue(context, attrs);
        pastPaint = new Paint();
        pastPaint.setAntiAlias(true);
        pastPaint.setColor(pastStepColor);
        unPastPaint = new Paint();
        unPastPaint.setAntiAlias(true);
        unPastPaint.setColor(unpPastStepColor);
        pastPaint.setStrokeWidth(lineHeight);
        unPastPaint.setStrokeWidth(lineHeight);
        checkValue();
    }

    /**
     * check value
     */
    private void checkValue() {
        if (curStep < 1) {
            throw new RuntimeException("The current step count is greater than 1!");
        }
        if (totalStep % 2 == 0) {
            throw new RuntimeException("It takes an odd number of steps!");
        }
        if (lineHeight > cirRadius * 2) {
            throw new RuntimeException("A line is bigger than a circle?");
        }
        int screenHeight = getResources().getDisplayMetrics().heightPixels;
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        if (widthOrHeight > screenWidth || widthOrHeight > screenHeight) {
            throw new RuntimeException("It's a little wide!");
        }
    }


    /**
     * get value
     *
     * @param context
     * @param attrs
     */
    private void getValue(Context context, @Nullable AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.StepView);
        totalStep = typedArray.getInteger(R.styleable.StepView_total_step, 7);
        curStep = typedArray.getInteger(R.styleable.StepView_current_step, 1);
        cirRadius = typedArray.getDimensionPixelSize(R.styleable.StepView_circle_radius, dp2px(context, 5));
        lineWidth = typedArray.getDimensionPixelSize(R.styleable.StepView_line_width, dp2px(context, 30));
        lineHeight = typedArray.getDimensionPixelSize(R.styleable.StepView_line_height, dp2px(context, 1));
        circleLineSpace = typedArray.getDimensionPixelSize(R.styleable.StepView_circle_line_space, dp2px(context, 10));
        int orientation = typedArray.getInteger(R.styleable.StepView_orientation, HORIZONTAL_LEFT_TO_RIGHT);
        horizontal = orientation == HORIZONTAL_LEFT_TO_RIGHT || orientation == HORIZONTAL_RIGHT_TO_LEFT;
        commonOrder = orientation == HORIZONTAL_LEFT_TO_RIGHT || orientation == VERTICAL_TOP_TO_BOTTOM;
        pastStepColor = typedArray.getColor(R.styleable.StepView_past_step_color, Color.parseColor("#D95454"));
        unpPastStepColor = typedArray.getColor(R.styleable.StepView_un_past_step_color, Color.parseColor("#19000000"));
        typedArray.recycle();
        int circleSize = (totalStep + 1) / 2;
        int lineSize = (totalStep - 1) / 2;
        widthOrHeight = (circleSize) * cirRadius * 2 + lineSize * lineWidth + lineSize * 2 * circleLineSpace;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (horizontal) {
            setMeasuredDimension(widthOrHeight, cirRadius * 2);
        } else {
            setMeasuredDimension(cirRadius * 2, widthOrHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (int i = 1; i <= totalStep; i++) {
            drawShape(canvas, i);
        }
    }


    /**
     * draw shape
     *
     * @param canvas
     * @param i
     */
    private void drawShape(Canvas canvas, int i) {
        if (commonOrder) {
            if (i <= curStep) {
                drawCircleOrLine(canvas, pastPaint, i);
            } else {
                drawCircleOrLine(canvas, unPastPaint, i);
            }
        } else {
            if (totalStep - i < curStep) {
                drawCircleOrLine(canvas, pastPaint, i);
            } else {
                drawCircleOrLine(canvas, unPastPaint, i);
            }
        }
    }

    /**
     * Draw a line or circle
     *
     * @param canvas
     * @param paint
     * @param step   current step
     */
    private void drawCircleOrLine(Canvas canvas, Paint paint, int step) {
        if (step % 2 == 0) {
            int circleSize = step / 2;
            int lineSize = circleSize - 1;
            int lineSpaceSize = lineSize * 2 + 1;
            int startXOrY = circleSize * cirRadius * 2 + lineSize * lineWidth + lineSpaceSize * circleLineSpace;
            if (horizontal) {
                canvas.drawLine(startXOrY, cirRadius, startXOrY + lineWidth, cirRadius, paint);
            } else {
                canvas.drawLine(cirRadius, startXOrY, cirRadius, startXOrY + lineWidth, paint);
            }
        } else {
            int lineSize = (step - 1) / 2;
            int lineSpaceSize = step - 1;
            if (horizontal) {
                int x = lineSpaceSize * circleLineSpace + lineSize * lineWidth + lineSize * cirRadius * 2 + cirRadius;
                canvas.drawCircle(x, cirRadius, cirRadius, paint);
            } else {
                int y = lineSpaceSize * circleLineSpace + lineSize * lineWidth + lineSize * cirRadius * 2 + cirRadius;
                canvas.drawCircle(cirRadius, y, cirRadius, paint);
            }
        }
    }


    /**
     * Gets the current step count
     *
     * @return
     */
    public int getCurStep() {
        return curStep;
    }


    /**
     * Sets the current step count
     *
     * @param curStep
     */
    public void setCurStep(int curStep) {
        if (curStep <= 0) {
            throw new RuntimeException("The number of steps starts at 1!");
        }
        this.curStep = curStep;
    }

    /**
     * dp to px
     *
     * @param context
     * @param value
     * @return
     */
    private int dp2px(Context context, int value) {
        return (int) (TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics()) + 0.5f);
    }
}
