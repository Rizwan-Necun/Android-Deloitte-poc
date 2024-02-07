package com.example.cameraxapp

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.example.cameraxapp.databinding.ActivityEmailBinding
import com.example.cameraxapp.databinding.ActivitySplashBinding
import com.example.cameraxapp.databinding.FragmentSingleAccountModeBinding
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalClientException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalServiceException
import org.json.JSONObject

class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding?= null
    private var mSingleAccountApp: ISingleAccountPublicClientApplication? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding!!.root)



        supportActionBar?.hide()
        PublicClientApplication.createSingleAccountPublicClientApplication(
            this@SplashActivity,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    /**
                     * This test app assumes that the app is only going to support one account.
                     * This requires "account_mode" : "SINGLE" in the config json file.
                     *
                     */
                    mSingleAccountApp = application


                }

                override fun onError(exception: MsalException) {
                    //binding.txtLog.text = "11111111"+exception.toString()
                }
            })

        Handler().postDelayed(Runnable { displayLogin()},3500)

    }

    private fun displayLogin(){
       checkIfUserLoggedIn()
//        if (mSingleAccountApp == null) {
//            return
//        }
//
//        mSingleAccountApp!!.signIn(
//            this as Activity,
//            "",
//            getScopes(),
//            getAuthInteractiveCallback()
//        )
    }

    private fun checkIfUserLoggedIn(){
        mSingleAccountApp?.getCurrentAccountAsync(object : ISingleAccountPublicClientApplication.CurrentAccountCallback {
            override fun onAccountLoaded(activeAccount: IAccount?) {
                if (activeAccount != null) {
                    // User is logged in
                    Log.d(TAG, "User is already logged in: ${activeAccount.username}")
                    val intent = Intent(this@SplashActivity,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    // updateUI(activeAccount) // Update your UI accordingly
                } else {
                    // User is not logged in
                    val intent = Intent(this@SplashActivity,SignInActivity::class.java)
                    startActivity(intent)
                    finish()
                    // updateUI(null) // Update UI to reflect not logged in status
                }
            }

            override fun onAccountChanged(priorAccount: IAccount?, currentAccount: IAccount?) {
                // Handle account changes if needed
            }

            override fun onError(exception: MsalException) {
                Log.d(TAG, "Error checking login status: ${exception.message}")
            }
        })
    }

//    private fun getScopes(): Array<String> {
//        return "user.read".toLowerCase().split(" ".toRegex()).dropLastWhile { it.isEmpty() }
//            .toTypedArray()
//    }

//    private fun getAuthInteractiveCallback(): AuthenticationCallback {
//        return object : AuthenticationCallback {
//
//            override fun onSuccess(authenticationResult: IAuthenticationResult) {
//                /* Successfully got a token, use it to call a protected resource - MSGraph */
//                Log.d(TAG, "Successfully authenticated")
//                Log.d(TAG, "ID Token: " + authenticationResult.account.claims!!["id_token"])
//
//                /* Update account */
//                //updateUI(authenticationResult.account)
//                // startActivity(Intent(requireActivity(),MainActivity::class.java))
//
//                /* call graph */
//                callGraphAPI(authenticationResult)
//            }
//
//            override fun onError(exception: MsalException) {
//                /* Failed to acquireToken */
//                Log.d(TAG, "Authentication failed: $exception")
////                displayError(exception)
//
//                if (exception is MsalClientException) {
//                    /* Exception inside MSAL, more info inside MsalError.java */
//                    displayError(exception)
//                } else if (exception is MsalServiceException) {
//                    /* Exception when communicating with the STS, likely config issue */
//                    displayError(exception)
//                }
//            }
//
//            override fun onCancel() {
//                /* User canceled the authentication */
//                Log.d(TAG, "User cancelled login.")
//            }
//        }
//    }

//    private fun callGraphAPI(authenticationResult: IAuthenticationResult) {
//        MSGraphRequestWrapper.callGraphAPIWithVolley(
//            this as Context,
//            "https://graph.microsoft.com/v1.0/me",
//            authenticationResult.accessToken,
//            Response.Listener<JSONObject> { response ->
//                /* Successfully called graph, process data and send to UI */
//                Log.d(TAG, "Response: $response")
//                displayGraphResult(response)
//            },
//            Response.ErrorListener { error ->
//                Log.d(TAG, "Error: $error")
//                displayError(error)
//            })
//    }

//    private fun displayError(exception: Exception) {
//        binding.txtLog.text = "444444444444444"+exception.toString()
//    }

//    private fun displayGraphResult(graphResponse: JSONObject) {
//        binding.txtLog.text = "333333"+graphResponse.toString()
//        val userEmailId = graphResponse.get("mail")
//        val sharedPreference =  this@SplashActivity.getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
//        var editor = sharedPreference.edit()
//
//        editor.putString("userEmailId", userEmailId.toString())
//        editor.apply()
//        editor.commit()
//        val intent = Intent(this,MainActivity::class.java)
//        // intent.putExtra("userEmailId",userEmailId.toString())
//        startActivity(intent)
//        finish()
//    }

}