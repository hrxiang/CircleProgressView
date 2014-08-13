package com.hannesdorfmann.circleprogressbar;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

public class CircularProgressDrawable extends Drawable implements Animatable {

  private static final Interpolator ANGLE_INTERPOLATOR = new LinearInterpolator();
  private static final Interpolator SWEEP_INTERPOLATOR = new DecelerateInterpolator();
  private int angleAnimatorDuration = 2000;
  private int sweepAnimatorDuration = 600;
  private static final int MIN_SWEEP_ANGLE = 30;
  private final RectF fBounds = new RectF();

  private ObjectAnimator mObjectAnimatorSweep;
  private ObjectAnimator mObjectAnimatorAngle;
  private boolean mModeAppearing;
  private Paint mPaint;
  private float mCurrentGlobalAngleOffset;
  private float mCurrentGlobalAngle;
  private float mCurrentSweepAngle;
  private float mStrokeWidth;
  private boolean mRunning;

  /**
   *
   * @param color The color
   * @param strokeWidth The stroke width
   * @param circleAnimDuration How long does it take to draw a whole rotation of 360°
   * @param sweepAnimatorDuration How long does the sweep animation takes on the tail
   */
  public CircularProgressDrawable(int color, float strokeWidth, int circleAnimDuration, int sweepAnimatorDuration) {
    mStrokeWidth = strokeWidth;
    mPaint = new Paint();
    mPaint.setAntiAlias(true);
    mPaint.setStyle(Paint.Style.STROKE);
    mPaint.setColor(color);
    mPaint.setStrokeWidth(strokeWidth);
    setCircleAnimationDuration(circleAnimDuration);
    setSweepAnimationDuration(sweepAnimatorDuration);

    setupAnimations();
  }

  /**
   * Set the duration of the circle. How long should it take to draw a whole rotation of 360°
   * @param durationMs The duration in milliseconds
   */
  public void setCircleAnimationDuration(int durationMs){
    this.angleAnimatorDuration = durationMs;
  }

  /**
   * The duration of the "sweep" part (tail)
   * @param durationMs duration in milliseconds
   */
  public void setSweepAnimationDuration(int durationMs){
    this.sweepAnimatorDuration = durationMs;
  }

  public void setStrokeWidth(float strokeWidth){
    mStrokeWidth = strokeWidth;
    mPaint.setStrokeWidth(strokeWidth);
    invalidateSelf();
  }

  @Override
  public void draw(Canvas canvas) {
    float startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset;
    float sweepAngle = mCurrentSweepAngle;
    if (!mModeAppearing) {
      startAngle = startAngle + sweepAngle;
      sweepAngle = 360 - sweepAngle - MIN_SWEEP_ANGLE;
    } else {
      sweepAngle += MIN_SWEEP_ANGLE;
    }
    canvas.drawArc(fBounds, startAngle, sweepAngle, false, mPaint);
  }

  @Override
  public void setAlpha(int alpha) {
    mPaint.setAlpha(alpha);
  }

  @Override
  public void setColorFilter(ColorFilter cf) {
    mPaint.setColorFilter(cf);
  }

  @Override
  public int getOpacity() {
    return PixelFormat.TRANSPARENT;
  }

  private void toggleAppearingMode() {
    mModeAppearing = !mModeAppearing;
    if (mModeAppearing) {
      mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + MIN_SWEEP_ANGLE * 2) % 360;
    }
  }

  public void setColor(int color){
    mPaint.setColor(color);
    invalidateSelf();
  }

  @Override
  protected void onBoundsChange(Rect bounds) {
    super.onBoundsChange(bounds);
    fBounds.left = bounds.left + mStrokeWidth / 2f + .5f;
    fBounds.right = bounds.right - mStrokeWidth / 2f - .5f;
    fBounds.top = bounds.top + mStrokeWidth / 2f + .5f;
    fBounds.bottom = bounds.bottom - mStrokeWidth / 2f - .5f;
  }

  public void setAngle(float value) {
    setCurrentGlobalAngle(value);
  }

  public float getAngle() {
    return getCurrentGlobalAngle();
  }

  public void setArc(float value) {
    setCurrentSweepAngle(value);
  }

  public float getArc() {
    return getCurrentSweepAngle();
  }

  private void setupAnimations() {
    mObjectAnimatorAngle = ObjectAnimator.ofFloat(this, "angle", 360f);
    mObjectAnimatorAngle.setInterpolator(ANGLE_INTERPOLATOR);
    mObjectAnimatorAngle.setDuration(angleAnimatorDuration);
    mObjectAnimatorAngle.setRepeatMode(ValueAnimator.RESTART);
    mObjectAnimatorAngle.setRepeatCount(ValueAnimator.INFINITE);

    mObjectAnimatorSweep = ObjectAnimator.ofFloat(this, "arc", 360f - MIN_SWEEP_ANGLE * 2);
    mObjectAnimatorSweep.setInterpolator(SWEEP_INTERPOLATOR);
    mObjectAnimatorSweep.setDuration(sweepAnimatorDuration);
    mObjectAnimatorSweep.setRepeatMode(ValueAnimator.RESTART);
    mObjectAnimatorSweep.setRepeatCount(ValueAnimator.INFINITE);
    mObjectAnimatorSweep.addListener(new Animator.AnimatorListener() {
      @Override
      public void onAnimationStart(Animator animation) {

      }

      @Override
      public void onAnimationEnd(Animator animation) {

      }

      @Override
      public void onAnimationCancel(Animator animation) {

      }

      @Override
      public void onAnimationRepeat(Animator animation) {
        toggleAppearingMode();
      }
    });
  }

  @Override
  public void start() {
    if (isRunning()) {
      return;
    }
    mRunning = true;
    mObjectAnimatorAngle.start();
    mObjectAnimatorSweep.start();
    invalidateSelf();
  }

  @Override
  public void stop() {
    if (!isRunning()) {
      return;
    }
    mRunning = false;
    mObjectAnimatorAngle.cancel();
    mObjectAnimatorSweep.cancel();
    invalidateSelf();
  }

  @Override
  public boolean isRunning() {
    return mRunning;
  }

  public void setCurrentGlobalAngle(float currentGlobalAngle) {
    mCurrentGlobalAngle = currentGlobalAngle;
    invalidateSelf();
  }

  public float getCurrentGlobalAngle() {
    return mCurrentGlobalAngle;
  }

  public void setCurrentSweepAngle(float currentSweepAngle) {
    mCurrentSweepAngle = currentSweepAngle;
    invalidateSelf();
  }

  public float getCurrentSweepAngle() {
    return mCurrentSweepAngle;
  }

  public int getCircleAnimationDuration() {
    return angleAnimatorDuration;
  }

  public int getSweepAnimationDuration() {
    return sweepAnimatorDuration;
  }

  public float getStrokeWidth() {
    return mStrokeWidth;
  }

  public int getColor(){
    return mPaint.getColor();
  }
}