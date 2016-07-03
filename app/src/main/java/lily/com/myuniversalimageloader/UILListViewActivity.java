package lily.com.myuniversalimageloader;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.PauseOnScrollListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/**
 * Created by Administrator on 2016/6/26.
 */
public class UILListViewActivity extends Activity {

    private ImageLoader imageLoader = ImageLoader.getInstance();
    private ListView listView = null;

    private String TAG = "mytest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uil_listview);


        listView = (ListView)findViewById(R.id.activity_uil_listview);

        listView.setAdapter(new MyAdapter(Constants.images));

        /**
         * 3个参数->
         * 1:图片加载对象ImageLoader,
         * 2:控制是否在滑动过程中暂停加载图片，如果需要暂停传true就行了
         * 3:控制猛的滑动界面的时候图片是否加载
         *
         */
        listView.setOnScrollListener(new PauseOnScrollListener(imageLoader,false,true));

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //回收该页面缓存在内存的图片
        imageLoader.clearDiskCache();
    }

    private class MyAdapter extends BaseAdapter{

        private String[] imageUrls;

        public MyAdapter(String[] imageUrls) {
            this.imageUrls = imageUrls;
        }

        @Override
        public int getCount() {
            return imageUrls.length;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View mView = convertView;
            ViewHolder holder = null;

            Log.d(TAG, "getview");

            if (convertView == null){
                mView = getLayoutInflater().inflate(R.layout.activity_uil_listview_item,parent,false);
                holder = new ViewHolder();
                holder.myImageView = (ImageView)mView.findViewById(R.id.activity_uil_listview_item_imageview);
                mView.setTag(holder);
            }else{
                holder = (ViewHolder) mView.getTag();
            }

            Log.d(TAG, "url:" + imageUrls[position]);

            imageLoader.displayImage(imageUrls[position], holder.myImageView, ImageLoaderHelper.getInstance(getApplicationContext()).getSimpleDisplayImageOptions(),
                    new SimpleImageLoadingListener() {
                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {
                            super.onLoadingCancelled(imageUri, view);
                            Log.d(TAG, "onLoadingCancelled");
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            super.onLoadingComplete(imageUri, view, loadedImage);
                            Log.d(TAG, "onLoadingComplete");
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            super.onLoadingFailed(imageUri, view, failReason);
                            Log.d(TAG, "onLoadingFailed");
                        }

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            super.onLoadingStarted(imageUri, view);
                            Log.d(TAG, "onLoadingStarted");
                        }
                    });


            return mView;
        }
    }
}
