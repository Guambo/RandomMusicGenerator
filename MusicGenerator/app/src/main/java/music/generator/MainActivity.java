/*
 Authors: David J Martinez, Matthew Aber, & Nicholas Vallejos
 HackBU 2019
 Helpful GUI Links: https://developer.android.com/training/constraint-layout/
*/
package music.generator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
// import javax.sound.midi.*;
import android.widget.TextView;
import jp.kshoji.javax.sound.midi.*;
import java.io.File;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    // Fields
    private EditText mEdit[];
    private Button mButton;
    private TextView tv;
    private int MAX_INPUTS = 3;
    private int bpm;
    private String key;
    private String filename;
    private File midiFile;
    private Random rand = new Random();
    private Sequencer sequencer;
    private Sequence sequence;
    private Track track;

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

                bpm = Integer.valueOf(mEdit[0].getText().toString());
                key = mEdit[1].getText().toString();
                filename = mEdit[2].getText().toString() + ".mid";

                tv = findViewById(R.id.textView);
                tv.setText("Generated file \"MyFiles/Internal Storage/Download/" + filename + "\"");

                // generate_music(custom_inputs[0], etc...);
                // Need to guard against bad input
                // Also may be a good idea to create a new thread and have that thread run the setupAndPlay function
                setupAndPlay(15);
            }
        });
    }

    public void setupAndPlay(int numNotes) {
        try {
            sequencer = MidiSystem.getSequencer();
            sequencer.open();
            midiFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

            sequence = new Sequence(Sequence.PPQ, 4);

            track = sequence.createTrack();

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
            sequencer.setTempoInBPM(bpm);
            MidiSystem.write(sequence, 1, midiFile);
            //sequencer.start();

            //while(sequencer.isRunning()) {
            //    ;
            //}
            //sequencer.close();
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
    public void generate_music() {
        int barR = ((int) Math.random() * 8) + 2;
        int numOfChords = (int)((Math.random()*12) - 2);
        int key = (int) Math.random()*11;
        int tempo = (int) Math.random()*60 + 100;
        int upperBound = 5;
        int lowerBound =5;
        int[][] chords = {
                {0, 4, 7},
                {2, 5, 9},
                {4, 7, 11},
                {4, 8, 11},
                {5, 9, 0},
                {5, 8, 0},
                {7, 11, 2},
                {9, 0, 4},
                {11, 2, 5}
        };
        int[][] seventh = {
                {0, 4, 7, 11},
                {2, 5, 9, 0},
                {4, 7, 11, 2},
                {5, 9, 0, 4},
                {7, 11, 2, 6},
                {9, 0, 4, 7}
        };
        int[][] chordProg = new int[numOfChords][4];
        for(int i = 0; i < numOfChords ; i++)
        {
            chordProg[i] = seventh[(int) Math.random()*seventh.length];
        }
        int [] bassRhythm = rhythm(barR, 90);
        int [] cRhythm = rhythm(barR, 50);
        int [] offRhythm = rhythm(barR, 10);
        int [] bassNotes;
        for (int i = 0; i < barR; i ++) {

            // note on
            bassNotes = generateSNotes(lowerBound, upperBound, barR, key, chordProg[i] , (int) Math.random()*3);
            for(int j = 0; j<barR;j++){
                if(bassRhythm[j]==1)
                {
                    track.add(makeEvent(144, 1, i, 100, i * 4 + j * 4));
                }
                else{
                    track.add(makeEvent(128, 1, i, 100, i * 4 + j * 4));
                }
                //track.add(makeEvent(144, 1, i, 100, i));
                //track.add(makeEvent(144, 1, i, 100, i));
            }

            // note off
            //track.add(makeEvent(128, 1, i, 100, i + 2));
        }

    }
    int[] generateSNotes(int lowerBound, int upperBound, int totalNotes, int key, int[] chord, int degree) {

        int[] ret = new int[totalNotes];
        for(int i = 0; i < totalNotes; i++) {
            i = chord[degree] + 12 * rand.nextInt(upperBound) + lowerBound;
        }
        return ret;
    }
    public int[] rhythm(int rLength, int percentage)//makes a rhythm for a certain part. rLength is the bar length, percentage is the chance for a higher frequency of notes
    {
        int[] retval = new int[rLength];
        for(int i = 0; i < rLength; i++) {
            if (Math.random() * 100 < percentage) {
                retval[i] = 1;
            }
        }
        return retval;
    }
}
