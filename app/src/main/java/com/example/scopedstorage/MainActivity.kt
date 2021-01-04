package com.example.scopedstorage

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.scopedstorage.databinding.ActivityMainBinding

//import java.util.*

class MainActivity : AppCompatActivity() {

    private val OPEN_GALLERY = 1
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
//        val go_intent = findViewById(R.id.loadImageBtn) as Button
        binding.loadImageBtn.setOnClickListener{ openGallery() }
    }

    private fun openGallery(){
        val intent: Intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.setType("image/*")
        startActivityForResult(intent,OPEN_GALLERY)
    }

    @Override
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

//        val imageView = findViewById(R.id.imageView) as ImageView
        if(requestCode == OPEN_GALLERY){
            if(resultCode == RESULT_OK){
                var dataUri = data?.data
                try{
                   Glide.with(this).load(dataUri).into(binding.imageView)
                }catch (e:Exception){
                    Toast.makeText(this,"$e",Toast.LENGTH_SHORT).show()
                }
            }else {

            }
        }
    }
}

//
//
//data class Image(
//    val uri: Uri,
//    val name: String,
//    val date: Date
//)
//
//val imageList = mutableListOf<Image>()
//val uri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//
//val projection = arrayOf(
//    MediaStore.Images.Media._ID,
//    MediaStore.Images.Media.DISPLAY_NAME,
//    MediaStore.Images.Media.DATE_TAKEN
//)
//
////        val selection = MediaStore.Images.Media.MIME_TYPE + "=?"
////        val selectionArgs = arrayOf(MimeTypeMap.getSingleton().getMimeTypeFromExtension("txt"))
//
//val sortOrder = "${MediaStore.Images.Media.DATE_TAKEN} DESC"
//
//val query = contentResolver.query(uri, projection, null, null, sortOrder)
//
//query?.use { cursor ->
//    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//    val dateTakenColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_TAKEN)
//    val displayNameColumn =
//        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
//    while (cursor.moveToNext()) {
//        val id = cursor.getLong(idColumn)
//        val dateTaken = Date(cursor.getLong(dateTakenColumn))
//        val displayName = cursor.getString(displayNameColumn)
//        val contentUri = Uri.withAppendedPath(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            id.toString()
//        )
//        Log.d(
//            "Test",
//            "id : $id, display_name : $displayName, data_taken : $dateTaken, content_uri : $contentUri\n"
//        )
//        imageList += Image(contentUri, displayName, dateTaken)
//        Glide.with(this@MainActivity)
//            .load(contentUri)
//            .into(imageView)
//    }
//

//
//}