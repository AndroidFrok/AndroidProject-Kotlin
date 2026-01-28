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

# ========================================================================
# R8 自动生成的缺失规则 (由 missing_rules.txt 导入)
# ========================================================================

# AndroidX 相关警告忽略
-dontwarn androidx.appcompat.graphics.drawable.DrawableWrapper
-dontwarn androidx.databinding.DataBinderMapperImpl
-dontwarn androidx.databinding.DataBindingComponent
-dontwarn androidx.renderscript.Allocation$MipmapControl
-dontwarn androidx.renderscript.Allocation
-dontwarn androidx.renderscript.Element
-dontwarn androidx.renderscript.RenderScript
-dontwarn androidx.renderscript.ScriptIntrinsicBlur
-dontwarn androidx.renderscript.Type

# Gson 内部类
-dontwarn com.google.gson.internal.$Gson$Types

# Java AWT 相关（Android 不支持桌面 Java 图形库）
-dontwarn java.awt.AWTException
-dontwarn java.awt.AlphaComposite
-dontwarn java.awt.BasicStroke
-dontwarn java.awt.Color
-dontwarn java.awt.Composite
-dontwarn java.awt.Desktop
-dontwarn java.awt.Dimension
-dontwarn java.awt.Font
-dontwarn java.awt.FontFormatException
-dontwarn java.awt.FontMetrics
-dontwarn java.awt.Graphics2D
-dontwarn java.awt.Graphics
-dontwarn java.awt.Image
-dontwarn java.awt.Point
-dontwarn java.awt.Rectangle
-dontwarn java.awt.RenderingHints$Key
-dontwarn java.awt.RenderingHints
-dontwarn java.awt.Robot
-dontwarn java.awt.Shape
-dontwarn java.awt.Stroke
-dontwarn java.awt.Toolkit
-dontwarn java.awt.color.ColorSpace
-dontwarn java.awt.datatransfer.Clipboard
-dontwarn java.awt.datatransfer.ClipboardOwner
-dontwarn java.awt.datatransfer.DataFlavor
-dontwarn java.awt.datatransfer.StringSelection
-dontwarn java.awt.datatransfer.Transferable
-dontwarn java.awt.datatransfer.UnsupportedFlavorException
-dontwarn java.awt.font.FontRenderContext
-dontwarn java.awt.geom.AffineTransform
-dontwarn java.awt.geom.Ellipse2D$Double
-dontwarn java.awt.geom.Rectangle2D
-dontwarn java.awt.geom.RoundRectangle2D$Double
-dontwarn java.awt.image.AffineTransformOp
-dontwarn java.awt.image.BufferedImage
-dontwarn java.awt.image.ColorConvertOp
-dontwarn java.awt.image.ColorModel
-dontwarn java.awt.image.CropImageFilter
-dontwarn java.awt.image.DataBuffer
-dontwarn java.awt.image.DataBufferByte
-dontwarn java.awt.image.DataBufferInt
-dontwarn java.awt.image.FilteredImageSource
-dontwarn java.awt.image.ImageFilter
-dontwarn java.awt.image.ImageObserver
-dontwarn java.awt.image.ImageProducer
-dontwarn java.awt.image.RenderedImage
-dontwarn java.awt.image.SampleModel
-dontwarn java.awt.image.WritableRaster

# Java Beans 相关
-dontwarn java.beans.BeanInfo
-dontwarn java.beans.IntrospectionException
-dontwarn java.beans.Introspector
-dontwarn java.beans.PropertyDescriptor
-dontwarn java.beans.PropertyEditor
-dontwarn java.beans.PropertyEditorManager
-dontwarn java.beans.Transient
-dontwarn java.beans.XMLDecoder
-dontwarn java.beans.XMLEncoder

# JMX 管理 API
-dontwarn java.lang.management.ManagementFactory
-dontwarn java.lang.management.RuntimeMXBean

# javax.imageio 相关
-dontwarn javax.imageio.IIOImage
-dontwarn javax.imageio.ImageIO
-dontwarn javax.imageio.ImageReader
-dontwarn javax.imageio.ImageTypeSpecifier
-dontwarn javax.imageio.ImageWriteParam
-dontwarn javax.imageio.ImageWriter
-dontwarn javax.imageio.metadata.IIOMetadata
-dontwarn javax.imageio.stream.ImageInputStream
-dontwarn javax.imageio.stream.ImageOutputStream

# javax.lang.model 相关
-dontwarn javax.lang.model.element.Element
-dontwarn javax.lang.model.element.Modifier

# javax.swing 相关
-dontwarn javax.swing.ImageIcon

# javax.tools 编译器 API
-dontwarn javax.tools.DiagnosticCollector
-dontwarn javax.tools.DiagnosticListener
-dontwarn javax.tools.FileObject
-dontwarn javax.tools.ForwardingJavaFileManager
-dontwarn javax.tools.JavaCompiler$CompilationTask
-dontwarn javax.tools.JavaCompiler
-dontwarn javax.tools.JavaFileManager$Location
-dontwarn javax.tools.JavaFileManager
-dontwarn javax.tools.JavaFileObject$Kind
-dontwarn javax.tools.JavaFileObject
-dontwarn javax.tools.SimpleJavaFileObject
-dontwarn javax.tools.StandardJavaFileManager
-dontwarn javax.tools.StandardLocation
-dontwarn javax.tools.ToolProvider