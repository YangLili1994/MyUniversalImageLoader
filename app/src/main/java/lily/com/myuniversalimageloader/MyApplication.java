package lily.com.myuniversalimageloader;

import android.app.Application;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by Administrator on 2016/6/25.
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        ImageLoader.getInstance().init((ImageLoaderHelper.getInstance(getApplicationContext())).getSimpleImageLoaderConfig());


    }


}
