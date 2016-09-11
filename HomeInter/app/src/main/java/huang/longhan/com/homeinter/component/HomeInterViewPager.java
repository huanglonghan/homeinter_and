package huang.longhan.com.homeinter.component;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by 龙汗 on 2016/3/20.
 */
public class HomeInterViewPager extends ViewPager {
    public HomeInterViewPager(Context context) {
        super(context);
    }

    public HomeInterViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount()>0){
            View view = getChildAt(0);
            view.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED));
            heightMeasureSpec = MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(),MeasureSpec.EXACTLY);
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
