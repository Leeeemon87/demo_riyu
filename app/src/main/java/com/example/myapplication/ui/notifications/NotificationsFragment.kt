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
import com.example.myapplication.HistoryActivity
import android.widget.ImageView
import android.provider.MediaStore
import android.app.Activity
import android.net.Uri
import java.io.File
import android.graphics.Bitmap
import java.io.FileOutputStream
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import android.content.Context
import android.content.SharedPreferences
import com.bumptech.glide.Glide

class NotificationsFragment : Fragment() {
    private var selectedImageUri: Uri? = null
    private var _binding: FragmentNotificationsBinding? = null
    private lateinit var avatarImageView: ImageView
    private val PICK_IMAGE_REQUEST = 1

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


        avatarImageView = binding.avatarImageView
        avatarImageView.setOnClickListener {
            openImageSelection()

            val savedImageUri = getSavedSelectedImageUri()
            loadImageIntoAvatar(savedImageUri)
        }
            val hisButton = binding.historyButton
            // 在 NotificationFragment.kt 中的按钮点击事件中
            hisButton.setOnClickListener {
                val intent = Intent(requireContext(), HistoryActivity::class.java)
                intent.putExtra("key", "value") // 添加要传递的参数
                startActivity(intent)
            }

            return root
        }
        private fun openImageSelection() {
            // ...

            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PICK_IMAGE_REQUEST
                )
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, PICK_IMAGE_REQUEST)
            }
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
                selectedImageUri = data.data
                loadImageIntoAvatar(selectedImageUri)

                // Save the selected image URI or other identifier to shared preferences
                saveSelectedImageUri(selectedImageUri)
            }
        }
        private fun saveSelectedImageUri(uri: Uri?) {
            val sharedPrefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.putString("selected_image_uri", uri?.toString())
            editor.apply()
        }
        private fun loadImageIntoAvatar(imageUri: Uri?) {
            imageUri?.let {
                Glide.with(this).load(imageUri).into(avatarImageView)
            }
        }
        private fun getSavedSelectedImageUri(): Uri? {
            val sharedPrefs = requireContext().getSharedPreferences("my_prefs", Context.MODE_PRIVATE)
            val uriString = sharedPrefs.getString("selected_image_uri", null)
            return uriString?.let { Uri.parse(it) }
        }
        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
        }
    }