package com.okg.easysocket.adapter;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.baymax.base.adapter.base.BaseQuickAdapter;
import com.baymax.base.adapter.base.BaseViewHolder;
import com.baymax.base.dialog.BaseDialog;
import com.baymax.base.network.RetrofitUtil;
import com.baymax.utilslib.ToastUtil;
import com.okg.easysocket.R;
import com.okg.easysocket.activity.DeviceEditActivity;
import com.okg.easysocket.api.device.DeviceInterface;
import com.okg.easysocket.bean.DeviceInfo;
import com.okg.easysocket.bean.ResponseMsg;
import com.okg.easysocket.dialog.MessageDialog;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * @author Baymax
 * @date 2019-09-22
 * 描述：设备列表Adapter
 */
public class DeviceListAdapter extends BaseQuickAdapter<DeviceInfo, BaseViewHolder> {
    public DeviceListAdapter(int layoutResId, @Nullable List<DeviceInfo> data) {
        super(layoutResId, data);
    }

    public DeviceListAdapter(@Nullable List<DeviceInfo> data) {
        super(data);
    }

    @Override
    protected void convert(BaseViewHolder helper, DeviceInfo item) {
        if (item == null) {
            return;
        }
        TextView tvDeviceName = helper.getView(R.id.item_device_name);
        TextView tvDeviceSerial = helper.getView(R.id.item_device_serial);
        TextView tvDeviceStatus = helper.getView(R.id.item_device_status);

        tvDeviceName.setText(item.getDeviceName());
        tvDeviceSerial.setText(item.getDeviceSn());
        int statusColorId = item.isDeviceOnline() ?
                mContext.getResources().getColor(android.R.color.holo_green_dark) :
                mContext.getResources().getColor(android.R.color.holo_red_dark);
        tvDeviceStatus.setTextColor(statusColorId);
        String statusText = item.isDeviceOnline() ? "在线" : "已下线";
        tvDeviceStatus.setText(statusText);

        View btnControl = helper.getView(R.id.item_device_control);
        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ToastUtil.showToast(mContext, "功能完善中");
            }
        });
        helper.addOnClickListener(R.id.item_device_edit);
        helper.addOnClickListener(R.id.item_device_delete);

    }
}
