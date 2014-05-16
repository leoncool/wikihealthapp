package uk.ac.imperial.wikihealth.UI;

import org.json.JSONException;
import org.json.JSONObject;

import uk.ac.imperial.wikihealth.R;
import uk.ac.imperial.wikihealth.DataUpload.HttpClient;
import uk.ac.imperial.wikihealth.Utils.ConnectivityUtil;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class LoginScreen extends Fragment {

	/**
	 * The default email to populate the email field with.
	 */
	public static final String EXTRA_EMAIL = "com.example.android.authenticatordemo.extra.EMAIL";

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private UserLoginTask mAuthTask = null;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private View mView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout wrapper = new LinearLayout(getActivity());
		//Show the new view of the fragment
		mView= inflater.inflate(R.layout.activity_login_screen, wrapper, true);
		// Set up the login form.
		mEmailView = (EditText) mView.findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPasswordView = (EditText) mView.findViewById(R.id.password);
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

		mLoginFormView = mView.findViewById(R.id.login_form);
		mLoginStatusView = mView.findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) mView.findViewById(R.id.login_status_message);

		mView.findViewById(R.id.sign_in_button).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						attemptLogin();
					}
				});
		
		return mView;
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
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if (TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		} else if (mPassword.length() < 4) {
			mPasswordView.setError(getString(R.string.error_invalid_password));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}

		if(!ConnectivityUtil.isNetworkAvailable(this.getActivity().getApplicationContext())){
			cancel=true;
			ConnectivityUtil.buildAlertMessageNoInternet(getActivity());
		}
			
		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
					.alpha(show ? 1 : 0)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginStatusView.setVisibility(show ? View.VISIBLE
									: View.GONE);
						}
					});

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
		String token=null;
		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				JSONObject jsonObjSend = new JSONObject("{" + 
						"\"loginid\":\""+mEmail+"\"," + 
						"\"password\":\""+mPassword+"\"," + 
						"\"expire_in_seconds\":99999999" + 
						"}");
			
			
				JSONObject jsonObjReceive=HttpClient.SendHttpJSONPost("http://wikihealth.bigdatapro.org:55555/healthbook/v1/users/gettoken?api_key=special-key",jsonObjSend);
				token = jsonObjReceive.getJSONObject("usertoken").getString("token");

				
			} catch (JSONException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;

			if (success) {
//				String FILENAME = "id_file";
//				FileOutputStream fos;
//				try {
//					fos = mView.getContext().getApplicationContext().openFileOutput(FILENAME, Context.MODE_PRIVATE);
//				
//				fos.write((mEmail+"-"+mPassword).getBytes());
//				
//				fos.close();
//				} catch (Exception e) {
//					
//					e.printStackTrace();
//				}
				SharedPreferences settings = mView.getContext().getSharedPreferences("id_credentials", 0);
				settings.edit().putString("username",mEmail).commit();
				settings.edit().putString("password",mPassword).commit();
				
				SharedPreferences read = mView.getContext().getSharedPreferences("id_credentials", 0);
				System.out.println(read.getString("username","NOUSER"));
				System.out.println(read.getString("password","NOPASS"));
				
				
				settings = mView.getContext().getSharedPreferences("first_time", 0);
            	settings.edit().putBoolean("first_time", false).commit();

				Intent statusIntent = new Intent(mView.getContext().getApplicationContext(), StatusScreen.class);
				showProgress(false);
				startActivity(statusIntent);
			} else {
				showProgress(false);
				mPasswordView
						.setError(getString(R.string.error_incorrect_password));
				mPasswordView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
}
