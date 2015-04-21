package es.uam.eps.dadm.adrian_lorenzo.menu;

import es.uam.eps.dadm.adrian_lorenzo.MainActivity;
import es.uam.eps.dadm.adrian_lorenzo.R;
import es.uam.eps.dadm.adrian_lorenzo.R.id;
import es.uam.eps.dadm.adrian_lorenzo.R.layout;
import es.uam.eps.dadm.adrian_lorenzo.R.menu;
import es.uam.eps.dadm.adrian_lorenzo.R.string;
import es.uam.eps.dadm.adrian_lorenzo.SettingsActivity;
import es.uam.eps.dadm.adrian_lorenzo.account.LoginActivity;
import es.uam.eps.dadm.adrian_lorenzo.account.SignInActivity;
import es.uam.eps.dadm.adrian_lorenzo.account.LoginActivity.UserLoginTask;
import es.uam.eps.dadm.adrian_lorenzo.database.DatabaseAdapter;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainMenuActivity extends Activity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	/**
	 * Fragment managing the behaviors, interactions and presentation of the
	 * navigation drawer.
	 */
	private NavigationDrawerFragment mNavigationDrawerFragment;

	/**
	 * Used to store the last screen title. For use in
	 * {@link #restoreActionBar()}.
	 */
	private CharSequence mTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main_menu);
		getActionBar().setIcon(
				   new ColorDrawable(getResources().getColor(android.R.color.transparent))); 
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();

		// Set up the drawer.
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		
		
	}

	@Override
	public void onNavigationDrawerItemSelected(int position) {
		// update the main content by replacing fragments
		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager
				.beginTransaction()
				.replace(R.id.container,
						PlaceholderFragment.newInstance(position + 1)).commit();
	}

	public void onSectionAttached(int number) {
		switch (number) {
		case 1:
			mTitle = getString(R.string.title_activity_modo_local);
			break;
		case 2:
			mTitle = getString(R.string.title_activity_modo_online);
			break;
		case 3:
			mTitle = getString(R.string.title_fragment_active_rounds);
			break;
		case 4:
			mTitle = getString(R.string.title_activity_statistics);
			break;
		case 5:
			mTitle = getString(R.string.title_activity_ayuda);
			break;
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (!mNavigationDrawerFragment.isDrawerOpen()) {
			// Only show items in the action bar relevant to this screen
			// if the drawer is not showing. Otherwise, let the drawer
			// decide what to show in the action bar.
			getMenuInflater().inflate(R.menu.main_menu, menu);
			restoreActionBar();
			return true;
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			Intent intent = new Intent(this, SettingsActivity.class);
	        this.startActivity(intent);
	        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private static final String ARG_SECTION_NUMBER = "section_number";

		/**
		 * Returns a new instance of this fragment for the given section number.
		 */
		public static Fragment newInstance(int sectionNumber) {
			Fragment fragment = new ModoLocalFragment();
			switch(sectionNumber){
		case 1:
			fragment = new ModoLocalFragment();
			break;
		case 2:
			fragment = new FragmentModoOnline();
			break;
		case 3:
			fragment = new FragmentActiveRounds();
			break;
		case 4:
			fragment = new FragmentStatistics();
			break;
		case 5:
			fragment = new AyudaFragment();
			break;
		}
			Bundle args = new Bundle();
			args.putInt(ARG_SECTION_NUMBER, sectionNumber);
			fragment.setArguments(args);
			return fragment;
		}

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main_menu,
					container, false);
			return rootView;
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainMenuActivity) activity).onSectionAttached(getArguments()
					.getInt(ARG_SECTION_NUMBER));
		}
	}
	
	
//public static class StatisticsFragment extends Fragment{
//		
//		private static final String ARG_SECTION_NUMBER = "section_number";
//		
//		public View onCreateView(LayoutInflater inflater, ViewGroup container,
//				Bundle savedInstanceState) {
//		
//			this.getActivity().startActivity(new Intent(this.getActivity(), StatisticsActivity.class));	
//			this.getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//			this.getActivity().finish();
//	
//			View view = this.getView();
//			return view;
//
//		}
//}
	
	

	
	public static class ModoLocalFragment extends Fragment{
		
		private static final String ARG_SECTION_NUMBER = "section_number";
		
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_modo_local, container, false);
			
			Button modoLocalButton = (Button) view.findViewById(R.id.button_modo_local);
			modoLocalButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View view) {
				
					Intent intent = new Intent(getActivity(), MainActivity.class);
					intent.putExtra(SettingsActivity.LOCAL_GAME, SettingsActivity.LOCAL_GAME);
					startActivity(intent);
					getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
					getActivity().finish();
				}
			});
			
			return view;

		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainMenuActivity) activity).onSectionAttached(getArguments()
					.getInt(ARG_SECTION_NUMBER));
		}
	}
	
	public static class AyudaFragment extends Fragment{
		
		private static final String ARG_SECTION_NUMBER = "section_number";
		
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.fragment_ayuda, container, false);
			return view;
		
		
		}
		
		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
			((MainMenuActivity) activity).onSectionAttached(getArguments()
					.getInt(ARG_SECTION_NUMBER));
		}
	}


	
	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, LoginActivity.class));
    	finish();
	}
	



}
