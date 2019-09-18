package org.lzu.collectingdata;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class CurrentBarView extends View {

    private Paint mRecPaint1;
    private Paint mRecPaint2;
    private int mRectHeight;
    private int mRectWidth;
    private Float mPercent;
    private float mcurrentHeight;
    private int mBgColor;
    private int mCurColor;

    public CurrentBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public void setPercent(float percent) {
        mPercent = percent;
    }

    public void setCurColor(int curColor) {
        mCurColor = curColor;
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CurrentBarView);
        if (typedArray != null) {
            mRectWidth = typedArray.getInteger(R.styleable.CurrentBarView_rectWidth, 0);
            mRectHeight = typedArray.getInteger(R.styleable.CurrentBarView_rectHeight, 0);
            mPercent = typedArray.getFloat(R.styleable.CurrentBarView_percent, 0);
            mBgColor = typedArray.getColor(R.styleable.CurrentBarView_bgColor, getResources().getColor(R.color.color_bar_bg));
            mCurColor = typedArray.getColor(R.styleable.CurrentBarView_curColor, getResources().getColor(R.color.color_green));
            typedArray.recycle();
        }
    }

    public CurrentBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurrentBarView(Context context) {
        this(context, null);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int mWidth = mRectWidth;
        int mHeight = mRectHeight;

        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mRecPaint1 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRecPaint1.setStyle(Paint.Style.FILL);
        mRecPaint1.setColor(mCurColor);
        mRecPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRecPaint2.setColor(mBgColor);
        mRecPaint2.setStyle(Paint.Style.FILL);
        mcurrentHeight = mRectHeight * mPercent;
        canvas.drawRect(0, mcurrentHeight, mRectWidth, mRectHeight, mRecPaint1);
        canvas.drawRect(0, 0, mRectWidth, mcurrentHeight, mRecPaint2);
        invalidate();
    }
}
