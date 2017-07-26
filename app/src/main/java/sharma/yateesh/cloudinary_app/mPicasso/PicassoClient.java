package sharma.yateesh.cloudinary_app.mPicasso;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import sharma.yateesh.cloudinary_app.R;

/**
 * Created by welcome on 24-07-2017.
 */

public class PicassoClient {

    public static void downloadImage(Context c, String url, ImageView img) {
        if (url != null && url.length() > 0) {
            Picasso.with(c).load(url).placeholder(R.drawable.p1).into(img);
        } else {
            Picasso.with(c).load(R.drawable.p1).into(img);
        }

    }
}
