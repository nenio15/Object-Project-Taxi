package com.example.taxicar_app

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.taxicar_app.databinding.FragmentSettingBinding
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SettingFragment : Fragment() {
    var binding : FragmentSettingBinding? = null
    var googleSignInClient: GoogleSignInClient? = null
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)
        auth = Firebase.auth

        binding?.btnLogout?.setOnClickListener{
            Toast.makeText(activity, "로그아웃에 성공했습니다", Toast.LENGTH_SHORT).show()
            auth.signOut()
            googleSignInClient?.signOut()
            activity?.finish()
            //val rActivity = activity as MainActivity
            //rActivity.replaceFragment(MainFragment.newInstance())
            //val intent = Intent(activity, MainActivity::class.java)
            //intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            //startActivity(intent)
        }
        binding?.btnDelete?.setOnClickListener {
            Toast.makeText(activity, "회원탈퇴에 성공했습니다", Toast.LENGTH_SHORT).show()
            auth.currentUser?.delete()
            activity?.finish()
        }

        return binding?.root
    }

}