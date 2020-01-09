package com.myapps.avoidingbricksgame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class Settings extends AppCompatActivity {

    private Button submitButton;
    private EditText editText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        submitButton = findViewById(R.id.submit);
        editText = findViewById(R.id.plain_text_input);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =
                        getApplication()
                                .getSharedPreferences(getString(R.string.app_name),Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                String nick = editText.getText().toString();
                if (nick.compareTo("") != 0){
                    editor.putString(getString(R.string.nickname), nick).apply();
                    editText.setText("");
                }


            }
        });
    }
}
