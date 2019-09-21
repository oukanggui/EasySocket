package com.okg.easysocket.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.baymax.base.activity.BaseToorBarActivity;
import com.baymax.base.constant.Constants;
import com.baymax.base.network.RetrofitUtil;
import com.baymax.utilslib.SharedPreferencesUtil;
import com.baymax.utilslib.TextUtil;
import com.baymax.utilslib.ToastUtil;
import com.okg.easysocket.R;
import com.okg.easysocket.api.login.RegisterInterface;
import com.okg.easysocket.bean.ResponseMsg;
import com.okg.easysocket.constant.AppConstants;
import com.baymax.base.image.ImageLoader;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class RegisterActivity extends BaseToorBarActivity implements View.OnClickListener {

    private static final String URL_GET_QRCODE = Constants.BASE_URL + "/user/code";
    private View bt_register;
    private EditText et_phone, et_password, et_password_again, et_qrcode;
    private ImageView iv_deletePhone, iv_deletePsw, iv_deletePswAgain, iv_qrcode;
    private String password = null;
    private String passwordAgain = null;
    private String phone = null;

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        setResult(1, intent);
        finish();
        overridePendingTransition(R.anim.anim_activity_enter_left, R.anim.anim_activity_exit_right);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    public void initView(Bundle savedInstanceState) {
        bt_register = findViewById(R.id.register_bt_register);

        et_phone = findViewById(R.id.register_et_phone);
        et_password = findViewById(R.id.register_et_psw);
        et_password_again = findViewById(R.id.register_et_psw_again);
        et_qrcode = findViewById(R.id.register_et_qrcode);

        iv_deletePhone = findViewById(R.id.register_delete_phone);
        iv_deletePhone.setVisibility(View.GONE);
        iv_deletePsw = findViewById(R.id.register_delete_psw);
        iv_deletePsw.setVisibility(View.GONE);
        iv_deletePswAgain = findViewById(R.id.register_delete_psw_again);
        iv_deletePswAgain.setVisibility(View.GONE);
        iv_qrcode = findViewById(R.id.register_iv_qrcode);

        iv_qrcode.setOnClickListener(this);
        bt_register.setOnClickListener(this);
        iv_deletePhone.setOnClickListener(this);
        iv_deletePsw.setOnClickListener(this);
        iv_deletePswAgain.setOnClickListener(this);
        et_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    iv_deletePhone.setVisibility(View.VISIBLE);
                } else {
                    iv_deletePhone.setVisibility(View.GONE);
                }
            }
        });
        et_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    iv_deletePsw.setVisibility(View.VISIBLE);
                } else {
                    iv_deletePsw.setVisibility(View.GONE);
                }
            }
        });
        et_password_again.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    iv_deletePswAgain.setVisibility(View.VISIBLE);
                } else {
                    iv_deletePswAgain.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void initData() {
        setHeaderTitle("欢迎注册");
        showBackLayout(true);
        ImageLoader.loadImage(mContext, URL_GET_QRCODE, iv_qrcode);
    }


    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if (viewId == R.id.register_delete_phone) {
            et_phone.setText("");
            return;
        }

        if (viewId == R.id.register_delete_psw) {
            et_password.setText("");
//            et_phone.setText("");
//            et_password_again.setText("");
            return;
        }

        if (viewId == R.id.register_delete_psw_again) {
//            et_password.setText("");
//            et_phone.setText("");
            et_password_again.setText("");
            return;
        }
        if (viewId == R.id.register_iv_qrcode) {
            ImageLoader.loadImage(mContext, URL_GET_QRCODE, iv_qrcode);
            return;
        }

        if (viewId == R.id.register_bt_register) {
            //处理注册逻辑(手机和密码不能为空)-返回登陆
            password = et_password.getText().toString().trim();
            passwordAgain = et_password_again.getText().toString().trim();
            phone = et_phone.getText().toString().trim();
            String qrCode = et_qrcode.getText().toString().trim();

            if (TextUtil.isEmpty(phone)) {
                //若为空，提示用户再次输入
                ToastUtil.showToast(RegisterActivity.this, "手机号不能为空！");
                return;

            }
            if (!TextUtil.isPhoneNumber(phone)) {
                ToastUtil.showToast(RegisterActivity.this, "请正确输入十一位手机号码！");
                return;
            }
            if (TextUtil.isEmpty(password)) {
                //若为空，提示用户再次输入
                ToastUtil.showToast(RegisterActivity.this, "密码不能为空！");
                return;
            }
            //检查密码长度
            if (password.length() != 6) {
                ToastUtil.showToast(RegisterActivity.this, "密码长度不对，请输入六位密码！");
                return;
            }
            if (TextUtil.isEmpty(passwordAgain) || !password.equals(passwordAgain)) {
                //若为空，提示用户再次输入
                ToastUtil.showToast(RegisterActivity.this, "两次密码输入不一致，请确认后再输入!");
                return;
            }

            if (TextUtil.isEmpty(qrCode)) {
                //若为空，提示用户再次输入
                ToastUtil.showToast(RegisterActivity.this, "验证码不能为空");
                return;
            }

            Retrofit retrofit = RetrofitUtil.createRetrofit();
            Call<ResponseMsg> call = retrofit.create(RegisterInterface.class).register(phone, password, qrCode);
            showLoadingDialog("注册中");
            call.enqueue(new Callback<ResponseMsg>() {
                @Override
                public void onResponse(Call<ResponseMsg> call, Response<ResponseMsg> response) {
                    dismissLoadingDialog();
                    ResponseMsg responseMsg = response.body();
                    if (responseMsg != null && responseMsg.getCode() == 1) {
                        ToastUtil.showToast(mContext, responseMsg.getMsg());
                        SharedPreferencesUtil.saveString(mContext, AppConstants.USER_ACCOUNT, phone);
                        SharedPreferencesUtil.saveString(mContext, AppConstants.USER_PASSWORD, password);
                        finish();
                        overridePendingTransition(R.anim.anim_activity_enter_left, R.anim.anim_activity_exit_right);
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

}

