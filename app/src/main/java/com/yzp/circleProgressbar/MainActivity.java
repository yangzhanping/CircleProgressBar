package com.yzp.circleProgressbar;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.yzp.circleProgressbar.view.CircleProgressBar;

public class MainActivity extends AppCompatActivity {

    CircleProgressBar rp_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rp_progress = findViewById(R.id.rp_progress);

        Button btn_progress = findViewById(R.id.btn_progress);
        btn_progress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rp_progress.setProgress(10);
            }
        });
    }
}