package gasior.testmikrofonu2;

import android.support.v4.app.FragmentManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView tv;
    String TAG = this.getClass().getName();
    RecordFragment recordFragment;

    Button startButton;
    Button stopButton;
    Button stopPlayButton;
    ListView lvFiles;
    ArrayAdapter<String> adapter = null;

    //ArrayList<String> files = null;

    ArrayList<String> getFiles() {
        ArrayList<String> files = new ArrayList<>();
        File katalog = this.getExternalFilesDir(null);
        for(File f: katalog.listFiles()) {
            files.add(f.getName());
        }
        return files;
    }

    public void addNewFile(final String nazwa) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(nazwa);
            }
        });
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        tv=(TextView)findViewById(R.id.textView);
        String stor1 = Environment.getExternalStorageDirectory().getAbsolutePath();
        Log.i(TAG,"stor1: "+stor1);
        String stor2 = this.getExternalFilesDir(null).getAbsolutePath();
        Log.i(TAG,"stor2: "+stor2);
        String getfilesdir = this.getFilesDir().getAbsolutePath();
        Log.i(TAG,"getfilesdir: "+getfilesdir);

        FragmentManager fm = getSupportFragmentManager();
        recordFragment = (RecordFragment) fm.findFragmentByTag("recordFragment");
        if (recordFragment == null) {
            // add the fragment
            recordFragment = new RecordFragment();
            fm.beginTransaction().add(recordFragment, "recordFragment").commit();
            // load the data from the web
        }
        lvFiles = (ListView)findViewById(R.id.listView);
        startButton = (Button)findViewById(R.id.startButton);
        stopButton = (Button)findViewById(R.id.stopButton);
        //stopButton.setEnabled(false);
        stopPlayButton = (Button)findViewById(R.id.stopPlayButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordFragment.startRecording();
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
                lvFiles.setEnabled(false);
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordFragment.stopRecording();
                stopButton.setEnabled(false);
                startButton.setEnabled(true);
                lvFiles.setEnabled(true);
            }
        });




        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,getFiles());


        lvFiles.setAdapter(adapter);
        lvFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String plik = (String) ((ListView) parent).getItemAtPosition(position);
                recordFragment.play(plik);
                stopPlayButton.setEnabled(true);
                lvFiles.setEnabled(false);
            }
        });

        //stopPlayButton.setEnabled(false);
        stopPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordFragment.stopPlaying();
                lvFiles.setEnabled(true);
                stopPlayButton.setEnabled(false);
            }
        });
        if(savedInstanceState!=null) {
            startButton.setEnabled(savedInstanceState.getBoolean("startButton"));
            stopButton.setEnabled(savedInstanceState.getBoolean("stopButton"));
            stopPlayButton.setEnabled(savedInstanceState.getBoolean("stopPlayButton"));
            lvFiles.setEnabled(savedInstanceState.getBoolean("lvFiles"));
            tv.setText(savedInstanceState.getString("tv"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("startButton", startButton.isEnabled());
        outState.putBoolean("stopButton", stopButton.isEnabled());
        outState.putBoolean("stopPlayButton",stopPlayButton.isEnabled());
        outState.putBoolean("lvFiles",lvFiles.isEnabled());
        outState.putString("tv",tv.getText().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void setTimeElapsed(final String timeElapsed) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv.setText(timeElapsed);
            }
        });
    }

    public void disableStopPlayButton() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopPlayButton.setEnabled(false);
                lvFiles.setEnabled(true);
            }
        });
    }
}
