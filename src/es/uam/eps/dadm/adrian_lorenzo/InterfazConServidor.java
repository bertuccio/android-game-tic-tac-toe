package es.uam.eps.dadm.adrian_lorenzo;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class InterfazConServidor {

	private static final String BASE_URL = "http://ptha.ii.uam.es/juegosreunidos/";
	private static final String ACCOUNT_PHP = "account.php";
	private static final String NEWROUND_PHP = "newround.php";
	private static final String OPENROUNDS_PHP = "openrounds.php";
	private static final String ADDPLAYER_ROUND_PHP = "addplayertoround.php";
	private static final String REMOVEPLAYER_ROUND_PHP = "removeplayerfromround.php";
	private static final String ACTIVEROUNDS_PHP = "activerounds.php";
	private static final String ISMYTURN_PHP = "ismyturn.php";
	private static final String NEWMOVEMENT_PHP = "newmovement.php";

	
	private RequestQueue queue;
	private static InterfazConServidor serverInterface;

	private InterfazConServidor(Context context) {
		queue = Volley.newRequestQueue(context);
	}

	public static InterfazConServidor getServer(Context context) {
		if (serverInterface == null)
			serverInterface = new InterfazConServidor(context);
		return serverInterface;
	}

	public void account(final String playername, final String playerpassword,
			Listener<String> callback, ErrorListener errorCallback) {

		String url = BASE_URL + ACCOUNT_PHP;
//		Log.d("Debug", url);

		StringRequest request = new StringRequest(Request.Method.POST, url,
				callback, errorCallback) {
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(SettingsActivity.KEY_PLAYER_NAME, playername);
				params.put(SettingsActivity.KEY_PLAYER_PASSWORD, playerpassword);

				return params;
			}
		};

		queue.add(request);
	}

	public void login(final String playername, final String playerpassword,
			Listener<String> callback, ErrorListener errorCallback) {

		String url = BASE_URL + ACCOUNT_PHP;
		Log.d("Debug", url);

		StringRequest request = new StringRequest(Request.Method.POST, url,
				callback, errorCallback) {
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(SettingsActivity.KEY_PLAYER_NAME, playername);
				params.put(SettingsActivity.KEY_PLAYER_PASSWORD, playerpassword);
				params.put("login", "");
				return params;
			}
		};

		queue.add(request);
	}

	public void newRound(final String playeruuid, Listener<String> callback,
			ErrorListener errorCallback) {

		String url = BASE_URL + NEWROUND_PHP;
//		Log.d("Debug", url);

		StringRequest request = new StringRequest(Request.Method.POST, url,
				callback, errorCallback) {
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(SettingsActivity.KEY_GAME_ID,
						SettingsActivity.GAME_ID);
				params.put(SettingsActivity.KEY_PLAYER_ID, playeruuid);
				return params;
			}
		};

		queue.add(request);
	}

	public void openRounds(final String playeruuid,
			Listener<JSONArray> callback, ErrorListener errorCallback) {

		String url = BASE_URL + OPENROUNDS_PHP + "?"
				+ SettingsActivity.KEY_GAME_ID + "=" + SettingsActivity.GAME_ID
				+ "&" + SettingsActivity.KEY_PLAYER_ID + "=" + playeruuid
				+ "&json";
//		Log.d("Debug", url);

		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, callback,
				errorCallback);
		queue.add(jsonArrayRequest);

	}

	public void addPlayerToRound(final String playeruuid, final String roundid,
			Listener<String> callback, ErrorListener errorCallback) {

		String url = BASE_URL + ADDPLAYER_ROUND_PHP;
//		Log.d("Debug", url);

		StringRequest request = new StringRequest(Request.Method.POST, url,
				callback, errorCallback) {
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(SettingsActivity.KEY_ROUND_ID, roundid);
				params.put(SettingsActivity.KEY_PLAYER_ID, playeruuid);
				return params;
			}
		};

		queue.add(request);
	}

	public void removePlayerToRound(final String playeruuid,
			final String roundid, Listener<String> callback,
			ErrorListener errorCallback) {

		String url = BASE_URL + REMOVEPLAYER_ROUND_PHP;
//		Log.d("Debug", url);

		StringRequest request = new StringRequest(Request.Method.POST, url,
				callback, errorCallback) {
			protected Map<String, String> getParams() {
				Map<String, String> params = new HashMap<String, String>();
				params.put(SettingsActivity.KEY_ROUND_ID, roundid);
				params.put(SettingsActivity.KEY_PLAYER_ID, playeruuid);
				return params;
			}
		};

		queue.add(request);
	}

	public void activeRounds(final String playeruuid,
			Listener<JSONArray> callback, ErrorListener errorCallback) {

		String url = BASE_URL + ACTIVEROUNDS_PHP + "?"
				+ SettingsActivity.KEY_GAME_ID + "=" + SettingsActivity.GAME_ID
				+ "&" + SettingsActivity.KEY_PLAYER_ID + "=" + playeruuid
				+ "&json";
//		Log.d("Debug", url);

		JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, callback,
				errorCallback);
		queue.add(jsonArrayRequest);

	}
	
	public void isMyTurn(final String playeruuid,
			final String roundid, Listener<JSONObject> callback,
			ErrorListener errorCallback) {
		
		String url = BASE_URL + ISMYTURN_PHP + "?"
				+ SettingsActivity.KEY_GAME_ID + "=" + SettingsActivity.GAME_ID
				+ "&" + SettingsActivity.KEY_PLAYER_ID + "=" + playeruuid
				+ "&" + SettingsActivity.KEY_ROUND_ID + "=" + roundid
				+ "&json";
//		Log.d("Debug", url);

		JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(url, null, callback,
				errorCallback);
		queue.add(jsonArrayRequest);

	}
	
	public void newMovement(final String playeruuid,
			final String roundid, final String codeboard, Listener<JSONObject> callback,
			ErrorListener errorCallback) {
		
		String url = BASE_URL + NEWMOVEMENT_PHP + "?"
				+ SettingsActivity.KEY_GAME_ID + "=" + SettingsActivity.GAME_ID
				+ "&" + SettingsActivity.KEY_PLAYER_ID + "=" + playeruuid
				+ "&" + SettingsActivity.KEY_ROUND_ID + "=" + roundid
				+ "&" + SettingsActivity.KEY_CODEBOARD_ID + "=" + codeboard
				+ "&json";
//		Log.d("Debug", url);

		JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(url, null, callback,
				errorCallback);
		queue.add(jsonArrayRequest);

	}

}
