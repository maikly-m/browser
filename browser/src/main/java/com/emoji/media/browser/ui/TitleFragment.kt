package com.emoji.media.browser.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.emoji.media.browser.PathBuilder
import com.emoji.media.browser.PathSelector
import com.emoji.media.browser.R
import com.emoji.media.browser.databinding.FragmentTitleBinding

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TitleFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TitleFragment : BaseFragment() {
    private lateinit var binding: FragmentTitleBinding
    private var param1: PathBuilder? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getSerializable(ARG_PARAM1) as PathBuilder?
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTitleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fragmentTitleIvBack.setOnClickListener {
            controller?.exit()
        }
    }

    override fun onStart() {
        super.onStart()
        controller!!.setPathChangeListener(TitleFragment::class.java.simpleName){info, change ->
            //todo
            binding.fragmentTitleTvName.text = info.currentPath
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: PathBuilder, param2: String) =
            TitleFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}