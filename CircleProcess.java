

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;


public  class CircleProcess extends View {
    private int max=100;
    private int min=0;
    private int progress=0;
    private int color =Color.rgb(255,100,0);
    private int textsize =40;
    private float strokeWidth =26;
    private Paint backGroupPaint;
    private Paint foreGroupPaint;
    private Paint percentPaint;
    private RectF rectF;
    boolean animatation =true;
    private int  valueDRAW=0; // animate
    private boolean autoColor=false;



//constractor -----------------------------------------------------------------------
    public CircleProcess(Context context) {
        super(context);
        init(context,null,0);

    }

    public CircleProcess(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init(context ,attrs,defStyleAttr);

    }


    public CircleProcess(Context context, @Nullable AttributeSet attrs) {
       this(context,attrs,0);
    }
        //init -------------------------------------------------------------------------------
    private void init(Context context,@Nullable AttributeSet attrs,  int defStyleAttr) {
       //Attributes set -----------------------------------------------in init
        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs,R.styleable.circleProcess,defStyleAttr,0);
        setMax(ta.getInt(R.styleable.circleProcess_cp_max,100));
        setMin(ta.getInt(R.styleable.circleProcess_cp_min,0));
        setProgress(ta.getInt(R.styleable.circleProcess_cp_progress,0));

        color =ta.getColor(R.styleable.circleProcess_cp_color, Color.rgb(255,100,0));
        strokeWidth=ta.getDimensionPixelSize(R.styleable.circleProcess_cp_strokeWidth,26);
        ta.recycle();


        //set paints
        //set backGroupPaint
        backGroupPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        backGroupPaint.setColor(adjustAlpha(color,0.2f));
        backGroupPaint.setStyle(Paint.Style.STROKE);
        backGroupPaint.setStrokeWidth(strokeWidth);
        //set foreGroupPaint
        foreGroupPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        foreGroupPaint.setColor(color);
        foreGroupPaint.setStyle(Paint.Style.STROKE);
        foreGroupPaint.setStrokeWidth(strokeWidth);
        //percent paint ------------------------------------------------in init
        percentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        percentPaint.setColor(color);
        percentPaint.setTypeface(Typeface.create(Typeface.MONOSPACE,Typeface.BOLD));
        Float density = context.getResources().getDisplayMetrics().density;
        percentPaint.setTextSize(textsize*density);
        percentPaint.setTextAlign(Paint.Align.CENTER);
        //rectf ----------------------------------------------------------
        rectF = new RectF();
        //


    }
  // getters and setters --------------------------------------------
    public void setMax(int max) {
      this.max = (max>min) ? max :100;
        invalidate();
    }

    public void setMin(int min) {
      this.min =(min>0) ? min : 0;
        invalidate();
    }

    public void setProgress(int progress) {
        if(max<=progress)
            this.progress=max;
        else if(progress<min){
            this.progress =min;
        }else
            this.progress=progress;
        invalidate();
    }

    public void setColor(@ColorInt int color) {
        this.color = color;
        foreGroupPaint.setColor(color);
       backGroupPaint.setColor(adjustAlpha(color,0.2f));
       percentPaint.setColor(color);
        invalidate();
    }

    public void setStrokeWidth(float strokeWidth) {
        if(strokeWidth<0 || strokeWidth==this.strokeWidth)return;
        this.strokeWidth = strokeWidth;
        backGroupPaint.setStrokeWidth(strokeWidth);
        foreGroupPaint.setStrokeWidth(strokeWidth);
        invalidate();
        requestLayout();
    }

    public int getMax() {
        return max;
    }

    public int getMin() {
        return min;
    }

    public int getProgress() {
        return progress;
    }

    public int getColor() {
        return color;
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public void setAutoColor(boolean autoColor) {
        this.autoColor = autoColor;
        invalidate();
    }

    public boolean isAutoColor() {
        return autoColor;
    }
// on measure ------------------------------------------------------------

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width =getDefaultSize(getSuggestedMinimumWidth(),widthMeasureSpec);
        int height = getDefaultSize(getSuggestedMinimumHeight(),heightMeasureSpec);
        int min = Math.min(width,height);
        setMeasuredDimension(min,min);
        rectF.set(strokeWidth/2,strokeWidth/2,min-strokeWidth/2,min-strokeWidth/2);
    }
    //Ondraw-----------------------------------------------------------------------------------------------------

    @Override
    protected void onDraw(Canvas canvas) {
        int percent =(progress-min)*100 / (max-min);
        if(autoColor){
            int red = 255 - (255*percent)/max;
            int green = (255*percent)/max;
            foreGroupPaint.setColor(Color.rgb(red,green,0));
            backGroupPaint.setColor(adjustAlpha(Color.rgb(red,green,0),0.2f));
            percentPaint.setColor(Color.rgb(red,green,0));
            invalidate();

        } else {
            setColor(color);
        }



        canvas.drawOval(rectF,backGroupPaint);
        int sweepAngle =((progress-min)*360 )/(max-min);
        canvas.drawArc(rectF,-90,sweepAngle,false,foreGroupPaint);
        String percentLabale = String.valueOf(percent) + "%";
        int w =getWidth();
        int h =getHeight();
        Float x = Float.valueOf((w /2));
        Float y = Float.valueOf((h/2));
        Rect bounds =new Rect();
        percentPaint.getTextBounds(percentLabale,0,percentLabale.length(),bounds);
        y+= bounds.height()/2;
        canvas.drawText(percentLabale,x,y,percentPaint);

    }

    private int adjustAlpha(@ColorInt int color , Float factor){
        if(factor>1f || factor<0f){
            return color;
        }
        int alpha = Math.round( Color.alpha(color) * factor);
        return Color.argb(alpha,Color.red(color),Color.green(color),Color.blue(color));

    }

    public int getTextsize() {
        return textsize;

    }

    public void setTextsize(int textsize) {
        this.textsize = textsize;
        invalidate();
    }

    public void setAprogressWithTime (int toPROGRESS,long timeOfProgress){
            if(toPROGRESS<this.progress)return;


             int prevProgress = this.progress;
            ValueAnimator valueAnimator = ValueAnimator.ofInt(prevProgress, toPROGRESS);
            int delta = Math.abs(this.progress - prevProgress);

            valueAnimator.setDuration(timeOfProgress);
            valueAnimator.start();
            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    progress = (int) animation.getAnimatedValue();
                    setProgress(progress);
                }
            });

    }

    //save state ----------------------------------------------------------------------- in  orientation

    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        valueSaveState vss = new valueSaveState(super.onSaveInstanceState());
        vss.value =getProgress();
        vss.valueDRAW=valueDRAW;
        return vss;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {

        valueSaveState vss= (valueSaveState)state;
        super.onRestoreInstanceState(vss.getSuperState());
        animatation=false;
        setProgress(vss.value);
        animatation=true;


    }

    public static class valueSaveState extends BaseSavedState {
        int valueDRAW;
        int value;
        public valueSaveState(Parcel source) {
            super(source);
            value=source.readInt();
            valueDRAW =source.readInt();

        }

        public valueSaveState(Parcelable superState) {
            super(superState);
        }





        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(value);
            out.writeInt(valueDRAW);
        }
        public static final Parcelable.Creator<valueSaveState> CREATOR= new Creator<valueSaveState>() {
            @Override
            public valueSaveState createFromParcel(Parcel source) {
                return new valueSaveState(source);
            }

            @Override
            public valueSaveState[] newArray(int size) {
                return new valueSaveState[size];
            }
        };
    }


}
