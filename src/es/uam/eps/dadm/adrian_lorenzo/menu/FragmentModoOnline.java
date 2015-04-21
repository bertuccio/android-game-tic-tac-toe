package es.uam.eps.dadm.adrian_lorenzo.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;

import es.uam.eps.dadm.adrian_lorenzo.InterfazConServidor;
import es.uam.eps.dadm.adrian_lorenzo.MainActivity;
import es.uam.eps.dadm.adrian_lorenzo.R;
import es.uam.eps.dadm.adrian_lorenzo.SettingsActivity;
import es.uam.eps.dadm.adrian_lorenzo.database.DatabaseAdapter;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentModoOnline extends Fragment {

	private NewRoundTask mAuthTask = null;
	private View mProgressView;
	private View mButtonNewPartida;
	private View view;
	private boolean waitingResponse = true;
	private ListView lv;
	private boolean newGame = false;
	private Activity context = null;
	private String TWO_PLAYERS = "2\n";

	private static final String ARG_SECTION_NUMBER = "section_number";

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		view = inflater.inflate(R.layout.fragment_modo_online, container,
				false);

		lv = (ListView) view.findViewById(R.id.listview_rounds);
		mButtonNewPartida = (Button) view.findViewById(R.id.new_game_button);
		mProgressView = view.findViewById(R.id.newgame_progress);
		
		if (context == null)
			context = getActivity();
		
		if (SettingsActivity.getWorkWithConnection(context)) {

			mButtonNewPartida.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					showProgress(true);
					mAuthTask = new NewRoundTask();
					mAuthTask.execute((Void) null);
				}
			});

			view.findViewById(R.id.buttonSearchOpenRounds).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {

							getopenRounds();
						}
					});

			getopenRounds();
		}
		return view;

	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainMenuActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	public void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mButtonNewPartida.setVisibility(show ? View.GONE : View.VISIBLE);
			mButtonNewPartida.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mButtonNewPartida.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});

			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mProgressView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mProgressView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
			mButtonNewPartida.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	public boolean attemptNewRound() {

		InterfazConServidor is = InterfazConServidor.getServer(getActivity());

		String username = SettingsActivity.getSessionUser(getActivity());

		DatabaseAdapter db = new DatabaseAdapter(getActivity());
		db.open();
		String playerUuid = db.getUserUuid(username);
		db.close();
		
		is.newRound(playerUuid, new Listener<String>() {

			@Override
			public void onResponse(String uuid) {
				waitingResponse = false;
				if (!uuid.equals(SettingsActivity.ERROR_CODE)) {
					newGame = true;
					

				} else {
					newGame = false;
				}
			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				waitingResponse = false;
				newGame = false;
			}
		});
		return true;
	}

	public void getopenRounds() {

		InterfazConServidor is = InterfazConServidor.getServer(context);

		String username = SettingsActivity.getSessionUser(context);

		DatabaseAdapter db = new DatabaseAdapter(context);
		db.open();

		String playerUuid = db.getUserUuid(username);
		db.close();
		is.openRounds(playerUuid, new Listener<JSONArray>() {

			@Override
			public void onResponse(JSONArray response) {

				try {
					processOpenRoundsJSONArray(response);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				waitingResponse = false;
			}
		});
	}

	private void processOpenRoundsJSONArray(JSONArray response)
			throws JSONException {

		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();

		String[] from = new String[] { "round_username_value",
				"round_round_value", "round_date_value" };
		int[] to = new int[] { R.id.round_username_value,
				R.id.round_round_value, R.id.round_date_value };

		for (int i = 0; i < response.length(); i++) {

			JSONObject round = (JSONObject) response.get(i);
			HashMap<String, String> map = new HashMap<String, String>();

			String playernames = round
					.getString(SettingsActivity.KEY_JSON_PLAYERNAMES);
			
			if (!playernames.equals(SettingsActivity.getSessionUser(context))) {
				
				String roundid = round
						.getString(SettingsActivity.KEY_ROUND_ID);
				String dateevent = round
						.getString(SettingsActivity.KEY_JSON_DATE_EVENT);

				map.put("round_username_value", playernames);
				map.put("round_round_value", String.valueOf(roundid));
				map.put("round_date_value", String.valueOf(dateevent));
				fillMaps.add(map);

			}
		}
		SimpleAdapter adapter = new SimpleAdapter(context, fillMaps,
				R.layout.item_round, from, to);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {

				// selected item
				final String roundid = ((TextView) view
						.findViewById(R.id.round_round_value)).getText()
						.toString();
				
				final String firstPlayer = ((TextView) view
						.findViewById(R.id.round_username_value)).getText()
						.toString();
				SettingsActivity.setFirstPlayer(firstPlayer, context);
				

				InterfazConServidor is = InterfazConServidor.getServer(context);

				String username = SettingsActivity.getSessionUser(context);
				
				SettingsActivity.setSecondPlayer(username, context);
				DatabaseAdapter db = new DatabaseAdapter(context);
				db.open();

				String playerUuid = db.getUserUuid(username);
				db.close();

				is.addPlayerToRound(playerUuid, roundid,
						new Listener<String>() {

							@Override
							public void onResponse(String response) {

								if (response.equals(TWO_PLAYERS)) {
									
									SettingsActivity.setRoundId(roundid, context);
									startActivity(new Intent(context,MainActivity.class));
									context.finish();
									
								} else {
									
									toastErrorAddRound(context);
								}
							}

						}, new Response.ErrorListener() {

							@Override
							public void onErrorResponse(VolleyError error) {
								Log.d("Debug", error.getMessage());
								toastErrorAddRound(context);
							}
						});
			}
		});

	}

	public class NewRoundTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			try {

				attemptNewRound();
				while (waitingResponse);
				waitingResponse = true;

				Thread.sleep(1000);

			} catch (InterruptedException e) {
				return false;
			}
			return newGame;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);
			Toast toast;
			if (success) {

				toast = Toast.makeText(getActivity(),
						R.string.new_online_game_toast_success,
						Toast.LENGTH_SHORT);
				getopenRounds();
			} else {

				toast = Toast
						.makeText(getActivity(),
								R.string.new_online_game_toast_error,
								Toast.LENGTH_SHORT);
			}

			toast.setGravity(Gravity.CENTER, 0, 0);
			toast.show();
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	
	private void toastErrorAddRound(Context context){
		
		String message = getResources()
				.getString(
						R.string.join_online_game_toast_error);
		
		Toast toast = Toast.makeText(context,
				message,
				Toast.LENGTH_SHORT);
		toast.show();
		
	}
	
	public void refreshOpenRounds(View v){
		getopenRounds();
	}

}
