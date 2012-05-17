package com.alibaba.akita.samples;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.alibaba.akita.ui.activity.AbsBottomTabActivity;

public class TabActivity1 extends AbsBottomTabActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);    // default Templates

        /*TopApi topApi = ProxyFactory.getProxy(TopApi.class);
        try {
            ItemGetResult result = topApi.taobao_item_get(
                    "F05F6769F2F4C22D679C70CE054D7798", "2012-05-14+17%3A01%3A05",
                    "2.0", 12129701, "taobao.item.get", "top-apitools", "json",
                    15824668129L, "detail_url,num_iid,title,nick,type,cid,seller_cids,desc");
            Toast.makeText(this, result.item_get_response.item.desc, Toast.LENGTH_LONG).show();
            // ...
        } catch (AkInvokeException e) {
            e.printStackTrace();  //defaults
        } catch (AkServerStatusException e) {
            e.printStackTrace();  //defaults
        }*/
    }

    @Override
    protected void presetTab() {
        Integer[] tabImages = { R.drawable.ic_launcher,
                R.drawable.ic_launcher, R.drawable.ic_launcher };
        String[] tabLabels = { "TabPage1", "TabPage2", "TabPage3" };
        Intent[] intents = new Intent[3];
        intents[0] = new Intent(TabActivity1.this, TabPage1.class);
        intents[1] = new Intent(TabActivity1.this, TabPage2.class);
        intents[2] = new Intent(TabActivity1.this, TabPage3.class);
        doSetTab(tabLabels, tabImages, intents, Color.YELLOW, Color.BLACK);
    }
}


