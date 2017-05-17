package com.view.drop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build.VERSION;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class WaterDrop extends RelativeLayout {
    private Paint mPaint = new Paint();
    private TextView mTextView;
    private DropCover.OnDragCompeteListener mOnDragCompeteListener;
    private boolean mHolderEventFlag;
    private static int DEFAULT_LR_PADDING_DIP = 5;
    private static int DEFAULT_CORNER_RADIUS_DIP = 8;

    public WaterDrop(Context context) {
        this(context, null);
    }

    public WaterDrop(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void setText(String str) {
        int value = 0;
        try {
            value = Integer.parseInt(str);
        } catch (Exception e) {
            e.printStackTrace();
            value = 0;
        }
        if (value != 0) {
            mTextView.setText(str);
            setVisibility(VISIBLE);
        } else {
            mTextView.setText("");
            setVisibility(GONE);
        }
    }

    public void setTextSize(int size) {
        mTextView.setTextSize(size);
    }

    int bgColor=0xcf00;
    int textColor = 0xfff;
    int textSize = 14;
    private String text;

    @SuppressLint("NewApi")
    private void init(Context context, AttributeSet attrs) {
        mPaint.setAntiAlias(true);
        if (VERSION.SDK_INT > 11) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.WaterDrop);
            bgColor = ta.getColor(R.styleable.WaterDrop_bgColor, 0xcf00);
            textColor = ta.getColor(R.styleable.WaterDrop_textColor, 0xfff);
            DEFAULT_LR_PADDING_DIP = (int) ta.getDimension(R.styleable.WaterDrop_textPadding, 5);
            text = ta.getString(R.styleable.WaterDrop_text);
            textSize = ta.getDimensionPixelSize(R.styleable.WaterDrop_textSize, 14);
        }
        mTextView = new TextView(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        int paddingPixels = dipToPixels(DEFAULT_LR_PADDING_DIP);
        mTextView.setPadding(paddingPixels, paddingPixels, paddingPixels, paddingPixels);
        mTextView.setTypeface(Typeface.DEFAULT_BOLD);
        mTextView.setTextColor(textColor);
        mTextView.setTextSize(textSize);
        mTextView.setLayoutParams(params);
        addView(mTextView);

        this.setVisibility(GONE);
        setText(text);
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {

        mPaint.setColor(bgColor);
        int r = dipToPixels(DEFAULT_CORNER_RADIUS_DIP);
        RectF rectF = new RectF(0, 0, getWidth(), getHeight());
        canvas.drawRoundRect(rectF, r, r, mPaint);
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    private boolean canMove;

    public boolean isCanMove() {
        return canMove;
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.canMove) {
            return false;
        }
        ViewGroup parent = getScrollableParent();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHolderEventFlag = !CoverManager.getInstance().isRunning();
                if (mHolderEventFlag) {
                    if (parent != null)
                        parent.requestDisallowInterceptTouchEvent(true);
                    CoverManager.getInstance().start(this, event.getRawX(), event.getRawY(), mOnDragCompeteListener);
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mHolderEventFlag) {
                    CoverManager.getInstance().update(event.getRawX(), event.getRawY());
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mHolderEventFlag) {
                    if (parent != null)
                        parent.requestDisallowInterceptTouchEvent(false);
                    CoverManager.getInstance().finish(this, event.getRawX(), event.getRawY());
                }
                break;
        }

        return true;
    }

    private ViewGroup getScrollableParent() {
        View target = this;
        while (true) {
            Log.i("WaterDrop", " : " + target.getParent());
            View parent = (View) target.getParent();
            if (parent == null)
                return null;
            if (parent instanceof AbsListView || parent instanceof ScrollView) {
                return (ViewGroup) parent;
            }
            target = parent;
        }

    }

    public void setOnDragCompeteListener(com.view.drop.DropCover.OnDragCompeteListener onDragCompeteListener) {
        mOnDragCompeteListener = onDragCompeteListener;
    }

    private int dipToPixels(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return (int) px;
    }
}
