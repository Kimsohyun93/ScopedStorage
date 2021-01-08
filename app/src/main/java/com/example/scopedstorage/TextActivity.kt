package com.example.scopedstorage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.scopedstorage.databinding.ActivityTextBinding

class TextActivity : AppCompatActivity() {
    lateinit var binding:ActivityTextBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_text)

        binding = ActivityTextBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        if (intent.hasExtra("file text")) {
            binding.textView.text = intent.getStringExtra("file text")

        } else {
            Toast.makeText(this, "전달된 이름이 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

}