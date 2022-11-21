package com.example.taxicar_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taxicar_app.databinding.ActivityMenuBinding
import com.example.taxicar_app.databinding.ActivitySubBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SubActivity : AppCompatActivity() {
    lateinit var binding: ActivitySubBinding
    private var mFirebaseD: FirebaseDatabase = FirebaseDatabase.getInstance()
    //아래 문장이 realtime base를 get하는것. 원리는 json의 key값을 받는 형식과 같다.
    private var mDatabaseR: DatabaseReference = mFirebaseD.getReference()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubBinding.inflate(layoutInflater)
        //기본 양식은 아래와 같다. 자세한 것은 firebase주소를 직접 찾아가 볼것.
        //mDatabaseR.child("room").child("taxi").child("toSchool").child("room1")

        binding.btnTest3.setOnClickListener{
            finish()
        }

        setContentView(binding.root)
    }
}