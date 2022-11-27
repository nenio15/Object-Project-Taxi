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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_sign_up.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

/**
 * A simple [Fragment] subclass.
 * Use the [SignInFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    private var auth: FirebaseAuth? = null
    var binding: FragmentSignUpBinding? = null

    private lateinit var mdatabase: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = Firebase.auth

        // db 초기화
        mdatabase = Firebase.database.reference

//        arguments?.let {
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        //     val mActivity = activity as MainActivity

        binding?.signUpIn?.setOnClickListener{
            //Log.d("SIGNUP", "to sign in - new address")
            //mActivity.replaceFragment(SignInFragment.newInstance())
            val email = binding?.emailUp?.text.toString().trim()
            val password = binding?.passwordUp?.text.toString().trim()
            val username = binding?.nameUp?.text.toString().trim()
            signUp(username, email, password)


        }
        binding?.signInBut?.setOnClickListener{
            val mActivity = activity as MainActivity
            Log.d("SIGNUP", "to sign in")
            mActivity.replaceFragment(SignInFragment.newInstance())
        }

        return binding?.root

    }
    private fun signUp(username: String, email: String, password: String){
        if(email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() ) {
            auth?.createUserWithEmailAndPassword(email, password)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // we need to nicknames..
                        //auth?.currentUser?.updateProfile({ displayName: useState(null)}) // auth에 이름 필요..
                        Firebase.firestore.collection("Nicknames")
                            .document(auth?.currentUser?.uid!!)
                            .set(hashMapOf("nickname" to username))
                            .addOnSuccessListener { Log.d("SignUp", "$username is register's name") }
                            .addOnFailureListener{ e -> Log.d("SignUp", "Error occcurs: $e") }
                        // 정상적으로 이메일과 비번이 넘어감. 즉, 새로운 유저 계정 생성
                        //mActivity.replaceFragment(ChoiceFragment.newInstance())
                        val intent = Intent(activity, MenuActivity::class.java)
                        startActivity(intent)
                        addUserToDatabase(username, email, auth?.currentUser?.uid!!)
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

    private fun addUserToDatabase(username: String, email: String, uId: String){
        mdatabase.child("user").child(uId).setValue(User(username, email, uId))
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
            SignUpFragment().apply {
                arguments = Bundle().apply {
                }
            }
    }
}