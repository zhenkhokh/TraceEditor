package ru.android.zheka.sound.util;

import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.media.AudioFormat.CHANNEL_IN_MONO;
import static android.media.AudioFormat.CHANNEL_IN_STEREO;
import static android.media.AudioFormat.ENCODING_PCM_16BIT;
import static android.media.AudioRecord.ERROR_BAD_VALUE;
import static android.media.AudioRecord.STATE_INITIALIZED;
import static android.media.AudioRecord.STATE_UNINITIALIZED;
import static ru.android.zheka.sound.util.Constants.BytesPerElement;
import static ru.android.zheka.sound.util.Constants.RECORDER_SAMPLERATE;

public class AudioRecorder {
    private final String path;
    private AudioRecord mRecorder;
    private boolean isRecording = false;
    private final File mFileName;
    private File mWaveFile;
    private boolean isReleased;
    private int minBufferSize;

    public boolean isReleased() {
        return isReleased;
    }

    public AudioRecorder(String path) throws IOException {
        this.path = path;
        mFileName = new File (path);
        if (!mFileName.exists ())
            mFileName.createNewFile ();
        isReleased = false;
        minBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,
                CHANNEL_IN_MONO, ENCODING_PCM_16BIT);

        mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE, CHANNEL_IN_MONO,
                ENCODING_PCM_16BIT, minBufferSize * BytesPerElement);
        if (mRecorder.getState () != STATE_UNINITIALIZED) {
            Object[] mRecorderChunk = findAudioRecord ();
            mRecorder = (AudioRecord) mRecorderChunk[0];
            minBufferSize = (int) mRecorderChunk[1];
        }
    }
    public Object[] findAudioRecord() {
        for (int rate : new int[] { 44100, 22050, 11025, 8000 }) {
            for (short audioFormat : new short[] { ENCODING_PCM_16BIT }) { // 8 bit is not supported in speech api
                for (short channelConfig : new short[] { CHANNEL_IN_MONO, CHANNEL_IN_STEREO }) {
                        int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);
                        if (bufferSize != ERROR_BAD_VALUE) {
                            // check if we can instantiate and have a success
                            AudioRecord recorder = new AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                                    rate, channelConfig, audioFormat, bufferSize*BytesPerElement);
                            if (recorder.getState() == STATE_INITIALIZED)
                                return new Object[] {recorder, new Integer (bufferSize)};
                        }
                }
            }
        }
        throw new RuntimeException("Cant make sound record");
    }

     public void start() throws IOException, IllegalStateException {
        mRecorder.startRecording();
        if (!isReleased) {
             writeAudioDataToFile();
        }
    }

    synchronized private void writeAudioDataToFile()  throws IOException{
        // Write the output audio in byte
            isRecording = true;
            DataOutputStream os = new DataOutputStream (new FileOutputStream (mFileName));
            short[] sData = new short[minBufferSize];
            while (isRecording) {
                // gets the voice output from microphone to byte format
                mRecorder.read (sData, 0, minBufferSize);
                byte increseadRateData[] = short2byte (sData);
                os.write (increseadRateData, 0, minBufferSize * BytesPerElement);
            }
            os.close ();
    }

    private byte[] short2byte(short[] s) {
        byte[] b = new byte[s.length*2];//BytesPerElement
        int i=0;
        for (short in:s) {
            b[i++] = (byte) (in & 0xff);
            b[i++] = (byte) ((in >> 8) & 0xff);
        }
        return b;
    }

    public void release() {
        isReleased = true;
        mRecorder.release ();
    }

    public void stop() throws IOException {
        isRecording = false;
        synchronized (this) {
            mWaveFile = new File (path + ".wav");
            rawToWave (mFileName, mWaveFile);
        }
    }

    private void rawToWave(final File rawFile, final File waveFile) throws IOException {
        byte[] rawData = new byte[(int) rawFile.length()];
        DataInputStream input = new DataInputStream (new FileInputStream(rawFile));
        input.read(rawData);
        input.close ();

        DataOutputStream output = new DataOutputStream(new FileOutputStream(waveFile));
        // WAVE header
        writeString(output, "RIFF"); // chunk id
        writeInt(output, 36 + rawData.length); // chunk size
        writeString(output, "WAVE"); // format
        writeString(output, "fmt "); // subchunk 1 id
        writeInt(output, 16); // subchunk 1 size
        writeShort(output, (short) 1); // audio format (1 = PCM)
        writeShort(output, (short) 1); // number of channels
        writeInt(output, mRecorder.getSampleRate ()); // sample rate
        writeInt(output, mRecorder.getSampleRate ()* BytesPerElement); // byte rate
        writeShort(output, (short) 2); // block align
        writeShort(output, (short) 16); // bits per sample
        writeString(output, "data"); // subchunk 2 id
        writeInt(output, rawData.length); // subchunk 2 size
        output.write(fullyReadFileToBytes(rawFile));
        output.close();
    }
    byte[] fullyReadFileToBytes(File f) throws IOException {
        int size = (int) f.length();
        byte bytes[] = new byte[size];
        byte tmpBuff[] = new byte[size];
        FileInputStream fis= new FileInputStream(f);
        int read = fis.read(bytes, 0, size);
        if (read < size) {
            int remain = size - read;
            while (remain > 0) {
                read = fis.read(tmpBuff, 0, remain);
                System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                remain -= read;
            }
        }
        fis.close();
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

    public File getmWaveFile() {
        return mWaveFile;
    }
}
