package lily.com.myuniversalimageloader;

import android.content.Context;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

/**
 * Created by Administrator on 2016/6/26.
 */
public class ImageLoaderHelper {

    private Context mContext = null;
    private static volatile ImageLoaderHelper instance = null;

    private ImageLoaderHelper(Context mContext) {
        this.mContext = mContext;
    }

    public static ImageLoaderHelper getInstance(Context context){
        if (null == instance){
            synchronized (ImageLoaderHelper.class){
                if (null == instance){
                    instance = new ImageLoaderHelper(context);
                }
            }
        }

        return instance;
    }

    /**
     * 图片显示默认配置
     */
    public DisplayImageOptions getDefaultDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .showImageOnFail(R.color.default_image_background)
                .showImageOnFail(R.color.default_image_background)
                .showImageForEmptyUri(R.color.default_image_background)
                .cacheInMemory(false)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .cacheOnDisk(false)
                .considerExifParams(false)
                .build();
    }

    /**
     * 显示图片的所有配置
     */
    public DisplayImageOptions getWholeDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.color.default_image_background)//设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(R.color.default_image_background)//设置图片加载/解码过程中错误时候显示的图片
                .showImageOnLoading(R.color.default_image_background)//设置图片在下载期间显示的图片
                .cacheInMemory(true)//设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)//设置下载的图片是否缓存在SD卡中
                .considerExifParams(true) //是否考虑JPEG图像EXIF参数（旋转，翻转）
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)//设置图片以如何的编码方式显示(缩放方式)IN_SAMPLE_INT:图像将被二次采样的整数倍
                .bitmapConfig(Bitmap.Config.RGB_565)//设置图片的解码类型
                .delayBeforeLoading(0)//int delayInMillis为你设置的下载前的延迟时间
                .resetViewBeforeLoading(true)//设置图片在下载前是否重置，复位
                .displayer(new RoundedBitmapDisplayer(80))//不推荐用！！！！是否设置为圆角，弧度为多少
                .displayer(new FadeInBitmapDisplayer(100))//是否图片加载好后渐入的动画时间，可能会出现闪动
                .build();//构建完成
    }

    /**
     * 显示图片的常用配置
     */
    public DisplayImageOptions getSimpleDisplayImageOptions(){
        return new DisplayImageOptions.Builder()
                .showImageForEmptyUri(R.color.default_image_background)//这里最好自定义
                .showImageOnFail(R.color.default_image_background)
                .showImageOnLoading(R.color.default_image_background)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                .bitmapConfig(Bitmap.Config.RGB_565)//默认是ARGB_8888，使用RGB_565会比使用ARGB_8888少消耗2倍的内存
                //.displayer(new RoundedBitmapDisplayer(100))//不推荐用  注意！！！！
                .build();
    }


    /**
     * 所有的配置参数举例
     */
    public ImageLoaderConfiguration getWholeImageLoaderConfig(){
        //设置缓存的路径
        File cacheDir = StorageUtils.getOwnCacheDirectory(mContext, "ImageLoader/Cache");

        //配置ImageLoader
        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration
                .Builder(mContext)
                //设置内存缓存的大小、缓存策略等
                .memoryCacheExtraOptions(480,800)//即保存的每个缓存文件的最大长宽  default:480,800
                .memoryCache(new WeakMemoryCache())//缓存策略  可自定义，这里用弱引用----缺点：太容易被回收  default:LruMemoryCache(2 * 1024 * 1024)
                .memoryCacheSize(2 * 1024 * 1024)//内存缓存的大小   2M  default:2 * 1024 * 1024
                        //设置磁盘缓存的路径、文件名加密方式、大小、数量等
                .diskCacheSize(50 * 1024 * 1024)//磁盘缓存的大小   50M  default:50 * 1024 * 1024
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密  default:HashCodeFileNameGenerator()
                .diskCacheFileCount(100)//缓存的文件数量 default:100
                .diskCache(new UnlimitedDiskCache(cacheDir))//设置磁盘缓存策略，自定义缓存路径 default:UnlimitedDiskCache
                        //图片加载超时时间设置
                .imageDownloader(new BaseImageDownloader(mContext, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                        //线程池数量和线程优先级
                .threadPoolSize(3)//线程池内加载的数量 default:3
                .threadPriority(Thread.NORM_PRIORITY - 2) //default:Thread.NORM_PRIORITY - 1
                .tasksProcessingOrder(QueueProcessingType.FIFO)   //default:FIFO
                        //解释：当同一个Uri获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
                .denyCacheImageMultipleSizesInMemory() //拒绝缓存多个图片。  default
                .writeDebugLogs()//打开调试日志
                .build();//开始构建

        return configuration;
    }

    /**
     * 比较常用的配置方案
     */
    public ImageLoaderConfiguration getSimpleImageLoaderConfig(){
        //设置缓存的路径
        File cacheDir = StorageUtils.getOwnCacheDirectory(mContext,"ImageLoader/Cache");

        ImageLoaderConfiguration configuration = new ImageLoaderConfiguration.Builder(mContext)
                .memoryCache(new LRULimitedMemoryCache(40*1024*1024))//使用LRU算法，和LruMemoryCache不同，它缓存的是bitmap的弱引用
                .memoryCacheSize(50*1024*1024)//内存缓存大小  50M
                .memoryCacheExtraOptions(480,800)//缓存文件的最大宽和长
                .diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                .diskCacheSize(200*1024*1024)//200M
                .diskCacheFileCount(200)
                .denyCacheImageMultipleSizesInMemory()
                .tasksProcessingOrder(QueueProcessingType.LIFO)  //后进先出
                .writeDebugLogs()//发行版app中需要remove
                .build();

        return configuration;
    }
}
