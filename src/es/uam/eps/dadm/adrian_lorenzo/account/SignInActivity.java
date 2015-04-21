package es.uam.eps.dadm.adrian_lorenzo.account;

import org.json.JSONArray;
import org.json.JSONException;

import com.android.volley.Response.Listener;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import es.uam.eps.dadm.adrian_lorenzo.InterfazConServidor;
import es.uam.eps.dadm.adrian_lorenzo.R;
import es.uam.eps.dadm.adrian_lorenzo.SettingsActivity;

import com.android.volley.*;

import es.uam.eps.dadm.adrian_lorenzo.database.DatabaseAdapter;

/**
 * A login screen that offers login via email/password.
 */
public class SignInActivity extends Activity {

	private void log(String text) {
		Log.d("Debug", text);
	}

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserSignInTask mAuthTask = null;

	// UI references.
	private EditText mUserView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;

	private boolean accountCreated;
	private boolean accountOnlineCreated = false;
	private boolean waitingResponse = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signin);

		// Set up the login form.
		mUserView = (EditText) findViewById(R.id.user);

		mPasswordView = (EditText) findViewById(R.id.password);

		Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				attemptAccount();
			}
		});

		Button volverButton = (Button) findViewById(R.id.button_register_volver);
		volverButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				startActivity(new Intent(SignInActivity.this,
						LoginActivity.class));
				finish();
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);

	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptAccount() {

		// Reset errors.
		mUserView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String user = mUserView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid user.
		if (TextUtils.isEmpty(user)) {
			mUserView.setError(getString(R.string.error_field_required));
			focusView = mUserView;
			cancel = true;
		}

		// Check for a valid password, if the user entered one.
		else if (TextUtils.isEmpty(password)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (!isPasswordValid(password)) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			mAuthTask = new UserSignInTask(user, password);
			mAuthTask.execute((Void) null);

			// accountCreated = newAccount();
			// if(accountCreated){
			// Toast toast = Toast.makeText(SignInActivity.this,
			// R.string.accountFirstToastMessage,
			// Toast.LENGTH_LONG);
			// toast.setGravity(Gravity.CENTER, 0, 0);
			// toast.show();
			// }

		}
	}

	private boolean isPasswordValid(String password) {
		return password.length() > 4;
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

			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
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
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserSignInTask extends AsyncTask<Void, Void, Boolean> {

		private final String mUser;
		private final String mPassword;

		UserSignInTask(String user, String password) {
			mUser = user;
			mPassword = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			accountCreated = newAccount();

			try {
				// Simulate network access.
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				return false;
			}

			return accountCreated;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {

				Toast toast = Toast.makeText(SignInActivity.this,
						R.string.accountFirstToastMessage, Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();

				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				startActivity(new Intent(SignInActivity.this,
						LoginActivity.class));
				finish();

			} else {
				mUserView.setError(getString(R.string.error_signin));
				mUserView.requestFocus();

				mUserView.getText().clear();
				mPasswordView.getText().clear();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}

	private boolean newAccount() {
		String name = mUserView.getText().toString();
		String pass = mPasswordView.getText().toString();

		if (!pass.equals("") && !name.equals("") /* && pass.equals(confPass) */) {

			if (!DatabaseAdapter.check(name, pass, SignInActivity.this)) {
				
				boolean wifiCon = SettingsActivity.isWifiConnected(SignInActivity.this);
				
				SettingsActivity.setWorkWithConnection(SignInActivity.this,wifiCon);
				
				if (SettingsActivity.getWorkWithConnection(SignInActivity.this)) {
					newAccountOnline(name, pass);
					while (waitingResponse)
						;
					return accountOnlineCreated;

				} else {
					DatabaseAdapter db = new DatabaseAdapter(
							SignInActivity.this);
					db.open();
					db.insertUser(name, pass);
					db.close();

					return true;
				}
			} else
				return false;
		}

		return false;
	}

	public void newAccountOnline(final String username, final String password) {

		InterfazConServidor is = InterfazConServidor
				.getServer(SignInActivity.this);

		is.account(username, password, new Listener<String>() {

			@Override
			public void onResponse(String uuid) {
				waitingResponse = false;
				if (!uuid.equals(SettingsActivity.ERROR_CODE)) {
					DatabaseAdapter db = new DatabaseAdapter(
							SignInActivity.this);
					db.open();
					db.insertUser(username, password, uuid);
					db.close();
					accountOnlineCreated = true;
				} else
					accountOnlineCreated = false;

			}

		}, new Response.ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError error) {
				Log.d("Debug", error.getMessage());
				waitingResponse = false;
			}
		});

	}

}
