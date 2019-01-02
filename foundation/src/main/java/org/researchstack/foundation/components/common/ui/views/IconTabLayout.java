package org.researchstack.backbone.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;

import org.researchstack.backbone.R;

public class IconTabLayout extends TabLayout {
    private int tabIconColor = Color.WHITE;
    private int tabIconIndicatorColor = Color.RED;
    private int tabTextColor = Color.WHITE;

    public IconTabLayout(Context context) {
        this(context, null);
    }

    public IconTabLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, R.attr.icontablayoutStyle);
    }

    public IconTabLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.IconTabLayout,
                defStyleAttr,
                R.style.Widget_Backbone_IconTabLayout);

        Drawable background = a.getDrawable(R.styleable.IconTabLayout_android_background);
        setBackground(background);

        int minHeight = a.getDimensionPixelSize(R.styleable.IconTabLayout_android_minHeight, 0);
        setMinimumHeight(minHeight);

        tabIconColor = a.getColor(R.styleable.IconTabLayout_tabIconColor, tabIconColor);
        tabIconIndicatorColor = a.getColor(R.styleable.IconTabLayout_tabIconIndicatorColor,
                tabIconIndicatorColor);
        tabTextColor = a.getColor(R.styleable.IconTabLayout_tabTextColor, tabTextColor);

        a.recycle();
    }

    public TabLayout.Tab addIconTab(int title, int icon, boolean showIndicator, boolean isSelected) {
        TabLayout.Tab tabItem = newTab();
        IconTab iconTab = new IconTab(getContext());
        iconTab.setText(title);
        iconTab.setTextColor(tabTextColor);
        iconTab.setIcon(icon);
        iconTab.setIconTint(tabIconColor);
        iconTab.setIsIndicatorShow(showIndicator);
        iconTab.setIndicatorTint(tabIconIndicatorColor);
        iconTab.setSelected(isSelected);
        iconTab.setOnClickListener(v -> tabItem.select());
        tabItem.setCustomView(iconTab);
        if (isSelected) {
            tabItem.select();
        }
        addTab(tabItem);
        return tabItem;
    }

    public static class OnTabSelectedListenerAdapter implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(Tab tab) {
        }

        @Override
        public void onTabUnselected(Tab tab) {
        }

        @Override
        public void onTabReselected(Tab tab) {
        }
    }

}
