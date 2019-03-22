package com.ppm.simpleannotation;

import android.os.Bundle;
import android.widget.TextView;

import com.ppm.annotation.BindView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.txt_hello)
    TextView txtHello;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        txtHello.setText("injected");
    }
}
