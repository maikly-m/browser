package com.emoji.media.browser.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.lifecycle.coroutineScope
import com.emoji.media.browser.PathBuilder
import com.emoji.media.browser.control.Controller
import com.emoji.media.browser.databinding.FragmentFilterBinding
import com.emoji.media.browser.parsePath
import timber.log.Timber

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FilterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FilterFragment : BaseFragment() {
    private lateinit var binding: FragmentFilterBinding
    private var pathBuilder: PathBuilder? = null
    private var param2: String? = null
    private lateinit var titleFragment: TitleFragment
    private lateinit var pathFragment: PathFragment
    private lateinit var containerFragment: ContainerFragment


    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            controller?.exit()
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pathBuilder = it.getSerializable(ARG_PARAM1) as PathBuilder?
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //add child fragments
        childFragmentManager.let {
            val transaction = it.beginTransaction()

            titleFragment = TitleFragment.newInstance(pathBuilder!!, "")
            transaction.add(
                binding.filterFragmentFlTitle.id, titleFragment,
                TitleFragment::class.java.simpleName
            )
            pathFragment = PathFragment.newInstance(pathBuilder!!, "")
            transaction.add(
                binding.filterFragmentFlPath.id, pathFragment,
                PathFragment::class.java.simpleName
            )
            containerFragment = ContainerFragment.newInstance(pathBuilder!!, "")
            transaction.add(
                binding.filterFragmentFlContainer.id, containerFragment,
                ContainerFragment::class.java.simpleName
            )

            transaction.commitAllowingStateLoss()
            initData()
        }
    }

    private fun initData() {
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackPressedCallback)
        pathBuilder?.let {
            controller = Controller(parsePath(it.rootPath), it.dirFilter, it.fileFilter, lifecycle.coroutineScope){ c ->
                //exit
                if (c.originPaths.size > c.pathList.size-1){
                    Timber.d("exit .. ")
                    onBackPressedCallback.isEnabled = false
                    requireActivity().finish()
                }else{
                    c.back(c.pathList.size-2)
                }
            }
            titleFragment.controller = controller
            pathFragment.controller = controller
            containerFragment.controller = controller
        }
        controller!!.setPathChangeListener(FilterFragment::class.java.simpleName) {info, change ->
            //todo
        }
    }

    override fun onStart() {
        super.onStart()
        controller!!.init()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: PathBuilder, param2: String) =
            FilterFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}