package com.inmoment.moments.home.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player.EventListener;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.inmoment.moments.R;
import com.inmoment.moments.framework.common.Logger;
import com.inmoment.moments.home.model.Feed;
import com.inmoment.moments.home.ui.adapter.FeedsAdapter;
import com.inmoment.moments.home.ui.adapter.view_holder.AudioHolder;
import com.inmoment.moments.home.ui.adapter.view_holder.VideoHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.google.android.exoplayer2.Player.STATE_BUFFERING;
import static com.google.android.exoplayer2.Player.STATE_ENDED;
import static com.google.android.exoplayer2.Player.STATE_IDLE;
import static com.google.android.exoplayer2.Player.STATE_READY;
import static com.google.android.exoplayer2.util.Util.getUserAgent;
import static com.inmoment.moments.framework.common.ConstantsKt.initialDuration;
import static com.inmoment.moments.framework.common.ConstantsKt.seekBackwardTime;
import static com.inmoment.moments.framework.common.ConstantsKt.seekForwardTime;

public class ExoPlayerRecyclerView extends RecyclerView {

    private static final String TAG = "ExoPlayerRecyclerView";
    /**
     * PlayerViewHolder UI component
     * Watch PlayerViewHolder class
     */
    private View viewHolderParent;
    private FrameLayout mediaContainer;
    private PlayerView surfaceView;
    private SimpleExoPlayer videoPlayer;
    /**
     * variable declaration
     */
    // Media List
    private List<Feed> mediaObjects = new ArrayList<>();

    private Context context;
    private int playPosition = -1;
    private boolean isVideoViewAdded;
    private VolumeState volumeState;

    private final OnClickListener videoViewClickListener = v -> toggleVolume();
    // Audio player
    private SimpleExoPlayer mediaPlayer;
    private View viewAudioHolderParent;
    private SeekBar seekBar;
    private Handler handler;
    private boolean isPlaying = false;
    private TextView seekBarTotalDuration;
    private final EventListener eventListener = new EventListener() {

        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            Logger.i(TAG, "onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            Logger.i(TAG, "onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            Logger.i(TAG, "onPlayerStateChanged: playWhenReady = " + playWhenReady
                    + " playbackState = " + playbackState);
            switch (playbackState) {
                case STATE_ENDED:
                    Logger.i(TAG, "Playback ended!");
                    mediaPlayer.stop(true);
                    mediaObjects.get(playPosition).setCurrentSeekValue(0);
                    isPlaying = false;
                    if (ExoPlayerRecyclerView.this.getAdapter() != null) {
                        ExoPlayerRecyclerView.this.getAdapter().notifyItemChanged(playPosition);
                    }
                    break;
                case STATE_READY:
                    if (seekBar != null) {
                        setAudioCurrentDuration();
                    }
                    break;
                case STATE_BUFFERING:
                    Logger.i(TAG, "Playback buffering!");
                    break;
                case STATE_IDLE:
                    Logger.i(TAG, "ExoPlayer idle!");
                    break;
                default:
                    break;
            }
        }

        @Override
        public void onPlayerError(ExoPlaybackException error) {
            Logger.i(TAG, "onPlaybackError: " + error.getMessage());
        }
    };
    private ImageView playAudioIV;
    private ImageView playVideoIV;

    public ExoPlayerRecyclerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public ExoPlayerRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.context = context.getApplicationContext();
        surfaceView = new PlayerView(this.context);
        surfaceView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
        BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(videoTrackSelectionFactory);

        //Create the player using ExoPlayerFactory
        videoPlayer = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        // Disable Player Control
        surfaceView.setUseController(false);
        // Bind the player to the view.
        surfaceView.setPlayer(videoPlayer);
        // Turn on Volume
        setVolumeControl(VolumeState.ON);

