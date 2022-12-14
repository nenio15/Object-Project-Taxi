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

                            // ??????????????? ???????????? ????????? ?????????. ???, ????????? ?????? ?????? ??????
                            val intent = Intent(activity, MenuActivity::class.java)
                            startActivity(intent)
                            Log.d("SIGNUP", "to sign in - new address")
                            Toast.makeText(activity, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()

                        } else {
                            // ????????? ?????? ???????????????, ?????? ????????? ???????????????
                            Toast.makeText(
                                activity,
                                "?????? ???????????? ???????????????, ??????????????? ??????????????????.",
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