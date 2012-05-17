/*
 * Copyright 1999-2101 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 	http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.akita.samples;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import com.alibaba.akita.widget.TitlePageIndicator;

public class SampleTitlesStyledMethods extends BaseSampleActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_titles);
		
		mAdapter = new TestTitleFragmentAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		TitlePageIndicator indicator = (TitlePageIndicator)findViewById(R.id.indicator);
		mIndicator = indicator;
		indicator.setViewPager(mPager);
		
		final float density = getResources().getDisplayMetrics().density;
		indicator.setBackgroundColor(0x18FF0000);
		indicator.setFooterColor(0xFFAA2222);
		indicator.setFooterLineHeight(1 * density); //1dp
		indicator.setFooterIndicatorHeight(3 * density); //3dp
		indicator.setFooterIndicatorStyle(TitlePageIndicator.IndicatorStyle.Underline);
		indicator.setTextColor(0xAA000000);
		indicator.setSelectedColor(0xFF000000);
		indicator.setSelectedBold(true);
	}
}