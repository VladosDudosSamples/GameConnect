package diplom.project.gameconnect.view.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import diplom.project.gameconnect.R
import diplom.project.gameconnect.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {

    private val binding: FragmentProfileBinding by lazy { FragmentProfileBinding.inflate(layoutInflater) }

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

    }
}