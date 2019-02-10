/*
 Authors: David J Martinez, Matthew Aber, & Nicholas Vallejos
 HackBU 2019
 Helpful GUI Links: https://developer.android.com/training/constraint-layout/
 ੭•̀ω•́)੭̸*✩⁺˚
*/
package music.generator;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.Context;

import jp.kshoji.javax.sound.midi.*;
import java.io.File;
import java.util.Random;

// TODO: @Matt Add content URI (https://medium.com/@ali.muzaffar/what-is-android-os-fileuriexposedexception-and-what-you-can-do-about-it-70b9eb17c6d0)
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
    private Intent fileIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GUI();
    }
    // TODO: @Nick Add dropdown menu for user input AND store usr input (https://developer.android.com/guide/topics/ui/controls/spinner#java)
    // User input values: Key, Time Signature, Checkbox for producing 1. Melody 2. Bass
    // 3. Include seventh chords XOR Include major third 4. Include minor fourth 5. Include long chord progression
    public void GUI() {
        //Spinner spinner = (Spinner) findViewById(R.id.spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        //        R.array.keys, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        //spinner.setAdapter(adapter);

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
                tv.setText("Generated file \"MyFiles/Internal Storage/Download/" + filename + "\""); // TODO: add a 'go to' button

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

            sequence = new Sequence(Sequence.PPQ, 4);
            track = sequence.createTrack();

            midiFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

            generate_music();

            sequencer.setSequence(sequence);
            sequencer.setTempoInBPM(bpm);

            MidiSystem.write(sequence, 1, midiFile);
            DownloadManager dm = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
            dm.addCompletedDownload(filename, "sample description", false, "audio/midi", midiFile.getPath(), midiFile.length(), true);
            startActivity(new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public MidiEvent makeEvent(int command, int channel, int note, int velocity, long tick) {
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

    // TODO: @David make very long chord progression
    public void generate_music() {
        int k = 0;
        if(key.equals("Random")){
            k = (int) (Math.random()*11);
        }
        else if(key.equals("C")){
            k = 0;
        }
        else if(key.equals("C#")){
            k = 1;
        }
        else if(key.equals("D")){
            k = 2;
        }
        else if(key.equals("Eb")){
            k = 3;
        }
        else if(key.equals("E")){
            k = 4;
        }
        else if(key.equals("F")){
            k = 5;
        }
        else if(key.equals("F#")){
            k = 6;
        }
        else if(key.equals("G")){
            k = 7;
        }
        else if(key.equals("G#")){
            k = 8;
        }
        else if(key.equals("A")){
            k = 9;
        }
        else if(key.equals("Bb")){
            k = 10;
        }
        else if(key.equals("B")){
            k = 11;
        }
        int barR = ((int) (Math.random() * 8)) + 3;
        int numOfChords = (int)(Math.random()*20)+6;
        int tempo = (int) Math.random()*60 + 100;
        int upperBound = 5;
        int lowerBound = 5;
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
                //{4, 8, 11},
                {5, 9, 0, 4},
                {7, 11, 2, 6},
                {9, 0, 4, 7}
        };
        int[][] chordProg = new int[numOfChords][4];
        for(int i = 0; i < numOfChords ; i++)
        {
            chordProg[i] = seventh[(int) (Math.random()*seventh.length)];
        }
        int [] bassRhythm = rhythm(barR, 20);
        int [] cRhythm = rhythm(barR, 60);
        int [] offRhythm = rhythm(barR, 15);
        int [] bassNotes;
        int [] trebNotes;
        int [] offNotes;
        for (int i = 0; i < numOfChords; i ++) {

            // note on
            bassNotes = generateNotes(lowerBound-3, upperBound-3, barR, k, chordProg[i] , (int) (Math.random()*3));
            trebNotes = generateNotes(lowerBound-2, upperBound-2, barR, k, chordProg[i] , (int) (Math.random()*3));
            offNotes = generateNotes(lowerBound-3, upperBound-2, barR, k, chordProg[i] , (int) (Math.random()*3));
            for(int j = 0; j<barR;j++){
                if(bassRhythm[j]==1)
                {
                    track.add(makeEvent(144, 1, bassNotes[j], 100, 4*i*barR + 4*j));
                    //track.add(makeEvent(128, 1, bassNotes[j], 100, 4*i*barR + 4*j + 5));
                }
                if(cRhythm[j]==1)
                {
                    track.add(makeEvent(144, 1, trebNotes[j], 100, 4*i*barR + 4*j));
                    //track.add(makeEvent(128, 1, bassNotes[j], 100, 4*i*barR + 4*j + 5));
                }
                if(offRhythm[j]==1)
                {
                    track.add(makeEvent(144, 1, offNotes[j], 100, (4*i*barR + 4*j)+2));
                    //track.add(makeEvent(128, 1, bassNotes[j], 100, 4*i*barR + 4*j + 5));
                }
                //track.add(makeEvent(144, 1, i, 100, i));
                //track.add(makeEvent(144, 1, i, 100, i));
            }

            // note off
            //track.add(makeEvent(128, 1, i, 100, i + 2));
        }
        /*track.add(makeEvent(144, 1, 60, 100, 15));
        track.add(makeEvent(144, 1, 66, 100, 20));
        track.add(makeEvent(144, 1, 67, 100, 30));
        track.add(makeEvent(144, 1, 101, 100, 300));
        track.add(makeEvent(128, 1, 101, 100, 305));*/
    }

    int[] generateNotes(int lowerBound, int upperBound, int totalNotes, int key, int[] chord, int degree) {

        int[] ret = new int[totalNotes];
        for(int i = 0; i < totalNotes; i++) {
            ret[i] = (chord[degree] + 12*(rand.nextInt(upperBound) + lowerBound))+key;
        }
        return ret;
    }

    public int[] rhythm(int rLength, int percentage)//makes a rhythm for a certain part. rLength is the bar length, percentage is the chance for a higher frequency of notes
    {
        int[] retval = new int[rLength];
        for(int i = 0; i < rLength; i++) {
            if ((int)(Math.random()* 100) < percentage) {
                retval[i] = 1;
                Log.d("YEPPPPPPPP", "WWWWWWWWWWWWWWWWWWWWWWWWWWWWWW");
            }
            else{
                retval[i] = 0;
                Log.d("NOPEE", "YEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEEET");
            }
        }
        return retval;
    }
}
