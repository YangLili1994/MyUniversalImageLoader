package lily.com.myuniversalimageloader.MyView;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import lily.com.myuniversalimageloader.ImageLoaderHelper;
import lily.com.myuniversalimageloader.R;

/**
 * Created by Administrator on 2016/7/2.
 */
public class MyViewPagerActivity  extends Activity{

    private LoopViewPager mLoopViewPager = null;

    private ImageLoader imageLoader = ImageLoader.getInstance();

    private String TAG = "test_mypager";

    private Boolean isTouched = false;

    String[] images = {
            "http://pic28.nipic.com/20130420/10753400_140144592172_2.jpg",
            "http://pic.58pic.com/58pic/14/04/92/35V58PICBgS_1024.jpg",
            "http://pic28.nipic.com/20130415/2005877_192727416000_2.jpg",
            "http://img0.imgtn.bdimg.com/it/u=1153677277,1840182388&fm=21&gp=0.jpg"
    };

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG,"have received a msg");
            mLoopViewPager.setCurrentItem(mLoopViewPager.getCurrentItem()+1,true);
        }
    };

   Runnable runnable = new Runnable() {
       @Override
       public void run() {
           Message message = new Message();
           handler.sendMessage(message);
       }
   };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_viewpager);

        mLoopViewPager = (LoopViewPager)findViewById(R.id.activity_my_viewpager_loopviewpager);
        mLoopViewPager.setAdapter(new ImagePagerAdapter(images));

        mLoopViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                isTouched = true;
            }

            @Override
            public void onPageSelected(int position) {
                handler.removeCallbacks(runnable);
                Log.d(TAG, "page selected position:" + position);
                if (position != 5 && position != 0) {
                    isTouched = false;
                    handler.postDelayed(runnable, 3 * 1000);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                isTouched = true;
            }
        });

        handler.postDelayed(runnable,3*1000);

//        mLoopViewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_MOVE:
//                    case MotionEvent.ACTION_DOWN:
//                        Log.d(TAG, "there is a touch");
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        Log.d(TAG, "there is a up");
//                }
//                return false;
//            }
//        });

    }

    private class ImagePagerAdapter extends PagerAdapter {
        private String[] images;
        private LayoutInflater inflater;

        public ImagePagerAdapter(String[] images) {
            this.images = images;
            this.inflater = getLayoutInflater();

        }

        @Override
        public int getCount() {
            //return Integer.MAX_VALUE;
             return images.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //  ((ViewPager)container).removeView((View) object);
        }


        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            //View mView = inflater.inflate(R.layout.activity_uil_viewpager_item,container,false);
            View mView = View.inflate(getApplicationContext(), R.layout.activity_uil_viewpager_item, null);

            ImageView imageView = (ImageView) mView.findViewById(R.id.myimage);
            final ProgressBar bar = (ProgressBar) mView.findViewById(R.id.loading);

            Log.d("mytest", "position:" + position);


            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "hahaha  click");
                }
            });


            imageLoader.displayImage(images[position % images.length], imageView, ImageLoaderHelper.getInstance(getApplicationContext()).getSimpleDisplayImageOptions(),
                    new SimpleImageLoadingListener() {

                        @Override
                        public void onLoadingStarted(String imageUri, View view) {//开始加载的时候执行
                            bar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {//加载成功的时候执行
                            bar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {//加载取消的时候执行
                            super.onLoadingCancelled(imageUri, view);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {//加载失败的时候执行
                            String message = null;
                            switch (failReason.getType()) {//加载失败类型
                                case IO_ERROR:// 文件I/O错误
                                    message = "IO_ERROR";
                                    break;
                                case DECODING_ERROR:// 解码错误
                                    message = "DECODING_ERROR";
                                    break;
                                case NETWORK_DENIED:// 网络延迟
                                    message = "NETWORK_DENIED";
                                    break;
                                case OUT_OF_MEMORY:// 内存不足
                                    message = "OUT_OF_MEMORY";
                                    break;
                                case UNKNOWN:// 原因不明
                                default:
                                    message = "UNKNOWN";
                                    break;
                            }

                            //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();

                            bar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String s, View view, int i, int i1) {//在这里更新 ProgressBar的进度信息

                            int progress = 100 * i / i1;
                            //Log.d(TAG,"progress:"+progress+"%");
                            bar.setProgress(progress);

                        }
                    });

            ((ViewPager) container).addView(mView, 0);
            return mView;
        }
    }
}
