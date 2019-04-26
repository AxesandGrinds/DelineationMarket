package com.eli.orange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.eli.orange.R;
import com.eli.orange.utils.SharedPreferencesManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    @BindView(R.id.login_btn_login)
    Button loginButton;
    @BindView(R.id.login_btn_reset_password)
    Button reseetPassword;
    @BindView(R.id.login_btn_signup)
    Button signUp;
    @BindView(R.id.login_email)
    TextInputEditText loginEmail;
    @BindView(R.id.login_password)
    TextInputEditText loginPassword;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    private FirebaseAuth auth;
    private SharedPreferencesManager sharedPreferencesManager;
    private MainActivity mainActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        sharedPreferencesManager = new SharedPreferencesManager(this);


        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
    }
    @OnClick(R.id.login_btn_signup)
    void signUp(){
        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
    }
    @OnClick(R.id.login_btn_reset_password)
    void resetPass(){
        startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));

    }
    @OnClick(R.id.login_btn_login)
    void login(){
        String email = loginEmail.getText().toString();
        final String password = loginPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            loginEmail.setError("Email Address Required !");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            loginPassword.setError("Password is required !!!");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        progressBar.setVisibility(View.GONE);
                        if (!task.isSuccessful()) {

                            if (password.length() < 6) {
                                loginPassword.setError(getString(R.string.minimum_password));
                            } else {
                                Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                            }
                        } else {
                            showsnackbar("Logged in Successful...");
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }


                    }
                });
    }
    void showsnackbar(String message){
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG)
                .setAction("OK", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
        //Other stuff in
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
