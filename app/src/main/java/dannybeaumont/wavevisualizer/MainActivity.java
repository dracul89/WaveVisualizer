package dannybeaumont.wavevisualizer;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import dannybeaumont.visualizer.view.WaveFormView;

public class MainActivity extends AppCompatActivity implements RecognitionListener {

    private static final int PERMISSION_CODE = 255;
    public static final String SPEECH_RECOGNIZER = "Speech Recognizer";
    @BindView(R.id.imageButton)
    ImageButton micButton;

    @BindView(R.id.waveformView)
    WaveFormView waveFormView;

    @BindView(R.id.editText)
    EditText responseText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        if (canNotRecord()) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSION_CODE);
        } else {
            micButton.setEnabled(true);
        }
    }

    public void onRecord(View view) {
        if (canNotRecord()) {
            return;
        }
            // do speech recognition here
            SpeechRecognizer speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(this);
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            speechRecognizer.startListening(intent);

    }

    private boolean canNotRecord() {
        return ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.i(SPEECH_RECOGNIZER,"Ready for Speech");
    }

    @Override
    public void onBeginningOfSpeech() {
        Log.i(SPEECH_RECOGNIZER,"Speech started");
    }

    @Override
    public void onRmsChanged(float rmsdB) {
        waveFormView.updateAmplitude(rmsdB/10);
    }

    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.i(SPEECH_RECOGNIZER,"buffer received");
    }

    @Override
    public void onEndOfSpeech() {
        Log.i(SPEECH_RECOGNIZER,"Speech Ended");
    }

    @Override
    public void onError(int error) {
        Log.e(SPEECH_RECOGNIZER,"Error "+error);
    }

    @Override
    public void onResults(Bundle results) {
        List<String> resultList = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        responseText.setText(resultList.get(0));
    }

    @Override
    public void onPartialResults(Bundle partialResults) {
        Log.i(SPEECH_RECOGNIZER,"partialResults");
    }

    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.i(SPEECH_RECOGNIZER,"Event "+eventType);
    }
}
