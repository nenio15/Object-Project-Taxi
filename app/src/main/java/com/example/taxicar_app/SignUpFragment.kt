package com.example.taxicar_app


import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.taxicar_app.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SignUpFragment : Fragment() {
    private var auth: FirebaseAuth? = null
    var binding: FragmentSignUpBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignUpBinding.inflate(inflater, container, false)

        binding?.signUpIn?.setOnClickListener{
            val email = binding?.emailUp?.text.toString()
            val password = binding?.passwordUp?.text.toString()
            val username = binding?.nameUp?.text.toString()
            val nameUpdate = UserProfileChangeRequest.Builder().setDisplayName(username).build()

            if(email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() ) {
                auth?.createUserWithEmailAndPassword(email, password)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            auth?.currentUser?.updateProfile(nameUpdate)
                            Firebase.firestore.collection("Nicknames")
                                .document(auth?.currentUser?.uid!!)
                                .set(hashMapOf("nickname" to username))
                                .addOnSuccessListener { Log.d("SignUp", "$username is register's name") }
                                .addOnFailureListener{ e -> Log.d("SignUp", "Error occcurs: $e") }

                            // 정상적으로 이메일과 비번이 넘어감. 즉, 새로운 유저 계정 생성
                            val intent = Intent(activity, MenuActivity::class.java)
                            startActivity(intent)
                            Log.d("SIGNUP", "to sign in - new address")
                            Toast.makeText(activity, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show()

                        } else {
                            // 기존에 있는 계정이거나, 서버 연결이 실패됐거나
                            Toast.makeText(
                                activity,
                                "이미 존재하는 계정이거나, 회원가입에 실패했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        binding?.signInBut?.setOnClickListener{
            val mActivity = activity as MainActivity
            Log.d("SIGNUP", "to sign in")
            mActivity.replaceFragment(SignInFragment.newInstance())
        }

        return binding?.root

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            SignUpFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}