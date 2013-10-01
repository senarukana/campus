package com.campusrecruit.widget;

import java.util.Calendar;

import com.krislq.sliding.R;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout.LayoutParams;

public class DateWidgetDayCell extends View implements GestureDetector.OnGestureListener {
  // types
  public interface OnItemClick {
    public void OnClick(DateWidgetDayCell item);

    public void OnTouchMove(DateWidgetDayCell item, int type);
  }

  public static int ANIM_ALPHA_DURATION = 100;
  // fields
  private final static float fTextSize = 18;
  private final static int iMargin = 1;
  private final static int iAlphaInactiveBg = 0x11;
  private final static int iAlphaInactiveSelectBg = 0x22;
  private final static int iAlphaInactiveText = 0xbb;
  private final static int iAlphaInactiveSelecrtText = 0x88;

  private static final int SWIPE_MIN_DISTANCE = 100;
  private static final int SWIPE_THRESHOLD_VELOCITY = 200;

  // fields
  private int iDateYear = 0;
  private int iDateMonth = 0;
  private int iDateDay = 0;
  private int iDayOfWeek = 0;

  // fields
  private OnItemClick itemClick = null;
  private Paint pt = new Paint();
  private RectF rect = new RectF();
  private String sDate = "";
  private String sLunarDate = "";
  // fields
  private boolean bSelected = false;
  private boolean bIsActiveMonth = false;
  private boolean bToday = false;
  private boolean bScheduled = false;
  private boolean bHoliday = false;
  private boolean bTouchedDown = false;

  GestureDetector mGestureDetector;

  // methods
  public DateWidgetDayCell(Context context, int iWidth, int iHeight) {
    super(context);
    setFocusable(true);
    setLayoutParams(new LayoutParams(iWidth, iHeight));
    mGestureDetector = new GestureDetector(DateWidgetDayCell.this);
    // setLongClickable(true);
  }

  public boolean getSelected() {
    return this.bSelected;
  }

  @Override
  public void setSelected(boolean bEnable) {
    if (this.bSelected != bEnable) {
      this.bSelected = bEnable;
      this.invalidate();
    }
  }

  public void setData(int iYear, int iMonth, int iDay, boolean bToday, boolean bScheduled, boolean bHoliday, int iActiveMonth, int iDayOfWeek) {
    iDateYear = iYear;
    iDateMonth = iMonth;
    iDateDay = iDay;

    this.sDate = Integer.toString(iDateDay);
//    this.sLunarDate = iLunarDay;
    this.bIsActiveMonth = (iDateMonth == iActiveMonth);
    this.bToday = bToday;
    this.bScheduled = bScheduled;
    this.bHoliday = bHoliday;
    this.iDayOfWeek = iDayOfWeek;
  }

  public void setItemClick(OnItemClick itemClick) {
    this.itemClick = itemClick;
  }

  private int getTextHeight() {
    return (int) (-pt.ascent() + pt.descent());
  }

  @Override
  public boolean onKeyDown(int keyCode, KeyEvent event) {
    boolean bResult = super.onKeyDown(keyCode, event);
    if ((keyCode == KeyEvent.KEYCODE_DPAD_CENTER) || (keyCode == KeyEvent.KEYCODE_ENTER)) {
      doItemClick();
    }
    return bResult;
  }

  @Override
  public boolean onKeyUp(int keyCode, KeyEvent event) {
    boolean bResult = super.onKeyUp(keyCode, event);
    return bResult;
  }

  public void doItemClick() {
    if (itemClick != null)
      itemClick.OnClick(this);
  }

  public void doItemMove(int type) {
    if (itemClick != null) {
      itemClick.OnTouchMove(this, type);
    }
  }

