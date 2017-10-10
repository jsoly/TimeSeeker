package d.cityaurora.com.libtimeseeker.v;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import d.cityaurora.com.libtimeseeker.R;


/**
 * Created by jsj on 2017/10/9.
 */

public class TimeSeeker extends View {

    /**
     * data
     */
    private float offset = 0;
    private float topline;//时间轴上边线
    private float bottomline;//时间轴下边线
    private static final float endX = 60*60*24;//秒分时
    private int colorLine = Color.GRAY;
    private int colorPointer = Color.RED;
    private int dataColor = Color.parseColor("#00B2EE");
    private int textBackgroundColor = colorLine;
    private int textColor = Color.WHITE;
    private Path pathPointer;
    private int widthPointer = 4;//指针宽度
    private float spaceClock = 40;
    private SeekerTime bean;
    private List<SeekerData> mData = null;
    /**
     * view
     */
    private Paint mPaint;
    private ValueAnimator anim;
    private void init(Context context,AttributeSet attrs) {
        this.mData = new ArrayList<>();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(30);
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TimeSeeker);
        colorLine = ta.getColor(R.styleable.TimeSeeker_clockColor,Color.GRAY);
        textBackgroundColor = ta.getColor(R.styleable.TimeSeeker_textBackgroundColor,Color.GRAY);
        textColor = ta.getColor(R.styleable.TimeSeeker_clockColor,Color.WHITE);
        dataColor = ta.getColor(R.styleable.TimeSeeker_dataColor,Color.parseColor("#00B2EE"));
        spaceClock = ta.getDimension(R.styleable.TimeSeeker_spaceWidth,40f);
        if(spaceClock<20){
            spaceClock = 20;
        }
        if(spaceClock>300){
            spaceClock = 300;
        }
        ta.recycle();
    }

    public TimeSeeker(Context context) {
        this(context,null);
    }

    public TimeSeeker(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TimeSeeker(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }



    public void setData(List<SeekerData> mData){
        this.mData = mData;
        postInvalidate();
    }

    public void addData(SeekerData data){
        addData(data,this.mData.size());
        postInvalidate();
    }

    public void addData(SeekerData data,int index){
        if(mData!=null)
            mData.add(index,data);
    }

    public void clearData(){
        if(mData!=null)
            mData.clear();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        offset = getMeasuredWidth()/2;
        topline = getMeasuredHeight()/3;
        bottomline = getMeasuredHeight()*3/4;
        setPivotX(getMeasuredWidth()/2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawClockLine(canvas);
        drawTimePointer(canvas);
        drawTimeText(canvas);
    }

    private void drawData(Canvas canvas) {
        if(mData!=null && mData.size()!=0){
            mPaint.setColor(dataColor);
            mPaint.setAlpha(120);
            Path path = new Path();
            for(SeekerData data : mData){
                int left = (int) (data.getStartTime()*spaceClock*5f/3600f);
                int right = (int) (data.getEndTime()*spaceClock*5f/3600f);
                path.addRect(left,topline,right,bottomline, Path.Direction.CW);
            }
            canvas.drawPath(path,mPaint);
            mPaint.setAlpha(255);
        }
    }

    private void drawTimeText(Canvas canvas) {
        int centerX = getMeasuredWidth()/2;
        mPaint.setColor(textBackgroundColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(centerX - 80,topline-50,centerX+80,topline-20,10,10,mPaint);
        }
        mPaint.setColor(textColor);
        float sceond = getSecond();
        float hour = sceond/3600;
        float minute = sceond%3600/60;
        float sec = sceond%3600%60;
        canvas.drawText(String.format("%02d",(int)hour)+":"+String.format("%02d",(int)minute)+":"+String.format("%02d",(int)sec),centerX-60,topline-25,mPaint);

    }

    /**
     * 获取当前秒数
     * @return
     */
    public float getSecond(){
        return Math.abs(offset-getMeasuredWidth()/2)*3600/spaceClock/5;
    }

    /**
     * 加一秒
     */
    public void secondpp(){
        offset-=spaceClock*5f/3600f;
        resolveOffset();
        postInvalidate();
    }

    /**
     * 减一秒
     */
    public void secondmm(){
        offset+=spaceClock*5f/3600f;
        resolveOffset();
        postInvalidate();
    }

    public void seekTo(int second){
        if(second<0 || second>60*60*24){
            return;
        }
        offset = second * spaceClock*5f/3600f - getMeasuredWidth()/2;
        postInvalidate();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        /**
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                secondpp();
            }
        },1000,10);*/
    }

    /**
     * 刻度线指针
     * @param canvas
     */
    private void drawTimePointer(Canvas canvas) {
        mPaint.setColor(colorPointer);
        if(pathPointer==null){
            pathPointer = new Path();
            pathPointer.reset();
            pathPointer.moveTo(getMeasuredWidth()/2-16,topline);
            pathPointer.quadTo(getMeasuredWidth()/2,topline+18,getMeasuredWidth()/2-widthPointer/2,topline+36);
            pathPointer.lineTo(getMeasuredWidth()/2-widthPointer/2,bottomline);
            pathPointer.lineTo(getMeasuredWidth()/2+widthPointer/2,bottomline);
            pathPointer.lineTo(getMeasuredWidth()/2+widthPointer/2,topline+36);
            pathPointer.quadTo(getMeasuredWidth()/2,topline+18,getMeasuredWidth()/2+16,topline);
            pathPointer.close();
        }
        canvas.drawPath(pathPointer,mPaint);
    }

    /**
     * 刻度线
     * @param canvas
     */
    private void drawClockLine(Canvas canvas) {
        mPaint.setColor(colorLine);
        canvas.drawLine(-getMeasuredWidth()/2,topline,endX+getMeasuredWidth()/2,topline,mPaint);
        canvas.drawLine(-getMeasuredWidth()/2,bottomline,endX+getMeasuredWidth()/2,bottomline,mPaint);
        canvas.save();
        canvas.translate(offset,0);
        int height = 15;
        double t = 0;
        int sceond = 0;
        for(int i=0;i<=spaceClock*5*24;i+=spaceClock){
            if(t%5!=0){
                canvas.drawLine(i,topline,i,topline+height,mPaint);
                canvas.drawLine(i,bottomline,i,bottomline-height,mPaint);
            }else {//长刻度线
                canvas.drawLine(i,topline,i,topline+height*2,mPaint);
                canvas.drawLine(i,bottomline,i,bottomline-height*2,mPaint);
                int hour = sceond/3600;
                int minute = sceond%3600/60;
                canvas.drawText(String.format("%02d",hour)+":"+String.format("%02d",minute),i,topline+height*4,mPaint);
                sceond += 3600;
            }
            t++;
        }
        drawData(canvas);
        canvas.restore();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getPointerCount() != 1){
            if(mScaleGestureDetector.onTouchEvent(event)){
                invalidate();
                return true;
            }
        }
        if(mGestureDetector.onTouchEvent(event)){
            return true;
        }
        if(event.getAction()==MotionEvent.ACTION_UP){
            if(mlinstener!=null){
                mlinstener.onChangeFinish(bean = new SeekerTime(getSecond()));
            }
            Log.i("timeseeker","ACTION_UP");
        }
        return true;
    }

    ScaleGestureDetector mScaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener(){
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scale = detector.getScaleFactor();
            float before = offset - getMeasuredWidth()/2;
            if(scale>1){
                spaceClock = (int) (spaceClock *1.1f);
                scale = 1.095f;
            }
            else{
                spaceClock *= 0.9f;
                scale = 0.895f;
            }
            if(spaceClock<=20){
                spaceClock=20;
                scale = 1f;
            }
            if(spaceClock>=300){
                spaceClock = 300;
                scale = 1f;
            }
            if(spaceClock>20 && spaceClock<300)
                offset = before*scale +getMeasuredWidth()/2;
//            resolveOffset();
            Log.i("timeseeker","onScale:"+scale+",spaceClock:"+spaceClock+",before:"+before+",after:"+(offset-getMeasuredWidth()/2));
            invalidate();
            return true;
        }
    });

    float getLength(){
        return spaceClock*24*5;
    }

    GestureDetector mGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onDown(MotionEvent e) {
            if(mlinstener!=null){
                mlinstener.onChangeStart(bean = new SeekerTime(getSecond()));
            }
            return true;
        }
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            offset-=distanceX;
            resolveOffset();
            invalidate();
            if(mlinstener!=null){
                mlinstener.onChanging(bean = new SeekerTime(getSecond()));
            }
            Log.i("timeseeker",",offset:"+offset);
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            startFling(velocityX/2);
            return true;
        }
    });

    private void startFling(float velocityX) {
        float s = offset + velocityX;
        if(s>getMeasuredWidth()/2){
            s = getMeasuredWidth()/2;
        }
        if(s < -spaceClock*5*24+getMeasuredWidth()/2){
            s = -spaceClock*5*24+getMeasuredWidth()/2;
        }
        anim = ValueAnimator.ofFloat(offset,s);
        anim.setDuration(666);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.start();
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if(mlinstener!=null){
                    mlinstener.onChangeFinish(bean = new SeekerTime(getSecond()));
                }
            }
        });
    }

    /**
     * 限制范围
     */
    void resolveOffset(){
        if(offset>getMeasuredWidth()/2){
            offset = getMeasuredWidth()/2;
        }
        if(offset < -spaceClock*5*24+getMeasuredWidth()/2){
            offset = -spaceClock*5*24+getMeasuredWidth()/2;
        }
    }

    private OnTimeChangeLinstener mlinstener;
    public void setOnTimeChangeLinstener(OnTimeChangeLinstener l){
        mlinstener = l;
    }


    public class SeekerTime{
        private int second;
        private String secondStr;

        public SeekerTime(float second) {
            this.second = (int)second;
            float hour = this.second/3600f;
            float minute = this.second%3600f/60f;
            float sec = this.second%3600f%60f;
            this.secondStr = String.format("%02d",(int)hour)+":"+String.format("%02d",(int)minute)+":"+String.format("%02d",(int)sec);
        }

        public int getSecond() {
            return second;
        }

        public String getSecondStr() {
            return secondStr;
        }
    }

    public interface OnTimeChangeLinstener {
        void onChangeStart(SeekerTime time);
        void onChanging(SeekerTime time);
        void onChangeFinish(SeekerTime time);
    }


}
