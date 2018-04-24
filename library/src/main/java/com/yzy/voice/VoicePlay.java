package com.yzy.voice;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;

import com.yzy.voice.constant.VoiceConstants;
import com.yzy.voice.event.PlayEvent;
import com.yzy.voice.util.FileUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author 志尧
 * @date on 2018-01-12 15:09
 * @email 1417337180@qq.com
 * @describe 音频播放
 * @ideas
 */

public class VoicePlay {

    private ExecutorService mExecutorService;
    private Context mContext;

    private VoicePlay(Context context) {
        this.mContext = context;
        this.mExecutorService = Executors.newCachedThreadPool();
    }

    private volatile static VoicePlay mVoicePlay = null;

    /**
     * 单例
     *
     * @return
     */
    public static VoicePlay with(Context context) {
        if (mVoicePlay == null) {
            synchronized (VoicePlay.class) {
                if (mVoicePlay == null) {
                    mVoicePlay = new VoicePlay(context);
                }
            }
        }
        return mVoicePlay;
    }

    /**
     * 默认收款成功样式
     *
     * @param money
     */
    public void play(String money) {
        play(money, false, false);
    }

    /**
     * 设置播报数字
     *
     * @param money
     * @param checkNum
     */
    public void play(String money, boolean checkNum, boolean account) {
        VoiceBuilder.Builder voiceBuilder = new VoiceBuilder.Builder();
        if (!account) {
            voiceBuilder.start(VoiceConstants.RECEIPT_SUCCESS);
        } else {
            voiceBuilder.start(VoiceConstants.ACCOUNT_SUCCESS);
        }
        voiceBuilder.money(money);
        voiceBuilder.unit(VoiceConstants.YUAN);
        voiceBuilder.checkNum(checkNum);
        executeStart(voiceBuilder.builder());
    }

    /**
     * 接收自定义
     *
     * @param voiceBuilder
     */
    public void play(VoiceBuilder voiceBuilder) {
        executeStart(voiceBuilder);
    }

    /**
     * 开启线程
     *
     * @param builder
     */
    private void executeStart(VoiceBuilder builder) {
        List<String> voicePlay = VoiceTextTemplate.genVoiceList(builder);
        if (voicePlay == null || voicePlay.isEmpty()) {
            return;
        }

        mExecutorService.execute(() -> start(voicePlay));
    }

    /**
     * 开始播报
     *
     * @param voicePlay
     */
    private void start(List<String> voicePlay) {
        synchronized (VoicePlay.this) {
            MediaPlayer mMediaPlayer = new MediaPlayer();
            CountDownLatch mCountDownLatch = new CountDownLatch(1);
            AssetFileDescriptor assetFileDescription = null;
            try {
                final int[] counter = {0};
                assetFileDescription = FileUtils.getAssetFileDescription(mContext,
                        String.format(VoiceConstants.FILE_PATH, voicePlay.get(counter[0])));
                mMediaPlayer.setDataSource(
                        assetFileDescription.getFileDescriptor(),
                        assetFileDescription.getStartOffset(),
                        assetFileDescription.getLength());
                mMediaPlayer.prepareAsync();
                mMediaPlayer.setOnPreparedListener(mediaPlayer -> mMediaPlayer.start());
                mMediaPlayer.setOnCompletionListener(mediaPlayer -> {
                    mediaPlayer.reset();
                    counter[0]++;
                    if (counter[0] < voicePlay.size()) {
                        try {
                            AssetFileDescriptor fileDescription2 = FileUtils.getAssetFileDescription(mContext,
                                    String.format(VoiceConstants.FILE_PATH, voicePlay.get(counter[0])));
                            mediaPlayer.setDataSource(
                                    fileDescription2.getFileDescriptor(),
                                    fileDescription2.getStartOffset(),
                                    fileDescription2.getLength());
                            mediaPlayer.prepare();
                        } catch (IOException e) {
                            e.printStackTrace();
                            mCountDownLatch.countDown();
                            EventBus.getDefault().post(new PlayEvent(false));
                        }
                    } else {
                        mediaPlayer.release();
                        mCountDownLatch.countDown();
                        EventBus.getDefault().post(new PlayEvent(true));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                mCountDownLatch.countDown();
                EventBus.getDefault().post(new PlayEvent(false));
            } finally {
                if (assetFileDescription != null) {
                    try {
                        assetFileDescription.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                mCountDownLatch.await();
                notifyAll();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
