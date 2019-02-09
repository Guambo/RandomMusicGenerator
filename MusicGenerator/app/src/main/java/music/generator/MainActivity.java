/*
 Authors: David J Martinez, Mathew Aber, & Nicholas Vallejos
 HackBU 2019
 Helpful GUI Links: https://developer.android.com/training/constraint-layout/
*/
package music.generator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
// import javax.sound.midi.*;
import android.widget.TextView;
import jp.kshoji.javax.sound.midi.*;

public class MainActivity extends AppCompatActivity {
    // Fields
    private EditText mEdit[];
    private Button mButton;
    private TextView tv;
    private int MAX_INPUTS = 3;
    private int custom_input[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GUI();
    }

    public void GUI() {
        mButton = findViewById(R.id.button);
        mButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Getting user input and converting it
                // from string to int
                mEdit = new EditText[MAX_INPUTS];
                mEdit[0] = findViewById(R.id.editText1);
                mEdit[1] = findViewById(R.id.editText2);
                mEdit[2] = findViewById(R.id.editText3);

                for(int i = 0; i < MAX_INPUTS; i++) {
                    //custom_input[i] = Integer.valueOf(mEdit[i].getText());
                }
                tv = findViewById(R.id.textView);
                tv.setText("Number of Notes: " + mEdit[0].getText() +
                        "\nLorum: " + mEdit[1].getText() +
                        "\nIpsum: " + mEdit[2].getText());

                // generate_music(custom_inputs[0], etc...);
            }
        });
    }

   // public void generate_music(.....) {
        // This is where the music generation
        // logic occurs
   // }
}
