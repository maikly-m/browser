package com.molihuan.pathselector.dialog.impl

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import com.molihuan.pathselector.dialog.BaseDialog
import com.molihuan.pathselector.entity.FontBean
import com.blankj.molihuan.utilcode.util.ScreenUtils
import com.molihuan.pathselector.R
import com.molihuan.pathselector.databinding.GeneralTitleContentBinding


class MessageDialog(context: Context) : BaseDialog(context) {
    private lateinit var binding: GeneralTitleContentBinding
    private var titleFont: FontBean? = null
    private var contentFont: FontBean? = null
    private var confirmFont: FontBean? = null
    private var cancelfont: FontBean? = null
    private var confirmListener: IOnConfirmListener? = null
    private var cancelListener: IOnCancelListener? = null
    fun setTitle(titleFont: FontBean?): MessageDialog {
        this.titleFont = titleFont
        return this
    }

    fun setContent(contentFont: FontBean?): MessageDialog {
        this.contentFont = contentFont
        return this
    }

    fun setConfirm(confirmFont: FontBean?, confirmListener: IOnConfirmListener?): MessageDialog {
        this.confirmFont = confirmFont
        this.confirmListener = confirmListener
        return this
    }

    fun setCancel(cancelfont: FontBean?, cancelListener: IOnCancelListener?): MessageDialog {
        this.cancelfont = cancelfont
        this.cancelListener = cancelListener
        return this
    }

    override fun setContentView(): View {
        binding = GeneralTitleContentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun initData() {}
    override fun initView() {
        window!!.setLayout(ScreenUtils.getScreenWidth() * 75 / 100, ViewGroup.LayoutParams.WRAP_CONTENT)
        if (titleFont != null) {
            binding.generalTitleContentTvTitle.run {
                text = titleFont!!.text
                titleFont!!.size.toFloat()
                setTextColor(titleFont!!.color)
            }
        } else {
            binding.generalTitleContentTvTitle.run {
                setText(R.string.default_dialog_title)
                setTextColor(Color.BLACK)
            }
        }
        if (contentFont != null) {
            binding.generalTitleContentTvContent.run {
                text = contentFont!!.text
                contentFont!!.size.toFloat()
                setTextColor(contentFont!!.color)
            }
        }
        if (confirmFont != null) {
            binding.generalTitleContentTvConfirm.run {
                text = confirmFont!!.text
                confirmFont!!.size.toFloat()
                setTextColor(confirmFont!!.color)
            }
        }
        if (cancelfont != null) {
            binding.generalTitleContentTvCancel.run {
                text = cancelfont!!.text
                cancelfont!!.size.toFloat()
                setTextColor(cancelfont!!.color)
            }
        }
    }

    override fun setListeners() {
        binding.generalTitleContentTvConfirm.run {
            setOnClickListener(this@MessageDialog)
            setOnClickListener(this@MessageDialog)
        }
    }

    override fun onClick(v: View) {
        val id = v.id
        if (id == R.id.general_title_content_tv_confirm) {
            confirmListener!!.onClick(v, this)
        } else if (id == R.id.general_title_content_tv_cancel) {
            cancelListener!!.onClick(v, this)
        }
    }
}