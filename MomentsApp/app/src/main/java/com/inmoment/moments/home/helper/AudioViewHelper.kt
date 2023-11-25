package com.inmoment.moments.home.helper

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.widget.SeekBar


class AudioViewHelper {

    companion object {
        private var wasPlaying = false
        var initialDuration = "0:00"
        const val seekBackwardTime: Int = 15000 // 15000 milliseconds
        const val seekForwardTime: Int = 30000 // 30000 milliseconds

        @SuppressLint("SetTextI18n")
        internal fun playSong(
            mediaPlayer: MediaPlayer,
            seekBar: SeekBar,
        ) {
            try {
                if (mediaPlayer.isPlaying) {
                    clearMediaPlayer(mediaPlayer)
                    seekBar.progress = 0
                    wasPlaying = true
                }
                if (!wasPlaying) {
                    mediaPlayer.start()
                    Thread(Runnable {
                        var currentPosition = mediaPlayer.currentPosition
                        val total = mediaPlayer.duration
                        while (mediaPlayer.isPlaying && currentPosition < total) {
                            currentPosition = try {
                                Thread.sleep(1000)
                                mediaPlayer.currentPosition
                            } catch (e: InterruptedException) {
                                return@Runnable
                            } catch (e: java.lang.Exception) {
                                return@Runnable
                            }
                            seekBar.progress = currentPosition
                        }
                    }).start()
                }
                wasPlaying = false
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        }

        private fun clearMediaPlayer(mediaPlayer: MediaPlayer) {
            mediaPlayer.stop()
            mediaPlayer.release()
        }

        fun milliSecondsToTimer(milliseconds: Long): String? {
            var finalTimerString = ""
            val secondsString: String

            // Convert total duration into time
            val hours = (milliseconds / (1000 * 60 * 60)).toInt()
            val minutes = (milliseconds % (1000 * 60 * 60)).toInt() / (1000 * 60)
            val seconds = (milliseconds % (1000 * 60 * 60) % (1000 * 60) / 1000).toInt()
            // Add hours if there
            if (hours > 0) {
                finalTimerString = "$hours:"
            }

            // Prepending 0 to seconds if it is one digit
            secondsString = if (seconds < 10) {
                "0$seconds"
            } else {
                "" + seconds
            }
            finalTimerString = "$finalTimerString$minutes:$secondsString"

            // return timer string
            return finalTimerString
        }

    }


}