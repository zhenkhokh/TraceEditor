package ru.android.zheka.sound.util

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import java.io.*

class AudioRecorder(private val path: String) {
    private var mRecorder: AudioRecord
    private var isRecording = false
    private val mFileName: File
    private var mWaveFile: File? = null
    var isReleased: Boolean
        private set
    private var minBufferSize: Int

    fun findAudioRecord(): Array<Any> {
        for (rate in intArrayOf(44100, 22050, 11025, 8000)) {
            for (audioFormat in shortArrayOf(AudioFormat.ENCODING_PCM_16BIT.toShort())) { // 8 bit is not supported in speech api
                for (channelConfig in shortArrayOf(AudioFormat.CHANNEL_IN_MONO.toShort(), AudioFormat.CHANNEL_IN_STEREO.toShort())) {
                    val bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig.toInt(), audioFormat.toInt())
                    if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
                        // check if we can instantiate and have a success
                        val recorder = AudioRecord(MediaRecorder.AudioSource.DEFAULT,
                                rate, channelConfig.toInt(), audioFormat.toInt(), bufferSize * Constants.BytesPerElement)
                        if (recorder.state == AudioRecord.STATE_INITIALIZED) return arrayOf(recorder, bufferSize)
                    }
                }
            }
        }
        throw RuntimeException("Cant make sound record")
    }

    @Throws(IOException::class, IllegalStateException::class)
    fun start() {
        mRecorder.startRecording()
        if (!isReleased) {
            writeAudioDataToFile()
        }
    }

    @Synchronized
    @Throws(IOException::class)
    private fun writeAudioDataToFile() {
        // Write the output audio in byte
        isRecording = true
        val os = DataOutputStream(FileOutputStream(mFileName))
        val sData = ShortArray(minBufferSize)
        while (isRecording) {
            // gets the voice output from microphone to byte format
            mRecorder.read(sData, 0, minBufferSize)
            val increseadRateData = short2byte(sData)
            os.write(increseadRateData, 0, minBufferSize * Constants.BytesPerElement)
        }
        os.close()
    }

    private fun short2byte(s: ShortArray): ByteArray {
        val b = ByteArray(s.size * 2) //BytesPerElement
        var i = 0
        for (`in` in s) {
            val si = `in`.toInt()
            b[i++] = (si and 0xff).toByte()
            b[i++] = (si shr 8 and 0xff).toByte()
        }
        return b
    }

    fun release() {
        isReleased = true
        mRecorder.release()
    }

    @Throws(IOException::class)
    fun stop() {
        isRecording = false
        synchronized(this) {
            mWaveFile = File("$path.wav")
            rawToWave(mFileName, mWaveFile!!)
        }
    }

    @Throws(IOException::class)
    private fun rawToWave(rawFile: File, waveFile: File) {
        val rawData = ByteArray(rawFile.length().toInt())
        val input = DataInputStream(FileInputStream(rawFile))
        input.read(rawData)
        input.close()
        val output = DataOutputStream(FileOutputStream(waveFile))
        // WAVE header
        writeString(output, "RIFF") // chunk id
        writeInt(output, 36 + rawData.size) // chunk size
        writeString(output, "WAVE") // format
        writeString(output, "fmt ") // subchunk 1 id
        writeInt(output, 16) // subchunk 1 size
        writeShort(output, 1) // audio format (1 = PCM)
        writeShort(output, 1) // number of channels
        writeInt(output, mRecorder.sampleRate) // sample rate
        writeInt(output, mRecorder.sampleRate * Constants.BytesPerElement) // byte rate
        writeShort(output, 2) // block align
        writeShort(output, 16) // bits per sample
        writeString(output, "data") // subchunk 2 id
        writeInt(output, rawData.size) // subchunk 2 size
        output.write(fullyReadFileToBytes(rawFile))
        output.close()
    }

    @Throws(IOException::class)
    fun fullyReadFileToBytes(f: File): ByteArray {
        val size = f.length().toInt()
        val bytes = ByteArray(size)
        val tmpBuff = ByteArray(size)
        val fis = FileInputStream(f)
        var read = fis.read(bytes, 0, size)
        if (read < size) {
            var remain = size - read
            while (remain > 0) {
                read = fis.read(tmpBuff, 0, remain)
                System.arraycopy(tmpBuff, 0, bytes, size - remain, read)
                remain -= read
            }
        }
        fis.close()
        return bytes
    }

    @Throws(IOException::class)
    private fun writeInt(output: DataOutputStream, value: Int) {
        output.write(value shr 0)
        output.write(value shr 8)
        output.write(value shr 16)
        output.write(value shr 24)
    }

    @Throws(IOException::class)
    private fun writeShort(output: DataOutputStream, value: Int) {
        output.write(value shr 0)
        output.write(value shr 8)
    }

    @Throws(IOException::class)
    private fun writeString(output: DataOutputStream, value: String) {
        for (i in 0 until value.length) {
            output.write(value[i].toInt())
        }
    }

    fun getmWaveFile(): File {
        return mWaveFile!!
    }

    init {
        mFileName = File(path)
        if (!mFileName.exists()) mFileName.createNewFile()
        isReleased = false
        minBufferSize = AudioRecord.getMinBufferSize(Constants.RECORDER_SAMPLERATE,
                AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
        mRecorder = AudioRecord(MediaRecorder.AudioSource.MIC,
                Constants.RECORDER_SAMPLERATE, AudioFormat.CHANNEL_IN_MONO,
                AudioFormat.ENCODING_PCM_16BIT, minBufferSize * Constants.BytesPerElement)
        if (mRecorder.state != AudioRecord.STATE_UNINITIALIZED) {
            val mRecorderChunk = findAudioRecord()
            mRecorder = mRecorderChunk[0] as AudioRecord
            minBufferSize = mRecorderChunk[1] as Int
        }
    }
}