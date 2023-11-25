package com.inmoment.moments.login.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import com.inmoment.moments.R
import com.inmoment.moments.databinding.FragmentWelcomeBinding
import com.inmoment.moments.framework.ui.BaseFragment
import com.inmoment.moments.home.HomeActivity
import dagger.hilt.android.AndroidEntryPoint


/**
 * @author Cybage
 * @version 1.0
 * @since 09/10/20
 */

private const val deltaValueZero = 0f
private const val deltaValueMinusZero = -0f
private const val deltaValueFiveHundred = 250f
private const val deltaValueMinusFiveHundred = -250f

@AndroidEntryPoint
class WelcomeFragment : BaseFragment() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var animation: Animation
    private lateinit var mBinding: FragmentWelcomeBinding

    companion object {
        fun newInstance() = WelcomeFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = baseBinding(
            R.layout.fragment_welcome,
            inflater,
            container,
            FragmentWelcomeBinding::class.java
        )
        return mBinding.root
    }

    @SuppressLint("ResourceType")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        handler.postDelayed(initialRunnable, 1000)
    }

    private fun makeWelcomeContentVisible() {
        mBinding.imageAnimationLL.visibility = View.GONE
        mBinding.xiApplicationContent.visibility = View.GONE
        mBinding.productTourContents.visibility = View.VISIBLE
        mBinding.welcomeContent.visibility = View.VISIBLE
        mBinding.momentsContent.visibility = View.GONE
    }

    private val animationListener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            /*Not required*/
        }

        override fun onAnimationEnd(animation: Animation?) {
            mBinding.imageAnimationLL.visibility = View.GONE
            handler.postDelayed(runnable, 1000)
        }

        override fun onAnimationStart(animation: Animation?) {
            /*Not required*/
        }
    }

    private val secondAnimationListener = object : Animation.AnimationListener {
        override fun onAnimationRepeat(animation: Animation?) {
            /*Not required*/
        }

        override fun onAnimationEnd(animation: Animation?) {
            handler.postDelayed(finalRunnable, 300)
        }

        override fun onAnimationStart(animation: Animation?) {
            mBinding.momentsLogoIV.visibility = View.GONE
            /*Not required*/
        }
    }
    private val initialRunnable = Runnable {
        val animation: Animation =
            AlphaAnimation(1f, 0.4f) //to change visibility from visible to invisible
        animation.duration = 400
        mBinding.momentsLogoIV.startAnimation(animation)
        mBinding.imageAnimationLL.visibility = View.VISIBLE
        showAnimation()
    }
    private val runnable = Runnable {
        makeWelcomeContentVisible()
        val anim: Animation = AnimationUtils.loadAnimation(activity, R.anim.splash_anim)
        mBinding.welcomeContent.startAnimation(anim)
        anim.setAnimationListener(secondAnimationListener)
    }
    private val finalRunnable = Runnable {
        val intent = Intent(activity, HomeActivity::class.java)
        startActivity(intent)
        activity.finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(finalRunnable)
        handler.removeCallbacks(initialRunnable)
    }

    private fun showAnimation() {
        startAnimationToImage(
            mBinding.img1,
            deltaValueZero,
            deltaValueMinusFiveHundred,
            deltaValueZero,
            deltaValueMinusFiveHundred
        )
        startAnimationToImage(
            mBinding.img2,
            deltaValueZero,
            deltaValueZero,
            deltaValueZero,
            deltaValueMinusFiveHundred
        )
        startAnimationToImage(
            mBinding.img3,
            deltaValueZero,
            deltaValueFiveHundred,
            deltaValueZero,
            deltaValueMinusFiveHundred
        )
        startAnimationToImage(
            mBinding.img4,
            deltaValueZero,
            deltaValueMinusFiveHundred,
            deltaValueZero,
            deltaValueZero
        )
        startAnimationToImage(
            mBinding.img6,
            deltaValueMinusZero,
            deltaValueFiveHundred,
            deltaValueZero,
            deltaValueZero
        )
        startAnimationToImage(
            mBinding.img7,
            deltaValueZero,
            deltaValueMinusFiveHundred,
            deltaValueZero,
            deltaValueFiveHundred
        )
        startAnimationToImage(
            mBinding.img8,
            deltaValueZero,
            deltaValueZero,
            deltaValueMinusZero,
            deltaValueFiveHundred
        )
        startAnimationToImage(
            mBinding.img9,
            deltaValueZero,
            deltaValueFiveHundred,
            deltaValueZero,
            deltaValueFiveHundred
        )
        animation.setAnimationListener(animationListener)
    }

    private fun startAnimationToImage(
        imageView: ImageView,
        fromXDelta: Float,
        toXDelta: Float,
        fromYDelta: Float,
        toYDelta: Float
    ) {
        animation = TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta)
        animation.duration = 400
        imageView.startAnimation(animation)
    }
}