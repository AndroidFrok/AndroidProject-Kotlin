import android.content.Context
import timber.log.Timber
import java.io.File
import java.util.Calendar

class TimberFile(ctx: Context) : Timber.Tree() {
    private val context = ctx;
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        val second = calendar.get(Calendar.SECOND)
        val stb = StringBuilder();
        stb.append("f_$year 年")
        stb.append("$month 月")
        stb.append("$dayOfMonth 日")
        stb.append("$hour 时")
        stb.append("$minute 分")
//        stb.append("$second 秒")
        stb.append(".txt")
        val fileName = stb.toString()
        val f = File(context.getExternalFilesDir(null), fileName);
        if (!f.exists()) {
            f.createNewFile();
//            Timber.d("创建文件成功")
        } else {
//            Timber.d("无需创建")
        }
        f.appendText("第$second 秒$tag-- $message \n")

//        Timber.w("tag--:$tag--");// + f.path

    }
}