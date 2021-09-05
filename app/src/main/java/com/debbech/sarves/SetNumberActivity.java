package com.debbech.sarves;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SetNumberActivity extends AppCompatActivity {

    private TextView number;
    private Button submit;
    private SetNumberActivity act;
    private Intent i;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_set_number);

        number = findViewById(R.id.number_set);
        submit = findViewById(R.id.submit_set);
        this.act= this;
        i = new Intent(this, MainActivity.class);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nu = number.getText().toString().replaceAll("[^0-9]", "");
                if(nu.length() > 8){
                    number.setError("Number should not pass 8 numbers");
                    return;
                }
                SharedPreferences sharedPreferences = getSharedPreferences("number", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("num", number.getText().toString());
                editor.commit();

                String nn = number.getText().toString().replaceAll("[^0-9]", "");
                if(nn.length() > 8){
                    nn = nn.substring(3);
                }
                Synchronizer.syncLastSeen(nn);

                finish();
                Intent i = new Intent(SetNumberActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

}
