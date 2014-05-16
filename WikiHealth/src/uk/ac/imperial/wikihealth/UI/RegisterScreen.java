package uk.ac.imperial.wikihealth.UI;

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
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */


public class RegisterScreen extends Fragment {
	/**
	 * A dummy authentication store containing known user names and passwords.
	 * TODO: remove after connecting to a real authentication system.
	 */

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
	private String mConfirmPassword;
	private String mBirthDay;
	private String mBirthMonth;
	private String mBirthYear;
	private String mGender;
	private String mScreenName;
	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mConfirmPasswordView;
	private View mLoginFormView;
	private View mLoginStatusView;
	private TextView mLoginStatusMessageView;
	private EditText mBirthDayView;
	private EditText mBirthMonthView;
	private EditText mBirthYearView;
	private RadioButton mMaleView;
	private RadioButton mFemaleView;
	private EditText mScreenNameView;
	private View mView;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		LinearLayout wrapper = new LinearLayout(getActivity());
		//Show the new view of the fragment
		mView= inflater.inflate(R.layout.activity_register_screen, wrapper, true);

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
		
		mConfirmPasswordView = (EditText) mView.findViewById(R.id.confirmpassword);
		mConfirmPasswordView
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
		
		mBirthDayView= (EditText)mView.findViewById(R.id.daytext);
		mBirthMonthView=(EditText)mView.findViewById(R.id.monthtext);
		mBirthYearView=(EditText)mView.findViewById(R.id.yeartext);
		mScreenNameView=(EditText)mView.findViewById(R.id.screenname);
		
		mGender="male";
		mMaleView=(RadioButton)mView.findViewById(R.id.maleradio);
		mFemaleView=(RadioButton)mView.findViewById(R.id.femaleradio);
		
		mMaleView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mGender="male";
				mMaleView.setChecked(true);
				mFemaleView.setChecked(false);				
			}
		});
		
		mFemaleView.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mGender="female";
				mFemaleView.setChecked(true);
				mMaleView.setChecked(false);				
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
		if(!ConnectivityUtil.isNetworkAvailable(mView.getContext())){
			ConnectivityUtil.buildAlertMessageNoInternet(mView.getContext());
			return;
		}
		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);
		mConfirmPasswordView.setError(null);
		mBirthDayView.setError(null);
		mBirthMonthView.setError(null);
		mBirthYearView.setError(null);
		
		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();
		mConfirmPassword=mConfirmPasswordView.getText().toString();
		mBirthDay=mBirthDayView.getText().toString();
		mBirthMonth=mBirthMonthView.getText().toString();
		mBirthYear=mBirthYearView.getText().toString();
		mScreenName=mScreenNameView.getText().toString();
		
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

		// Check for a valid password.
		if (!mConfirmPassword.equals(mPassword)) {
			mConfirmPasswordView.setError("Passwords do not match");
			focusView = mConfirmPasswordView;
			cancel = true;
		}
		
		
		
		// Check for a valid email address.
		if (TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		} else if (!mEmail.contains("@")) {
			mEmailView.setError(getString(R.string.error_invalid_email));
			focusView = mEmailView;
			cancel = true;
		}

		// Check for a valid birth day.
		if (TextUtils.isEmpty(mBirthDay)) {
			mBirthDayView.setError(getString(R.string.error_field_required));
			focusView = mBirthDayView;
			cancel = true;
		} else if (!isInteger(mBirthDay) || Integer.valueOf(mBirthDay)<1 || Integer.valueOf(mBirthDay)>31) {
			mBirthDayView.setError("Invalid Input");
			focusView = mBirthDayView;
			cancel = true;
		}

		// Check for a valid birth day.
		if (TextUtils.isEmpty(mBirthMonth)) {
			mBirthMonthView.setError(getString(R.string.error_field_required));
			focusView = mBirthMonthView;
			cancel = true;
		} else if (!isInteger(mBirthMonth) || Integer.valueOf(mBirthMonth)<1 || Integer.valueOf(mBirthMonth)>12) {
			mBirthMonthView.setError("Invalid Input");
			focusView = mBirthMonthView;
			cancel = true;
		}
				
		// Check for a valid birth day.
		if (TextUtils.isEmpty(mBirthYear)) {
			mBirthYearView.setError(getString(R.string.error_field_required));
			focusView = mBirthYearView;
			cancel = true;
		} else if (!isInteger(mBirthYear) || Integer.valueOf(mBirthYear)<1910 || Integer.valueOf(mBirthYear)>2013) {
			mBirthYearView.setError("Invalid Input");
			focusView = mBirthYearView;
			cancel = true;
		}
		
		
		if (TextUtils.isEmpty(mScreenName)) {
			mScreenNameView.setError(getString(R.string.error_field_required));
			focusView = mScreenNameView;
			cancel = true;
		} else if (mScreenName.length()<5) {
			mScreenNameView.setError("Invalid Input");
			focusView = mBirthYearView;
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
			mLoginStatusMessageView.setText("Invalid Input");
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
		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO: attempt authentication against a network service.
			try {
				// Simulate network access.

			JSONObject registerRequest =new JSONObject("{\n" + 
									"  \"loginid\": \""+mEmail+"\",\n" + 
									"  \"password\": \""+mPassword+"\",\n" + 
									"  \"email\": \""+mEmail+"\",\n" + 
									"  \"screenname\": \""+mScreenName+"\",\n" + 
									"  \"birthday\": \""+mBirthDay+"/"+mBirthMonth+"/"+mBirthYear+"\",\n" + 
									"  \"gender\": \""+mGender+"\"\n" + 
									"}");
			
			JSONObject jsonObjReceive=HttpClient.SendHttpJSONPost("http://wikihealth.bigdatapro.org:55555/healthbook/v1/users/register?api_key=special-key",registerRequest);
			if(jsonObjReceive.getString("result").equals("error")){
				return false;
			}
			System.out.println(jsonObjReceive.toString());

			} catch (Exception e) {
				System.out.println("error in creation");
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
				
				settings = mView.getContext().getSharedPreferences("first_time", 0);
            	settings.edit().putBoolean("first_time", false).commit();
    			showProgress(false);
				Intent statusIntent = new Intent(mView.getContext().getApplicationContext(), StatusScreen.class);
				mView.getContext().startActivity(statusIntent);
			} else {
    			showProgress(false);

				Toast.makeText(mView.getContext(), "Email address already exists. If you already have an account swipe on the left to login instead", Toast.LENGTH_LONG).show();
				mEmailView
						.setError("Email address alreadys exists");
				mEmailView.requestFocus();
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(false);
		}
	}
	   public boolean isInteger(String s) {
		    try { 
		        Integer.parseInt(s); 
		    } catch(NumberFormatException e) { 
		        return false; 
		    }
		    // only got here if we didn't return false
		    return true;
		}
}
