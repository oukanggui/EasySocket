package com.baymax.base.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.widget.TextView;

import com.example.base.R;


/**
 * Created by 欧康贵 on 2017/5/10.
 */

public class LoadingDialog extends Dialog {
    private static final int CHANGE_TITLE_WHAT = 1;
    private static final int CHNAGE_TITLE_DELAYMILLIS = 300;
    private static final int MAX_SUFFIX_NUMBER = 3;
    private static final char SUFFIX = '.';


    //private ImageView iv_route;
    private TextView detail_tv;
    private TextView tv_point;


    private Handler handler = new Handler() {
        private int num = 0;


        public void handleMessage(android.os.Message msg) {
            if (msg.what == CHANGE_TITLE_WHAT) {
                StringBuilder builder = new StringBuilder();
                if (num >= MAX_SUFFIX_NUMBER) {
                    num = 0;
                }
                num++;
                for (int i = 0; i < num; i++) {
                    builder.append(SUFFIX);
                }
                tv_point.setText(builder.toString());
                if (isShowing()) {
                    handler.sendEmptyMessageDelayed(CHANGE_TITLE_WHAT, CHNAGE_TITLE_DELAYMILLIS);
                } else {
                    num = 0;
                }
            }
        }
    };


    public LoadingDialog(Context context) {
        super(context, R.style.Transparentdialog);
        //@android:style/Theme.Dialog
        //super(context,android.R.style.Theme_Dialog);
        init();
    }

    public static void dismissDialog(LoadingDialog loadingDialog) {
        if (null == loadingDialog) {
            return;
        }
        if (loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void init() {
        setContentView(R.layout.dialog_loading_layout);
        setCancelable(false);

        //iv_route = (ImageView) findViewById(R.id.iv_route);
        detail_tv = (TextView) findViewById(R.id.detail_tv);
        tv_point = (TextView) findViewById(R.id.tv_point);
    }

    @Override
    public void show() {//在要用到的地方调用这个方法
        handler.sendEmptyMessage(CHANGE_TITLE_WHAT);
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void setTitle(CharSequence title) {
        if (title == null) {
            detail_tv.setText("正在加载");
        } else {
            detail_tv.setText(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getContext().getString(titleId));
    }
}

