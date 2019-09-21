package com.okg.easysocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.baymax.base.activity.BaseToorBarActivity;
import com.baymax.base.network.RetrofitUtil;
import com.baymax.utilslib.SharedPreferencesUtil;
import com.baymax.utilslib.TextUtil;
import com.baymax.utilslib.ToastUtil;
import com.okg.easysocket.R;
import com.okg.easysocket.api.login.LoginInterface;
import com.okg.easysocket.bean.ResponseMsg;
import com.okg.easysocket.constant.AppConstants;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class LoginActivity extends BaseToorBarActivity implements View.OnClickListener {
    private PopupWindow popWindow = null;
    private EditText inputNumber;
    private EditText inputPassword;
    private View deleteNumber;
    private View deletePassword;
    private View findPassword;
    private View logonButton;
    private View register;
    private boolean isExit = false;
    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x123) {
                isExit = false;
            }
        }
    };

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (isExit == true) {
            finish();
        } else {
            ToastUtil.showToast(LoginActivity.this, "再按一下退出应用");
            handler.sendEmptyMessageDelayed(0x123, 2000);
            isExit = true;
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        inputNumber = (EditText) findViewById(R.id.input_number);
        inputPassword = (EditText) findViewById(R.id.input_password);
        deleteNumber = findViewById(R.id.number_delete);
        deleteNumber.setOnClickListener(this);
        deleteNumber.setVisibility(View.GONE);
        deletePassword = findViewById(R.id.password_delete);
        deletePassword.setOnClickListener(this);
        deletePassword.setVisibility(View.GONE);
        findPassword = findViewById(R.id.find_password);
        findPassword.setOnClickListener(this);
        logonButton = findViewById(R.id.logon_button);
        logonButton.setOnClickListener(this);
        register = findViewById(R.id.logon_register);
        register.setOnClickListener(this);
        inputNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    deleteNumber.setVisibility(View.VISIBLE);
                } else {
                    deleteNumber.setVisibility(View.GONE);
                }
            }
        });
        inputPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    deletePassword.setVisibility(View.VISIBLE);
                } else {
                    deletePassword.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void initData() {
        setHeaderTitle("登录");
        showBackLayout(false);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.number_delete) {
            inputNumber.setText("");
            return;
        }

        if (viewId == R.id.password_delete) {
            inputPassword.setText("");
            inputNumber.setText("");
            return;
        }

        if (viewId == R.id.find_password) {
            ToastUtil.showToast(this, "功能建设中");
            return;
        }

        if (viewId == R.id.logon_button) {
            login();
            return;
        }

        if (viewId == R.id.logon_register) {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.anim_activity_enter_left, R.anim.anim_activity_exit_right);
            return;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        // TODO
        inputNumber.setText(SharedPreferencesUtil.getString(this, AppConstants.USER_ACCOUNT, null));
        inputPassword.setText(SharedPreferencesUtil.getString(this, AppConstants.USER_PASSWORD, null));
    }

    private void login() {
        Log.e("time start", new Date().getTime() + "");
        String account = inputNumber.getText().toString();
        String password = inputPassword.getText().toString();
        if (TextUtil.isEmpty(account)) {
            ToastUtil.showToast(mContext, "账号不能为空！");
            return;
        }
        if (!TextUtil.isPhoneNumber(account)) {
            ToastUtil.showToast(mContext, "请正确输入十一位手机号码");
            return;
        }
        if (TextUtil.isEmpty(password)) {
            ToastUtil.showToast(mContext, "密码不能为空！");
            return;
        }
        //检查密码长度
        if (password.length() != 6) {
            ToastUtil.showToast(mContext, "长度不对，密码长度为六位！");
            return;
        }
        Retrofit retrofit = RetrofitUtil.createRetrofit();
        Call<ResponseMsg> call = retrofit.create(LoginInterface.class).login(account, password);
        showLoadingDialog("登录中");
        call.enqueue(new Callback<ResponseMsg>() {
            @Override
            public void onResponse(Call<ResponseMsg> call, Response<ResponseMsg> response) {
                dismissLoadingDialog();
                ResponseMsg responseMsg = response.body();
                if (responseMsg == null) {
                    return;
                }
                if (responseMsg.getCode() == 1) {
                    //UserInfo userInfo = JsonUtil.parse(responseMsg.getExtra(), UserInfo.class);
                    //SharePrefrenceUtils.saveUserInfo(userInfo);
                    ToastUtil.showToast(mContext, responseMsg.getMsg());
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    SharedPreferencesUtil.saveString(mContext, AppConstants.USER_ACCOUNT, account);
                    SharedPreferencesUtil.saveString(mContext, AppConstants.USER_PASSWORD, password);
                    overridePendingTransition(R.anim.anim_activity_enter_left, R.anim.anim_activity_exit_right);
                    finish();
                } else {
                    ToastUtil.showToast(mContext, responseMsg.getMsg());
                }
            }

            @Override
            public void onFailure(Call<ResponseMsg> call, Throwable t) {
                dismissLoadingDialog();
                ToastUtil.showToast(mContext, AppConstants.ERROR_MSG);
            }
        });

    }
}