        addOnChildAttachStateChangeListener(new OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                // do nothing
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                if (viewHolderParent != null && viewHolderParent.equals(view)) {
                    videoPlayer.seekTo(0);
                    onPausePlayer();
                }
                if (viewAudioHolderParent != null && viewAudioHolderParent.equals(view)) {
                    onPausePlayer();
                }
            }
        });

        videoPlayer.addListener(new EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                // do nothing
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups,
                                        TrackSelectionArray trackSelections) {
                // do nothing
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                // do nothing
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                switch (playbackState) {
                    case STATE_BUFFERING:
                        Logger.e(TAG, "onPlayerStateChanged: Buffering video.");
                        break;
                    case STATE_ENDED:
                        Logger.d(TAG, "onPlayerStateChanged: Video ended.");
                        videoPlayer.stop(true);
                        mediaObjects.get(playPosition).setCurrentSeekValue(0);
                        Objects.requireNonNull(ExoPlayerRecyclerView.this.getAdapter()).notifyItemChanged(playPosition);
                        break;
                    case STATE_READY:
                        Logger.e(TAG, "onPlayerStateChanged: Ready to play.");
                        if (seekBar != null) {
                            setAudioCurrentDuration();
                        }
                        if (!isVideoViewAdded) {
                            addVideoView();
                        }
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                // do nothing
            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                // do nothing
            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                // do nothing
            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                // do nothing
            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                // do nothing
            }

            @Override
            public void onSeekProcessed() {
                // do nothing
            }
        });
    }

    public void prepareVideo(int targetPosition, VideoHolder holder) {
        String videoPath =  mediaObjects.get(playPosition).getVideoUrl();
        if (videoPath == null || videoPath.isEmpty()) {
            return;
        }
        if (surfaceView == null) {
            return;
        }

        // remove any old surface views from previously playing videos
        surfaceView.setVisibility(INVISIBLE);
        removeOlderView(surfaceView);
        //resetAudioPlayer();
        resetVideoView();

        if (holder == null) {
            playPosition = -1;
            return;
        }
        // set the position of the list-item that is to be played
        playPosition = targetPosition;

        viewHolderParent = holder.itemView;
        mediaContainer = holder.itemView.findViewById(R.id.feedsVV);
        playVideoIV = viewHolderParent.findViewById(R.id.videoPlayIV);

        surfaceView.setPlayer(videoPlayer);
        viewHolderParent.setOnClickListener(videoViewClickListener);

        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                context, getUserAgent(context, TAG));
        String mediaUrl =  mediaObjects.get(playPosition).getVideoUrl();
        if (mediaUrl != null) {
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(Uri.parse(mediaUrl));
            videoPlayer.prepare(videoSource);
            videoPlayer.seekTo(0);
        }
    }

    public void playVideo(int targetPosition) {

        Logger.d(TAG, "playVideo: target position: " + targetPosition);
        String mediaUrl =  mediaObjects.get(playPosition).getVideoUrl();
        if (mediaUrl != null) {
            videoPlayer.setPlayWhenReady(true);
        }

    }

    // Remove the old player
    private void removeOlderView(PlayerView videoView) {
        ViewGroup parent = (ViewGroup) videoView.getParent();
        if (parent == null) {
            return;
        }

        int index = parent.indexOfChild(videoView);
        if (index >= 0) {
            parent.removeViewAt(index);
            isVideoViewAdded = false;
            viewHolderParent.setOnClickListener(null);
        }
    }

    private void addVideoView() {
        mediaContainer.addView(surfaceView);
        isVideoViewAdded = true;
        surfaceView.requestFocus();
        surfaceView.setVisibility(VISIBLE);
        surfaceView.setAlpha(1);
    }

    private void resetVideoView() {
        if (isVideoViewAdded && viewHolderParent != null) {
            removeOlderView(surfaceView);
            mediaObjects.get(playPosition).setCurrentSeekValue(videoPlayer.getCurrentPosition());
            playPosition = -1;
            surfaceView.setVisibility(INVISIBLE);
            videoPlayer.stop(true);
            Logger.INSTANCE.v(TAG, "resetVideoView");
            viewHolderParent.findViewById(R.id.videoPlayIV).setVisibility(View.VISIBLE);
            // viewHolderParent = null;
        }
    }

    public void releasePlayer() {
        if (videoPlayer != null) {
            videoPlayer.release();
            videoPlayer = null;
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        viewAudioHolderParent = null;
        viewHolderParent = null;
    }

    public void onPausePlayer() {
        if (videoPlayer != null && isPlaying) {
            playVideoIV.setVisibility(View.VISIBLE);
            videoPlayer.setPlayWhenReady(false);
        }
        if (mediaPlayer != null && isPlaying) {
            playAudioIV.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_play_control));
            setAudioInitialDuration();
            mediaPlayer.setPlayWhenReady(false);
        }
    }

    public void onResumePlayer() {
        if (videoPlayer != null) {
            videoPlayer.setPlayWhenReady(true);
        }
        if (mediaPlayer != null && isPlaying) {
            mediaPlayer.setPlayWhenReady(true);
        }
    }

    private void toggleVolume() {
        if (videoPlayer != null) {
            if (volumeState == VolumeState.OFF) {
                Logger.d(TAG, "togglePlaybackState: enabling volume.");
                setVolumeControl(VolumeState.ON);
            } else if (volumeState == VolumeState.ON) {
                Logger.d(TAG, "togglePlaybackState: disabling volume.");
                setVolumeControl(VolumeState.OFF);
            }
        }
    }

    private void setVolumeControl(VolumeState state) {
        volumeState = state;
        if (state == VolumeState.OFF) {
            videoPlayer.setVolume(0f);
        } else if (state == VolumeState.ON) {
            videoPlayer.setVolume(1f);
        }
    }

    public void setMediaObjects(List<Feed> mediaObjects) {
        this.mediaObjects = mediaObjects;
    }

    public void rewindAudio() {
        if (isPlaying) {
            long currentPosition = mediaPlayer.getCurrentPosition();
            // check if seekBackward time is greater than 0 sec
            if (currentPosition >= seekBackwardTime) {
                // backward song
                mediaPlayer.seekTo(currentPosition - seekBackwardTime);
            } else {
                // backward to starting position
                mediaPlayer.seekTo(0);
                playAudioIV.setImageResource(R.drawable.ic_play_control);
                setAudioInitialDuration();
                onPausePlayer();
            }
        }
    }

    public void forwardAudio() {
        if (isPlaying) {
            // get current song position
            long currentPosition = mediaPlayer.getCurrentPosition();
            // check if seekForward time is lesser than song duration
            if (currentPosition + seekForwardTime <= mediaPlayer.getDuration()) {
                // forward song
                mediaPlayer.seekTo(currentPosition + seekForwardTime);
            } else {
                // forward to end position
                mediaPlayer.seekTo(0);
                playAudioIV.setImageResource(R.drawable.ic_play_control);
                setAudioInitialDuration();
                onPausePlayer();
            }
        }
    }

    public void prepareAudio(Feed feed, int targetPosition, AudioHolder holder) {
        resetAudioPlayer();
        // resetVideoView();
        if (playPosition != targetPosition) {
            playPosition = targetPosition;
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }
        }
        viewAudioHolderParent = holder.itemView;
        seekBar = viewAudioHolderParent.findViewById(R.id.seekBar);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekbar, int progress, boolean fromTouch) {
                String durationText = null;
                if (mediaPlayer.getDuration() > 0) durationText =
                        DateUtils.formatElapsedTime((Long) (mediaPlayer.getDuration() / 1000)).substring(
                                1
                        );
                seekBar.setMax((int) mediaPlayer.getDuration() / 1000);
                if (!isPlaying) {
                    seekBar.setProgress(0);
                    playAudioIV.setImageResource(R.drawable.ic_play_control);
                    if (mediaPlayer.getCurrentPosition() == 0) {
                        setAudioInitialDuration();
                    }
                } else {
                    //if (fromTouch) {
                    mediaPlayer.seekTo(progress);
                    int currentPosition = (int) mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
          /*}else {
            int currentPosition = (int) mediaPlayer.getCurrentPosition() / 1000;
            seekBar.setProgress(currentPosition);
          }*/
                    setAudioCurrentDuration();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        seekBarTotalDuration = viewAudioHolderParent.findViewById(R.id.seekBarTotalDuration);
        playAudioIV = viewAudioHolderParent.findViewById(R.id.playControlIV);
        mediaPlayer = ExoPlayerFactory.newSimpleInstance(this.context);
        if (feed.getAudio() != null && !feed.getAudio().isEmpty()) {
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(this.context, getUserAgent(this.context, ""));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(Uri.parse(feed.getAudio()));
            mediaPlayer.prepare(mediaSource);
            mediaPlayer.addListener(eventListener);
        }
    }

    private void setAudioInitialDuration() {
        seekBarTotalDuration.setText(String.format(context.getString(R.string.space), initialDuration, DateUtils.formatElapsedTime((Long) (mediaPlayer.getDuration() / 1000))
                .substring(1)));
    }

    public void playAudio(Feed feed, int targetPosition, AudioHolder holder) {
        viewAudioHolderParent = holder.itemView;
        if (feed.getAudio() != null && !feed.getAudio().isEmpty()) {
            mediaPlayer.setPlayWhenReady(true);
        }
        if (!isPlaying) {
            playAudioIV.setImageResource(R.drawable.ic_pause);
            isPlaying = true;
            mediaPlayer.seekTo( mediaObjects.get(playPosition).getCurrentSeekValue());
            mediaPlayer.setPlayWhenReady(true);
            // setProgress();
        } else {
            playAudioIV.setImageResource(R.drawable.ic_play_control);
            isPlaying = false;
            mediaObjects.get(playPosition).setCurrentSeekValue(mediaPlayer.getCurrentPosition());
            mediaPlayer.setPlayWhenReady(false);
        }
    }

    public void resetAudioPlayer() {
        if (mediaPlayer != null && viewAudioHolderParent != null) {
            isPlaying = false;
            Logger.INSTANCE.d(TAG, "resetAudioPlayer");
            mediaObjects.get(playPosition).setCurrentSeekValue(mediaPlayer.getCurrentPosition());
            mediaPlayer.stop(true);
            playAudioIV.setImageResource(R.drawable.ic_play_control);
            viewAudioHolderParent = null;
            playPosition = -1;
        }
    }

    private void setProgress() {
        if (!isPlaying) {
            seekBar.setProgress(0);
        }
        seekBar.setMax((int) mediaPlayer.getDuration() / 1000);

        if (handler == null) {
            handler = new Handler();
        }
        //Make sure you update SeekBar on UI thread
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null && isPlaying) {
                    seekBar.setMax((int) mediaPlayer.getDuration() / 1000);
                    int currentPosition = (int) mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(currentPosition);
                    handler.postDelayed(this, 1000);
                    setAudioCurrentDuration();
                }
            }
        }, 0);
    }

    private void setAudioCurrentDuration() {
        seekBar.setProgress(0);
        String remainingTime = DateUtils.formatElapsedTime((Long) ((mediaPlayer.getDuration() - mediaPlayer.getCurrentPosition()) / 1000)).substring(
                1
        );
        float current = mediaPlayer.getCurrentPosition();
        if (!remainingTime.contains("-")) {
            seekBarTotalDuration.setText(String.format(context.getString(R.string.space), DateUtils.formatElapsedTime((Math.round(current / 1000))).substring(
                    1
            ), remainingTime));
        }
    }

    public boolean getAudioPlaying() {
        return isPlaying;
    }

    /**
     * Volume ENUM
     */
    private enum VolumeState {
        ON, OFF
    }
}
