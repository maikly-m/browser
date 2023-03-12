package com.molihuan.pathselector.fragment.impl;

import android.annotation.SuppressLint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.chad.library.adapter.base.listener.OnItemLongClickListener;
import com.molihuan.pathselector.R;
import com.molihuan.pathselector.adapter.MorePopupAdapter;
import com.molihuan.pathselector.dialog.impl.SelectStorageDialog;
import com.molihuan.pathselector.entity.FontBean;
import com.molihuan.pathselector.fragment.AbstractTitlebarFragment;
import com.molihuan.pathselector.listener.CommonItemListener;
import com.molihuan.pathselector.utils.MConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: TitlebarFragment
 * @Author: molihuan
 * @Date: 2022/11/22/18:15
 * @Description:
 */
public class TitlebarFragment extends AbstractTitlebarFragment implements View.OnClickListener, OnItemClickListener, OnItemLongClickListener {

    protected View positionView;                      //定位视图
    protected RelativeLayout relParent;               //父控件
    protected ImageView backImgView;                  //返回按钮
    protected ImageView storageImgView;                  //内存卡按钮

    protected ImageView searchImgView;                //搜索按钮
    protected ImageView moreImgView;                  //更多选项
    protected TextView mainTitleTv;                   //主标题
    protected TextView subtitleTv;                    //副标题(跑马灯还没实现)
    protected TextView oneOptionTv;                   //一个选项
    protected PopupWindow optionsPopup;               //选项 PopupWindow

    protected MorePopupAdapter morePopupAdapter;               //选项 PopupWindow数据适配器
    protected List<CommonItemListener> morePopupItemListeners;     //选项列表

    protected FontBean mainTitle;                     //主标题字样式
    protected FontBean subtitle;                      //副标题字样式
    protected boolean isDialogBuild;                   //是否是dialog模式

    protected TextView morePopupItemTv;

    protected SelectStorageDialog selectStorageDialog;


    @Override
    public int setFragmentViewId() {
        return R.layout.fragment_titlebar;
    }

    @Override
    public void getComponents(View view) {
        positionView = view.findViewById(R.id.view_position_titlebar);
        relParent = view.findViewById(R.id.rel_titlebar);
        backImgView = view.findViewById(R.id.imgv_back_titlebar);
        storageImgView = view.findViewById(R.id.imgv_storage_titlebar);

        searchImgView = view.findViewById(R.id.imgv_seach_titlebar);
        moreImgView = view.findViewById(R.id.imgv_more_options_titlebar);
        mainTitleTv = view.findViewById(R.id.tv_main_title_titlebar);
        subtitleTv = view.findViewById(R.id.tv_subtitle_titlebar);
        oneOptionTv = view.findViewById(R.id.tv_one_option_titlebar);
    }

    @Override
    public void initData() {
        super.initData();
        mainTitle = mConfigData.titlebarMainTitle;
        subtitle = mConfigData.titlebarSubtitleTitle;

        //将监听回调列表转换为数组
        if (morePopupItemListeners == null) {
            morePopupItemListeners = new ArrayList<>();
            if (mConfigData.morePopupItemListeners != null) {
                for (CommonItemListener listener : mConfigData.morePopupItemListeners) {
                    morePopupItemListeners.add(listener);
                }
            }
        }

        if (mConfigData.buildType == MConstants.BUILD_DIALOG) {
            isDialogBuild = true;
        }
    }

    @Override
    public void initView() {
        relParent.setBackgroundColor(mConfigData.titlebarBG);
        setViewSize();
        setTitleFont();
        setOptions();

    }

    protected void setViewSize() {

        if (isDialogBuild) {
            int icoSize = 65;

            relParent.getLayoutParams().height = 115;

            backImgView.getLayoutParams().height = icoSize;
            backImgView.getLayoutParams().width = icoSize;

            storageImgView.getLayoutParams().height = icoSize;
            storageImgView.getLayoutParams().width = icoSize;

            searchImgView.getLayoutParams().height = icoSize;
            searchImgView.getLayoutParams().width = icoSize;

            moreImgView.getLayoutParams().height = icoSize;
            moreImgView.getLayoutParams().width = icoSize;

        } else {

        }
    }

    protected void setOptions() {
        if (!mConfigData.showSelectStorageBtn) {
            storageImgView.setVisibility(View.GONE);
        }

        if (morePopupItemListeners == null || morePopupItemListeners.size() == 0) {
            //没有选项
            moreImgView.setVisibility(View.GONE);
        } else if (morePopupItemListeners.size() == 1) {
            //一个选项
            moreImgView.setVisibility(View.INVISIBLE);
            oneOptionTv.setVisibility(View.VISIBLE);
            FontBean font = morePopupItemListeners.get(0).getFontBean();

            oneOptionTv.setText(font.getText());
            oneOptionTv.setTextColor(font.getColor());
            oneOptionTv.setTextSize(font.getSize());
        } else {
            //多个选项的字样式设置通过Adapter来设置
        }
    }

