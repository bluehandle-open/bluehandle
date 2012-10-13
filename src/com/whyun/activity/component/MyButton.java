package com.whyun.activity.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.whyun.bluetooth.R;
public class MyButton extends Button implements OnClickListener {  
    //private Paint mPaint;  
    //private Context mContext;  
//    private static final String mString = "Welcome to Mr Wei's blog"; 
//	private Integer keyValue;
	
      
    public MyButton(Context context) {  
        super(context);  
//        mPaint = new Paint();  
    }  
    public MyButton(Context context,AttributeSet attrs)  
    {  
        super(context,attrs);  
        //mPaint = new Paint();  
          
        TypedArray a = context.obtainStyledAttributes(attrs,  
                R.styleable.MyButton);  
          
//        int textColor = a.getColor(R.styleable.MyView_textColor,  
//                0XFFFFFFFF);  
//        float textSize = a.getDimension(R.styleable.MyView_textSize, 36);  
          
//        mPaint.setTextSize(textSize);  
//        mPaint.setColor(textColor);  
//        String keyname = a.getString(R.styleable.MyButton_keyname);
//        keyValue = KeyMap.KEY_MAP.get(keyname);
        a.recycle();  
        this.setOnClickListener(this);
    }  
    @Override  
    protected void onDraw(Canvas canvas) {  
        super.onDraw(canvas);  
        //设置填充  
//        mPaint.setStyle(Style.FILL);  
//          
//        //画一个矩形,前俩个是矩形左上角坐标，后面俩个是右下角坐标  
//        canvas.drawRect(new Rect(10, 10, 100, 100), mPaint);  
//          
//        mPaint.setColor(Color.BLUE);  
//        //绘制文字  
//        canvas.drawText(mString, 10, 110, mPaint);  
    }
	@Override
	public void onClick(View v) {

	}  
}  