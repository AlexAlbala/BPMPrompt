package com.a2t.autobpmprompt.media.audio;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.media.MediaSyncEvent;
import android.util.Log;

public class Recorder {
    //TODO: Driver for microphone
    //TODO: Maybe JNI bridge for calculate BPM ?

    private static String TAG = "ABPM - AUDIORECORDER";
    private static int SAMPLERATE = 44100;
    private static int CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static int  ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static int BYTES_PER_SAMPLE = 2;
    //private static int NUMSAMPLES = 1024;
    //private static int SIZE = NUMSAMPLES * BYTES_PER_SAMPLE;

    AudioRecord rec;

    public Recorder(){

    }

    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
    public AudioRecord findAudioRecord() {
        for (int rate : mSampleRates) {
            for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
                for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
                    try {
                        Log.d(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
                                + channelConfig);
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED)
                                return recorder;
                        }
                    } catch (Exception e) {
                        Log.e(TAG, rate + "Exception, keep trying.",e);
                    }
                }
            }
        }
        return null;
    }

    public byte[] Record(int milliseconds){
        int SIZE = (milliseconds * SAMPLERATE / 1000) *  BYTES_PER_SAMPLE;
        //rec = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLERATE, CHANNELS, ENCODING, SIZE);

        rec = findAudioRecord();
        rec.startRecording();

        byte[] buff = new byte[SIZE];
        rec.read(buff, 0, SIZE);
        rec.stop();
        return buff;
    }
}
