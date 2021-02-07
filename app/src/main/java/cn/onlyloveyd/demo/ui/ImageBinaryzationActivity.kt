package cn.onlyloveyd.demo.ui

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import cn.onlyloveyd.demo.R
import cn.onlyloveyd.demo.databinding.ActivityImageBinaryzationBinding
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.imgproc.Imgproc

/**
 * 图像二值化
 * author: yidong
 * 2020/2/12
 * 图像二值化（ Image Binarization）就是将图像上的像素点的灰度值设置为0或255，也就是将整个图像呈现出明显的黑白效果的过程。
 * 在数字图像处理中，二值图像占有非常重要的地位，图像的二值化使图像中数据量大为减少，从而能凸显出目标的轮廓。
 */
class ImageBinaryzationActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityImageBinaryzationBinding
    private lateinit var mGray: Mat
    private var mType = -1;
    private var mBinarizationValue: Double = 127.toDouble()
        set(value) {
            field = value
            if (this.mType != -1) {
                threshold(mType)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_image_binaryzation)
        val bgr = Utils.loadResource(this, R.drawable.lena)
        mGray = Mat()
        Imgproc.cvtColor(bgr, mGray, Imgproc.COLOR_BGR2GRAY)
        showMat(mGray)
        title = "Gray"
        bgr.release()

        mBinding.seekBar.progress = (mBinarizationValue / 255f * 100f).toInt()
        mBinding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                mBinarizationValue = (progress / 100f * 255f).toDouble()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_binaryzation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        title = item.title
        when (item.itemId) {
            R.id.binary -> binary()
            R.id.binary_inv -> binaryInv()
            R.id.trunc -> trunc()
            R.id.tozero -> toZero()
            R.id.tozero_inv -> toZeroInv()
            R.id.otsu -> otsu()
            R.id.triangle -> triangle()
            R.id.otsu_binary_inv -> otsuBinaryInv()
            R.id.triangle_binary_inv -> triangleBinaryInv()
            R.id.adaptive_mean -> adaptiveMean()
            R.id.adaptive_gaussian -> adaptiveGaussian()
        }
        return true
    }

    private fun showMat(source: Mat) {
        val bitmap = Bitmap.createBitmap(source.width(), source.height(), Bitmap.Config.ARGB_8888)
        Utils.matToBitmap(source, bitmap)
        mBinding.ivLena.setImageBitmap(bitmap)
    }

    private fun threshold(type: Int) {
        mType = type
        val ret = Mat()
        Imgproc.threshold(mGray,    //待二值化的多通道图像，只能是CV_8U和CV_32F两种数据类型
                ret,                //二值化后的图像，与输入图像具有相同的尺寸、类型和通道数
                mBinarizationValue, //二值化的阈值
                255.toDouble(),     //二值化过程的最大值，此函数只在THRESH_BINARY和THRESH_BINARY_INV两种二值化方法中才使用
                type)               //二值化类型
        showMat(ret)
        title = getTypeName(type)
    }

    private fun binary() {
        threshold(Imgproc.THRESH_BINARY)
    }

    private fun binaryInv() {
        threshold(Imgproc.THRESH_BINARY_INV)
    }

    private fun trunc() {
        threshold(Imgproc.THRESH_TRUNC)
    }

    private fun toZero() {
        threshold(Imgproc.THRESH_TOZERO)
    }

    private fun toZeroInv() {
        threshold(Imgproc.THRESH_TOZERO_INV)
    }

    private fun otsu() {
        threshold(Imgproc.THRESH_OTSU)
    }

    private fun triangle() {
        threshold(Imgproc.THRESH_TRIANGLE)
    }

    private fun otsuBinaryInv() {
        threshold(Imgproc.THRESH_OTSU or Imgproc.THRESH_BINARY_INV)
    }

    private fun triangleBinaryInv() {
        threshold(Imgproc.THRESH_TRIANGLE or Imgproc.THRESH_BINARY_INV)
    }

    private fun adaptiveMean() {
        val ret = Mat()
        Imgproc.adaptiveThreshold(
            mGray,
            ret,
            255.toDouble(),
            Imgproc.ADAPTIVE_THRESH_MEAN_C,
            Imgproc.THRESH_BINARY,
            55,
            0.0
        )
        showMat(ret)
        title = "ADAPTIVE_THRESH_MEAN_C"
    }

    private fun adaptiveGaussian() {
        val ret = Mat()
        Imgproc.adaptiveThreshold(
            mGray,
            ret,
            255.toDouble(),
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY,
            55,
            0.0
        )
        showMat(ret)
        title = "ADAPTIVE_THRESH_GAUSSIAN_C"
    }

    private fun getTypeName(type: Int): String {
        return when (type) {
            0 -> "THRESH_BINARY"
            1 -> "THRESH_BINARY_INV"
            2 -> "THRESH_TRUNC"
            3 -> "THRESH_TOZERO"
            4 -> "THRESH_TOZERO_INV"
            7 -> "THRESH_MASK"
            8 -> "THRESH_OTSU"
            16 -> "THRESH_TRIANGLE"
            Imgproc.THRESH_OTSU or Imgproc.THRESH_BINARY_INV -> "THRESH_OTSU | THRESH_BINARY_INV"
            Imgproc.THRESH_TRIANGLE or Imgproc.THRESH_BINARY_INV -> "THRESH_TRIANGLE | THRESH_BINARY_INV"
            else -> ""
        }
    }
}