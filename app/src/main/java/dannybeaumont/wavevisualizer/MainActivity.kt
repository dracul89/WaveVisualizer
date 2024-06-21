package dannybeaumont.wavevisualizer

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import dannybeaumont.wavevisualizer.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity(), RecognitionListener {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (canNotRecord()) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_CODE
            )
        } else {
            binding.imageButton.isEnabled = true
        }
    }

    fun onRecord(view: View?) {
        if (canNotRecord()) {
            return
        }
        // do speech recognition here
        val speechRecognizer = SpeechRecognizer.createSpeechRecognizer(view!!.context)
        speechRecognizer.setRecognitionListener(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        speechRecognizer.startListening(intent)
    }

    private fun canNotRecord(): Boolean {
        return (ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        )
                != PackageManager.PERMISSION_GRANTED)
    }

    override fun onReadyForSpeech(params: Bundle) {
        Log.i(SPEECH_RECOGNIZER, "Ready for Speech")
    }

    override fun onBeginningOfSpeech() {
        Log.i(SPEECH_RECOGNIZER, "Speech started")
    }

    override fun onRmsChanged(rmsdB: Float) {
        binding.waveformView.updateAmplitude(rmsdB / 10)
    }

    override fun onBufferReceived(buffer: ByteArray) {
        Log.i(SPEECH_RECOGNIZER, "buffer received")
    }

    override fun onEndOfSpeech() {
        Log.i(SPEECH_RECOGNIZER, "Speech Ended")
    }

    override fun onError(error: Int) {
        Log.e(SPEECH_RECOGNIZER, "Error $error")
    }

    override fun onResults(results: Bundle) {
        val resultList: List<String>? =
            results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        binding.editText.setText(resultList!![0])
    }

    override fun onPartialResults(partialResults: Bundle) {
        Log.i(SPEECH_RECOGNIZER, "partialResults")
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        Log.i(SPEECH_RECOGNIZER, "Event $eventType")
    }

    companion object {
        private const val PERMISSION_CODE = 255
        const val SPEECH_RECOGNIZER: String = "Speech Recognizer"
    }
}
