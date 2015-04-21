package es.uam.eps.dadm.adrian_lorenzo.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.uam.eps.dadm.adrian_lorenzo.R;
import es.uam.eps.dadm.adrian_lorenzo.database.DatabaseAdapter;
import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class FragmentStatistics extends Fragment {

	private static final String ARG_SECTION_NUMBER = "section_number";

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_statistics, container,
				false);
		ListView lv = (ListView) view.findViewById(R.id.listview);

		DatabaseAdapter db = new DatabaseAdapter(getActivity());
		db.open();
		Cursor c = db.getNumPartidas();
		while (c.moveToNext()) {
			TextView t = (TextView) view.findViewById(R.id.numberGames);
			t.setText(" " + String.valueOf(c.getString(0)));
		}
		db.close();

		// create the grid item mapping
		String[] from = new String[] { "rowid", "col_1", "col_2" };
		int[] to = new int[] { R.id.item1, R.id.item2, R.id.item3 };

		// prepare the list of all records
		List<HashMap<String, String>> fillMaps = new ArrayList<HashMap<String, String>>();
		db.open();

		Cursor users = db.getAllUsers();
		while (users.moveToNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			String username = users.getString(0);

			map.put("rowid", username);

			Cursor wins = db.getWinsScores(username);
			Cursor defeats = db.getDefeatsScores(username);
			wins.moveToFirst();
			defeats.moveToFirst();

			map.put("col_1", String.valueOf(wins.getInt(0)));
			map.put("col_2", String.valueOf(defeats.getInt(0)));
			fillMaps.add(map);

		}

		db.close();

		// fill in the grid_item layout
		SimpleAdapter adapter = new SimpleAdapter(getActivity(), fillMaps,
				R.layout.item_score, from, to);
		lv.setAdapter(adapter);

		return view;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		((MainMenuActivity) activity).onSectionAttached(getArguments().getInt(
				ARG_SECTION_NUMBER));
	}

}
