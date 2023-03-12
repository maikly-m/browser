package com.emoji.media.browser.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emoji.media.browser.PathBuilder
import com.emoji.media.browser.databinding.FragmentPathBinding
import com.emoji.media.browser.databinding.FragmentPathRvItemBinding
import timber.log.Timber

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PathFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PathFragment : BaseFragment() {
    private lateinit var pathAdapter: PathAdapter
    private lateinit var binding: FragmentPathBinding
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
        binding = FragmentPathBinding.inflate(inflater, container, false)

        binding.fragmentPathRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        pathAdapter = PathAdapter()
        binding.fragmentPathRv.adapter = pathAdapter

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        controller!!.setPathChangeListener(PathFragment::class.java.simpleName){info, change ->
            pathAdapter.notifyDataSetChanged()
            binding.fragmentPathRv.layoutManager?.scrollToPosition(controller!!.pathList.size-1)
        }
    }

    inner class PathAdapter: RecyclerView.Adapter<ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return FragmentPathRvItemBinding.inflate(LayoutInflater.from(requireContext()), parent, false).run {
                ViewHolder(this)
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (holder.binding as FragmentPathRvItemBinding).let {
                it.fragmentPathRvItemPath.text = controller!!.pathList[position]
                it.fragmentPathRvItemCl.setOnClickListener {
                    //go forward destination that you select
                    controller!!.back(position)
                }
            }
        }

        override fun getItemCount(): Int {
            return controller!!.pathList.size
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }



    companion object {
        @JvmStatic
        fun newInstance(param1: PathBuilder, param2: String) =
            PathFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}