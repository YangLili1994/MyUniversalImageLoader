package lily.com.myuniversalimageloader;

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

/**
 * Created by Administrator on 2016/6/26.
 */
public class UILViewPagerActivity extends Activity {

    ViewPager mViewPager = null;
    ImageLoader imageLoader;

    String TAG = "mytestaaa";

    Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            Log.d(TAG, "have receive a msg");
            int curindex = (mViewPager.getCurrentItem()+1)%(Constants.images.length+2);
            mViewPager.setCurrentItem(curindex,true);
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
        setContentView(R.layout.activity_uil_viewpager);

        imageLoader = ImageLoader.getInstance();
        mViewPager = (ViewPager) findViewById(R.id.image_viewpager);

        mViewPager.setAdapter(new ImagePagerAdapter(Constants.images));
        mViewPager.setCurrentItem(0);


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            /**
             * 当页面在滑动了调用
             * @param position 当前页面，即点击滑动的页面
             * @param positionOffset 当前页面偏移的百分比
             * @param positionOffsetPixels 当前页面偏移的像素位置
             */
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position == Constants.images.length && positionOffset > 0.99) {
                    //在position4左滑且左滑positionOffset百分比接近1时，偷偷替换为position1（原本会滑到position5）
                    mViewPager.setCurrentItem(1, false);
                } else if (position == 0 && positionOffset < 0.01) {
                    //在position1右滑且右滑百分比接近0时，偷偷替换为position4（原本会滑到position0）
                    mViewPager.setCurrentItem(4, false);
                }
            }


            /**
             * This method will be invoked when a new page becomes selected. Animation is not
             * necessarily complete.   一般在滑动30%的时候就会调用
             *
             * @param position Position index of the new selected page.
             */

            @Override
            public void onPageSelected(int position) {
                //当有手动操作时，remove掉之前auto的runnable。延迟将由手动的这次决定。
                //总之，一个页面selected之后  最多只有一个runnable，要把多的remove掉
                handler.removeCallbacks(runnable);
                Log.d(TAG, "onPageSelected，page:" + position);
                if (position != Constants.images.length+1 && position != 0){
                    handler.postDelayed(runnable,3*1000);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case 0://什么都没做  空闲状态
                        break;
                    case 1://正在滑动
                        break;
                    case 2://滑动完毕
                        break;
                }
            }
        });
        handler.postDelayed(runnable, 3 * 1000);
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();

        //回收该页面缓存在内存的图片
        imageLoader.clearDiskCache();
    }

    private class ImagePagerAdapter extends PagerAdapter{
        private String[] images;
        private LayoutInflater inflater;

        public ImagePagerAdapter(String[] images) {
            this.images = images;
            this.inflater = getLayoutInflater();
        }

        @Override
        public int getCount() {
            //返回实际要显示的图片数+2
            return images.length + 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //注意不要remove  否则容易闪屏
            //  ((ViewPager)container).removeView((View) object);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            View mView = View.inflate(getApplicationContext(),R.layout.activity_uil_viewpager_item,null);
            //这是重点
            int realPosition = (position - 1 + images.length)%images.length;

            ImageView imageView = (ImageView) mView.findViewById(R.id.myimage);
            final ProgressBar bar = (ProgressBar) mView.findViewById(R.id.loading);

            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG,"hahaha  click");
                }
            });

            //通过UIL加载图片
            imageLoader.displayImage(images[realPosition], imageView, ImageLoaderHelper.getInstance(getApplicationContext()).getSimpleDisplayImageOptions(),
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
                            switch (failReason.getType()){//加载失败类型
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

                            bar.setVisibility(View.GONE);
                        }
                    }, new ImageLoadingProgressListener() {
                        @Override
                        public void onProgressUpdate(String s, View view, int i, int i1) {//在这里更新 ProgressBar的进度信息

                            int progress = 100*i/i1;
                            bar.setProgress(progress);

                        }
                    });

            ((ViewPager)container).addView(mView,0);
            return mView;
        }
    }
}
