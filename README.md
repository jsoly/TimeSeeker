# TimeSeeker
## 视频时间轴拖拽进度条
由于公司项目需求，要做一个视频历史录像进度拖动的控件。 

![image](https://github.com/jsoly/TimeSeeker/blob/master/demo.gif)

![image](https://github.com/jsoly/TimeSeeker/blob/master/img2.png)

依赖方法：
``` 
allprojects {
repositories {
...
  maven { url 'https://jitpack.io' }
}
}
dependencies {
  compile 'com.github.jsoly:timeseeker:v1.0'
}
```
## 使用方法
* 在xml添加布局
```
<d.cityaurora.com.libtimeseeker.v.TimeSeeker
        android:id="@+id/mTimeSeeker"
        android:layout_centerInParent="true"
        android:layout_width="match_parent"
        android:layout_height="200dp" />
```

* 还可以自定义颜色

![image](https://github.com/jsoly/TimeSeeker/blob/master/img1.png)
```
 <d.cityaurora.com.libtimeseeker.v.TimeSeeker
        android:id="@+id/mTimeSeeker"
        app:textColor="#1c3248"
        app:clockColor="#86294e"
        app:textBackgroundColor="#8abe57"
        app:dataColor="@color/colorAccent"
        android:layout_centerInParent="true"
        android:layout_width="match_parent"
        android:layout_height="200dp" />
```
* MainActivity
```
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private TimeSeeker mTimeSeeker;
    private TextView mText;
    private void init() {
        mText = (TextView) findViewById(R.id.mText);
        mTimeSeeker = (TimeSeeker) findViewById(R.id.mTimeSeeker);
        //监听回调实例1 看自己喜欢选择
        mTimeSeeker.setOnTimeChangeLinstener(new TimeSeeker.OnTimeChangeLinstener() {
            @Override
            public void onChangeStart(TimeSeeker.SeekerTime seekerTime) {

            }

            @Override
            public void onChanging(TimeSeeker.SeekerTime seekerTime) {
                initData(seekerTime);
            }

            @Override
            public void onChangeFinish(TimeSeeker.SeekerTime seekerTime) {
                initData(seekerTime);
            }
        });
        //监听回调实例2 看自己喜欢选择
//        mTimeSeeker.setOnTimeChangeLinstener(new SimpleChangeLinstener() {
//            @Override
//            public void onChangeFinish(TimeSeeker.SeekerTime seekerTime) {
//                initData(seekerTime);
//            }
//        });

        //添加数据（可选）
        mTimeSeeker.addData(new SeekerData(100,600));
        mTimeSeeker.addData(new SeekerData(1000,2345));
        mTimeSeeker.addData(new SeekerData(3000,7890));
        mTimeSeeker.addData(new SeekerData(8888,33333));
        mTimeSeeker.addData(new SeekerData(45678,78910));

        //配合定时器使用样例（可选）
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                mTimeSeeker.secondpp();//时间加一秒
            }
        },1000,10);
    }


    void initData(TimeSeeker.SeekerTime seekerTime){
        int second = seekerTime.getSecond();
        String secondStr = seekerTime.getSecondStr();
        mText.setText("当前秒数："+second +"\n当前时间为："+secondStr);
    }
 }
 ```
    
###END
    
    
