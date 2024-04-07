package diplom.project.gameconnect.view.fragment

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.afollestad.materialdialogs.MaterialDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.FragmentAuthregisterBinding
import diplom.project.gameconnect.model.LoginType
import diplom.project.gameconnect.statik.LoginTypeHolder.loginType
import diplom.project.gameconnect.view.activity.MainActivity

class AuthRegisterFragment() : Fragment() {

    private val binding: FragmentAuthregisterBinding by lazy {
        FragmentAuthregisterBinding.inflate(
            layoutInflater
        )
    }
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()
        if (loginType == LoginType.REGISTRATION) visionRegister()
    }

    private fun onClick() {
        binding.additionalFunction.setOnClickListener {
            if (loginType == LoginType.AUTHORISATION) visionRegister()
            else visionAuthorise()
        }
        binding.forgetPassword.setOnClickListener {
            resetEmail()
        }
        binding.mainButton.setOnClickListener {
            if (loginType == LoginType.AUTHORISATION) authorise()
            else registration()
        }
    }

    private fun visionAuthorise() {
        loginType = LoginType.AUTHORISATION
        binding.passwordRepeat.visibility = View.GONE
        binding.forgetPassword.visibility = View.VISIBLE
        binding.mainText.text = resources.getText(R.string.authorisation)
        binding.mainButton.text = resources.getText(R.string.enter)
        binding.additionalFunction.text = resources.getText(R.string.create_account)
    }

    private fun visionRegister() {
        loginType = LoginType.REGISTRATION
        binding.passwordRepeat.visibility = View.VISIBLE
        binding.forgetPassword.visibility = View.GONE
        binding.mainText.text = resources.getText(R.string.registration)
        binding.mainButton.text = resources.getText(R.string.create_account)
        binding.additionalFunction.text = resources.getText(R.string.authorise)
    }

    private fun authorise() {
        val mail = binding.emailEdit.text.toString()
        val password = binding.passwordEdit.text.toString()
        if (checkInput()) {
            auth.signInWithEmailAndPassword(mail, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "signInWithEmail:success")
                        val user = auth.currentUser
                        updateUI(user)
                        App.dm.setUserKey(user!!.uid)
                        if (App.dm.isAdditionalInfoEntered(user.uid))
                            startActivity(Intent(requireActivity(), MainActivity::class.java))
                        else findNavController().navigate(R.id.action_authRegisterFragment_to_additionalIfoFragment)
                    } else {
                        Log.w(ContentValues.TAG, "signInWithEmail:failure", task.exception)
                        makeToast(getString(R.string.some_problems_went))
                    }
                }
        }
    }

    private fun checkInput(): Boolean {
        if (checkEmail()) {
            if (!binding.passwordEdit.text.isNullOrEmpty()) {
                return true
            } else makeToast(getString(R.string.enter_the_password))
        }
        return false
    }

    private fun makeToast(m: String) {
        Toast.makeText(activity, m, Toast.LENGTH_SHORT).show()
    }

    private fun updateUI(account: FirebaseUser?) {
        if (account != null) {
            makeToast(getString(R.string.you_have_been_successfully_authorised))
        } else {
            makeToast(getString(R.string.failed_to_authorize))
        }
    }

    private fun resetEmail() {
        if (checkEmail()) {
            MaterialDialog(requireActivity())
                .title(text = getString(R.string.do_you_want_to_reset_password_for))
                .message(text = binding.emailEdit.text.toString() + " ?")
                .positiveButton(text = "Yes") {
                    auth.sendPasswordResetEmail(binding.emailEdit.text.toString())
                        .addOnCompleteListener {
                            if (!it.isSuccessful) {
                                Toast.makeText(
                                    activity,
                                    getString(R.string.some_problems_went),
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        }
                }
                .show { }
        } else Toast.makeText(
            activity,
            getString(R.string.your_email_must_be_correct),
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun checkEmail() : Boolean {
        when {
            !Patterns.EMAIL_ADDRESS.matcher(binding.emailEdit.text)
                .matches() -> makeToast(getString(R.string.enter_correct_email))

            binding.emailEdit.text.isNullOrEmpty() -> makeToast(getString(R.string.enter_email))
            else -> return true
        }
        return false
    }

    private fun registration() {
        val mail = binding.emailEdit.text.toString()
        val password = binding.passwordEdit.text.toString()

        if (checkInputPass()) {
            auth.createUserWithEmailAndPassword(mail, password)
                .addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        Log.d(ContentValues.TAG, "createUserWithEmail:success")
                        val user = auth.currentUser
                        App.dm.setUserKey(user!!.uid)
                        findNavController().navigate(R.id.action_authRegisterFragment_to_additionalIfoFragment)
                        updateUI(user)
                    } else {
                        Log.w(ContentValues.TAG, "createUserWithEmail:failure", task.exception)
                        makeToast(task.exception!!.message.toString())
                    }
                }
        }
    }
    private fun checkInputPass(): Boolean {
        when {
            binding.passwordEdit.text.isNullOrEmpty() -> makeToast(getString(R.string.enter_the_password))
            binding.passwordEdit.text.toString().length < 6 -> makeToast(getString(R.string.password_must_be_6_symbols_at_least))
            binding.passwordRepeat.text.toString() != binding.passwordEdit.text.toString() -> makeToast(
                getString(R.string.password_mismatch)
            )

            else -> return checkInput()
        }
        return false
    }
}