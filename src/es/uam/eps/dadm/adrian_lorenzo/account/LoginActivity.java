package es.uam.eps.dadm.adrian_lorenzo.account;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.Response.Listener;

import es.uam.eps.dadm.adrian_lorenzo.InterfazConServidor;
import es.uam.eps.dadm.adrian_lorenzo.MainActivity;
import es.uam.eps.dadm.adrian_lorenzo.R;
import es.uam.eps.dadm.adrian_lorenzo.SettingsActivity;
import es.uam.eps.dadm.adrian_lorenzo.SplashActivity;
import es.uam.eps.dadm.adrian_lorenzo.R.anim;
import es.uam.eps.dadm.adrian_lorenzo.R.id;
import es.uam.eps.dadm.adrian_lorenzo.R.layout;
import es.uam.eps.dadm.adrian_lorenzo.R.string;
import es.uam.eps.dadm.adrian_lorenzo.database.DatabaseAdapter;
import es.uam.eps.dadm.adrian_lorenzo.menu.MainMenuActivity;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends Activity {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// UI references.
	private EditText mUserView;
	private EditText mPasswordView;
	private View mProgressView;
	private View mLoginFormView;
	private boolean loginOnline = false;
	private CheckBox autoLoginCheckBox;
	private boolean waitingResponse = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);

		// Set up the login form.
		mUserView = (EditText) findViewById(R.id.user);

		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView
				.setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView textView, int id,
							KeyEvent keyEvent) {
						if (id == R.id.login || id == EditorInfo.IME_NULL) {
							attemptLogin();
							return true;
						}
						return false;
					}
				});

		Button mUserLogInButton = (Button) findViewById(R.id.email_log_in_button);
		mUserLogInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				attemptLogin();
			}
		});

		Button mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
		mSignInButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

				inputManager.hideSoftInputFromWindow(getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				startActivity(new Intent(LoginActivity.this,
						SignInActivity.class));
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				finish();

			}
		});

		autoLoginCheckBox = (CheckBox) findViewById(R.id.checkbox_autologin);

		String user = SettingsActivity.getAutoLoginUser(this);
		String password = SettingsActivity.getAutoLoginPassword(this);

		if (user != null && password != null) {
			autoLoginCheckBox.setChecked(true);
			mUserView.setText(user);
			mPasswordView.setText(password);
		}

		mLoginFormView = findViewById(R.id.login_form);
		mProgressView = findViewById(R.id.login_progress);
	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if (mAuthTask != null) {
			return;
		}

		// Reset errors.
		mUserView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		String user = mUserView.getText().toString();
		String password = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid user address.
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
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			showProgress(true);
			mAuthTask = new UserLoginTask(user, password);
			mAuthTask.execute((Void) null);

		}
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
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

		private final String mUser;
		private final String mPassword;

		UserLoginTask(String user, String password) {
			mUser = user;
			mPassword = password;
		}

		@Override
		protected Boolean doInBackground(Void... params) {

			boolean authentication = DatabaseAdapter.check(mUser, mPassword,
					LoginActivity.this);
			
			boolean wifiCon = SettingsActivity.isWifiConnected(LoginActivity.this);
			
			SettingsActivity.setWorkWithConnection(LoginActivity.this,wifiCon);

			if (SettingsActivity.getWorkWithConnection(LoginActivity.this)) {

				attemptLoginOnline(mUser, mPassword);
				while (waitingResponse)
					;
				authentication = authentication && loginOnline;

			}

			try {
				// Simulate network access.
				Thread.sleep(1000);

			} catch (InterruptedException e) {
				return false;
			}
			return authentication;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);

			if (success) {

				SettingsActivity.setSessionUser(mUser, LoginActivity.this);

				if (autoLoginCheckBox.isChecked()) {
					SettingsActivity.setAutoLogin(mUser, mPassword,
							LoginActivity.this);
				} else {
					SettingsActivity.resetAutoLogin(LoginActivity.this);
				}

				startActivity(new Intent(LoginActivity.this,
						MainMenuActivity.class));
				overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
				finish();

			} else {

				mUserView.setError(getString(R.string.error_login));
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

	public void attemptLoginOnline(final String username, final String password) {

		InterfazConServidor is = InterfazConServidor
				.getServer(LoginActivity.this);

		is.login(username, password, new Listener<String>() {

			@Override
			public void onResponse(String uuid) {
				Log.d("Debug", uuid);

				if (!uuid.equals(SettingsActivity.ERROR_CODE)) {

					loginOnline = true;
					waitingResponse = false;
				} else
					loginOnline = false;
				waitingResponse = false;

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
