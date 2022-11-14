package com.example.taxicar_app


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.taxicar_app.databinding.FragmentSignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

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
    // TODO: Rename and change types of parameters

    override fun onCreate(savedInstanceState: Bundle?) {
        auth = Firebase.auth
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentSignUpBinding.inflate(inflater, container, false)
        val mActivity = activity as MainActivity

        binding?.signUp?.setOnClickListener{
            val email = binding?.emailUp?.text.toString()
            val password = binding?.passwordUp?.text.toString()

            auth?.createUserWithEmailAndPassword(email,password)
                ?.addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        // 정상적으로 이메일과 비번이 넘어감. 즉, 새로운 유저 계정 생성
                        mActivity.replaceFragment(ChoiceFragment.newInstance())
                        Log.d("SIGNUP", "to sign in - new address")
                        Toast.makeText(mActivity,"회원가입에 성공했습니다.",Toast.LENGTH_SHORT).show()
                    }else {
                        // 기존에 있는 계정이거나, 서버 연결이 실패됐거나
                        Toast.makeText(mActivity,"이미 존재하는 계정이거나, 회원가입에 실패했습니다.",Toast.LENGTH_SHORT).show()
                    }
                }
        }


        binding?.signInBut?.setOnClickListener{
            Log.d("SIGNUP", "to sign in")
            mActivity.replaceFragment(SignInFragment.newInstance())
        }

        return binding?.root

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