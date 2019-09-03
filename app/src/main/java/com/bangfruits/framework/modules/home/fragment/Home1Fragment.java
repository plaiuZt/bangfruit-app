package com.bangfruits.framework.modules.home.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;

import com.bangfruits.framework.R;
import com.bangfruits.framework.common.base.BaseFragment;
import com.bangfruits.framework.common.utils.ToastUtils;
import com.orhanobut.logger.Logger;
import com.qmuiteam.qmui.widget.QMUITopBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class Home1Fragment extends BaseFragment {

    @BindView(R.id.topbar)
    QMUITopBar mTopBar;

    public Home1Fragment() {
    }

    @Override
    protected View onCreateView() {
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_home1, null);
        ButterKnife.bind(this, root);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.d("第一页创建");
        initTopBar();
    }

    @OnClick(R.id.button)
    public void onViewClicked() {
        ToastUtils.info("主页");
    }

    private void initTopBar() {
        mTopBar.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.app_color_theme_4));
        mTopBar.setTitle("沉浸式状态栏示例");
    }

    /*private void initTopBar() {
        mTopBar.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.app_color_theme_4));

        mTopBar.setTitle("沉浸式状态栏示例");
    }*/

    @Override
    public void onDestroy() {
        super.onDestroy();
        Logger.d("第一页销毁");
    }

    /**
     * 当在activity设置viewPager + BottomNavigation + fragment时，
     * 为防止viewPager左滑动切换界面，与fragment左滑返回上一界面冲突引起闪退问题，
     * 必须加上此方法，禁止fragment左滑返回上一界面。
     *
     * 切记！切记！切记！否则会闪退！
     *
     * 当在fragment设置viewPager + BottomNavigation + fragment时，则不会出现这个问题。
     * */
    @Override
    protected boolean canDragBack() {
        return false;
    }
}
