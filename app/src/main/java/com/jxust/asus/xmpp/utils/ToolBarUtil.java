package com.jxust.asus.xmpp.utils;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jxust.asus.xmpp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by asus on 2016/8/4.
 *
 * @author Administrator
 * @time 2016/8/4 20:25
 */
public class ToolBarUtil {

    private List<TextView> mTextViews = new ArrayList<TextView>();

    public void createToolBar(LinearLayout container, String[] toolBarTitleArr, int[] iconArr){

        for(int i = 0;i < toolBarTitleArr.length;i++){
            TextView tv = (TextView) View.inflate(container.getContext(), R.layout.inflate_toolbar_btn,null);
            tv.setText(toolBarTitleArr[i]);
            // 动态修改textView里面的drawableTop属性
            tv.setCompoundDrawablesWithIntrinsicBounds(0,iconArr[i],0,0);   // 参数分别表示的左上右下的图片

            int width = 0;
            int height = LinearLayout.LayoutParams.MATCH_PARENT;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width,height);
            // 设置weight属性
            params.weight = 1;
            container.addView(tv,params);

            // 保存textView到集合中
            mTextViews.add(tv);
        }
    }

    public void changeColor(int position){

        // 还原所有的颜色
        for(TextView tv : mTextViews){
            tv.setSelected(false);

        }
        //
        mTextViews.get(position).setSelected(true);  // 通过设置selector属性，控制为选中效果
    }
}