    protected void setTitleFont() {
        if (mainTitle != null) {
            mainTitleTv.setText(mainTitle.getText());
            mainTitleTv.setTextColor(mainTitle.getColor());
            mainTitleTv.setTextSize(mainTitle.getSize());
        }
        if (subtitle != null) {
            subtitleTv.setText(subtitle.getText());
            subtitleTv.setTextColor(subtitle.getColor());
            subtitleTv.setTextSize(subtitle.getSize());
        }
    }


    @Override
    public void setListeners() {
        backImgView.setOnClickListener(this);
        storageImgView.setOnClickListener(this);
        searchImgView.setOnClickListener(this);
        moreImgView.setOnClickListener(this);
        oneOptionTv.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.imgv_back_titlebar) {

            //返回按钮
            if (mConfigData.buildType == MConstants.BUILD_DIALOG) {
                mConfigData.buildController.getDialogFragment().dismissAllowingStateLoss();
            } else {
                mActivity.finish();
            }

        } else if (id == R.id.imgv_more_options_titlebar) {
            //更多按钮
            showPopupWindow();
        } else if (id == R.id.imgv_storage_titlebar) {
            //内存卡按钮
            showSelectStorageDialog();

        } else if (id == R.id.tv_one_option_titlebar) {
            //一个选项按钮
            optionItemClick(v, (TextView) v, 0);
        } else if (id == R.id.imgv_seach_titlebar) {
            //搜索按钮

        }
    }

    public void showSelectStorageDialog() {
        if (selectStorageDialog == null) {
            selectStorageDialog = new SelectStorageDialog(mActivity);
        }
        selectStorageDialog.show();
    }

    // TODO 版本处理
    @SuppressWarnings("all")
    protected void showPopupWindow() {
        if (optionsPopup == null) {
            View popView = LayoutInflater.from(mActivity).inflate(R.layout.general_recyview, null);//加载布局文件
            optionsPopup = new PopupWindow(popView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);//设置宽度高度
            optionsPopup.setFocusable(true);
            optionsPopup.setOutsideTouchable(true);
            optionsPopup.setElevation(3);//设置阴影 (注意阴影穿透---父组件和子组件必须都设置阴影)
            RecyclerView recyclerView = popView.findViewById(R.id.general_recyclerview);
            recyclerView.setLayoutManager(new LinearLayoutManager(mActivity));

            morePopupAdapter = new MorePopupAdapter(R.layout.general_item_tv, morePopupItemListeners);
            recyclerView.setAdapter(morePopupAdapter);
            morePopupAdapter.setOnItemClickListener(this);//设置监听
            morePopupAdapter.setOnItemLongClickListener(this);//设置监听
        }

        optionsPopup.showAsDropDown(positionView, 0, 0, Gravity.RIGHT);//显示位置

    }

    @Override
    public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View v, int i) {
        if (adapter instanceof MorePopupAdapter) {
            optionsPopup.dismiss();
            morePopupItemTv = v.findViewById(R.id.general_item_textview);
            optionItemClick(v, mainTitleTv, i);
        }
    }

    /**
     * 点击option回调
     *
     * @param v 点击的视图
     * @param i 点击的索引
     */
    protected void optionItemClick(View v, TextView tv, int i) {
        morePopupItemListeners.get(i).onClick(v,
                tv,
                psf.getSelectedFileList(),
                psf.getCurrentPath(),
                psf
        );
    }

    @Override
    public boolean onItemLongClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View v, int position) {
        if (adapter instanceof MorePopupAdapter) {
            morePopupItemTv = v.findViewById(R.id.general_item_textview);
            return optionItemLongClick(v, morePopupItemTv, position);
        }

        return false;
    }

    /**
     * 长按option回调
     *
     * @param v 点击的视图
     * @param i 点击的索引
     */
    protected boolean optionItemLongClick(View v, TextView tv, int i) {
        return morePopupItemListeners.get(i).onLongClick(v,
                tv,
                psf.getSelectedFileList(),
                psf.getCurrentPath(),
                psf
        );
    }

    @Override
    public MorePopupAdapter getMorePopupAdapter() {
        return morePopupAdapter;
    }

    @Override
    public List<CommonItemListener> getMorePopupItemListeners() {
        return morePopupItemListeners;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void refreshMorePopup() {
        morePopupAdapter.notifyDataSetChanged();
    }

    @Override
    public TextView getOnlyOneMorePopupTextView() {
        return this.oneOptionTv;
    }
}
