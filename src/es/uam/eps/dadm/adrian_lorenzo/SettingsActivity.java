package es.uam.eps.dadm.adrian_lorenzo;

import es.uam.eps.dadm.adrian_lorenzo.account.LoginActivity;
import es.uam.eps.dadm.adrian_lorenzo.menu.MainMenuActivity;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class SettingsActivity extends Activity {

	private static final  String FIELD_USER = "user";
	private static final  String FIELD_PASSWORD = "password";
	private static final  String FIELD_ALERT_EXIT = "alert_exit";
	private static final  String FIELD_PREFLIST = "figure";
	private static final  String FIELD_SESSION_USER = "session_user";
	private static final  String FIELD_SECOND_PLAYER = "second_player";
	private static final  String FIELD_FIRST_PLAYER = "first_player";
	private static final  String FIELD_SESSION_GAME = "session_game";
	private static final  String FIELD_SESSION_ROUNDID = "session_roundid";
	private static final  String FIELD_WIFI = "wifi";
	public static final String KEY_PLAYER_NAME = "playername";
	public static final String KEY_PLAYER_PASSWORD= "playerpassword";
	public static final  String KEY_GAME_ID = "gameid";
	public static final  String KEY_PLAYER_ID = "playerid";
	public static final  String KEY_ROUND_ID = "roundid";
	public static final  String KEY_CODEBOARD_ID = "codedboard";
	public static final  String KEY_JSON_DATE_EVENT = "dateevent";
	public static final  String KEY_JSON_PLAYERNAMES = "playernames";
	public static final  String KEY_JSON_TURN = "turn";
	public static final  String ERROR_CODE = "-1";
	public static final  int NUM_PLAYERS = 2;
	public static final  String GAME_ID = "89";
	public static final String LOCAL_GAME = "local";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);

		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		SettingsFragment fragment = new SettingsFragment();
		fragmentTransaction.replace(android.R.id.content, fragment);
		fragmentTransaction.commit();

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public static class SettingsFragment extends PreferenceFragment {
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			addPreferencesFromResource(R.xml.settings);
		}
	}

	public static String getAutoLoginPassword(Context context) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return preferences.getString(FIELD_PASSWORD, null);
	}

	public static String getAutoLoginUser(Context context) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return preferences.getString(FIELD_USER, null);
	}

	public static void setAutoLogin(String user, String password,
			Context context) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(FIELD_USER, user);
		editor.putString(FIELD_PASSWORD, password);
		editor.commit();

	}

	public static void resetAutoLogin(Context context) {

		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(FIELD_USER, null);
		editor.putString(FIELD_PASSWORD, null);
		editor.commit();

	}
	
	public static void setSessionUser(String user, Context context){
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(FIELD_SESSION_USER, user);
		editor.commit();
	}
	
	public static String getSessionUser(Context context){
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return preferences.getString(FIELD_SESSION_USER, null);
	}

	public static boolean isAlertExitActivated(Context context) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return preferences.getBoolean(FIELD_ALERT_EXIT, false);
	}

	public static int getColorPlayer1(Context context) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		switch (Integer.parseInt(prefs.getString(FIELD_PREFLIST, "1"))) {
		case 1:

			return context.getResources()
					.getColor(R.color.tema1_circle_player1);
		default:

			return context.getResources()
					.getColor(R.color.tema2_circle_player1);
		}
	}

	public static int getColorPlayer2(Context context) {

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		switch (Integer.parseInt(prefs.getString(FIELD_PREFLIST, "1"))) {
		case 1:

			return context.getResources()
					.getColor(R.color.tema1_circle_player2);
		default:

			return context.getResources()
					.getColor(R.color.tema2_circle_player2);
		}
	}
	
	public static void setWorkWithConnection(Context context, boolean value){
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putBoolean(FIELD_WIFI, value);
		editor.commit();
	}
	
	public static boolean getWorkWithConnection(Context context){
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return preferences.getBoolean(FIELD_WIFI, false);
	}
	
	public static void setRoundId(String roundid, Context context){
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(FIELD_SESSION_ROUNDID, roundid);
		editor.commit();
	}
	
	public static String getRoundId(Context context){
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return preferences.getString(FIELD_SESSION_ROUNDID, null);
	}
	
	public static void setSecondPlayer(String name, Context context){
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(FIELD_SECOND_PLAYER, name);
		editor.commit();
	}
	
	public static String getSecondPlayer(Context context){
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return preferences.getString(FIELD_SECOND_PLAYER, null);
	}
	
	public static void setFirstPlayer(String name, Context context){
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(FIELD_FIRST_PLAYER, name);
		editor.commit();
	}
	
	public static String getFirstPlayer(Context context){
		
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(context);

		return preferences.getString(FIELD_FIRST_PLAYER, null);
	}
	
	public static boolean isWifiConnected(Context context){
		
		
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr
				.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

		return (networkInfo != null && networkInfo.isConnected());

	}
	
}
