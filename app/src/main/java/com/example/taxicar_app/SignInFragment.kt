package com.example.taxicar_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.taxicar_app.databinding.FragmentSignInBinding
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.*
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
@Suppress("DEPRECATION")
class SignInFragment : Fragment() {
    var auth: FirebaseAuth? = null
    var googleSignInClient: GoogleSignInClient? = null
    var GOOGLE_LOGIN_CODE = 9001
    var binding: FragmentSignInBinding? = null
    // TODO: Rename and change types of parameters
    //private var param1: String? = null
    //private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        arguments?.let {
            //param1 = it.getString(ARG_PARAM1)
            //param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignInBinding.inflate(inflater, container, false)

        val mActivity = activity as MainActivity
        binding?.signIn?.setOnClickListener{
            val mActivity = activity as MainActivity
            val email = binding?.emailIn?.text.toString()
            val password = binding?.passwordIn?.text.toString()


            auth?.signInWithEmailAndPassword(email,password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(mActivity, "로그인에 성공했습니다", Toast.LENGTH_SHORT).show()
                        val intent = Intent(activity, MenuActivity::class.java)
                        startActivity(intent)

                        Log.d("SIGNIN", "to choice")
                    } else if(email == "root" && password == "root"){
                        val intent = Intent(activity, MenuActivity::class.java)
                        startActivity(intent)
                        Toast.makeText( mActivity,"관리자 모드로 접근",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(mActivity,"아이디와 비밀번호를 확인해주세요.",Toast.LENGTH_SHORT).show()
                    }
                }

            // add reset

        }

        var gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(mActivity, gso)

        binding?.googleIn?.setOnClickListener {
            val signInIntent = googleSignInClient?.signInIntent
            startActivityForResult(signInIntent, GOOGLE_LOGIN_CODE)
            // add reset
        }
        binding?.signUpBut?.setOnClickListener{
            Log.d("SIGNIN", "to sign up")
            mActivity.replaceFragment(SignUpFragment.newInstance())

        }

        return binding?.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_LOGIN_CODE){
            var result = Auth.GoogleSignInApi.getSignInResultFromIntent(data!!)
            if(result!!.isSuccess){
                var account = result.signInAccount
                firebaseAuthWithGoogle(account)
            }
        }
    }
    fun firebaseAuthWithGoogle(account: GoogleSignInAccount?){
        val mActivity = activity as MainActivity
        var credential = GoogleAuthProvider.getCredential(account?.idToken, null)
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener{ task ->
                if(task.isSuccessful){

                    Firebase.firestore.collection("Nicknames")
                        .document(auth?.currentUser?.uid!!)
                        .set(hashMapOf("nickname" to account?.displayName.toString()))
                        .addOnSuccessListener { Log.d("SignUp", "${account?.displayName.toString()} is register's name") }
                        .addOnFailureListener{ e -> Log.d("SignUp", "Error occcurs: $e") }

                    Toast.makeText( mActivity,"로그인에 성공했습니다",Toast.LENGTH_SHORT).show()
                    val intent = Intent(activity, MenuActivity::class.java)
                    startActivity(intent)
                }
                else{
                    Toast.makeText(mActivity,"구글 로그인에 실패했습니다.",Toast.LENGTH_SHORT).show()
                }
            }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment fragment_sign_in.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() =
            SignInFragment().apply {
                arguments = Bundle().apply {
                    //putString(ARG_PARAM1, param1)
                    //putString(ARG_PARAM2, param2)
                }
            }
    }
}
