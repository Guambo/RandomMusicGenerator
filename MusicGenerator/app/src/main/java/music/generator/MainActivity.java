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

    public Sequencer sequencer;

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

                custom_input = new int[MAX_INPUTS];
                for(int i = 0; i < MAX_INPUTS; i++) {
                    custom_input[i] = Integer.valueOf(mEdit[i].getText().toString());
                }
                tv = findViewById(R.id.textView);
                tv.setText("Number of Notes: " + custom_input[0] +
                        "\nLorum: " + custom_input[1] +
                        "\nIpsum: " + custom_input[2]);

                // generate_music(custom_inputs[0], etc...);
                // Need to guard against bad input
                // Also may be a good idea to create a new thread and have that thread run the setupAndPlay function
                setupAndPlay(custom_input[0]);
            }
        });
    }

    public void setupAndPlay(int numNotes) {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();

            Sequence sequence = new Sequence(Sequence.PPQ, 4);

            Track track = sequence.createTrack();

            /* The following block is what populates the track with notes.
               This is what we will replace with our randomization and theory logic!
               ੭•̀ω•́)੭̸*✩⁺˚
             */
            for (int i = 0; i < (4 * numNotes) + 5; i += 4) {
                // note on
                track.add(makeEvent(144, 1, i, 100, i));

                // note off
                track.add(makeEvent(128, 1, i, 100, i + 2));
            }

            sequencer.setSequence(sequence);
            sequencer.setTempoInBPM(220);
            sequencer.start();

//            while (sequencer.isRunning()) {
//                if (!sequencer.isRunning()) {
//                    sequencer.close();
//                    //System.exit(1);
//                    break;
//                }
//            }
            while(sequencer.isRunning()) {
                ;
            }
            sequencer.close();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public MidiEvent makeEvent(int command, int channel, int note, int velocity, int tick) {
        MidiEvent event = null;

        try {
            ShortMessage a = new ShortMessage();
            a.setMessage(command, channel, note, velocity);
            event = new MidiEvent(a, tick);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return event;
    }

   // public void generate_music(.....) {
        // This is where the music generation
        // logic occurs
   // }
}
