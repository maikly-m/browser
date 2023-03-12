package com.emoji.media.browser.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.emoji.media.browser.Constants
import com.emoji.media.browser.PathBuilder
import com.emoji.media.browser.R
import com.emoji.media.browser.bean.DirInfo
import com.emoji.media.browser.bean.FileInfo
import com.emoji.media.browser.databinding.FragmentContainerBinding
import com.emoji.media.browser.databinding.FragmentContentRvItemBinding
import com.emoji.media.browser.ui.viewmodel.ContentFragmentVM

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ContainerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContainerFragment : BaseFragment() {
    private lateinit var mViewModel: ContentFragmentVM
    private lateinit var contentAdapter: ContentAdapter
    private lateinit var binding: FragmentContainerBinding
    private var pathBuilder: PathBuilder? = null
    private var param2: String? = null

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
        mViewModel = ViewModelProvider(this)[ContentFragmentVM::class.java]
        binding = FragmentContainerBinding.inflate(inflater, container, false)
        binding.fragmentContentRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

//        binding.fragmentContentRv.addItemDecoration(object : ItemDecoration() {
//            override fun getItemOffsets(
//                outRect: Rect, view: View, parent: RecyclerView,
//                state: RecyclerView.State
//            ) {
//                outRect.left = dp2px(4.0f)
//                outRect.top = dp2px(2.0f)
//                outRect.right = dp2px(4.0f)
//                outRect.bottom = dp2px(2.0f)
//            }
//        })
        contentAdapter = ContentAdapter()
        binding.fragmentContentRv.adapter = contentAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.showSelector.observe(viewLifecycleOwner){
            if (it) {
                binding.fragmentContentClBottom.visibility = View.VISIBLE
            } else {
                binding.fragmentContentClBottom.visibility = View.GONE
            }
        }
        mViewModel.selectorListSize.observe(viewLifecycleOwner){
            pathBuilder?.let { pb ->
                binding.fragmentContentClBottomNum.text = "$it/${pb.num}"
                if (it==pb.num){
                    binding.fragmentContentClBottomBtn.text = "完成"
                    binding.fragmentContentClBottomBtn.isEnabled = true
                }else{
                    binding.fragmentContentClBottomBtn.text = "选择"
                    binding.fragmentContentClBottomBtn.isEnabled = false
                }
            }
        }
        binding.fragmentContentClBottomBtn.setOnClickListener {
            //计算
            controller!!.selectFile?.invoke(arrayListOf<String>().apply {
                contentAdapter.selectorList.sorted().forEach { position ->
                    add(controller!!.dirInfo.list[position].fullPath.run {
                        var s = ""
                        forEach { ss ->
                            s += "/$ss"
                        }
                        return@run s
                    })
                }
            })
        }
    }

    override fun onStart() {
        super.onStart()
        controller!!.setPathChangeListener(ContainerFragment::class.java.simpleName) { info, change ->
            //clear
            contentAdapter.clearCondition()
            contentAdapter.notifyDataSetChanged()
        }
        controller!!.selectFile = {
            requireActivity().setResult(Constants.RESULT_CODE, Intent().apply {
                putStringArrayListExtra(Constants.RESULT_FILES, it)
            })
            requireActivity().finish()
        }
    }


    inner class ContentAdapter: RecyclerView.Adapter<ViewHolder>() {
        private var showSelector = false
        val selectorList = arrayListOf<Int>()

        fun clearCondition(){
            showSelector = false
            selectorList.clear()
            mViewModel.showSelector.value = false
            mViewModel.selectorListSize.value = 0
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return FragmentContentRvItemBinding.inflate(LayoutInflater.from(requireContext()), parent, false).run {
                ViewHolder(this)
            }
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            (holder.binding as FragmentContentRvItemBinding).let {
                if (position==0) {
                    it.fragmentContentRvItemCb.visibility = View.GONE
                    it.fragmentContentRvItemPath.text = ".."
                    it.fragmentContentRvItemModifiedTime.text = controller!!.dirInfo.modifiedTime.toString()
                    it.fragmentContentRvItemCount.text = ""
                    it.fragmentContentRvItemSize.text = ""
                    it.fragmentContentRvItemType.setImageResource(R.drawable.ic_browser_folder)
                    it.fragmentContentRvItemCl.setOnClickListener {
                        //go forward destination that you select
                        controller!!.go(controller!!.dirInfo.fullPath.apply {
                            if (size > controller!!.originPaths.size){
                                removeLast()
                            }
                        })
                    }
                    it.fragmentContentRvItemCl.setOnLongClickListener{false}
                } else {
                    val p = position - 1
                    controller!!.dirInfo.list[p].let { pathInfo ->
                        if (pathInfo is DirInfo) {
                            it.fragmentContentRvItemCb.visibility = View.GONE
                            it.fragmentContentRvItemPath.text = pathInfo.currentPath
                            it.fragmentContentRvItemModifiedTime.text = pathInfo.modifiedTime.toString()
                            it.fragmentContentRvItemCount.text = pathInfo.childSize.toString()
                            it.fragmentContentRvItemSize.text = ""
                            it.fragmentContentRvItemType.setImageResource(R.drawable.ic_browser_folder)
                            it.fragmentContentRvItemCl.setOnClickListener {
                                //go forward destination that you select
                                controller!!.go(pathInfo.fullPath)
                            }
                            it.fragmentContentRvItemCl.setOnLongClickListener{false}
                        } else if (pathInfo is FileInfo){
                            if (showSelector){
                                it.fragmentContentRvItemCb.visibility = View.VISIBLE
                            }else{
                                it.fragmentContentRvItemCb.visibility = View.GONE
                            }
                            it.fragmentContentRvItemPath.text = pathInfo.currentPath
                            it.fragmentContentRvItemModifiedTime.text = pathInfo.modifiedTime.toString()
                            it.fragmentContentRvItemSize.text = pathInfo.size ?: ""
                            it.fragmentContentRvItemCount.text = ""
                            it.fragmentContentRvItemType.setImageResource(R.drawable.ic_browser_question)
                            if (selectorList.contains(position)){
                                it.fragmentContentRvItemCb.isChecked = true
                            }
                            it.fragmentContentRvItemCl.setOnClickListener { _ ->
                                if (showSelector){
                                    if (it.fragmentContentRvItemCb.isChecked) {
                                        it.fragmentContentRvItemCb.isChecked = false
                                        selectorList.remove(position)
                                    } else {
                                        if (selectorList.size >= pathBuilder!!.num){
                                            Toast.makeText(requireActivity(),
                                                "选中不能超过${pathBuilder!!.num}个", Toast.LENGTH_SHORT)
                                                .show()
                                            return@setOnClickListener
                                        }
                                        it.fragmentContentRvItemCb.isChecked = true
                                        selectorList.add(position)
                                    }
                                    mViewModel.selectorListSize.value = selectorList.size
                                }
                            }
                            it.fragmentContentRvItemCl.setOnLongClickListener { _ ->
                                //go forward destination that you select
                                if (!showSelector){
                                    showSelector = true
                                    selectorList.add(position)
                                    mViewModel.showSelector.value = true
                                    mViewModel.selectorListSize.value = selectorList.size
                                    notifyItemRangeChanged(1, controller!!.dirInfo.list.size)
                                }
                                true
                            }
                        }
                    }
                }

            }
        }

        override fun getItemCount(): Int {
            return controller!!.dirInfo.list.size+1
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: PathBuilder, param2: String) =
            ContainerFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}