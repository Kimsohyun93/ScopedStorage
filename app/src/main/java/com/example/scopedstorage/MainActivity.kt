package com.example.scopedstorage

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.bumptech.glide.Glide
import com.example.scopedstorage.RotatePicture.handleSamplingAndRotationBitmap
import com.example.scopedstorage.databinding.ActivityMainBinding
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {

    private val OPEN_CAMERA = 0
    private val OPEN_GALLERY = 1
    private val OPEN_DOCUMENT = 2
    private val OPEN_TXT_FILE = 3

    private var mCameraPhotoPath: Uri? = null
    private var currentPhotoPath: String? = null

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.loadImageBtn.setOnClickListener { openGallery() }
        binding.takePhotoBtn.setOnClickListener { openCamera() }
        binding.loadTxtBtn.setOnClickListener { openTxtFile() }
        binding.loadDocBtn.setOnClickListener{openDocument()}

        TedPermission.with(this)
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.CAMERA)
            .check();

    }

    var permissionlistener: PermissionListener = object : PermissionListener {
        override fun onPermissionGranted() {
            Toast.makeText(this@MainActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
        }

        override fun onPermissionDenied(deniedPermissions: List<String>) {
            Toast.makeText(
                this@MainActivity,
                "Permission Denied\n$deniedPermissions",
                Toast.LENGTH_SHORT
            ).show()
        }
    }


// MediaFile -> MediaStore

    private fun openGallery() {
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent, OPEN_GALLERY)
    }


    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    Log.e("Error : ", "createImageFile error")
                    null
                }
                photoFile?.also {
                    mCameraPhotoPath = FileProvider.getUriForFile(
                        this,
                        "com.example.android.fileprovider",
                        it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraPhotoPath)
                    startActivityForResult(takePictureIntent, OPEN_CAMERA)
                }
            }
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File? {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        ).apply {
            currentPhotoPath = absolutePath
        }

    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == OPEN_GALLERY) {
            if (resultCode == RESULT_OK) {
                try {
                    var dataUri = data?.data
                    Glide.with(this).load(dataUri).into(binding.imageView)
                    Log.d("## Data Uri:  ", "$dataUri")
                } catch (e: Exception) {
                    Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == OPEN_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    Log.d("Data Uri : ", "${data?.data}")
                    binding.imageView.setImageBitmap(
                        handleSamplingAndRotationBitmap(
                            this,
                            mCameraPhotoPath
                        )
                    )
                } catch (e: Exception) {
                    Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
                    Log.e("Error", "${e.message}")
                }
            }
        } else if(requestCode == OPEN_TXT_FILE) {
            if(resultCode == RESULT_OK) {
                try {
                    var dataUri = data?.data
                    Log.e("## Data Uri:  ", "$dataUri")
                    val content: String = readFileContent(dataUri)
//                    textView.setText(content)
                    Log.e("text content", "$content")

                    val intent = Intent(this, TextActivity::class.java)
//                    intent.putExtra("file name",)
                    intent.putExtra("file text", content)
                    startActivity(intent)

                } catch (e: Exception) {
                    Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
                }
            }
        } else if (requestCode == OPEN_DOCUMENT) {
            if (resultCode == RESULT_OK) {
                try {
                    var dataUri = data?.data
                    Log.e("## Data Uri:  ", "$dataUri")

                } catch (e: Exception) {
                    Toast.makeText(this, "$e", Toast.LENGTH_SHORT).show()
                }
            } else {
            }
        }
    }

    private fun readFileContent(dataUri: Uri?): String {
        val inputStream = dataUri?.let { contentResolver.openInputStream(it) }
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var currentline: String ?= null
        while (reader.readLine().also { currentline = it } != null) {
                stringBuilder.append(
                    """
                    $currentline

                    """.trimIndent()
                )
            }
        inputStream!!.close()
        return stringBuilder.toString()
    }


    //documents -> SAF(Storage Access Framework)
    private fun openDocument() {

        val mimeTypes = arrayOf(
            "application/pdf",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document", // .doc & .docx
//            "text/plain" ,//txt
            "application/epub+zip" //epub
        )

        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)

        intent.type = if (mimeTypes.size == 1) mimeTypes[0] else "*/*"
        if (mimeTypes.size > 0) {
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }
        startActivityForResult(Intent.createChooser(intent, "ChooseFile"), OPEN_DOCUMENT)

    }

    private fun openTxtFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.setType("text/plain")
        startActivityForResult(intent, OPEN_TXT_FILE)
    }

}
