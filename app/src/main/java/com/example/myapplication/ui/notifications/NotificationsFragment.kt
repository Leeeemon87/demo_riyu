package com.example.myapplication.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.databinding.FragmentNotificationsBinding
import com.example.myapplication.PitchesActivity

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root


        val pitchButton =binding.testButton
        // 在 NotificationFragment.kt 中的按钮点击事件中
        pitchButton.setOnClickListener {
            val intent = Intent(requireContext(), PitchesActivity::class.java)
            intent.putExtra("key", "value") // 添加要传递的参数
            startActivity(intent)
        }

        val hisButton =binding.historyButton
        // 在 NotificationFragment.kt 中的按钮点击事件中
        hisButton.setOnClickListener {
            val intent = Intent(requireContext(), PitchesActivity::class.java)
            intent.putExtra("key", "value") // 添加要传递的参数
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}