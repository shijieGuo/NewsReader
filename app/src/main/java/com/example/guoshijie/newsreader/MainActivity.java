package com.example.guoshijie.newsreader;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class MainActivity extends FragmentActivity {
    private RadioGroup navGroup;
    private String tabs[] = { "首页", "新闻", "组图", "更多" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 初始化底部导航栏
        navGroup = (RadioGroup) findViewById(R.id.navgroup);
        // 设定导航栏中每个选项的选中事件响应
        navGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButton1:
                        // 切换到"首页"界面
                        switchFragmentSupport(R.id.content, tabs[0]);
                        break;
                    case R.id.radioButton2:
                        // 切换到"新闻"界面
                        switchFragmentSupport(R.id.content, tabs[1]);
                        break;
                    case R.id.radioButton3:
                        // 切换到"组图"界面
                        switchFragmentSupport(R.id.content, tabs[2]);
                        break;
                    case R.id.radioButton4:
                        // 切换到"更多"界面
                        switchFragmentSupport(R.id.content, tabs[3]);
                        break;
                }
            }
        });
        // 默认选中最左边的RadioButton
        RadioButton btn = (RadioButton) navGroup.getChildAt(0);
        btn.toggle();
    }

    /**
//     * 动态切换组件中显示的界面
//     *
//     * @param containerId
//     *            待切换界面的组件
//     * @param destFragment
//     *            目标界面
     */
    public void switchFragmentSupport(int containerId, String tag) {
        // 获取FragmentManager管理器
        FragmentManager manager = getSupportFragmentManager();
        // 根据tag标签名查找是否存在Fragment对象
        Fragment destFragment = manager.findFragmentByTag(tag);
        // 如果tag标签对应的Fragment对象不存在，则初始化它
        if (destFragment == null) {
            if (tag.equals(tabs[0])) destFragment = new Fragment1();
            if (tag.equals(tabs[1])) destFragment = new Fragment2();
            if (tag.equals(tabs[2])) destFragment = new Fragment3();
            if (tag.equals(tabs[3])) destFragment = new Fragment4();
        }

        // 获取事务对象
        FragmentTransaction ft = manager.beginTransaction();
        // 将组件id为containerId 的内部内容替换为destFragment，且将destFragment的标签设为tag变量的值
        ft.replace(containerId, destFragment, tag);
        // 下面的代码是设置Fragment 切换的过渡效果，可根据需要使用
        // ft.setTransition(FragmentTransaction.TRANSIT_NONE);
        // 也可以将状态保持到回退栈，以使按下Back 键时返回显示前一个Fragment 界面
        // ft.addToBackStack(null);
        ft.commit();
    }

}