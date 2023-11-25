package com.inmoment.moments.framework.common

/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */
const val NETWORK_TIMEOUT = 30L
const val GOOGLE_SIGN_IN = 99
const val FIRST_ROW = 1
const val COMMENT_ROW = 2
const val VIDEO_ROW = 3
const val AUDIO_ROW = 4
const val IMAGES_ROW = 5
const val FOOTER_ROW = 6
const val CAUGHT_UP = 7
const val initialDuration = "0:00"
const val seekBackwardTime: Int = 15000 // 15000 milliseconds
const val seekForwardTime: Int = 30000 // 30000 milliseconds

/**
 *  This file contains all the constant used in application
 */
object AppConstants {
    const val ACTIVITY_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss"
    const val FEEDS_DATE_FORMAT = "dd MMM yyyy hh:mm aa"
    const val EMPTY_VALUE = ""
    const val ACTION_CONTENT_SHARE_SUCCESSFULLY = "action.content.share.successfully"
    const val PAGE_ITEM_SIZE =25
    const val VISIBLE_THRESHOLD = 4

    // Navigation Controller Variables
    const val NAV_BACK_MOMENT_TYPE = "momentType"
    const val NAV_BACK_ADD_TO_FAV = "addToFavorite"
    const val NAV_BACK_PROGRAM = "program"
    const val NAV_BACK_REWARD = "reward"

}