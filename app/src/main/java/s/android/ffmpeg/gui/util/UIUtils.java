package s.android.ffmpeg.gui.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

/**
 * 作者： CodingMates
 * 创建时间： 2015/2/6 10:34.
 */
public class UIUtils {

    public static Bitmap createCircleImage(Bitmap bitmap, int minWidth) {
        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        Bitmap target = Bitmap.createBitmap(minWidth, minWidth, Bitmap.Config.ARGB_8888);
        /**
         * 产生一个同样大小的画布
         */
        Canvas canvas = new Canvas(target);
        /**
         * 首先绘制圆形
         */
        canvas.drawCircle(minWidth / 2, minWidth / 2, minWidth / 2, paint);
        /**
         * 使用SRC_IN
         */
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        /**
         * 绘制图片
         */
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return target;
    }
}
