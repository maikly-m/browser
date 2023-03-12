package com.emoji.media.pathselector

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.emoji.media.browser.Constants
import com.emoji.media.browser.PathBuilder
import com.emoji.media.browser.ui.FilterActivity
import com.emoji.media.pathselector.databinding.FragmentFirstBinding
import com.permissionx.guolindev.PermissionX

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private val TAG = "FirstFragment"

    private var _binding: FragmentFirstBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private lateinit var launch: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        launch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Constants.RESULT_CODE){
                it.data?.let { data ->
                    data.getStringArrayListExtra(Constants.RESULT_FILES)?.forEach { s ->
                        Log.d(TAG, "onCreate: file $s")
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonFirst.setOnClickListener {
            PermissionX.init(this)
                .permissions(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ).onForwardToSettings { scope, deniedList ->

                    }.request{ allGranted, grantedList, deniedList ->
                    if (allGranted){
                        fragmentSelectShow()
                    }
                }
        }

        binding.buttonSecond.setOnClickListener {
//            nativeLib.SayTest(1)
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Fragment方式
     */
    private fun fragmentSelectShow() {
        Intent(context, FilterActivity::class.java).run {
            putExtra(Constants.PATH_EXTRAS,  PathBuilder().apply {
                num = 2
                rootPath = Environment.getExternalStorageDirectory().path
                dirFilter = mutableListOf<String>().apply {

                    add("$rootPath/DCIM")
                    add("$rootPath/Movies")
                    add("$rootPath/Pictures")
                    add("$rootPath/Music")
                    add("$rootPath/Alarms")
                    add("$rootPath/Audiobooks")
                    add("$rootPath/Notifications")
                    add("$rootPath/Podcasts")
                    add("$rootPath/Ringtones")
                    add("$rootPath/Download")
                    add("$rootPath/Recordings")
                }
//                fileFilter = mutableListOf<String>().apply {
//                    add("avi")
//                    add("mp4")
//                    add("flv")
//                    add("rmvb")
//                    add("png")
//
//                    add("mp3")
//                    add("acc")
//                    add("m4a")
//                    add("wav")
//                    add("flac")
//                }
            })
            launch.launch(this)
        }
    }
}