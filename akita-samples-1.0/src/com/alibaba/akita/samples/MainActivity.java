package com.alibaba.akita.samples;

import android.content.Intent;
import android.os.Bundle;
import com.alibaba.akita.uitpl.activity.AbsBottomTabActivity;

public class MainActivity extends AbsBottomTabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    // default Templates


    }

    @Override
    protected void presetTab() {
        Integer[] tabImages = { R.drawable.ic_launcher, R.drawable.ic_launcher };
        String[] tabLabels = { "朋友动态", "拍照" };
        Intent[] intents = new Intent[2];
        intents[0] = new Intent(MainActivity.this, Page1.class);
        intents[1] = new Intent(MainActivity.this, Page2.class);
        doSetTab(tabLabels, tabImages, intents);
    }


}