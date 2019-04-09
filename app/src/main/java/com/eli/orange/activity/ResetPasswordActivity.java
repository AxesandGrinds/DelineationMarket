package com.eli.orange.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.eli.orange.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {
    @BindView(R.id.reset_btn_back)
    Button backButton;
    @BindView(R.id.reset_email)
    TextInputEditText resetEmail;
    @BindView(R.id.btn_reset_password)
    Button resetPassword;
    @BindView(R.id.reset_progressBar)
    ProgressBar progressBar;
    private FirebaseAuth auth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        ButterKnife.bind(this);

        auth = FirebaseAuth.getInstance();
    }
    @OnClick(R.id.reset_btn_back)
    void goBack(){
        finish();
    }
    @OnClick(R.id.btn_reset_password)
    void resetPassword(){
        String email = resetEmail.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            resetEmail.setError("Enter your registered email id");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            showsnackbar("We have sent you instructions to reset your password!");
                        } else {
                            showsnackbar("Failed to send reset email!");
                        }

                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    void showsnackbar(String message){
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_LONG)
                .setAction("CLOSE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                .show();
        //Other stuff in
    }
}
