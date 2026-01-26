# 忽略警告
#-ignorewarning

# 混淆保护自己项目的部分代码以及引用的第三方jar包
#-libraryjars libs/xxxxxxxxx.jar

# 不混淆这个包下的类
-keep class com.hjq.demo.http.api.** {
    <fields>;
}
-keep class com.hjq.demo.http.response.** {
    <fields>;
}
-keep class com.hjq.demo.http.model.** {
    <fields>;
}

# 不混淆被 Log 注解的方法信息
-keepclassmembernames class ** {
    @com.hjq.demo.aop.Log <methods>;
}
-keep public class com.alibaba.android.arouter.routes.**{*;}
-keep public class com.alibaba.android.arouter.facade.**{*;}
-keep class * implements com.alibaba.android.arouter.facade.template.ISyringe{*;}

# 如果使用了 byType 的方式获取 Service，需添加下面规则，保护接口
-keep interface * implements com.alibaba.android.arouter.facade.template.IProvider

# 如果使用了 单类注入，即不定义接口实现 IProvider，需添加下面规则，保护实现
# -keep class * implements com.alibaba.android.arouter.facade.template.IProvider


-keep class androidx.renderscript.** { *; }
-keep class androidx.renderscript.** {
    <fields>;
}
# 混淆所有自定义类（保留系统类）
-keep class android.** { *; }
-keep class androidx.** { *; }
# 不保留任何类名、方法名、变量名
-dontkeepnames
-keepparameternames
# 混淆程度最大化
-optimizationpasses 5
# 混淆时应用大小写混合（增加可读性难度）
-mixcaseclassnames