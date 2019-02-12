package com.trex.diamondbnv;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.annotation.DrawableRes;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class DiamondBnv extends LinearLayout {

    private int center_x, diamond_side, images_padding;
    private int diamond_margin, radius, shadow, diamond_icon, left_icon, right_icon, diamond_color, frame_color, icons_padding, diamond_padding; //attrs
    private boolean debug_mode = false;

    private OnClickListener mDiamondCb;
    private OnClickListener mLeftActionCb;
    private OnClickListener mRightActionCb;

    public DiamondBnv(Context context) {
        super(context);
    }

    public DiamondBnv(Context context, AttributeSet attrs) {
        super(context, attrs);
        addAttrs(context, attrs);
    }

    private void addAttrs(Context context, AttributeSet attrs) {
        TypedArray newAttrs = context.obtainStyledAttributes(attrs, R.styleable.DiamondBnv, 0, 0);
        radius = (int) newAttrs.getDimension(R.styleable.DiamondBnv_db_radius, getInPixels(8));
        diamond_margin = (int) newAttrs.getDimension(R.styleable.DiamondBnv_db_diamond_margin, getInPixels(8)) + radius;
        shadow = (int) newAttrs.getDimension(R.styleable.DiamondBnv_db_shadow, getInPixels(4));
        diamond_icon = newAttrs.getResourceId(R.styleable.DiamondBnv_db_diamond_icon, -1);
        left_icon = newAttrs.getResourceId(R.styleable.DiamondBnv_db_left_icon, -1);
        right_icon = newAttrs.getResourceId(R.styleable.DiamondBnv_db_right_icon, -1);
        diamond_color = newAttrs.getColor(R.styleable.DiamondBnv_db_diamond_color, Color.BLACK);
        frame_color = newAttrs.getColor(R.styleable.DiamondBnv_db_frame_color, Color.DKGRAY);
        icons_padding = (int) newAttrs.getDimension(R.styleable.DiamondBnv_db_icons_padding, getInPixels(4));
        diamond_padding = (int) newAttrs.getDimension(R.styleable.DiamondBnv_db_diamond_padding, getInPixels(4));
        setWillNotDraw(false);
        setOrientation(HORIZONTAL);
        newAttrs.recycle();
    }

    private void updateDimens() {
        center_x = getWidth() / 2;
        diamond_side = getHeight() - (diamond_margin);
        images_padding = diamond_side / 4;
    }

    public DiamondBnv(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        updateDimens();
        drawDiamondCurved(canvas);
        drawFrameCurved(canvas);
        putViews();
        if (debug_mode) {
            drawDiamondRects(canvas);
            drawFrameRects(canvas);
        }
        super.onDraw(canvas);
    }

    ImageView imgDiamond, imgLeft, imgRight;

    private void putViews() {
        if (imgLeft == null) {
            imgLeft = getImage(false, left_icon, mLeftActionCb);
            addView(imgLeft);
        }
        if (imgDiamond == null) {
            imgDiamond = getImage(true, diamond_icon, mDiamondCb);
            addView(imgDiamond);
        }

        if (imgRight == null) {
            imgRight = getImage(false, right_icon, mRightActionCb);
            addView(imgRight);
        }
    }

    private ImageView getImage(boolean is_diamond, Integer icon_resource, final OnClickListener callback) {
        //set commons
        ImageView img = new ImageView(getContext());
        if (icon_resource != -1) img.setImageResource(icon_resource);
        img.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (is_diamond) {
            //set diamond params
            LayoutParams diamond_params = new LayoutParams(diamond_side / 2, diamond_side / 2);
            diamond_params.setMargins(diamond_margin + images_padding, images_padding, diamond_margin + images_padding, diamond_margin * 2);
            img.setLayoutParams(diamond_params);
            img.setPadding(diamond_padding, diamond_padding, diamond_padding, diamond_padding);
        } else {
            //set left or right params
            LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1);
            params.topMargin = diamond_side / 2;
            img.setLayoutParams(params);
            img.setPadding(icons_padding, icons_padding, icons_padding, icons_padding);
        }
        if (debug_mode) {
            if (is_diamond) {
                img.setBackgroundColor(Color.RED);
            } else {
                img.setBackgroundColor(Color.CYAN);
            }
            img.setAlpha(0.6F);
        }
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(v);
            }
        });
        return img;
    }

    private void drawDiamondRects(Canvas canvas) {
        canvas.drawRect(getRectTopDiamond(), getFramePaint());
        canvas.drawRect(getRectRightDiamond(), getFramePaint());
        canvas.drawRect(getRectBottomDiamond(), getFramePaint());
        canvas.drawRect(getRectLeftDiamond(), getFramePaint());

    }

    private void drawFrameRects(Canvas canvas) {
        canvas.drawRect(getRectLeftFrame(), getDiamondPaint());
        canvas.drawRect(getRectBottomFrame(), getDiamondPaint());
        canvas.drawRect(getRectRightFrame(), getDiamondPaint());
    }

    private void drawFrameCurved(Canvas canvas) {
        Path frame = new Path();
        frame.moveTo(0, diamond_side / 2f);
        frame.arcTo(getRectLeftFrame(), 270, 45);
        frame.arcTo(getRectBottomFrame(), 135, -90);
        frame.arcTo(getRectRightFrame(), 225, 45);
        frame.lineTo(getWidth(), diamond_side / 2f);
        frame.lineTo(getWidth(), 0 + diamond_side / 2f);
        frame.lineTo(getWidth(), getHeight());
        frame.lineTo(0, getHeight());
        frame.close();
        canvas.drawPath(frame, getFramePaint());
    }

    private void drawDiamondCurved(Canvas canvas) {
        Path diamond = new Path();
        diamond.arcTo(getRectRightDiamond(), -45, 90);
        diamond.arcTo(getRectBottomDiamond(), 45, 90);
        diamond.arcTo(getRectLeftDiamond(), 135, 90);
        diamond.arcTo(getRectTopDiamond(), 230, 90);
        diamond.close();
        canvas.drawPath(diamond, getDiamondPaint());
    }

    private RectF getRectTopDiamond() {
        return new RectF(center_x - radius, 0, center_x + radius, radius * 2);
    }

    private RectF getRectRightDiamond() {
        return new RectF(center_x + diamond_side / 2f - (radius * 2), diamond_side / 2f - radius, center_x + diamond_side / 2f, diamond_side / 2f + radius);
    }

    private RectF getRectBottomDiamond() {
        return new RectF(center_x - radius, diamond_side - (2f * radius), center_x + radius, diamond_side);
    }

    private RectF getRectLeftDiamond() {
        return new RectF(center_x - diamond_side / 2f, diamond_side / 2f - radius, center_x - diamond_side / 2f + (radius * 2), diamond_side / 2f + radius);
    }

    private RectF getRectLeftFrame() {
        return new RectF(center_x - diamond_side / 2f - diamond_margin - radius, diamond_side / 2f, center_x - diamond_side / 2f - diamond_margin + radius, diamond_side / 2f + radius * 2);
    }

    private RectF getRectRightFrame() {
        return new RectF(center_x + diamond_side / 2f + diamond_margin - radius, diamond_side / 2f, center_x + diamond_side / 2f + diamond_margin + radius, diamond_side / 2f + radius * 2);
    }

    private RectF getRectBottomFrame() {
        return new RectF(center_x - radius, diamond_side + diamond_margin - radius * 3, center_x + radius, diamond_side + diamond_margin - radius);
    }

    private Paint getDiamondPaint() {
        Paint paint = new Paint();
        paint.setColor(diamond_color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        return paint;
    }

    private Paint getFramePaint() {
        Paint paint = new Paint();
        paint.setColor(frame_color);
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(3);
        return paint;
    }

    public int getImagesPadding() {
        return images_padding;
    }

    public void setImagesPadding(int images_padding) {
        this.images_padding = images_padding;
    }

    public int getMargin() {
        return diamond_margin;
    }

    public void setMargin(int margin) {
        this.diamond_margin = margin;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public int getShadow() {
        return shadow;
    }

    public void setShadow(int shadow) {
        this.shadow = shadow;
    }

    public int getDiamondIcon() {
        return diamond_icon;
    }

    public void setDiamondIcon(@DrawableRes int diamond_icon) {
        this.diamond_icon = diamond_icon;
    }

    public int getLeftIcon() {
        return left_icon;
    }

    public void setLeftIcon(@DrawableRes int left_icon) {
        this.left_icon = left_icon;
    }

    public int getRightIcon() {
        return right_icon;
    }

    public void setRightIcon(@DrawableRes int right_icon) {
        this.right_icon = right_icon;
    }

    public int getDiamondColor() {
        return diamond_color;
    }

    public void setDiamondColor(int diamond_color) {
        this.diamond_color = diamond_color;
    }

    public int getFrameColor() {
        return frame_color;
    }

    public void setFrameColor(int frame_color) {
        this.frame_color = frame_color;
    }

    public int getIconsPadding() {
        return icons_padding;
    }

    public void setIconsPadding(int icons_padding) {
        this.icons_padding = icons_padding;
    }

    public int getDiamondPadding() {
        return diamond_padding;
    }

    public void setDiamondPadding(int diamond_padding) {
        this.diamond_padding = diamond_padding;
    }

    public boolean isDebugMode() {
        return debug_mode;
    }

    public void setDebugMode(boolean debug_mode) {
        this.debug_mode = debug_mode;
    }

    public void setOnDiamondClickListener(OnClickListener onDiamondClickListener) {
        this.mDiamondCb = onDiamondClickListener;
    }

    public void setOnLeftActionClickListener(OnClickListener onLeftActionClickListener) {
        this.mLeftActionCb = onLeftActionClickListener;
    }

    public void setOnRightActionClickListener(OnClickListener onRightActionClickListener) {
        this.mRightActionCb = onRightActionClickListener;
    }

    private float getInPixels(int dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

}