package diplom.project.gameconnect.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import diplom.project.gameconnect.R
import diplom.project.gameconnect.databinding.ActivityStartBinding

class StartActivity : AppCompatActivity() {

    private val binding: ActivityStartBinding by lazy { ActivityStartBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}