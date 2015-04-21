package es.uam.eps.dadm.adrian_lorenzo.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;

import es.uam.eps.dadm.adrian_lorenzo.InterfazConServidor;
import es.uam.eps.dadm.adrian_lorenzo.MainActivity;
import es.uam.eps.dadm.adrian_lorenzo.R;
import es.uam.eps.dadm.adrian_lorenzo.SettingsActivity;
import es.uam.eps.dadm.adrian_lorenzo.database.DatabaseAdapter;

public class FragmentActiveRounds extends Fragment {

	private View view;
	private ListView lv;
	private Activity context = null;

	private static final String ARG_SECTION_NUMBER = "section_number";

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		if (context == null)
			context = getActivity();

		view = inflater.inflate(R.layout.fragment_active_rounds, container,
				false);

		lv = (ListView) view.findViewById(R.id.listview_active_rounds);

		if (SettingsActivity.getWorkWithConnection(context)) {

			getActiveRounds();

			view.findViewById(R.id.headerActiveRounds).setOnClickListener(
					new OnClickListener() {

						@Override
						public void onClick(View v) {

							getActiveRounds();
						}
					});

		}
		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainMenuActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

	public void getActiveRounds() {

		InterfazConServidor is = InterfazConServidor.getServer(context);

		String username = SettingsActivity.getSessionUser(context);

		DatabaseAdapter db = new DatabaseAdapter(context);
		db.open();

		String playerUuid = db.getUserUuid(username);
		db.close();
		is.activeRounds(playerUuid, new Listener<JSONArray>() {

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

			}
		});
	}

	private void processOpenRoundsJSONArray(JSONArray response)
			throws JSONException {

		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		int i = 0;
		String[] from = new String[] { "active_round_round_value",
				"active_round_username_value", "active_round_date_value" };
		int[] to = new int[] { R.id.active_round_round_value,
				R.id.active_round_username_value, R.id.active_round_date_value };

		for (i = 0; i < response.length(); i++) {


			JSONObject round = (JSONObject) response.get(i);
			HashMap<String, String> map = new HashMap<String, String>();

			String playernames = round
					.getString(SettingsActivity.KEY_JSON_PLAYERNAMES);

			String roundid = round.getString(SettingsActivity.KEY_ROUND_ID);
			String dateevent = round
					.getString(SettingsActivity.KEY_JSON_DATE_EVENT);

			map.put("active_round_username_value", playernames);
			map.put("active_round_round_value", String.valueOf(roundid));
			map.put("active_round_date_value", String.valueOf(dateevent));
			fillMaps.add(map);

		}

		((TextView) view.findViewById(R.id.number_active_rounds))
				.setText(String.valueOf(i));

		SimpleAdapter adapter = new SimpleAdapter(context, fillMaps,
				R.layout.item_active_round, from, to);
		lv.setAdapter(adapter);

		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, final View view,
					int position, long id) {

				// selected item
				String roundid = ((TextView) view
						.findViewById(R.id.active_round_round_value)).getText()
						.toString();

				String[] namesPlayers = ((TextView) view
						.findViewById(R.id.active_round_username_value))
						.getText().toString().split(",");

				SettingsActivity.setFirstPlayer(namesPlayers[0], context);
				SettingsActivity.setSecondPlayer(namesPlayers[1], context);

				SettingsActivity.setRoundId(roundid, context);

				startActivity(new Intent(context, MainActivity.class));
				context.finish();

			}
		});

	}

	public void refreshActiveRounds(View v) {
		getActiveRounds();
	}

}
