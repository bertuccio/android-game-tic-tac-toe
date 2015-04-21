package es.uam.eps.dadm.adrian_lorenzo;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;

import es.uam.eps.dadm.adrian_lorenzo.database.DatabaseAdapter;
import es.uam.eps.dadm.adrian_lorenzo.logic.*;
import es.uam.eps.dadm.adrian_lorenzo.menu.MainMenuActivity;
import es.uam.eps.dadm.adrian_lorenzo.menu.FragmentModoOnline.NewRoundTask;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("ClickableViewAccessibility")
public class MainActivity extends Activity implements Jugador {

	protected Partida partida = null;
	protected Vista3Raya vista;
	private View mProgressView;
	private IsMyTurnTask isMyTurnTask = null;
	protected Jugador jugadorRival;
	protected boolean waitingResponse = true;
	protected static boolean turnoDisponible = false;
	private final String NAME_IA = "Máquina";
	private final String MY_TURN = "1";
	private boolean flagEndGame = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_main);
		turnoDisponible = false;
		mProgressView = findViewById(R.id.refresh_game);
		isMyTurnTask = null;
		// Crea la partida
		Tablero3Raya tablero = new Tablero3Raya();

		ArrayList<Jugador> jugadores = new ArrayList<Jugador>();

		if (getIntent().getStringExtra(SettingsActivity.LOCAL_GAME) != null) {
			
			((View) this.findViewById(R.id.buttonTerminarPartida))
				.setVisibility(View.GONE);
			
			jugadores.add(this);
			jugadorRival = new JugadorAleatorio(this.NAME_IA);
			jugadores.add(jugadorRival);

		} else {
			if (this.getNombre().equals(SettingsActivity.getFirstPlayer(this))) {
				jugadores.add(this);
				jugadorRival = new JugadorOnline(
						SettingsActivity.getSecondPlayer(this));
				jugadores.add(jugadorRival);
			} else {
				jugadorRival = new JugadorOnline(
						SettingsActivity.getFirstPlayer(this));
				jugadores.add(jugadorRival);
				jugadores.add(this);
			}

		}

		this.partida = new Partida(tablero, jugadores);

		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

		// Inicializa la vista
		vista = (Vista3Raya) this.findViewById(R.id.vista3Raya);

		vista.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				// Hasta que el jugador no reciba el turno no mueve
				if (turnoDisponible) {

					try {
						turnoDisponible = false;
						partida.realizaAccion(new AccionMover(
								MainActivity.this, new Movimiento3Raya(vista
										.getYTablero(event), vista
										.getXTablero(event))));

						if (getIntent().getStringExtra(
								SettingsActivity.LOCAL_GAME) == null) {

							attemptNewMovement(MainActivity.this.partida
									.getTablero().tableroToString());

						}
					} catch (ExcepcionJuego e) {

						turnoDisponible = true;
					}

				}
				return false;
			}
		});


		if (getIntent().getStringExtra(SettingsActivity.LOCAL_GAME) == null) {
			((View) this.findViewById(R.id.buttonNuevaPartida))
				.setVisibility(View.GONE);
			this.nuevaPartida(vista);
			showProgress(true);
			isMyTurnTask = new IsMyTurnTask();
			isMyTurnTask.execute((Void) null);
		}

	}

	@Override
	protected void onResume() {

		if (getIntent().getStringExtra(SettingsActivity.LOCAL_GAME) == null) {
			// Log.d("Debug", "onResume");
			flagEndGame = false;
			ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
			Tablero3Raya tablero = new Tablero3Raya();
			if (this.getNombre().equals(SettingsActivity.getFirstPlayer(this))) {
				jugadores.add(this);
				jugadorRival = new JugadorOnline(
						SettingsActivity.getSecondPlayer(this));
				jugadores.add(jugadorRival);
			} else {
				jugadorRival = new JugadorOnline(
						SettingsActivity.getFirstPlayer(this));
				jugadores.add(jugadorRival);
				jugadores.add(this);
			}
			this.partida = new Partida(this.partida.getTablero(), jugadores);
			if (isMyTurnTask == null
					&& (getIntent().getStringExtra(SettingsActivity.LOCAL_GAME) == null)) {
	
				isMyTurnTask = new IsMyTurnTask();
				isMyTurnTask.execute((Void) null);
			}
		}
		super.onResume();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		// Guarda el tablero en el paquete
		outState.putString(getResources().getString(R.string.tableroBundle),
				partida.getTablero().tableroToString());

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {

		String tableroOld = savedInstanceState.getString(getResources()
				.getString(R.string.tableroBundle));

		try {

			// Carga el tablero desde el paquete
			partida.getTablero().stringToTablero(tableroOld);
			vista.setTablero((Tablero3Raya) this.partida.getTablero());
		} catch (ExcepcionJuego e) {

			// Si hay algún problema genera una nueva partida
			Tablero3Raya tableroNew = new Tablero3Raya();

			ArrayList<Jugador> jugadores = new ArrayList<Jugador>();
			if (getIntent().getStringExtra(SettingsActivity.LOCAL_GAME) != null) {
				jugadores.add(this);
				jugadorRival = new JugadorAleatorio(this.NAME_IA);
				jugadores.add(jugadorRival);

			} else {
				if (this.getNombre().equals(
						SettingsActivity.getFirstPlayer(this))) {
					jugadores.add(this);
					jugadorRival = new JugadorOnline(
							SettingsActivity.getSecondPlayer(this));
					jugadores.add(jugadorRival);
				} else {
					jugadorRival = new JugadorOnline(
							SettingsActivity.getFirstPlayer(this));
					jugadores.add(jugadorRival);
					jugadores.add(this);
				}

			}

			this.partida = new Partida(tableroNew, jugadores);

		}
		super.onRestoreInstanceState(savedInstanceState);

	}

	@Override
	public void onCambioEnPartida(Evento evento) {

		switch (evento.getTipo()) {

		case Evento.EVENTO_CAMBIO:

			// actualiza la animación (de momento solo hay esa funcionalidad)
			vista.notificaCambioVista();

			if (partida.getTablero().getEstado() != Tablero.EN_CURSO
					&& !flagEndGame) {

				String msgGameEnding = getString(R.string.game_ending_msg);
				flagEndGame = true;

				if (partida.getTablero().getEstado() == Tablero.FINALIZADA) {

					int turno = evento.getPartida().getTablero().getTurno();
					String ganador = evento.getPartida().getJugador(turno)
							.getNombre();

					DatabaseAdapter db = new DatabaseAdapter(this);
					db.open();
					db.insertWinner(ganador);

					if (!ganador.equals(this.getNombre())) {
						db.insertLooser(this.getNombre());
					}
					db.close();

					msgGameEnding += " "
							+ getString(R.string.game_ending_msg_winner);
					msgGameEnding += " " + ganador;

				}

				else
					msgGameEnding += getString(R.string.game_ending_msg_dead_heat);

				// Genera una alerta mostrándole al usuario quién ha ganado (o
				// si se
				// ha termiando en tablas)
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setMessage(msgGameEnding);
				AlertDialog dialog = builder.create();
				if (getIntent().getStringExtra(SettingsActivity.LOCAL_GAME) == null) {
					dialog.setCancelable(true);
					dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
						public void onCancel(DialogInterface dialog) {
							MainActivity.this.terminarPartida(null);
						}
					});
				}
				dialog.show();
			}

			break;

		case Evento.EVENTO_CONFIRMA:

			// este jugador confirma al azar
			try {
				evento.getPartida().confirmaAccion(this, evento.getCausa(),
						(Math.random() > .5));
			} catch (Exception e) {

			}
			break;

		case Evento.EVENTO_TURNO:
			if (getIntent().getStringExtra(SettingsActivity.LOCAL_GAME) != null) {
				turnoDisponible = true;
			}
			break;
		}
	}

	/**
	 * 
	 * Este método se ejecuta cada vez que el usuario le da al botón
	 * "Nueva Partida"
	 * 
	 * @param v
	 */
	public void nuevaPartida(View v) {

		flagEndGame = false;
		Tablero3Raya tablero = new Tablero3Raya();

		ArrayList<Jugador> jugadores = new ArrayList<Jugador>();

		tablero = new Tablero3Raya();
		jugadores.add(this);
		jugadorRival = new JugadorAleatorio(this.NAME_IA);
		jugadores.add(jugadorRival);

		this.partida = new Partida(tablero, jugadores);

		// Carga el tablero a la vista
		vista.setTablero((Tablero3Raya) this.partida.getTablero());

		this.partida.comienzaPartida();

		DatabaseAdapter db = new DatabaseAdapter(this);
		db.open();
		db.insertGame(this.getNombre());
		db.close();

	}

	@Override
	public String getNombre() {

		return SettingsActivity.getSessionUser(this);

	}

	@Override
	public boolean puedeJugar(Tablero tablero) {
		return true;
	}

	public void terminarPartida(View v) {

		Log.d("Debug", "Terminando");

		if (SettingsActivity.getWorkWithConnection(this)) {

			InterfazConServidor is = InterfazConServidor.getServer(this);

			DatabaseAdapter db = new DatabaseAdapter(this);
			db.open();
			String username = SettingsActivity.getSessionUser(this);
			String playerUuid = db.getUserUuid(username);
			db.close();

			String roundid = SettingsActivity.getRoundId(this);

			is.removePlayerToRound(playerUuid, roundid, new Listener<String>() {

				@Override
				public void onResponse(String response) {

				}

			}, new Response.ErrorListener() {

				@Override
				public void onErrorResponse(VolleyError error) {
					Log.d("Debug", error.getMessage());
				}
			});

			startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
			finish();
		}

	}

	/**
	 * Genera un diálogo de alerta preguntando al usuario si desea salir. Se
	 * ejecuta en el momento en el que el usuario presiona el botón de salir
	 * (back).
	 * 
	 */
	@Override
	public void onBackPressed() {

		if (SettingsActivity.isAlertExitActivated(this)) {

			AlertDialog.Builder adb = new AlertDialog.Builder(this);

			String titleAlert = this.vista.getResources().getString(
					R.string.alert_exit_title);
			String yes = this.vista.getResources().getString(
					R.string.alert_exit_title_yes);
			String no = this.vista.getResources().getString(
					R.string.alert_exit_title_no);

			adb.setTitle(titleAlert);
			adb.setIcon(android.R.drawable.ic_dialog_alert);

			adb.setPositiveButton(yes, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

					waitingResponse = false;
					if (isMyTurnTask != null
							&& isMyTurnTask.getStatus() != AsyncTask.Status.FINISHED)
						isMyTurnTask.cancel(true);
					isMyTurnTask = null;
					startActivity(new Intent(MainActivity.this,
							MainMenuActivity.class));
					finish();

				}
			});

			adb.setNegativeButton(no, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {

				}
			});
			adb.show();

		} else {
			waitingResponse = false;

			if (isMyTurnTask != null
					&& isMyTurnTask.getStatus() != AsyncTask.Status.FINISHED)
				isMyTurnTask.cancel(true);

			isMyTurnTask = null;

			startActivity(new Intent(MainActivity.this, MainMenuActivity.class));
			finish();
		}

	}

	public class IsMyTurnTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {

			int time = 2000;

			while (!turnoDisponible) {
				try {
					if (isCancelled()) {
						// had canceled the task
						return (false); // don't forget to terminate this method
					}
					Thread.sleep(time);

				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (isCancelled()) {
					// canceled the task
					return (false); // don't forget to terminate this method
				}
				attemptIsMyTurn();
				while (waitingResponse)
					;

			}
			waitingResponse = true;

			return turnoDisponible;
		}

		@Override
		protected void onPostExecute(final Boolean success) {

			if (success
					&& MainActivity.this.partida.getTablero().getEstado() == Tablero.EN_CURSO) {

				String turnMsg = MainActivity.this.getString(R.string.turn_msg);
				AlertDialog.Builder builder = new AlertDialog.Builder(
						MainActivity.this);
				builder.setMessage(turnMsg);
				AlertDialog dialog = builder.create();
				dialog.show();
			}
		}

		public boolean attemptIsMyTurn() {

			InterfazConServidor is = InterfazConServidor
					.getServer(MainActivity.this);

			String username = SettingsActivity
					.getSessionUser(MainActivity.this);

			DatabaseAdapter db = new DatabaseAdapter(MainActivity.this);
			db.open();

			String playerUuid = db.getUserUuid(username);
			db.close();

			is.isMyTurn(playerUuid,
					SettingsActivity.getRoundId(MainActivity.this),
					new Listener<JSONObject>() {

						@Override
						public void onResponse(JSONObject response) {

							waitingResponse = false;
							for (int i = 0; i < response.length(); i++) {

								try {

									String turn = response
											.getString(SettingsActivity.KEY_JSON_TURN);
									String codedboard = response
											.getString(SettingsActivity.KEY_CODEBOARD_ID);

									showProgress(false);
									if (turn != null) {

										if (codedboard != null
												&& !codedboard.isEmpty()) {

											MainActivity.this.partida
													.getTablero()
													.stringToTablero(codedboard);

											MainActivity.this.vista
													.invalidate();

											if (turn.equals(MY_TURN)) {

												if (MainActivity.this.partida
														.getTablero()
														.getEstado() != Tablero.EN_CURSO) {
													// Log.d("Debug",MainActivity.this.partida.getJugador(MainActivity.this.partida.getTablero().getTurno()).getNombre());
													MainActivity.this
															.onCambioEnPartida(new Evento(
																	Evento.EVENTO_CAMBIO,
																	"",
																	MainActivity.this.partida,
																	null));

												}
											}

										}
										if (turn.equals(MY_TURN)) {
											turnoDisponible = true;

										}
									} else
										showProgress(false);
								} catch (JSONException e) {
									e.printStackTrace();
								} catch (ExcepcionJuego e) {
									e.printStackTrace();
								}

							}
						}

					}, new Response.ErrorListener() {

						@Override
						public void onErrorResponse(VolleyError error) {
							Log.d("Debug", error.getMessage());
							waitingResponse = false;
						}
					});
			return turnoDisponible;
		}

		@Override
		protected void onCancelled() {

			waitingResponse = false;
			if (isMyTurnTask != null
					&& isMyTurnTask.getStatus() != AsyncTask.Status.FINISHED)
				isMyTurnTask.cancel(true);
			isMyTurnTask = null;

		}
	}

	public void attemptNewMovement(String codeboard) {

		InterfazConServidor is = InterfazConServidor
				.getServer(MainActivity.this);

		String username = SettingsActivity.getSessionUser(MainActivity.this);

		DatabaseAdapter db = new DatabaseAdapter(MainActivity.this);
		db.open();

		String playerUuid = db.getUserUuid(username);
		db.close();

		is.newMovement(playerUuid,
				SettingsActivity.getRoundId(MainActivity.this), codeboard,
				new Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.d("Debug", response.toString());
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("Debug", error.getMessage());
					}
				});

		if (isMyTurnTask != null
				&& isMyTurnTask.getStatus() != AsyncTask.Status.FINISHED)
			isMyTurnTask.cancel(true);
		isMyTurnTask = new IsMyTurnTask();
		isMyTurnTask.execute((Void) null);
	}

	@Override
	protected void onPause() {

		if (isMyTurnTask != null
				&& isMyTurnTask.getStatus() != AsyncTask.Status.FINISHED) {
			isMyTurnTask.cancel(true);

		}
		super.onPause();
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

			vista.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
			vista.animate().setDuration(shortAnimTime).alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							vista.setVisibility(show ? View.INVISIBLE
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
			vista.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
		}
	}

}
