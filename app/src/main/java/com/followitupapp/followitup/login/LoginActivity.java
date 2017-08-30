package com.followitupapp.followitup.login;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.followitupapp.followitup.R;
import com.followitupapp.followitup.activities.MainActivity;
import com.followitupapp.followitup.models.User;
import com.followitupapp.followitup.util.Misc;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.Manifest.permission.READ_CONTACTS;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoaderCallbacks<Cursor> {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 0xBECE;
    private static final int REQUEST_READ_CONTACTS = 0XFACE;

    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mUserReference;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    // Email/Pass login.
    @BindView(R.id.email_edittext)
    AutoCompleteTextView mEmailView;
    @BindView(R.id.password_edittext)
    EditText mPasswordView;
    @BindView(R.id.email_login_signup_button)
    Button emailLogin;

    // Facebook login
    @BindView(R.id.fb_auth_button)
    LoginButton fbLogin;
    private CallbackManager callbackManager;

    // Google login
    @BindView(R.id.google_auth_button)
    SignInButton gLogin;
    private GoogleSignInOptions googleSignInOptions;
    private GoogleApiClient googleApiClient;

    // misc. views
    @BindView(R.id.login_to_signup_lever_text)
    TextView bottomTextView;
    @BindView(R.id.signup_container)
    LinearLayout signupPannel;
    @BindView(R.id.userid_textedit)
    EditText userIdInput;
    @BindView(R.id.signup_date_picker_edittext)
    EditText userDOBInput;
    @BindView(R.id.signup_gender_switch)
    Switch userGenderSwitch;
    @BindView(R.id.login_signup_container)
    LinearLayout loginSignUpContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // hide action bar
        getSupportActionBar().hide();

        // inject views
        ButterKnife.bind(this);

        // Firebase realtime database
        mUserReference = FirebaseDatabase.getInstance().getReference().child("users");

        // Firebase Auth Setup
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + firebaseUser.getUid());
                    String userId = firebaseUser.getUid();
                    String userEmail = firebaseUser.getEmail();
                    Boolean userSex = userGenderSwitch == null || userGenderSwitch.isChecked();
                    String userDOB = (userDOBInput != null && !"".equals(userDOBInput.getText().toString()))
                            ? userDOBInput.getText().toString().trim()
                            : "1900-01-01";
                    User user = new User(userEmail, userId, userDOB, userSex);
                    // TODO: do a query, if there is a user with the userId, we proceed with the user
                    // TODO: if there's no user exists, we create a user with the info we have
                    mUserReference.child(userId).setValue(user);
                    jumpToMainActivity(user);
                } else {
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        // EmailPass login
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    String email = mEmailView.getText().toString().trim();
                    String password = mPasswordView.getText().toString().trim();
                    if (checkEmailPassword(email, password)) {
                        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(LoginActivity.this, new FireBaseListener("Email/Pass"));
                        return true;
                    }
                }
                return false;
            }
        });
        emailLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String action_sign_in = getResources().getString(R.string.action_sign_in);
                String action_sign_up = getResources().getString(R.string.action_sign_up);
                String btnText = (((Button) view).getText().toString());
                String email = mEmailView.getText().toString().trim();
                String password = mPasswordView.getText().toString().trim();
                if (!checkEmailPassword(email, password)) {
                    return;
                }
                if (btnText.equals(action_sign_in)) {
                    mFirebaseAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new FireBaseListener("Email/Pass"));
                } else if (btnText.equals(action_sign_up)) {
                    mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Log.e(TAG, "User sign up failed: ", task.getException());
                                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Sign up successful", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        // FB login
        callbackManager = CallbackManager.Factory.create();
        fbLogin.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "FB login successful");
                Log.d(TAG, "Jumping to Firebase credentials");
                AuthCredential credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                mFirebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(LoginActivity.this, new FireBaseListener("Facebook"));
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "login canceled");
            }

            @Override
            public void onError(FacebookException error) {
                Log.e(TAG, "login error " + error.getMessage());
            }
        });

        // Google login
        googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(LoginActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Log.d(TAG, "login failed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, googleSignInOptions)
                .build();
        gLogin.setSize(SignInButton.SIZE_STANDARD);
        gLogin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
    }

    private boolean checkEmailPassword(String email, String password) {
        boolean valid = true;
        if (!Misc.isValidEmail(email)) {
            mEmailView.setError("Invalid email");
            valid = false;
        }
        if (!Misc.isValidPassword(password)) {
            mPasswordView.setError("At least 6 characters");
            valid = false;
        }
        return valid;
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = mFirebaseAuth.getCurrentUser();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        if (user != null) {
            //TODO: retrieve user info from database
            jumpToMainActivity(null);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult googleSignInResult = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.d(TAG, "Google login: " + googleSignInResult.isSuccess());
            if (googleSignInResult.isSuccess()) {
                Log.d(TAG, "Login with Google success, exchange to Firebase user");
                GoogleSignInAccount account = googleSignInResult.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mFirebaseAuth.signInWithCredential(credential)
                        .addOnCompleteListener(LoginActivity.this, new FireBaseListener("Google"));
            }
        }
    }

    private void jumpToMainActivity(User user) {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("login from", user.getUserId());
        startActivity(intent);
    }

    private class FireBaseListener implements OnCompleteListener {
        private String greetings;

        public FireBaseListener(String greetings) {
            this.greetings = greetings;
        }

        @Override
        public void onComplete(@NonNull Task task) {
            if (!task.isSuccessful()) {
                Log.e(TAG, "Sign in with " + greetings + " failed: ", task.getException());
                Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * @param view the date and time EditText pressed
     */
    public void showDatePicker(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * TODO: show sign up pannel from the signup_login container
     * with animation
     *
     * @param v the SignUp text pressed
     */
    public void onBottomTextClicked(View v) {
        if (getResources().getString(R.string.action_show_sign_up).equals(((TextView) v).getText())) {
            showSignUpPanel(true);
        } else {
            showSignUpPanel(false);
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int date = c.get(Calendar.DATE);

            // Create a new instance of TimePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, date);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            ((TextView) getActivity().findViewById(R.id.signup_date_picker_edittext)).setText(
                    "" + year + "-" + (month + 1) + "-" + dayOfMonth);
        }
    }
    /*--------------------------------------------------------------------------------------------*
     *                             A U T O   C O M P L E T E   L O G I C
     *--------------------------------------------------------------------------------------------*/

    private void populateAutoComplete() {
        if (!mayRequestContacts()) {
            return;
        }

        getLoaderManager().initLoader(0, null, this);
    }

    private boolean mayRequestContacts() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        if (checkSelfPermission(READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        if (shouldShowRequestPermissionRationale(READ_CONTACTS)) {
            Snackbar.make(mEmailView, R.string.permission_rationale, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, new View.OnClickListener() {
                        @Override
                        @TargetApi(Build.VERSION_CODES.M)
                        public void onClick(View v) {
                            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
                        }
                    });
        } else {
            requestPermissions(new String[]{READ_CONTACTS}, REQUEST_READ_CONTACTS);
        }
        return false;
    }


    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateAutoComplete();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        List<String> emails = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            emails.add(cursor.getString(ProfileQuery.ADDRESS));
            cursor.moveToNext();
        }

        addEmailsToAutoComplete(emails);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private void addEmailsToAutoComplete(List<String> emailAddressCollection) {
        //Create adapter to tell the AutoCompleteTextView what to show in its dropdown list.
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(LoginActivity.this,
                        android.R.layout.simple_dropdown_item_1line, emailAddressCollection);

        mEmailView.setAdapter(adapter);
    }


    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    /*--------------------------------------------------------------------------------------------*
     *                             V I E W   A N I M A T I O N S
     *--------------------------------------------------------------------------------------------*/
    private void showSignUpPanel(final boolean signup) {
        final ObjectAnimator bottomTextSubmergeAnimator = ObjectAnimator.ofFloat(bottomTextView, "y", bottomTextView.getY() + 150f);
        bottomTextSubmergeAnimator.setDuration(200);
        bottomTextSubmergeAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bottomTextView.setText(signup ? R.string.action_show_login : R.string.action_show_sign_up);
                ObjectAnimator bottomTextPopAnimator = ObjectAnimator.ofFloat(bottomTextView, "y", bottomTextView.getY() - 150f);
                bottomTextPopAnimator.setDuration(200);
                bottomTextPopAnimator.start();
            }
        });
        bottomTextSubmergeAnimator.start();
        signupPannel.setVisibility(signup ? View.VISIBLE : View.GONE);
        emailLogin.setText(signup ? R.string.action_sign_up : R.string.action_sign_in);
    }
}

