package com.okg.easysocket.activity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;


import com.baymax.base.activity.BaseTitleBarActivity;
import com.baymax.base.constant.Constants;
import com.baymax.utilslib.TextUtil;
import com.baymax.utilslib.ToastUtil;
import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.signature.StringSignature;
import com.okg.easysocket.R;
import com.okg.easysocket.api.login.RegisterInterface;
import com.okg.easysocket.bean.ResponseMsg;
import com.okg.easysocket.constant.AppConstants;
import com.okg.easysocket.manager.CookiesManager;
import com.okg.easysocket.manager.UserInfoManager;

import java.io.InputStream;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RegisterActivity extends BaseTitleBarActivity implements View.OnClickListener {

    private static final String URL_GET_CODE = Constants.BASE_URL + "/user/code";
    private View bt_register;
    private EditText et_phone, et_password, et_password_again, et_qrcode;
    private ImageView iv_deletePhone, iv_deletePsw, iv_deletePswAgain, iv_qrcode;
    private String password;
    private String passwordAgain;
    private String phone;
    private OkHttpClient mOkHttpClient;

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
        iv_deletePsw = findViewById(R.id.register_delete_psw);
        iv_deletePswAgain = findViewById(R.id.register_delete_psw_again);
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
        registerOkHttpForGlide();
        loadCodeImage();
    }

    /**
     * Glide注册OkHttp进行网络加载
     */
    private void registerOkHttpForGlide() {
        //声明日志类
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        //设定日志级别
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        mOkHttpClient = new OkHttpClient.Builder()
                .cookieJar(new CookiesManager())   //cookie管理
                .addInterceptor(httpLoggingInterceptor)
                .build();
        // 使用OkHttp作为图片请求,不Register的话，图片请求不会经过OkHttpClient
        Glide.get(this).register(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(mOkHttpClient));
    }

    /**
     * 加载验证码图片
     */
    private void loadCodeImage() {
        String updateTime = String.valueOf(System.currentTimeMillis());
        // 验证码不用缓存
        Glide.with(this)
                .load(URL_GET_CODE)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .skipMemoryCache(true)
                .crossFade()
                .centerCrop()
                .signature(new StringSignature(updateTime))
                .placeholder(com.example.base.R.mipmap.ic_default_placeholder)
                .error(com.example.base.R.mipmap.ic_default_placeholder)
                .into(iv_qrcode);
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
            loadCodeImage();
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

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.BASE_URL) // 设置 网络请求base url
                    .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析
                    .client(mOkHttpClient)
                    .build();
            Call<ResponseMsg> call = retrofit.create(RegisterInterface.class).register(phone, password, qrCode);
            showLoadingDialog("注册中");
            call.enqueue(new Callback<ResponseMsg>() {
                @Override
                public void onResponse(Call<ResponseMsg> call, Response<ResponseMsg> response) {
                    dismissLoadingDialog();
                    ResponseMsg responseMsg = response.body();
                    if (responseMsg == null) {
                        ToastUtil.showToast(mContext, "注册异常");
                        return;
                    }
                    ToastUtil.showToast(mContext, responseMsg.getMsg());
                    if (responseMsg != null && responseMsg.getCode() == 1) {
                        UserInfoManager.getInstance(mContext).saveUserInfo(phone, password);
                        finish();
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

