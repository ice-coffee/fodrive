package com.jinglingtec.ijiazu.util;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import com.baidu.speechsynthesizer.SpeechSynthesizer;
import com.baidu.speechsynthesizer.SpeechSynthesizerListener;
import com.baidu.speechsynthesizer.publicutility.SpeechError;

/**
 * Created by coffee on 14-12-15.
 */
public class FoVoice
{
    private static SpeechSynthesizer speechSynthesizer;
    private String outCall = "android.intent.action.NEW_OUTGOING_CALL";


    public static SpeechSynthesizer getSpeechSynthesizer(Context context)

    {
        /**构造合成器**/
        speechSynthesizer = new SpeechSynthesizer(context, "holder", new SpeechSynthesizerListener()
        {
            @Override
            public void onStartWorking(SpeechSynthesizer synthesizer)
            {

            }

            @Override
            public void onSpeechStart(SpeechSynthesizer synthesizer)
            {

            }

            @Override
            public void onSpeechResume(SpeechSynthesizer synthesizer)
            {

            }

            @Override
            public void onSpeechProgressChanged(SpeechSynthesizer synthesizer, int progress)
            {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSpeechPause(SpeechSynthesizer synthesizer)
            {

            }

            @Override
            public void onSpeechFinish(SpeechSynthesizer synthesizer)
            {

            }

            /**
             * 获取合成数据
             */
            @Override
            public void onNewDataArrive(SpeechSynthesizer synthesizer, byte[] audioData, boolean isLastData)
            {

            }

            @Override
            public void onError(SpeechSynthesizer synthesizer, SpeechError error)
            {

            }

            @Override
            public void onCancel(SpeechSynthesizer synthesizer)
            {

            }

            @Override
            public void onBufferProgressChanged(SpeechSynthesizer synthesizer, int progress)
            {
                // TODO Auto-generated method stub

            }

            private void logDebug(String logMessage)
            {
                logMessage(logMessage, Color.BLUE);
            }

            private void logError(String logMessage)
            {
                logMessage(logMessage, Color.RED);
            }

            private void logMessage(String logMessage, int color)
            {

            }

        });

        // 此处需要将setApiKey方法的两个参数替换为你在百度开发者中心注册应用所得到的apiKey和secretKey
        /**设置开发者秘钥以获取token**/
        speechSynthesizer.setApiKey("xW1ico9wF8vMVDFYjRDdUUSw", "EEsadGFCF8riQP2yFfSNuX07wZVzOU7b");
        /**设置用于语音播报的音频流类型，类型列表请参见AudioManager， 默认类型为 AudioManager.STREAM_MUSIC**/
        speechSynthesizer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        setParams();
        return speechSynthesizer;
    }

    /**
     * 设置合成器参数
     */
    private static void setParams()
    {
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");//目前支持女声(0)和男声(1)
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_VOLUME, "9");//取值范围[0, 9]，数值越大，音量越大
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEED, "5");//取值范围[0, 9]，数值越大，语速越快
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PITCH, "6");//取值范围[0, 9]，数值越大，音量越高
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_ENCODE, "1");//支持bv/amr/opus/mp3
        speechSynthesizer.setParam(SpeechSynthesizer.PARAM_AUDIO_RATE, "4");//音频比特率
        // speechSynthesizer.setParam(SpeechSynthesizer.PARAM_LANGUAGE, "ZH");//当前支持ZH（中文）和EN（英文）
        // speechSynthesizer.setParam(SpeechSynthesizer.PARAM_NUM_PRON, "0");//数字发音方式，取值0，1，暂不支持
        // speechSynthesizer.setParam(SpeechSynthesizer.PARAM_ENG_PRON, "0");//英文发音方式，取值0，1，暂不支持
        // speechSynthesizer.setParam(SpeechSynthesizer.PARAM_PUNC, "0");//标点符号读法，暂不支持
        // speechSynthesizer.setParam(SpeechSynthesizer.PARAM_BACKGROUND, "0");//背景音，暂不支持
        // speechSynthesizer.setParam(SpeechSynthesizer.PARAM_STYLE, "0");//发音风格，暂不支持
        // speechSynthesizer.setParam(SpeechSynthesizer.PARAM_TERRITORY, "0");//领域，暂不支持
    }
}