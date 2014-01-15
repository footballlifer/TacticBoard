package nanofuntas.tb;

import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.tb.R;

public class TutorialActivity extends FragmentActivity {

	/**
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a
	 * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
	 * will keep every loaded fragment in memory. If this becomes too memory
	 * intensive, it may be best to switch to a
	 * {@link android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	SectionsPagerAdapter mSectionsPagerAdapter;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(
				getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.tutorial, menu);
		return true;
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {

		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			// getItem is called to instantiate the fragment for the given page.
			// Return a DummySectionFragment (defined as a static inner class
			// below) with the page number as its lone argument.
			
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
			default: 

			}
			
			Fragment fragment = new TutorialFragment1();
			Bundle args = new Bundle();
			args.putInt(TutorialFragment1.ARG_SECTION_NUMBER, position + 1);
			fragment.setArguments(args);
			return fragment;
		}

		@Override
		public int getCount() {
			// Show 3 total pages.
			return 3;
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
