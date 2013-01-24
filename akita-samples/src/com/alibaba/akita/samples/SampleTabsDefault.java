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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import com.alibaba.akita.widget.TabPageIndicator;
import com.alibaba.akita.widget.viewpagerindicator.TitleProvider;

public class SampleTabsDefault extends BaseSampleActivity {
	private static final String[] CONTENT = new String[] { "Recent", "Artists", "Albums", "Songs", "Playlists", "Genres" };
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.simple_tabs);
		
		mAdapter = new GoogleMusicAdapter(getSupportFragmentManager());
		
		mPager = (ViewPager)findViewById(R.id.pager);
		mPager.setAdapter(mAdapter);
		
		mIndicator = (TabPageIndicator)findViewById(R.id.indicator);
		mIndicator.setViewPager(mPager);
	}
	
	class GoogleMusicAdapter extends TestFragmentAdapter implements TitleProvider {
		public GoogleMusicAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return TestFragment.newInstance(SampleTabsDefault.CONTENT[position % SampleTabsDefault.CONTENT.length]);
		}

		@Override
		public int getCount() {
			return SampleTabsDefault.CONTENT.length;
		}

		@Override
		public String getTitle(int position) {
			return SampleTabsDefault.CONTENT[position % SampleTabsDefault.CONTENT.length].toUpperCase();
		}
	}
}
