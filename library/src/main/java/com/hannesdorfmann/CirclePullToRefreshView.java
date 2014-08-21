package com.hannesdorfmann;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import com.hannesdorfmann.circleprogressview.R;

/**
 * Simplest custom view possible, using CircularProgressDrawable
 */
public class CirclePullToRefreshView extends View {

  private CircularProgressDrawable mDrawable;

  public CirclePullToRefreshView(Context context) {
    this(context, null);
  }

  public CirclePullToRefreshView(Context context, AttributeSet attrs) {
    this(context, attrs, 0);
  }

  public CirclePullToRefreshView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs, defStyleAttr);
  }

  private void init(Context context, AttributeSet attrs, int defStyleAttr) {

    TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CircleProgressView);

    int color = a.getColor(R.styleable.CircleProgressView_cpvColor, Color.LTGRAY);

    // The colors
    int colorsId = a.getResourceId(R.styleable.CircleProgressView_cpvColors, 0);

    // The stroke size
    int strokeSize =
        a.getDimensionPixelSize(R.styleable.CircleProgressView_cpvStrokeWidth, dpToPx(context, 6));

    // How long should it take to make a complete circle
    int circleAnimDuration = a.getInt(R.styleable.CircleProgressView_cpvCircleAnimDuration, 2000);

    // How long should it take to sweep the tail
    int sweepAnimDuration = a.getInt(R.styleable.CircleProgressView_cpvSweepAnimDuration, 600);

    float speed = a.getFloat(R.styleable.CircleProgressView_cpvSpeed, 1.0f);

    int minSweepAngle = a.getInteger(R.styleable.CircleProgressView_cpvMinSweepAngle, 20);
    int maxSweepAngle = a.getInteger(R.styleable.CircleProgressView_cpvMaxSweepAngle, 300);

    a.recycle();

    int[] colors = null;
    if (colorsId != 0) {
      colors = context.getResources().getIntArray(colorsId);
    }

    if (colors == null) {
      colors = new int[1];
      colors[0] = color;
    }

    mDrawable = new CircularProgressDrawable(colors, (float) strokeSize, speed, minSweepAngle,
        maxSweepAngle, CircularProgressDrawable.Style.ROUNDED);

    mDrawable.setCallback(this);
  }

  public int dpToPx(Context context, int dp) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    return (int) ((dp * displayMetrics.density) + 0.5);
  }

  @Override
  protected void onVisibilityChanged(View changedView, int visibility) {
    super.onVisibilityChanged(changedView, visibility);

    if (mDrawable == null) {
      return;
    }

    if (visibility != VISIBLE) {
      mDrawable.stop();
    }
  }

  @Override
  protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    super.onSizeChanged(w, h, oldw, oldh);
    mDrawable.setBounds(0, 0, w, h);
  }

  @Override
  public void draw(Canvas canvas) {
    super.draw(canvas);
    mDrawable.draw(canvas);
  }

  @Override
  protected boolean verifyDrawable(Drawable who) {
    return who == mDrawable || super.verifyDrawable(who);
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    mDrawable.stop();
  }

  /**
   * Set the current progress on displayed as circle. Zero means no circle, one menas circle is
   * full
   * drawn
   *
   * @param progress A value between 0 and 1.
   */
  public void setProgress(float progress) {

    float progressMapped = mapPoint(progress, 0f, 1f, 0f, -360f);
    Log.d("Test", "pro " + progress + " " + progressMapped);
    mDrawable.setCurrentGlobalAngle(0);
    mDrawable.setCurrentSweepAngle(progressMapped);

    if (progressMapped == -360) {
      mDrawable.start();
    } else {
      mDrawable.stop();
    }
  }

  public void startAnimation() {
    mDrawable.start();
  }

  public void stopAnimation() {
    mDrawable.stop();
  }

  public void reset() {
    stopAnimation();
    mDrawable.reset();
  }

  /**
   * This method maps a number x, which is in the range [sourceStart,
   * sourceEnd], to a new range [targetStart, targetEnd]
   *
   * <p>
   * sourceStart <= x <= sourceEnd <br/>
   * targetStart <= returnValue <= targetEnd
   * </p>
   *
   * @param x The value that should be mapped
   * @param sourceStart The source range start (inclusive)
   * @param sourceEnd The source range end (inclusive)
   * @param targetStart The target range start (inclusive)
   * @param targetEnd The target range end (inclusive)
   * @return The corresponding value of x in the target range
   */
  public static float mapPoint(float x, float sourceStart, float sourceEnd, float targetStart,
      float targetEnd) {

    if (x <= sourceStart) {
      return targetStart;
    }

    if (x >= sourceEnd) {
      return targetEnd;
    }

    return (x - sourceStart) / (sourceEnd - sourceStart) * (targetEnd - targetStart) + targetStart;
  }

  public CirclePullToRefreshView setStrokeStyle(CircularProgressDrawable.Style style) {
    mDrawable.setStrokeStyle(style);
    return this;
  }

  public CirclePullToRefreshView setStrokeWidth(float strokeWidth) {
    mDrawable.setStrokeWidth(strokeWidth);
    return this;
  }

  public float getStrokeWidth() {
    return mDrawable.getStrokeWidth();
  }

  public CirclePullToRefreshView setColors(int colors[]) {
    mDrawable.setColors(colors);
    return this;
  }

  public CirclePullToRefreshView setSpeed(float speed) {
    mDrawable.setSpeed(speed);
    return this;
  }

  public float getSpeed() {
    return mDrawable.getSpeed();
  }

  public CirclePullToRefreshView setMinSweepAngle(int minSweepAngle) {
    mDrawable.setMinSweepAngle(minSweepAngle);
    return this;
  }

  public CirclePullToRefreshView setMaxSweepAngle(int maxSweepAngle) {
    mDrawable.setMaxSweepAngle(maxSweepAngle);
    return this;
  }

  public int getMinSweepAngle() {
    return mDrawable.getMinSweepAngle();
  }

  public int getMaxSweepAngle() {
    return mDrawable.getMaxSweepAngle();
  }


  /*
  @Override
  public Parcelable onSaveInstanceState() {
    //begin boilerplate code that allows parent classes to save state
    Parcelable superState = super.onSaveInstanceState();

    SavedState ss = new SavedState(superState);
    //end

    ss.color = mDrawable.getColor();
    ss.sweepAnimDuration = mDrawable.getSweepAnimationDuration();
    ss.circleAnimDuration = mDrawable.getCircleAnimationDuration();
    ss.strokeSize = mDrawable.getStrokeWidth();

    return ss;
  }

  @Override
  public void onRestoreInstanceState(Parcelable state) {
    //begin boilerplate code so parent classes can restore state
    if(!(state instanceof SavedState)) {
      super.onRestoreInstanceState(state);
      return;
    }

    SavedState ss = (SavedState)state;
    super.onRestoreInstanceState(ss.getSuperState());
    //end

    mDrawable.setCircleAnimationDuration(ss.circleAnimDuration);
    mDrawable.setSweepAnimationDuration(ss.sweepAnimDuration);
    mDrawable.setColor(ss.color);
    mDrawable.setStrokeWidth(ss.strokeSize);
  }

*/

  static class SavedState extends BaseSavedState {
    int color;
    float strokeSize;
    int circleAnimDuration;
    int sweepAnimDuration;

    SavedState(Parcelable superState) {
      super(superState);
    }

    private SavedState(Parcel in) {
      super(in);
      this.color = in.readInt();
      this.strokeSize = in.readFloat();
      this.circleAnimDuration = in.readInt();
      this.sweepAnimDuration = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
      super.writeToParcel(out, flags);
      out.writeInt(this.color);
      out.writeFloat(this.strokeSize);
      out.writeInt(this.circleAnimDuration);
      out.writeInt(this.sweepAnimDuration);
    }

    //required field that makes Parcelables from a Parcel
    public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
      public SavedState createFromParcel(Parcel in) {
        return new SavedState(in);
      }

      public SavedState[] newArray(int size) {
        return new SavedState[size];
      }
    };
  }
}