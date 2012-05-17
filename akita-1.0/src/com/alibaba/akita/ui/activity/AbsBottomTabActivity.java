/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.akita.ui.activity;

import android.app.ActivityGroup;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import com.alibaba.akita.R;
import com.alibaba.akita.ui.activity.bottomtabac.BottomTabImageAdapter;
import com.alibaba.akita.util.AndroidUtil;

public abstract class AbsBottomTabActivity extends ActivityGroup
{
    private GridView gv_tabPage;			//顶部Tab标签
    private BottomTabImageAdapter imageAdapter;		//图片适配器
    public LinearLayout pageContainer;		//放置子页面的容器
    private Intent[] intents;				//页面跳转Intent
    private Window[] subPageView;			//子页面视图View
    private Integer[] tabImages = { R.drawable.ic_launcher,	//tab标签图标
            R.drawable.ic_launcher };
    private String[] tabLabels = { "朋友动态",	//tab标签文字
            "拍照" };
    private int selectedColor;
    private int unSelectedColor;

    /**
     *  use doSetTab(...) to set these:
     *  tabImages tabLabels subPageView intents 修改
     *  and else directly...
     */
    protected abstract void presetTab();

    protected void doSetTab(String[] tabLabels, Integer[] tabImages, Intent[] intents,
                            int selectColor, int unSelectedColor) {
        this.tabLabels = tabLabels;
        this.tabImages = tabImages;
        this.intents = intents;
        this.selectedColor = selectColor;
        this.unSelectedColor = unSelectedColor;
        subPageView = new Window[intents.length];
        for(int i = 0; i < intents.length; i++) {
            subPageView[i] = null;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ac_absbottomtab);

        presetTab();

        gv_tabPage = (GridView) findViewById(R.id.gv_tabPage);
        gv_tabPage.setNumColumns(tabImages.length);// 设置列数
        gv_tabPage.setSelector(new ColorDrawable(Color.TRANSPARENT));//选中的时候为透明色
        gv_tabPage.setGravity(Gravity.CENTER);// 位置居中
        gv_tabPage.setVerticalSpacing(0);// 垂直间隔
        int width = this.getWindowManager().getDefaultDisplay().getWidth()//获取屏幕宽度
                / tabImages.length;//平分宽度
        imageAdapter = new BottomTabImageAdapter(   //创建图片适配器，传递图片所需高和宽
                this, tabImages, tabLabels, width, AndroidUtil.dp2px(this, 48),
                this.unSelectedColor, this.selectedColor
        );
        gv_tabPage.setAdapter(imageAdapter);// 设置菜单Adapter
        gv_tabPage.setOnItemClickListener(new ItemClickEvent()); //注册点击事件
        pageContainer = (LinearLayout) findViewById(R.id.pageContainer);
        SwitchPage(0);//默认打开第0页

    }

    class ItemClickEvent implements AdapterView.OnItemClickListener {

        public void onItemClick(AdapterView<?> arg0, View arg1, int idx,
                                long arg3) {
            SwitchPage(idx); //arg2表示选中的Tab标签号，从0~1
        }
    }

    /**
     * 用于获取intent和pageView，
     * 类似于单例模式，使得对象不用重复创建，同时，保留上一个对象的状态
     * 当重新访问时，仍保留原来数据状态，如文本框里面的值。
     * @param pageID 选中的tab序号（0~1）
     * @return
     */
    private Window getPageView(int pageID) {
        /*if(intents == null) {
            intents = new Intent[2];
            subPageView = new Window[2];
            intents[0] = new Intent(AbsBottomTabActivity.this, Page1.class);
            intents[1] = new Intent(AbsBottomTabActivity.this, Page2.class);
            for(int i = 0; i < 2; i++) {
                subPageView[i] = getLocalActivityManager().startActivity(
                        "subPageView" + i, intents[i]);
            }
            Log.v("New", "new");
        }*/
        if (subPageView[pageID] == null) {
            subPageView[pageID] = getLocalActivityManager().startActivity(
                    "subPageView" + pageID, intents[pageID]);
        }

        return subPageView[pageID];
    }

    /**
     * 根据ID打开指定的PageActivity
     * @param id 选中项的tab序号
     */
    private void SwitchPage(int id)
    {
        pageContainer.removeAllViews();//必须先清除容器中所有的View
        Window pageView = null;

        if (id <= subPageView.length-1) {
            imageAdapter.setFocus(id);
            pageView = getPageView(id);
        } else {
            imageAdapter.setFocus(0);
            pageView = getPageView(0);
        }

        //装载子页面View到LinearLayout容器里面
        pageContainer.addView(pageView.getDecorView(),
                LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
