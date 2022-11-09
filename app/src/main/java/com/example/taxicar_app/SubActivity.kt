package com.example.taxicar_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taxicar_app.databinding.ActivitySubBinding

class SubActivity : AppCompatActivity() {
    lateinit var binding: ActivitySubBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
    }
}