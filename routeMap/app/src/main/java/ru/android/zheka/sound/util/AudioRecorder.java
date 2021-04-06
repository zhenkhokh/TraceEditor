package ru.android.zheka.sound.util;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static ru.android.zheka.sound.util.Constants.BufferElements2Rec;
import static ru.android.zheka.sound.util.Constants.BytesPerElement;
import static ru.android.zheka.sound.util.Constants.RECORDER_AUDIO_ENCODING;
import static ru.android.zheka.sound.util.Constants.RECORDER_CHANNELS;
import static ru.android.zheka.sound.util.Constants.RECORDER_SAMPLERATE;

public class AudioRecorder {
    private final String path;
    private AudioRecord mRecorder;
    private boolean isRecording = false;
    private File mFileName;

    public boolean isReleased() {
        return isReleased;
    }

    private boolean isReleased;

    public AudioRecorder(String path) {
        this.path = path;
        mFileName = new File (path);
        isReleased = false;
    }

     public void start() throws IOException {

        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING, BufferElements2Rec * BytesPerElement);

        mRecorder.startRecording();

        if (!isReleased) {
            writeAudioDataToFile();
        }
    }

    synchronized private void writeAudioDataToFile() throws IOException{
        // Write the output audio in byte
        isRecording = true;
        FileOutputStream os = new FileOutputStream(mFileName ,false);
        short[] sData = new short[BufferElements2Rec];
        while (isRecording) {
            // gets the voice output from microphone to byte format
            mRecorder.read(sData, 0, BufferElements2Rec);
            byte increseadRateData[] = short2byte(sData);
            os.write(increseadRateData, 0, BufferElements2Rec * BytesPerElement);
        }
        os.close();
    }

    private byte[] short2byte(short[] s) {
        byte[] b = new byte[s.length*2];
        int i=0;
        for (short in:s) {
            b[i++] = (byte) (in & 0xff);
            b[i++] = (byte) ((in >> 8) & 0xff);
        }
        return b;
    }

    public void release() {
        isReleased = true;
    }

    public void stop() {
        isRecording = false;
        synchronized (this) {
            try {
                rawToWave (mFileName, new File (path + ".wav"));
            } catch (IOException e) {
                e.printStackTrace ();
            }
        }
    }

    private void rawToWave(final File rawFile, final File waveFile) throws IOException {

        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = null;
        try {
            input = new DataInputStream (new FileInputStream(rawFile));
            input.read(rawData);
        } finally {
            if (input != null) {
                input.close();
            }
        }

        DataOutputStream output = null;
        try {
            output = new DataOutputStream(new FileOutputStream(waveFile));
            // WAVE header
            writeString(output, "RIFF"); // chunk id
            writeInt(output, 36 + rawData.length); // chunk size
            writeString(output, "WAVE"); // format
            writeString(output, "fmt "); // subchunk 1 id
            writeInt(output, 16); // subchunk 1 size
            writeShort(output, (short) 1); // audio format (1 = PCM)
            writeShort(output, (short) 1); // number of channels
            writeInt(output, RECORDER_SAMPLERATE); // sample rate
            writeInt(output, RECORDER_SAMPLERATE * 2); // byte rate
            writeShort(output, (short) 2); // block align
            writeShort(output, (short) 16); // bits per sample
            writeString(output, "data"); // subchunk 2 id
            writeInt(output, rawData.length); // subchunk 2 size
            output.write(fullyReadFileToBytes(rawFile));
        } finally {
            if (output != null) {
                output.close();
            }
        }
    }
    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(f);
        try {
            int read = fis.read(bytes, 0, size);
            if (read < size) {
                int remain = size - read;
                while (remain > 0) {
                    read = fis.read(tmpBuff, 0, remain);
                    System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                    remain -= read;
                }
            }
        } finally {
            fis.close();
        }

        return bytes;
    }
    private void writeInt(final DataOutputStream output, final int value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
        output.write(value >> 16);
        output.write(value >> 24);
    }

    private void writeShort(final DataOutputStream output, final short value) throws IOException {
        output.write(value >> 0);
        output.write(value >> 8);
    }

    private void writeString(final DataOutputStream output, final String value) throws IOException {
        for (int i = 0; i < value.length(); i++) {
            output.write(value.charAt(i));
        }
    }
}
