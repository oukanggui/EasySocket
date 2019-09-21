package com.baymax.base.image;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.example.base.R;

/**
 * @author Baymax
 * @date 2019-09-20
 * 描述：图片加载器
 */
public class ImageLoader {
    public static void loadImage(Context context, String url, ImageView imageView) {
        String updateTime = String.valueOf(System.currentTimeMillis());
        Glide.with(context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .crossFade()
                .centerCrop()
                .placeholder(R.mipmap.ic_default_placeholder)
                .error(R.mipmap.ic_default_placeholder)
                .signature(new StringSignature(updateTime))
                .into(imageView);
    }

}
