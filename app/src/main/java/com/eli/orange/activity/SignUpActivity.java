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
import com.eli.orange.models.User;
import com.eli.orange.utils.Constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SignUpActivity extends AppCompatActivity {
    @BindView(R.id.supEmail)
    TextInputEditText supEmail;
    @BindView(R.id.supPassword)
    TextInputEditText supPassword;
    @BindView(R.id.sup_btn_reset_password)
    Button resetPassword;
    @BindView(R.id.sup_sign_in_button)
    Button signInButton;
    @BindView(R.id.sup_sign_up_button)
    Button signUpButton;
    @BindView(R.id.sup_progressBar)
    ProgressBar progressBar;
    @BindView(R.id.supUsername)
    TextInputEditText signUpUserName;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.bind(this);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
    }
    @OnClick(R.id.sup_sign_in_button)
     void Login(){
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
    }
    @OnClick(R.id.sup_sign_up_button)
     void signup(){
        String email = supEmail.getText().toString().trim();
        String password = supPassword.getText().toString().trim();
        String username = signUpUserName.getText().toString().trim();

        if (TextUtils.isEmpty(username)){
            signUpUserName.setError("Enter UserName");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            supEmail.setError("Enter email address!");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            supPassword.setError("Enter password!");
            return;
        }

        if (password.length() < 6) {
            supPassword.setError("Password too short, enter minimum 6 characters!");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        //create user
        auth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            if (task.getResult().getUser().getUid() != null){
                                addCreatedUserInUsersList(username,email,task.getResult().getUser().getUid());
                            }
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        }
                    }

                });


    }
    @OnClick(R.id.sup_btn_reset_password)
    void resetpass(){
        startActivity(new Intent(SignUpActivity.this, ResetPasswordActivity.class));

    }
    void addCreatedUserInUsersList(String username, String emailAddress,String userId){
        User user = new User(username,emailAddress,userId, Constants.USER_PROFILE_AVATAR);
        databaseReference.child("users").child(userId).setValue(user);
    }
}
