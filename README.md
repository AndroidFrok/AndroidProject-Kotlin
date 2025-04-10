# 安卓技术中台 Kotlin 版

# api19 / android4.4 特供分支
1. 分支api18 不可合并到其他分支，只能将主分支上的`新特性`合并到分支api18 

# fork编者 注
1. 文本显示直接用 SmartTextView  方便处理null等值的过滤      aaaaaaaaaaaaaaa
2. 物联网应用如果是横屏直接在land文件夹放xml
3. 物联网应用在首次安装，主界面里就应该申请到所有权限，避免后面无人值守 无法操作。

## 分支简述

0 .`getactivity` 推荐指数 ☆   仅供与原仓库贡献用 ，主要由原作者LTS   
1.`api19-gradle7` 推荐指数☆ 适用于暂时不想升级到gradle8的情况使用 ，不再更新 , api19+   
2.`api19-g8`**推荐指数☆☆☆☆☆ LTS 推荐使用 基于分支1  api19+**  
3.`master` 最新  
4.`iot` 测试分支 工控屏专用分支，android api等级尽量低 ，减少工作量   
5.`applib` 推荐指数☆ 基于分支0 api21+  

#### 用过的好用的东西

1- 带有全屏的视频播放器   https://github.com/CarGuo/GSYVideoPlayer    'com.github.xiaoyanger0825:
NiceVieoPlayer:v2.2'

2- DialogX ,com.blankj:utilcode ,eventbus ,io.github.youth5201314:
banner ,https://bugly.qq.com/docs/user-guide/instruction-manual-android/?v=20190418140644  , timber ,
in.xiandan:count-down-timer ,图片视频混合轮播 https://github.com/AndroidFrok/XBanner,

3- // 动画解析库：https://github.com/airbnb/lottie-android
// 动画资源：https://lottiefiles.com
、https://icons8.com/animated-icons https://www.iconfont.cn/lotties/index?spm=a313x.home_index.i1.4.58a33a8144qZSU

# 原作者注

* 详见 项目地址：[Github](https://github.com/getActivity/AndroidProject-Kotlin)
  、[码云](https://gitee.com/getActivity/AndroidProject-Kotlin)

* Java 版本：[AndroidProject](https://github.com/getActivity/AndroidProject)

* 博客地址：[但愿人长久，搬砖不再有](https://www.jianshu.com/p/77dd326f21dc)

* 另外你如果对 Kotlin 不熟悉，恰好想学习的话，推荐你通过下面这三篇文章来学习

    * [全民 Kotlin：Java我们不一样](https://www.jianshu.com/p/a01e6b957269)

    * [全民 Kotlin：你没有玩过的全新玩法](https://www.jianshu.com/p/884ca0a49e5e)

    * [全民 Kotlin：协程特别篇](https://www.jianshu.com/p/2e0746c7d4f3)

## License

```text
Copyright 2018 Huang JinQun

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```