  @Override
  protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
    super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    invalidate();
  }

  public Calendar getDate() {
    Calendar calDate = Calendar.getInstance();
    calDate.clear();
    calDate.set(Calendar.YEAR, iDateYear);
    calDate.set(Calendar.MONTH, iDateMonth);
    calDate.set(Calendar.DAY_OF_MONTH, iDateDay);
    return calDate;
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    // init rectangles
    rect.set(0, 0, this.getWidth(), this.getHeight());
    rect.inset(0.3f, 0.3f);

    // drawing
    final boolean bFocused = IsViewFocused();
//    final boolean bScheduled = IsScheduled();

    drawDayView(canvas, bFocused);
    drawDayNumber(canvas, bFocused);
  }

  private void drawDayView(Canvas canvas, boolean bFocused) {
    if (bSelected || bFocused) {
      if (bFocused) {
        setBackgroundDrawable(this.getResources().getDrawable(R.drawable.today_bg_mark_0));
      }
      if (bSelected) {
        setBackgroundDrawable(this.getResources().getDrawable(R.drawable.select_bg));
      }
      pt.setAlpha(iAlphaInactiveSelectBg);
      canvas.drawRect(rect, pt);
    } else {
      setBackgroundDrawable(this.getResources().getDrawable(R.drawable.block));
      pt.setAlpha(iAlphaInactiveBg);
      canvas.drawRect(rect, pt);
    }
  }

  public void drawDayNumber(Canvas canvas, boolean bFocused) {
    // draw day number
    pt.setTypeface(null);
    pt.setAntiAlias(true);
    pt.setShader(null);
    pt.setFakeBoldText(true);
    pt.setTextSize(fTextSize);

    pt.setUnderlineText(false);
    if (bToday) {
      pt.setUnderlineText(true);
      setBackgroundDrawable(this.getResources().getDrawable(R.drawable.today_bg_mark_0));
      
    }

    if(bScheduled){
    	pt.setUnderlineText(true);
        setBackgroundDrawable(this.getResources().getDrawable(R.drawable.today_bg_mark_1));
    }
    int iTextPosX = (int) rect.right - (int) pt.measureText(sDate);
    int iTextPosY = (int) rect.bottom + (int) (-pt.ascent()) - getTextHeight();

    iTextPosX -= ((int) rect.width() >> 1) - ((int) pt.measureText(sDate) >> 1);
    iTextPosY -= ((int) rect.height() >> 1) - (getTextHeight() >> 1);

    // draw text
    if (bToday || bScheduled) {
      pt.setColor(DayStyle.getColorText(bHoliday, bToday, bScheduled, iDayOfWeek));
      pt.setAlpha(iAlphaInactiveSelecrtText);
    }

    if (bIsActiveMonth) {
      pt.setAlpha(iAlphaInactiveText);
    } else {
      pt.setAlpha(iAlphaInactiveBg);
    }
    canvas.drawText(sDate, iTextPosX, iTextPosY - iMargin * 6, pt);
    pt.setTextSize(fTextSize - 2);
    pt.setUnderlineText(false);
    canvas.drawText(sLunarDate, iTextPosX - iMargin * 4, iTextPosY + iMargin * 9, pt);
  }

  public boolean IsViewFocused() {
    return (this.isFocused() || bTouchedDown);
  }
  
  public boolean IsScheduled(){
	  return true;
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return mGestureDetector.onTouchEvent(event);
  }

  public static void startAlphaAnimIn(View view) {
    AlphaAnimation anim = new AlphaAnimation(0.5F, 1);
    anim.setDuration(ANIM_ALPHA_DURATION);
    anim.startNow();
    view.startAnimation(anim);
  }

  public int getiDateMonth() {
    return iDateMonth;
  }

  public void setiDateMonth(int iDateMonth) {
    this.iDateMonth = iDateMonth;
  }

  @Override
  public boolean onDown(MotionEvent e) {
    invalidate();
    return true;
  }

  @Override
  public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
    bTouchedDown = false;
    // 左
    if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
      doItemMove(1);
    } // 右
    else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
      doItemMove(2);
    }// 上
    else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
      doItemMove(3);
    } // 下
    else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
      doItemMove(4);
    }
    return true;
  }

  @Override
  public void onLongPress(MotionEvent e) {

  }

  @Override
  public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

    return false;
  }

  @Override
  public void onShowPress(MotionEvent e) {
    // bTouchedDown = true;
    // startAlphaAnimIn(DateWidgetDayCell.this);

  }

  @Override
  public boolean onSingleTapUp(MotionEvent e) {
    bTouchedDown = false;
    doItemClick();
    return true;
  }

}
