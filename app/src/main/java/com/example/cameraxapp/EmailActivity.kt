package com.example.cameraxapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.cameraxapp.databinding.ActivityEmailBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileInputStream
import java.util.concurrent.TimeUnit

import okhttp3.logging.HttpLoggingInterceptor


class EmailActivity : AppCompatActivity() {
    //    curl --location 'http://13.200.238.163:5001/send-email' \
//    --form 'to_email="snehal.trapsiya@gmail.com"' \
//    --form 'subject="This is test"' \
//    --form 'message="Hello please check you image"' \
//    --form 'image=@"/C:/Users/Administrator/Downloads/Necun/DocumentScanner/Sample_Images/2.jpeg"'
    var enhancedImageType: String = ""
    private lateinit var viewBinding: ActivityEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        viewBinding = ActivityEmailBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
        enhancedImageType = intent.getStringExtra("enhancedImageType").toString()
        println("enhancedImageType:$enhancedImageType")
        initImageDisplay()
        viewBinding.emailImageView.setOnClickListener {
            initSendEmail()
        }
        viewBinding.cameraRetakeImgVw.setOnClickListener {
            deleteInternalStorageDirectoryy()
            val intent = Intent(this@EmailActivity, MainActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        deleteInternalStorageDirectoryy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        deleteInternalStorageDirectoryy()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

        // Optional: if you want to finish the current activity
        finishAffinity()
    }

    fun deleteInternalStorageDirectoryy() {
        if (ContextCompat.checkSelfPermission(
                this@EmailActivity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_DENIED
        ) {
            val input_pathh = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/CameraX-Image/"
            )


            val input_path = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/CameraX-Image-Input/"
            )
            val output_pathh = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    .toString() + "/CameraX-Image-Output/"
            )
            if (input_path.exists()) {
                input_path.deleteRecursively()
            }
            if (input_pathh.exists()) {
                input_pathh.deleteRecursively()
            }
            if (output_pathh.exists()) {
                output_pathh.deleteRecursively()
            }
        } else {
            requestRuntimePermissionn()
        }
    }

    private fun requestRuntimePermissionn(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this@EmailActivity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@EmailActivity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                14
            )
            return false
        }
        return true
    }

    @SuppressLint("SuspiciousIndentation")
    private fun initSendEmail() {
       // viewBinding.progressBar.visibility = View.VISIBLE
        startActivity(Intent(this@EmailActivity,EmailPopUpActivity::class.java))
        val sharedPreference = getSharedPreferences("PREFERENCE_NAME", Context.MODE_PRIVATE)
        val userEmailId = sharedPreference.getString("userEmailId", "defaultName")
        println("userEmailId:$userEmailId")
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY

//                  val enhancedImageType = intent.getStringExtra("enhancedImageType")
//                     println("454354353346546"+enhancedImageType)
        val output_path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/CameraX-Image-Output/"
        val file = File(output_path, "$enhancedImageType.jpg")
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("image", file.name, requestBody)
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(6, TimeUnit.MINUTES)
            .writeTimeout(6, TimeUnit.MINUTES)
            .readTimeout(6, TimeUnit.MINUTES)
            .build()
        val retrofit = Retrofit.Builder().baseUrl("http://13.200.238.163:5001/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(api::class.java)


//            val response =  retrofit.sendEmail(part,"agarwalkomal2030@gmail.com","filtered image","please the image in your mail")
//                   Log.d("response retrofit",response)
        val listCall: Call<UploadResponse> = retrofit.sendEmail(
            part,
            userEmailId.toString(),
            "Your Document is ready",
            "[PFA] Please find attached"
        )

        listCall.enqueue(object : Callback<UploadResponse> {
            override fun onResponse(
                call: Call<UploadResponse>,
                response: Response<UploadResponse>
            ) {

                try {
                    val response = response.body()
                    if (response != null) {

                        //viewBinding.progressBar.visibility = View.GONE
                      //  val intent = Intent(this@EmailActivity, EmailPopUpActivity::class.java)
                        //startActivity(intent)
                        deleteInternalStorageDirectoryy()
                        //showSuccessDialog()
//                            Toast.makeText(
//                                this@EmailActivity,
//                                "" + response.message,
//                                Toast.LENGTH_LONG
//                            ).show()
                    } else {

//                        Toast.makeText(
//                            this@EmailActivity,
//                            "please try again after sometime",
//                            Toast.LENGTH_LONG
//                        ).show()
                    }
                } catch (e: Exception) {
//                    Toast.makeText(
//                        this@EmailActivity,
//                        "please try again after sometime",
//                        Toast.LENGTH_LONG
//                    ).show()
                }
            }


            override fun onFailure(call: Call<UploadResponse>, t: Throwable) {
//                Toast.makeText(
//                    this@EmailActivity,
//                    "please try again after sometime",
//                    Toast.LENGTH_LONG
//                ).show()

            }
        })




    }

    private fun showSuccessDialog() {
        val successDialog = Dialog(this@EmailActivity)
        successDialog.setContentView(R.layout.mail_sent_dialog)
        successDialog.setCancelable(false)
        successDialog.setCanceledOnTouchOutside(true)
        successDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        successDialog.show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            14 -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this@EmailActivity, "Permission Granted", Toast.LENGTH_LONG)
                        .show()
                } else {
                    ActivityCompat.requestPermissions(
                        this@EmailActivity,
                        arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                        14
                    )
                }
            }
        }
    }

    private fun initImageDisplay() {


        val output_path =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .toString() + "/CameraX-Image-Output/"

//        if (output_path!=null){
//
//        }
        //val output_path = enhancedImageType
        println(output_path)
        val f = File(output_path, "$enhancedImageType.jpg")
        System.out.println("122334465=" + f)
        val b = BitmapFactory.decodeStream(FileInputStream(f))
        viewBinding.imageView.setImageBitmap(b)

    }
}