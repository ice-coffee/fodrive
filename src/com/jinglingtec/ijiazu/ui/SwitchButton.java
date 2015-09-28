package com.jinglingtec.ijiazu.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.jinglingtec.ijiazu.R;

/**
 * Created by coffee on 14-12-3.
 */
public class SwitchButton extends RelativeLayout
{

    private ImageView iv_on;
    private ImageView iv_off;
    private ImageView iv_bg;

    /**
     * 用于在代码中手工创建 对象
     *
     * @param context
     */
    public SwitchButton(Context context)
    {
        super(context);
    }

    /**
     * 从布局文件当中定义的对象，加调该方法创建对象
     *
     * @param context
     * @param attrs
     */
    public SwitchButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        initView();
    }

    /**
     * 初始化操作
     */
    public void initView()
    {

        //这里的this是关键
        View view = View.inflate(getContext(), R.layout.activity_setting_switch, this);
        Log.e("settingingintgggggasdf", view.toString());
        iv_on = (ImageView) view.findViewById(R.id.iv_setting_on);
        iv_off = (ImageView) view.findViewById(R.id.iv_setting_off);
        iv_bg = (ImageView) view.findViewById(R.id.iv_switch_bg);

    }

    public void changeState(boolean yes)
    {
        if (yes)
        {
            iv_on.setVisibility(VISIBLE);
            iv_off.setVisibility(INVISIBLE);
            iv_bg.setBackgroundResource(R.drawable.on_off_button_on_bg);
        }
        else
        {
            iv_on.setVisibility(INVISIBLE);
            iv_off.setVisibility(VISIBLE);
            iv_bg.setBackgroundResource(R.drawable.on_off_button_off_bg);
        }
    }
}
