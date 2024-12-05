package com.daimajia.numberprogressbar

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import android.view.View.MeasureSpec
import com.daimajia.numberprogressbar.NumberProgressBar.ProgressTextVisibility
import kotlin.math.max
import kotlin.math.min

/**
 * Created by daimajia on 14-4-30.
 */
open class NumberProgressBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var mMaxProgress = 100

    /**
     * Current progress, can not exceed the max progress.
     */
    private var mCurrentProgress = 0

    /**
     * The progress area bar color.
     */
    private var mReachedBarColor: Int

    /**
     * The bar unreached area color.
     */
    private var mUnreachedBarColor: Int

    /**
     * The progress text color.
     */
    private var mTextColor: Int

    /**
     * The progress text size.
     */
    private var mTextSize: Float

    /**
     * The height of the reached area.
     */
    private var mReachedBarHeight: Float

    /**
     * The height of the unreached area.
     */
    private var mUnreachedBarHeight: Float

    /**
     * The suffix of the number.
     */
    private var mSuffix: String? = "%"

    /**
     * The prefix.
     */
    private var mPrefix: String? = ""


    private val default_text_color = Color.rgb(66, 145, 241)
    private val default_reached_color = Color.rgb(66, 145, 241)
    private val default_unreached_color = Color.rgb(204, 204, 204)
    private val default_progress_text_offset: Float
    private val default_text_size: Float
    private val default_reached_bar_height: Float
    private val default_unreached_bar_height: Float

    /**
     * The width of the text that to be drawn.
     */
    private var mDrawTextWidth = 0f

    /**
     * The drawn text start.
     */
    private var mDrawTextStart = 0f

    /**
     * The drawn text end.
     */
    private var mDrawTextEnd = 0f

    /**
     * The text that to be drawn in onDraw().
     */
    private var mCurrentDrawText: String? = null

    /**
     * The Paint of the reached area.
     */
    private var mReachedBarPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * The Paint of the unreached area.
     */
    private var mUnreachedBarPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * The Paint of the progress text.
     */
    private var mTextPaint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * Unreached bar area to draw rect.
     */
    private val mUnreachedRectF = RectF(0f, 0f, 0f, 0f)

    /**
     * Reached bar area rect.
     */
    private val mReachedRectF = RectF(0f, 0f, 0f, 0f)

    /**
     * The progress text offset.
     */
    private val mOffset: Float

    /**
     * Determine if need to draw unreached area.
     */
    private var mDrawUnreachedBar = true

    private var mDrawReachedBar = true

    private var mIfDrawText = true

    /**
     * Listener
     */
    private var mListener: OnProgressBarListener? = null

    enum class ProgressTextVisibility {
        Visible, Invisible
    }

    init {
        default_reached_bar_height = dp2px(1.5f)
        default_unreached_bar_height = dp2px(1.0f)
        default_text_size = sp2px(10f)
        default_progress_text_offset = dp2px(3.0f)

        //load styled attributes.
        val attributes = context.theme.obtainStyledAttributes(
            attrs, R.styleable.NumberProgressBar,
            defStyleAttr, 0
        )

        mReachedBarColor = attributes.getColor(
            R.styleable.NumberProgressBar_progress_reached_color,
            default_reached_color
        )
        mUnreachedBarColor = attributes.getColor(
            R.styleable.NumberProgressBar_progress_unreached_color,
            default_unreached_color
        )
        mTextColor = attributes.getColor(
            R.styleable.NumberProgressBar_progress_text_color,
            default_text_color
        )
        mTextSize = attributes.getDimension(
            R.styleable.NumberProgressBar_progress_text_size,
            default_text_size
        )

        mReachedBarHeight = attributes.getDimension(
            R.styleable.NumberProgressBar_progress_reached_bar_height,
            default_reached_bar_height
        )
        mUnreachedBarHeight = attributes.getDimension(
            R.styleable.NumberProgressBar_progress_unreached_bar_height,
            default_unreached_bar_height
        )
        mOffset = attributes.getDimension(
            R.styleable.NumberProgressBar_progress_text_offset,
            default_progress_text_offset
        )

        val textVisible = attributes.getInt(
            R.styleable.NumberProgressBar_progress_text_visibility,
            PROGRESS_TEXT_VISIBLE
        )
        if (textVisible != PROGRESS_TEXT_VISIBLE) {
            mIfDrawText = false
        }

        setProgress(attributes.getInt(R.styleable.NumberProgressBar_progress_current, 0))
        setMax(attributes.getInt(R.styleable.NumberProgressBar_progress_max, 100))

        attributes.recycle()
        initializePainters()
    }

    override fun getSuggestedMinimumWidth(): Int {
        return mTextSize.toInt()
    }

    override fun getSuggestedMinimumHeight(): Int {
        return max(
            mTextSize,
            max(mReachedBarHeight, mUnreachedBarHeight)
        ).toInt()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(measure(widthMeasureSpec, true), measure(heightMeasureSpec, false))
    }

    private fun measure(measureSpec: Int, isWidth: Boolean): Int {
        var result: Int
        val mode = MeasureSpec.getMode(measureSpec)
        val size = MeasureSpec.getSize(measureSpec)
        val padding =
            if (isWidth) getPaddingLeft() + getPaddingRight() else paddingTop + paddingBottom
        if (mode == MeasureSpec.EXACTLY) {
            result = size
        } else {
            result = if (isWidth) suggestedMinimumWidth else suggestedMinimumHeight
            result += padding
            if (mode == MeasureSpec.AT_MOST) {
                if (isWidth) {
                    result = max(result, size)
                } else {
                    result = min(result, size)
                }
            }
        }
        return result
    }

    override fun onDraw(canvas: Canvas) {
        if (mIfDrawText) {
            calculateDrawRectF()
        } else {
            calculateDrawRectFWithoutProgressText()
        }

        if (mDrawReachedBar) {
            canvas.drawRect(mReachedRectF, mReachedBarPaint)
        }

        if (mDrawUnreachedBar) {
            canvas.drawRect(mUnreachedRectF, mUnreachedBarPaint)
        }

        if (mIfDrawText) canvas.drawText(
            mCurrentDrawText!!,
            mDrawTextStart,
            mDrawTextEnd,
            mTextPaint
        )
    }

    private fun initializePainters() {
        mReachedBarPaint.setColor(mReachedBarColor)

        mUnreachedBarPaint.setColor(mUnreachedBarColor)

        mTextPaint.setColor(mTextColor)
        mTextPaint.textSize = mTextSize
    }


    private fun calculateDrawRectFWithoutProgressText() {
        mReachedRectF.left = getPaddingLeft().toFloat()
        mReachedRectF.top = height / 2.0f - mReachedBarHeight / 2.0f
        mReachedRectF.right =
            (width - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() + getPaddingLeft()
        mReachedRectF.bottom = height / 2.0f + mReachedBarHeight / 2.0f

        mUnreachedRectF.left = mReachedRectF.right
        mUnreachedRectF.right = (width - getPaddingRight()).toFloat()
        mUnreachedRectF.top = height / 2.0f + -mUnreachedBarHeight / 2.0f
        mUnreachedRectF.bottom = height / 2.0f + mUnreachedBarHeight / 2.0f
    }

    private fun calculateDrawRectF() {
        mCurrentDrawText = String.format("%d", getProgress() * 100 / getMax())
        mCurrentDrawText = mPrefix + mCurrentDrawText + mSuffix
        mDrawTextWidth = mTextPaint.measureText(mCurrentDrawText)

        if (getProgress() == 0) {
            mDrawReachedBar = false
            mDrawTextStart = getPaddingLeft().toFloat()
        } else {
            mDrawReachedBar = true
            mReachedRectF.left = getPaddingLeft().toFloat()
            mReachedRectF.top = height / 2.0f - mReachedBarHeight / 2.0f
            mReachedRectF.right =
                (width - getPaddingLeft() - getPaddingRight()) / (getMax() * 1.0f) * getProgress() - mOffset + getPaddingLeft()
            mReachedRectF.bottom = height / 2.0f + mReachedBarHeight / 2.0f
            mDrawTextStart = (mReachedRectF.right + mOffset)
        }

        mDrawTextEnd =
            ((height / 2.0f) - ((mTextPaint.descent() + mTextPaint.ascent()) / 2.0f)).toInt()
                .toFloat()

        if ((mDrawTextStart + mDrawTextWidth) >= width - getPaddingRight()) {
            mDrawTextStart = width - getPaddingRight() - mDrawTextWidth
            mReachedRectF.right = mDrawTextStart - mOffset
        }

        val unreachedBarStart = mDrawTextStart + mDrawTextWidth + mOffset
        if (unreachedBarStart >= width - getPaddingRight()) {
            mDrawUnreachedBar = false
        } else {
            mDrawUnreachedBar = true
            mUnreachedRectF.left = unreachedBarStart
            mUnreachedRectF.right = (width - getPaddingRight()).toFloat()
            mUnreachedRectF.top = height / 2.0f + -mUnreachedBarHeight / 2.0f
            mUnreachedRectF.bottom = height / 2.0f + mUnreachedBarHeight / 2.0f
        }
    }

    /**
     * Get progress text color.
     *
     * @return progress text color.
     */
    fun getTextColor(): Int {
        return mTextColor
    }

    /**
     * Get progress text size.
     *
     * @return progress text size.
     */
    fun getProgressTextSize(): Float {
        return mTextSize
    }

    fun getUnreachedBarColor(): Int {
        return mUnreachedBarColor
    }

    fun getReachedBarColor(): Int {
        return mReachedBarColor
    }

    fun getProgress(): Int {
        return mCurrentProgress
    }

    fun getMax(): Int {
        return mMaxProgress
    }

    fun getReachedBarHeight(): Float {
        return mReachedBarHeight
    }

    fun getUnreachedBarHeight(): Float {
        return mUnreachedBarHeight
    }

    fun setProgressTextSize(textSize: Float) {
        this.mTextSize = textSize
        mTextPaint.textSize = mTextSize
        invalidate()
    }

    fun setProgressTextColor(textColor: Int) {
        this.mTextColor = textColor
        mTextPaint.setColor(mTextColor)
        invalidate()
    }

    fun setUnreachedBarColor(barColor: Int) {
        this.mUnreachedBarColor = barColor
        mUnreachedBarPaint.setColor(mUnreachedBarColor)
        invalidate()
    }

    fun setReachedBarColor(progressColor: Int) {
        this.mReachedBarColor = progressColor
        mReachedBarPaint.setColor(mReachedBarColor)
        invalidate()
    }

    fun setReachedBarHeight(height: Float) {
        mReachedBarHeight = height
    }

    fun setUnreachedBarHeight(height: Float) {
        mUnreachedBarHeight = height
    }

    fun setMax(maxProgress: Int) {
        if (maxProgress > 0) {
            this.mMaxProgress = maxProgress
            invalidate()
        }
    }

    fun setSuffix(suffix: String?) {
        if (suffix == null) {
            mSuffix = ""
        } else {
            mSuffix = suffix
        }
    }

    fun getSuffix(): String {
        return mSuffix!!
    }

    fun setPrefix(prefix: String?) {
        if (prefix == null) mPrefix = ""
        else {
            mPrefix = prefix
        }
    }

    fun getPrefix(): String {
        return mPrefix!!
    }

    fun incrementProgressBy(by: Int) {
        if (by > 0) {
            setProgress(getProgress() + by)
        }

        mListener?.onProgressChange(getProgress(), getMax())
    }

    fun setProgress(progress: Int) {
        if (progress <= getMax() && progress >= 0) {
            this.mCurrentProgress = progress
            invalidate()
        }
    }

    override fun onSaveInstanceState(): Parcelable {
        val bundle = Bundle()
        bundle.putParcelable(INSTANCE_STATE, super.onSaveInstanceState())
        bundle.putInt(INSTANCE_TEXT_COLOR, getTextColor())
        bundle.putFloat(INSTANCE_TEXT_SIZE, getProgressTextSize())
        bundle.putFloat(INSTANCE_REACHED_BAR_HEIGHT, getReachedBarHeight())
        bundle.putFloat(INSTANCE_UNREACHED_BAR_HEIGHT, getUnreachedBarHeight())
        bundle.putInt(INSTANCE_REACHED_BAR_COLOR, getReachedBarColor())
        bundle.putInt(INSTANCE_UNREACHED_BAR_COLOR, getUnreachedBarColor())
        bundle.putInt(INSTANCE_MAX, getMax())
        bundle.putInt(INSTANCE_PROGRESS, getProgress())
        bundle.putString(INSTANCE_SUFFIX, getSuffix())
        bundle.putString(INSTANCE_PREFIX, getPrefix())
        bundle.putBoolean(INSTANCE_TEXT_VISIBILITY, getProgressTextVisibility())
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            val bundle = state
            mTextColor = bundle.getInt(INSTANCE_TEXT_COLOR)
            mTextSize = bundle.getFloat(INSTANCE_TEXT_SIZE)
            mReachedBarHeight = bundle.getFloat(INSTANCE_REACHED_BAR_HEIGHT)
            mUnreachedBarHeight = bundle.getFloat(INSTANCE_UNREACHED_BAR_HEIGHT)
            mReachedBarColor = bundle.getInt(INSTANCE_REACHED_BAR_COLOR)
            mUnreachedBarColor = bundle.getInt(INSTANCE_UNREACHED_BAR_COLOR)
            initializePainters()
            setMax(bundle.getInt(INSTANCE_MAX))
            setProgress(bundle.getInt(INSTANCE_PROGRESS))
            setPrefix(bundle.getString(INSTANCE_PREFIX))
            setSuffix(bundle.getString(INSTANCE_SUFFIX))
            setProgressTextVisibility(if (bundle.getBoolean(INSTANCE_TEXT_VISIBILITY)) ProgressTextVisibility.Visible else ProgressTextVisibility.Invisible)
            super.onRestoreInstanceState(bundle.getParcelable<Parcelable?>(INSTANCE_STATE))
            return
        }
        super.onRestoreInstanceState(state)
    }

    fun dp2px(dp: Float): Float {
        val scale = resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun sp2px(sp: Float): Float {
        val scale = resources.displayMetrics.scaledDensity
        return sp * scale
    }

    fun setProgressTextVisibility(visibility: ProgressTextVisibility?) {
        mIfDrawText = visibility == ProgressTextVisibility.Visible
        invalidate()
    }

    fun getProgressTextVisibility(): Boolean {
        return mIfDrawText
    }

    fun setOnProgressBarListener(listener: OnProgressBarListener?) {
        mListener = listener
    }

    companion object {
        /**
         * For save and restore instance of progressbar.
         */
        private const val INSTANCE_STATE = "saved_instance"
        private const val INSTANCE_TEXT_COLOR = "text_color"
        private const val INSTANCE_TEXT_SIZE = "text_size"
        private const val INSTANCE_REACHED_BAR_HEIGHT = "reached_bar_height"
        private const val INSTANCE_REACHED_BAR_COLOR = "reached_bar_color"
        private const val INSTANCE_UNREACHED_BAR_HEIGHT = "unreached_bar_height"
        private const val INSTANCE_UNREACHED_BAR_COLOR = "unreached_bar_color"
        private const val INSTANCE_MAX = "max"
        private const val INSTANCE_PROGRESS = "progress"
        private const val INSTANCE_SUFFIX = "suffix"
        private const val INSTANCE_PREFIX = "prefix"
        private const val INSTANCE_TEXT_VISIBILITY = "text_visibility"

        private const val PROGRESS_TEXT_VISIBLE = 0
    }
}
