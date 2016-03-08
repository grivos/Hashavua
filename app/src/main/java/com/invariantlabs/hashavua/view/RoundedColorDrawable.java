package com.invariantlabs.hashavua.view;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

public class RoundedColorDrawable extends Drawable {

    private final Paint paint;
    private final float cornerRadius;
    private final boolean roundTopLeft;
    private final boolean roundTopRight;
    private final boolean roundBottomLeft;
    private final boolean roundBottomRight;
    private final RectF rect;
    private State state;
    private boolean mMutated;

    public RoundedColorDrawable(float cornerRadius, boolean roundTopLeft, boolean roundTopRight, boolean roundBottomLeft, boolean roundBottomRight) {
        this.cornerRadius = cornerRadius;
        this.roundTopLeft = roundTopLeft;
        this.roundTopRight = roundTopRight;
        this.roundBottomLeft = roundBottomLeft;
        this.roundBottomRight = roundBottomRight;
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        rect = new RectF();
    }

    public RoundedColorDrawable(State state) {
        this(state.cornerRadius, state.roundTopLeft, state.roundTopRight, state.roundBottomLeft, state.roundBottomRight);
        setColor(state.color);
    }

    public void setColor(int color) {
        paint.setColor(color);
        state = new State(this);
        invalidateSelf();
    }

    @Override
    public void setBounds(Rect bounds) {
        super.setBounds(bounds);
        rect.set(bounds);
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        rect.set(left, top, right, bottom);
    }

    @Override
    public void draw(Canvas canvas) {
        final int width = canvas.getWidth();
        final int height = canvas.getHeight();
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint);
        if (!roundTopLeft) {
            canvas.drawRect(0, 0, cornerRadius, cornerRadius, paint);
        }
        if (!roundTopRight) {
            canvas.drawRect(width - cornerRadius, 0, width, cornerRadius, paint);
        }
        if (!roundBottomLeft) {
            canvas.drawRect(0, height - cornerRadius, cornerRadius, height, paint);
        }
        if (!roundBottomRight) {
            canvas.drawRect(width -  2 * cornerRadius, height - 2 * cornerRadius, width, height, paint);
        }
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter cf) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }

    @Override
    public Drawable mutate() {
        state = new State(this);
        mMutated = true;
        return this;
    }

    @Override
    public int getChangingConfigurations() {
        return 0;
    }

    @Override
    public ConstantState getConstantState() {
        return new State(this);
    }


    static class State extends ConstantState {
        private final float cornerRadius;
        private final boolean roundTopLeft;
        private final boolean roundTopRight;
        private final boolean roundBottomLeft;
        private final boolean roundBottomRight;
        private final int color;


        State(RoundedColorDrawable drawable) {
            this(drawable.cornerRadius, drawable.roundTopLeft, drawable.roundTopRight, drawable.roundBottomLeft, drawable.roundBottomRight, drawable.paint.getColor());
        }

        State(float cornerRadius, boolean roundTopLeft, boolean roundTopRight, boolean roundBottomLeft, boolean roundBottomRight, int color) {
            this.cornerRadius = cornerRadius;
            this.roundTopLeft = roundTopLeft;
            this.roundTopRight = roundTopRight;
            this.roundBottomLeft = roundBottomLeft;
            this.roundBottomRight = roundBottomRight;
            this.color = color;
        }

        @Override
        public Drawable newDrawable() {
            return newDrawable(null /*res*/);
        }

        @Override
        public Drawable newDrawable(Resources res) {
            return new RoundedColorDrawable(this);
        }

        @Override
        public int getChangingConfigurations() {
            return 0;
        }
    }
}
