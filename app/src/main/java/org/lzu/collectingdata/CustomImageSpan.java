package org.lzu.collectingdata;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.DrawableRes;
import android.support.v4.content.ContextCompat;

import java.io.InputStream;

public class CustomImageSpan extends AbsDrawableSpan {
    private Drawable mDrawable;
    private Uri mContentUri;
    private int mResourceId;
    private Context mContext;

    CustomImageSpan(Context context, Bitmap b) {
        super();
        mContext = context;
        mDrawable = context != null
                ? new BitmapDrawable(context.getResources(), b)
                : new BitmapDrawable(b);
        int width = mDrawable.getIntrinsicWidth();
        int height = mDrawable.getIntrinsicHeight();
        mDrawable.setBounds(0, 0, width > 0 ? width : 0, height > 0 ? height : 0);
    }

    CustomImageSpan(Drawable d) {
        super();
        mDrawable = d;
    }

    CustomImageSpan(Context context, Uri uri) {
        super();
        mContext = context;
        mContentUri = uri;
    }

    CustomImageSpan(Context context, @DrawableRes int resourceId) {
        super();
        mContext = context;
        mResourceId = resourceId;
    }

    @Override
    public Drawable getDrawable() {
        Drawable drawable = null;
        if (mDrawable != null) {
            drawable = mDrawable;
        } else if (mContentUri != null) {
            Bitmap bitmap;
            try {
                InputStream is = mContext.getContentResolver().openInputStream(
                        mContentUri);
                bitmap = BitmapFactory.decodeStream(is);
                drawable = new BitmapDrawable(mContext.getResources(), bitmap);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
                if (is != null) {
                    is.close();
                }
            } catch (Exception e) {
            }
        } else {
            try {
                drawable = ContextCompat.getDrawable(mContext, mResourceId);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight());
            } catch (Exception e) {
            }
        }
        return drawable;
    }
}