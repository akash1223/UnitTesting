package com.inmoment.moments.login

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import com.inmoment.moments.R
import com.inmoment.moments.framework.common.replaceFragment
import com.inmoment.moments.framework.persist.SharedPrefsInf
import com.inmoment.moments.framework.ui.BaseActivity
import com.inmoment.moments.home.HomeActivity
import com.inmoment.moments.login.ui.fragment.LoginFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class LoginActivity : BaseActivity() {

    @Inject
    lateinit var sharedPrefsInf: SharedPrefsInf
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        setContentView(R.layout.login_activity)

      /*  var changeRefreshToken = false

        changeRefreshToken = true
        if(changeRefreshToken)
        {
            var accessToken = sharedPrefsInf.get(ACCESS_TOKEN_PREFERENCE_KEY,PREF_STRING_DEFAULT)
            if(accessToken.isNotEmpty())
            sharedPrefsInf.put(ACCESS_TOKEN_PREFERENCE_KEY,accessToken+"zx")
        }*/
        if (sharedPrefsInf.isUserLogin()) {
            val intent = Intent(this, HomeActivity::class.java)
            startActivity(intent)
            finish()
        } else {

            if (savedInstanceState == null) {
                replaceFragment(this, LoginFragment.newInstance())
            }
        }
    }
}