package nanofuntas.tb;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tb.R;

public class TutorialActivity extends FragmentActivity {
	private final boolean DEBUG = true;
	private final String TAG = "TutorialActivity";
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.tutorial, menu);
		return true;
	}

	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				Fragment fragment1 = new TutorialFragment1();
				return fragment1;
			case 1:
				Fragment fragment2 = new TutorialFragment2();
				return fragment2;
			case 2:
				Fragment fragment3 = new TutorialFragment3();
				return fragment3;
			case 3:
				Fragment fragmentFinal = new TutorialFinalFragment();
				return fragmentFinal;
				
			default: 
				Log.d(TAG, "getItem() default called");
				return new TutorialFinalFragment();
			}
		}

		@Override
		public int getCount() {
			return 4;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.title_section1).toUpperCase(l);
			case 1:
				return getString(R.string.title_section2).toUpperCase(l);
			case 2:
				return getString(R.string.title_section3).toUpperCase(l);
			case 3:
				return getString(R.string.title_section4).toUpperCase(l);
			}
			return null;
		}
	}

	public static class TutorialFragment1 extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";
		public TutorialFragment1() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tutorial1, 
					container, false);
			return rootView;
		}
	}

	public static class TutorialFragment2 extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";
		public TutorialFragment2() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tutorial2,
					container, false);
			return rootView;
		}
	}
	
	public static class TutorialFragment3 extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";
		public TutorialFragment3() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tutorial3,
					container, false);
			return rootView;
		}
	}
	
	public static class TutorialFinalFragment extends Fragment {
		public static final String ARG_SECTION_NUMBER = "section_number";
		public TutorialFinalFragment() {}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_tutorial_final,
					container, false);
			return rootView;
		}
		
	    @Override
	    public void onActivityCreated(Bundle savedInstanceState){
	    	super.onActivityCreated(savedInstanceState);
	    	
	    	Button b = (Button)getView().findViewById(R.id.button1);
	    	b.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent i = new Intent(getView().getContext(), TacticBoardActivity.class);
					startActivity(i);
				}
			});
	    }
	}
}
