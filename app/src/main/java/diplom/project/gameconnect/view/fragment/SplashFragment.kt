package diplom.project.gameconnect.view.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.FragmentSplashBinding
import diplom.project.gameconnect.model.LoginType
import diplom.project.gameconnect.statik.LoginTypeHolder
import diplom.project.gameconnect.view.activity.MainActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashFragment : Fragment() {

    private val binding: FragmentSplashBinding by lazy {
        FragmentSplashBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            delay(1100)

            if (!App.dm.isOnBoardingPassed()) {
                findNavController().navigate(R.id.action_splashFragment_to_onBoardingFragment2)
            } else if (App.dm.isLoginPassed()) {
                startActivity(Intent(requireActivity(), MainActivity::class.java))
                requireActivity().finish()
            } else {
                findNavController().navigate(R.id.action_splashFragment_to_authRegisterFragment)
                LoginTypeHolder.loginType = LoginType.AUTHORISATION
            }
        }
    }
}