package com.vedro401.reallifeachievement.ui

import android.os.Bundle
import android.util.Log
import com.vedro401.reallifeachievement.ui.profile.ProfileActivity
import org.jetbrains.anko.startActivity

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        um.isAuthorisedObs.first().subscribe{
            isAuth ->
            if(isAuth) {
                startActivity<ProfileActivity>()
            }
            else {
                startActivity<SignInActivity>()
            }
            finish()
        }
    }
}
