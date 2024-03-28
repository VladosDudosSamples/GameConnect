package diplom.project.gameconnect.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import diplom.project.gameconnect.R
import diplom.project.gameconnect.app.App
import diplom.project.gameconnect.databinding.FragmentOnboardingBinding
import diplom.project.gameconnect.model.LoginType
import diplom.project.gameconnect.statik.LoginTypeHolder.loginType

class OnBoardingFragment : Fragment() {

    private val binding: FragmentOnboardingBinding by lazy { FragmentOnboardingBinding.inflate(layoutInflater) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onClick()
    }
    private fun onClick(){
        binding.authoriseBtn.setOnClickListener {
            skipOnBoard()
            loginType = LoginType.AUTHORISATION
        }
        binding.createAccountBtn.setOnClickListener {
            skipOnBoard()
            loginType = LoginType.REGISTRATION
        }
    }
    private fun skipOnBoard(){
        findNavController().navigate(R.id.action_onBoardingFragment2_to_authRegisterFragment)
        App.dm.passOnBoarding()
    }
}