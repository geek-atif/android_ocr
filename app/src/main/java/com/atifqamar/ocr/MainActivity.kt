package com.atifqamar.ocr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.exifinterface.media.ExifInterface
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST_CAMERA = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var btcapture: Button
    private lateinit var myBitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initUI()
    }

    fun initUI() {
        btcapture = findViewById(R.id.btcapture)
        btcapture.setOnClickListener {
            if (isCameraPermissionGranted()) {
                dispatchTakePictureIntent()
            } else {
                requestForPermission()
            }
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            val picUri = getCaptureImageOutputUri()
            try {
                myBitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, picUri)
                detectTxt()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_CANCELED) {
            finish()
        }
    }

    private fun dispatchTakePictureIntent() {
        val outputFileUri: Uri = getCaptureImageOutputUri()
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }

    private fun getCaptureImageOutputUri(): Uri {
        lateinit var outputFileUri: Uri
        val getImage: File? = externalCacheDir
        if (getImage != null) {
            outputFileUri = if (Build.VERSION.SDK_INT < 24) {
                Uri.fromFile(File(getImage.path, "test.png"))
            } else {
                FileProvider.getUriForFile(this, this.packageName + ".OCRFileProvider",
                    File(getImage.path, "test.png")
                )
            }
        }
        return outputFileUri
    }

    private fun detectTxt() {
            Log.d("MainActivity", "detectTxt()")
            val image: InputImage = InputImage.fromBitmap(myBitmap, 0)
            //val image: InputImage = InputImage.fromBitmap(BitmapFactory.decodeResource(resources, R.drawable.offline_sbi), 0)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    processTextRecognitionResult(visionText)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                    Log.e("MainActivity", "texts ${e.toString()}")
                    Toast.makeText(this, "Fail to read! Try again", Toast.LENGTH_LONG).show()
               }
    }

    private fun processTextRecognitionResult(texts: Text) {
        Log.d("MainActivity", "texts ${texts}")
        if (texts.textBlocks.size == 0) {
            return
        }
        val stringBuilder = StringBuilder()
        for (block in texts.textBlocks) {
            val boundingBox = block.boundingBox
            val cornerPoints = block.cornerPoints
            val text = block.text
            stringBuilder.append(text)
        }
        var data = stringBuilder.toString()
        Log.d("MainActivity", "content ${data}")
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra("readText", data)
        startActivity(intent)
    }


    private fun isCameraPermissionGranted(): Boolean {
        return ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestForPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            PERMISSION_REQUEST_CAMERA
        )
    }

    @SuppressLint("MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode != PERMISSION_REQUEST_CAMERA) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            return
        }

        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (isCameraPermissionGranted()) {
                dispatchTakePictureIntent()
            } else {
                finish()
            }
        }
    }

}