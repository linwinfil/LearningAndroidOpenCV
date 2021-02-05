package cn.onlyloveyd.demo.ui

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.onlyloveyd.demo.R
import cn.onlyloveyd.demo.databinding.ActivityContrastBrightnessBinding
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

/**
 * 调整对比度和亮度
 * author: yidong
 * 2020/1/29
 */
class ContrastBrightnessActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ContrastBrightnessActivity"
    }
    private lateinit var mBinding: ActivityContrastBrightnessBinding
    private lateinit var source: Mat

    private var originBrightness: Double = 0.0

    private var brightness: Double = 0.0
        set(value) {
            field = value
            adjustBrightnessContrast()
        }
    private var contrast: Double = 100.0
        set(value) {
            field = value
            adjustBrightnessContrast()
        }

    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_contrast_brightness)
        val bgr = Utils.loadResource(this, R.drawable.kobe)

        source = Mat()
        Imgproc.cvtColor(bgr, source, Imgproc.COLOR_BGR2RGB)
        val bitmap = Bitmap.createBitmap(source.width(), source.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(source, bitmap)
        mBinding.ivSource.setImageBitmap(bitmap)
        originBrightness = Core.mean(source).`val`[0]
        Log.i(TAG, "originBrightness:${originBrightness}")
        mBinding.sbBrightness.progress = originBrightness.toInt()
        mBinding.sbBrightness.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                brightness = progress.toDouble()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        mBinding.sbContrast.progress = 100
        mBinding.sbContrast.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                contrast = progress.toDouble()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    //调整图像亮度和对比度属于像素变换-点操作
    //g(i,j) = αf(i,j) +β （其中 α>0 ，α 增益（放大倍数），用来控制图像的对比度，β （偏置），用控制图像的亮度。
    //操作方式：
    //像素值加法、像素值乘法
    @SuppressLint("LongLogTag")
    private fun adjustBrightnessContrast() {
        val pre = Mat()
        val newBrightness = brightness - originBrightness
        Core.add(
            source,
            Scalar(
                newBrightness,
                newBrightness,
                newBrightness
            ),
            pre
        )
        val dst = Mat()
        val newContrast = contrast / 100
        Core.multiply(
            pre,
            Scalar(
                newContrast,
                newContrast,
                newContrast,
                newContrast
            ),
            dst
        )
        Log.i(TAG, "newContrast:$newContrast, newBrightness:$newBrightness")
        val bitmap = Bitmap.createBitmap(dst.cols(), dst.rows(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(dst, bitmap)
        mBinding.ivSource.setImageBitmap(bitmap)
        pre.release()
        dst.release()
    }

    override fun onDestroy() {
        source.release()
        super.onDestroy()
    }
}