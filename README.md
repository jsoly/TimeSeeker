# TimeSeeker
##视频时间轴拖拽进度条
由于公司项目需求，要做一个视频历史录像进度拖动的控件。
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
##使用方法
* 在xml添加布局
```
<d.cityaurora.com.libtimeseeker.v.TimeSeeker
        android:id="@+id/mTimeSeeker"
        android:layout_centerInParent="true"
        android:layout_width="match_parent"
        android:layout_height="200dp" />
        ```
