package com.example.inhabitpototypr_0;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.firebase.ui.auth.AuthUI;

public class HomePageActivity extends AppCompatActivity {

    private Button checkFeedbtn, uploadBtn, logoutBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        init();
    }

    private void init() {

        checkFeedbtn = findViewById(R.id.check_feed_btn);
        uploadBtn = findViewById(R.id.upload_btn);
        logoutBtn = findViewById(R.id.sign_out_btn);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(HomePageActivity.this);
                finish();
            }
        });
    }
}
