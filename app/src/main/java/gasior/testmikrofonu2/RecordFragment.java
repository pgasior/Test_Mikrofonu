package gasior.testmikrofonu2;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by Piotrek on 21.03.2016.
 */
public class RecordFragment extends Fragment {

    private MediaRecorder mRecorder = null;
    private UpdateThread updateThread = null;
    private MediaPlayer mPlayer = null;

    private final String LOG_TAG = this.getClass().getName();
    private String currFilename;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);

    }


    public void startRecording(){
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);

        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //GregorianCalendar gc = new GregorianCalendar();
        String date = new SimpleDateFormat("dd-mm-yyyy_HH-mm-ss").format(new Date());
        currFilename = date+".aac";
        String filename = getActivity().getExternalFilesDir(null).getAbsolutePath() + "/" + currFilename;
        mRecorder.setOutputFile(filename);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }


        mRecorder.start();
        updateThread = new UpdateThread();
        updateThread.start();
    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        updateThread.setShouldRun(false);
        updateThread = null;
        ((MainActivity)getActivity()).setTimeElapsed("Koniec nagrywania");
        ((MainActivity)getActivity()).addNewFile(currFilename);
    }

    public void play(String plik) {
        mPlayer = new MediaPlayer();
        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                ((MainActivity)getActivity()).disableStopPlayButton();
            }
        });
        try {
            mPlayer.setDataSource(getActivity().getExternalFilesDir(null).getAbsolutePath() + "/" +plik);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }


    private class UpdateThread extends Thread {
        int i=0;
        private boolean shouldRun = true;

        public void run() {
            i=0;
            while(shouldRun) {
                ((MainActivity)getActivity()).setTimeElapsed(Integer.toString(i));
                i++;
                SystemClock.sleep(1000);
            }
        }

        public void setShouldRun(boolean shouldRun) {
            this.shouldRun = shouldRun;
        }
    }


}
