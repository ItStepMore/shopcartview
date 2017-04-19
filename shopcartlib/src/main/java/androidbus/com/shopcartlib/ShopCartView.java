package androidbus.com.shopcartlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

/*
 *  @项目名：  ShopCartViewLib 
 *  @包名：    androidbus.com.shopcartlib
 *  @文件名:   ShopCartView
 *  @创建者:   Administrator
 *  @创建时间:  2017/4/17 0017 上午 9:46
 *  @描述：    TODO
 */
public class ShopCartView extends View {
    private static final String TAG = "ShopCartView";
    private boolean mIsAddFillMode;
    private int mAddEnableBgColor;
    private int mAddEnableFgColor;
    private int mAddDisableBgColor;
    private int mAddDisableFgColor;
    private boolean mIsDelFillMode;
    private int mDelEnableBgColor;
    private int mDelEnableFgColor;
    private int mDelDisableBgColor;
    private int mDelDisableFgColor;
    private float mRadius;
    private float mCircleStrokeWidth;
    private float mLineWidth;
    private float mGapBetweenCircle;
    private float mNumTextSize;
    private int mMaxCount;
    private int mCount;
    /**
     * 是否需要显示hint提示文本
     */
    private boolean mIgnoreHintArea;
    private String mHintText;
    private int mHintBgColor;
    private int mHintFgColor;
    private float mHintTextSize;
    private float mHintBgRoundValue;
    private int mPerAnimDuration;
    private int mReplenishTextColor;
    private float mReplenishTextSize;
    private String mReplenishText;
    private Paint mHintPaint;
    private Paint mHintTextPaint;
    private Paint mAddPaint;
    private Paint mDelPaint;
    private Paint mNumPaint;
    private Region mAddRegion;
    private Region mDelRegion;
    private Path mAddPath;
    private Path mDelPath;
    private float mDelAnimatorFraction;
    private float mHintAnimatorFraction;
    private boolean showHintMode;
    private boolean showHintText;
    private ValueAnimator mDelExpandAnim;
    private ValueAnimator mHintExpandAnim;
    private ValueAnimator mHintClospAnim;
    private ValueAnimator mDelClospAnim;
    private int mWidth;
    private int mLeft;
    private int mHeight;
    private int mTop;
    private boolean isReplenishText = false;
    private Paint mReplenshTextPaint;
    private Rect mHintTextBound;
    private Rect mReplenshTextBound;
    private RectF mRectF;
    private Region mRegion;
    private OnAddOrDelListner mOnAddOrDelListner;
    private boolean isBind;
    private Context context;
    private View startView;
    private View endView;

    public ShopCartView(Context context) {
	this(context, null);
    }

    public ShopCartView(Context context, AttributeSet attrs) {
	this(context, attrs, 0);
    }

    public ShopCartView(Context context, AttributeSet attrs, int defStyleAttr) {
	super(context, attrs, defStyleAttr);
	//初始化默认属性
	initDefaultAttrs(context);
	//获取资源属性
	init(context, attrs, defStyleAttr);
	//初始化画笔
	initPaint();
	//先暂停所有动画
	cancelAnim();
	//根据当前数量初始化UI
	initUIByCount();
    }

    private void initUIByCount() {
	if (mCount == 0) {
	    showHintMode = true;
	    showHintText = true;
	    mHintAnimatorFraction = 0;
	    mDelAnimatorFraction = 1;
	} else {
	    showHintMode = false;
	    showHintText = false;
	    mHintAnimatorFraction = 1;
	    mDelAnimatorFraction = 0;
	}
    }

    private void cancelAnim() {
	if (mHintExpandAnim != null && mHintExpandAnim.isRunning()) {
	    mHintExpandAnim.cancel();
	}

	if (mHintClospAnim != null && mHintClospAnim.isRunning()) {
	    mHintClospAnim.cancel();
	}

	if (mDelExpandAnim != null && mDelExpandAnim.isRunning()) {
	    mDelExpandAnim.cancel();
	}

	if (mDelClospAnim != null && mDelClospAnim.isRunning()) {
	    mDelClospAnim.cancel();
	}
    }

    private void initPaint() {
	mHintPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mHintPaint.setStyle(Paint.Style.FILL);

	mHintTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mHintTextPaint.setStyle(Paint.Style.FILL);
	mHintTextPaint.setTextSize(mHintTextSize);
	mHintTextPaint.setColor(mHintFgColor);

	mReplenshTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mReplenshTextPaint.setStyle(Paint.Style.FILL);
	mReplenshTextPaint.setTextSize(mReplenishTextSize);
	mReplenshTextPaint.setColor(mReplenishTextColor);

	mAddPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	if (mIsAddFillMode) {
	    mAddPaint.setStyle(Paint.Style.FILL);
	} else {
	    mAddPaint.setStyle(Paint.Style.STROKE);
	}

	mDelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	if (mIsDelFillMode) {
	    mDelPaint.setStyle(Paint.Style.FILL);
	} else {
	    mDelPaint.setStyle(Paint.Style.STROKE);
	}

	mNumPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mNumPaint.setStyle(Paint.Style.FILL);
	mNumPaint.setTextSize(mNumTextSize);
	mNumPaint.setColor(mReplenishTextColor);

	mAddRegion = new Region();
	mDelRegion = new Region();
	mAddPath = new Path();
	mDelPath = new Path();

	//初始化动画属性
	//减少按钮的伸展动画
	mDelExpandAnim = ValueAnimator.ofFloat(1, 0);
	mDelExpandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	    @Override
	    public void onAnimationUpdate(ValueAnimator valueAnimator) {
		mDelAnimatorFraction = (float) valueAnimator.getAnimatedValue();
		invalidate();
	    }
	});
	mDelExpandAnim.setDuration(mPerAnimDuration);
	mDelExpandAnim.addListener(new AnimatorListenerAdapter() {
	    @Override
	    public void onAnimationEnd(Animator animation) {
		super.onAnimationEnd(animation);
	    }
	});

	//提示语扩展动画
	mHintExpandAnim = ValueAnimator.ofFloat(1, 0);
	mHintExpandAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	    @Override
	    public void onAnimationUpdate(ValueAnimator valueAnimator) {
		mHintAnimatorFraction = (float) valueAnimator.getAnimatedValue();
		invalidate();
	    }
	});
	mHintExpandAnim.setDuration(mIgnoreHintArea
				    ? 0
				    : mPerAnimDuration);
	mHintExpandAnim.addListener(new AnimatorListenerAdapter() {
	    @Override
	    public void onAnimationStart(Animator animation) {
		//提示语扩展动画开始时要显示提示语背景
		if (mCount == 0) {
		    showHintMode = true;
		}
	    }

	    @Override
	    public void onAnimationEnd(Animator animation) {
		//提示语扩展动画结束时要显示提示语文本
		if (mCount == 0) {
		    showHintText = true;
		}
	    }
	});

	//提示语收缩动画
	mHintClospAnim = ValueAnimator.ofFloat(0, 1);
	mHintClospAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	    @Override
	    public void onAnimationUpdate(ValueAnimator valueAnimator) {
		mHintAnimatorFraction = (float) valueAnimator.getAnimatedValue();
		invalidate();
	    }
	});
	mHintClospAnim.setDuration(mPerAnimDuration);
	mHintClospAnim.addListener(new AnimatorListenerAdapter() {
	    @Override
	    public void onAnimationStart(Animator animation) {
		//提示语收缩动画开始时先隐藏提示语文本
		if (mCount > 0) {
		    showHintText = false;
		}
	    }

	    @Override
	    public void onAnimationEnd(Animator animation) {
		//提示语收缩动画结束时先隐藏提示语背景
		if (mCount > 0) {
		    showHintMode = false;
		    //提示语收缩动画结束时开启减少按钮扩展动画
		    if (mDelExpandAnim != null && !mDelExpandAnim.isRunning()) {
			mDelExpandAnim.start();
		    }
		}
	    }
	});


	//减少按钮的收缩动画
	mDelClospAnim = ValueAnimator.ofFloat(0, 1);
	mDelClospAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
	    @Override
	    public void onAnimationUpdate(ValueAnimator valueAnimator) {
		mDelAnimatorFraction = (float) valueAnimator.getAnimatedValue();
		invalidate();
	    }
	});
	mDelClospAnim.setDuration(mPerAnimDuration);
	mDelClospAnim.addListener(new AnimatorListenerAdapter() {
	    @Override
	    public void onAnimationEnd(Animator animation) {
		//减少按钮的收缩动画结束后开启提示语扩展动画
		if (mCount == 0) {
		    if (mHintExpandAnim != null && !mHintExpandAnim.isRunning()) {
			mHintExpandAnim.start();
		    }
		}
	    }
	});

	mHintTextBound = new Rect();
	mHintTextPaint.getTextBounds(mHintText, 0, mHintText.length(), mHintTextBound);
	mReplenshTextBound = new Rect();
	mReplenshTextPaint.getTextBounds(mReplenishText, 0, mReplenishText
		.length(), mReplenshTextBound);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
	TypedArray ta = context.getTheme()
			       .obtainStyledAttributes(attrs, R.styleable.ShopCartView, defStyleAttr, 0);
	int indexCount = ta.getIndexCount();

	for (int i = 0; i < indexCount; i++) {
	    int attr = ta.getIndex(i);
	    if (attr == R.styleable.ShopCartView_isAddFillMode) {
		mIsAddFillMode = ta.getBoolean(attr, mIsAddFillMode);
	    } else if (attr == R.styleable.ShopCartView_addEnableBgColor) {
		mAddEnableBgColor = ta.getColor(attr, mAddEnableBgColor);
	    } else if (attr == R.styleable.ShopCartView_addEnableFgColor) {
		mAddEnableFgColor = ta.getColor(attr, mAddEnableFgColor);
	    } else if (attr == R.styleable.ShopCartView_addDisableBgColor) {
		mAddDisableBgColor = ta.getColor(attr, mAddDisableBgColor);
	    } else if (attr == R.styleable.ShopCartView_addDisableFgColor) {
		mAddDisableFgColor = ta.getColor(attr, mAddDisableFgColor);
	    } else if (attr == R.styleable.ShopCartView_isDelFillMode) {
		mIsDelFillMode = ta.getBoolean(attr, mIsDelFillMode);
	    } else if (attr == R.styleable.ShopCartView_delEnableBgColor) {
		mDelEnableBgColor = ta.getColor(attr, mDelEnableBgColor);
	    } else if (attr == R.styleable.ShopCartView_delEnableFgColor) {
		mDelEnableFgColor = ta.getColor(attr, mDelEnableFgColor);
	    } else if (attr == R.styleable.ShopCartView_delDisableBgColor) {
		mDelDisableBgColor = ta.getColor(attr, mDelDisableBgColor);
	    } else if (attr == R.styleable.ShopCartView_delDisableFgColor) {
		mDelDisableFgColor = ta.getColor(attr, mDelDisableFgColor);
	    } else if (attr == R.styleable.ShopCartView_radius) {
		mRadius = ta.getDimension(attr, mRadius);
	    } else if (attr == R.styleable.ShopCartView_circleStrokeWidth) {
		mCircleStrokeWidth = ta.getDimension(attr, mCircleStrokeWidth);
	    } else if (attr == R.styleable.ShopCartView_lineWidth) {
		mLineWidth = ta.getDimension(attr, mLineWidth);
	    } else if (attr == R.styleable.ShopCartView_gapBetweenCircle) {
		mGapBetweenCircle = ta.getDimension(attr, mGapBetweenCircle);
	    } else if (attr == R.styleable.ShopCartView_numTextSize) {
		mNumTextSize = ta.getDimension(attr, mNumTextSize);
	    } else if (attr == R.styleable.ShopCartView_maxCount) {
		mMaxCount = ta.getInt(attr, mMaxCount);
	    } else if (attr == R.styleable.ShopCartView_count) {
		mCount = ta.getInt(attr, mCount);
	    } else if (attr == R.styleable.ShopCartView_ignoreHintArea) {
		mIgnoreHintArea = ta.getBoolean(attr, mIgnoreHintArea);
	    } else if (attr == R.styleable.ShopCartView_hintText) {
		mHintText = ta.getString(attr);
	    } else if (attr == R.styleable.ShopCartView_hintBgColor) {
		mHintBgColor = ta.getColor(attr, mHintBgColor);
	    } else if (attr == R.styleable.ShopCartView_hintFgColor) {
		mHintFgColor = ta.getColor(attr, mHintFgColor);
	    } else if (attr == R.styleable.ShopCartView_hintTextSize) {
		mHintTextSize = ta.getDimension(attr, mHintTextSize);
	    } else if (attr == R.styleable.ShopCartView_hintBgRoundValue) {
		mHintBgRoundValue = ta.getDimension(attr, mHintBgRoundValue);
	    } else if (attr == R.styleable.ShopCartView_perAnimDuration) {
		mPerAnimDuration = ta.getInt(attr, mPerAnimDuration);
	    } else if (attr == R.styleable.ShopCartView_replenishTextColor) {
		mReplenishTextColor = ta.getColor(attr, mReplenishTextColor);
	    } else if (attr == R.styleable.ShopCartView_replenishTextSize) {
		mReplenishTextSize = ta.getDimension(attr, mReplenishTextSize);
	    } else if (attr == R.styleable.ShopCartView_replenishText) {
		mReplenishText = ta.getString(attr);
	    }
	}
	ta.recycle();
    }

    private void initDefaultAttrs(Context context) {
	mIsAddFillMode = true; //默认添加按钮为fill模式
	mAddEnableBgColor = Color.CYAN;
	mAddEnableFgColor = Color.WHITE;
	mAddDisableBgColor = Color.GRAY;
	mAddDisableFgColor = Color.WHITE;
	mIsDelFillMode = false;
	mDelEnableBgColor = Color.GRAY;
	mDelEnableFgColor = Color.GRAY;
	mDelDisableBgColor = Color.GRAY;
	mDelDisableFgColor = Color.LTGRAY;
	mRadius = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12.5f, context
		.getResources().getDisplayMetrics());
	mCircleStrokeWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1f, context
		.getResources().getDisplayMetrics());
	mLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, context
		.getResources().getDisplayMetrics());
	mGapBetweenCircle = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34f, context
		.getResources().getDisplayMetrics());
	mNumTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, context
		.getResources().getDisplayMetrics());
	mMaxCount = 100;
	mCount = 1;
	mIgnoreHintArea = false;
	mHintText = getResources().getString(R.string.str_hintText);
	mHintBgColor = Color.CYAN;
	mHintFgColor = Color.WHITE;
	mHintTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, context
		.getResources().getDisplayMetrics());
	mHintBgRoundValue = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5f, context
		.getResources().getDisplayMetrics());
	mPerAnimDuration = 500;
	mReplenishTextColor = Color.BLACK;
	mReplenishTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14f, context
		.getResources().getDisplayMetrics());
	mReplenishText = getResources().getString(R.string.str_replenishText);
    }

    private Region getRegion() {
	if (mRegion == null) {
	    mRegion = new Region();
	}
	mRegion.set(mLeft, mTop, mWidth - getPaddingRight(), mHeight - getPaddingBottom());
	return mRegion;
    }

    @NonNull
    private RectF getRectF() {
	if (mRectF == null) {
	    mRectF = new RectF();
	}
	mRectF.left = mLeft + (mWidth - mRadius * 2 - mCircleStrokeWidth * 2) * mHintAnimatorFraction;
	mRectF.top = mTop;
	mRectF.right = mLeft + mWidth;
	mRectF.bottom = mTop + mHeight;
	return mRectF;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
	int action = event.getAction();
	float x = event.getX();
	float y = event.getY();
	if (isReplenishText) {
	    return true;
	}

	if (mHintExpandAnim.isRunning() || mHintClospAnim.isRunning() || mDelExpandAnim
		.isRunning() || mDelClospAnim.isRunning())
	{
	    //如果当前有动画在运行就不能有其他操作
	    return true;
	}

	switch (action) {
	    case MotionEvent.ACTION_DOWN:
		if (mCount == 0) {
		    addClick();
		    return true;
		} else {

		    if (mAddRegion.contains((int) x, (int) y)) {
			addClick();

			return true;
		    } else if (mDelRegion.contains((int) x, (int) y)) {
			delClick();
			return true;
		    }
		}
		break;
	    case MotionEvent.ACTION_MOVE:
		break;
	    case MotionEvent.ACTION_UP:
		break;
	}
	return super.onTouchEvent(event);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
	super.onSizeChanged(w, h, oldw, oldh);
	mWidth = w;
	mLeft = getPaddingLeft();
	mHeight = h;
	mTop = getPaddingTop();
    }

    @Override
    protected void onDraw(Canvas canvas) {
	if (isReplenishText) {
	    //计算"补货中"文本的起终点
	    int startX = mWidth / 2 - mReplenshTextBound.width() / 2;
	    int startY = (int) (mHeight / 2 - (mReplenshTextPaint.ascent() + mReplenshTextPaint
		    .descent()) / 2);
	    //绘制"补货中"文本
	    canvas.drawText(mReplenishText, startX, startY, mReplenshTextPaint);
	    return;
	}

	if (!mIgnoreHintArea && showHintMode) {
	    //绘制提示语背景
	    mHintPaint.setColor(mHintBgColor);
	    RectF rectF = getRectF();
	    canvas.drawRoundRect(rectF, mHintBgRoundValue, mHintBgRoundValue, mHintPaint);
	    //绘制提示语文本
	    if (showHintText) {
		int startX = mWidth / 2 - mHintTextBound.width() / 2;
		int startY = (int) (mHeight / 2 - (mHintTextPaint.ascent() + mHintTextPaint
			.descent()) / 2);
		canvas.drawText(mHintText, startX, startY, mHintTextPaint);
	    }
	} else {
	    //绘制加减按钮
	    //获取按钮动画的最大位移距离
	    int animOffsetMax = (int) (mRadius * 2 + mCircleStrokeWidth * 2 + mGapBetweenCircle);
	    int alphaMax = 255;
	    int animRotateMax = 360;

	    //绘制删除按钮
	    if (mCount > 0) {
		mDelPaint.setColor(mDelEnableBgColor);
	    } else {
		mDelPaint.setColor(mDelDisableBgColor);
	    }
	    mDelPaint.setStrokeWidth(mCircleStrokeWidth);
	    mDelPaint.setAlpha((int) (alphaMax * (1 - mDelAnimatorFraction)));
	    mDelPath.reset();
	    canvas.save();
	    //删除按钮动画
	    canvas.translate(animOffsetMax * mDelAnimatorFraction + mLeft, 0);
	    canvas.rotate((int) (animRotateMax * (1 - mDelAnimatorFraction)), mCircleStrokeWidth + mRadius, mTop + mCircleStrokeWidth + mRadius);
	    mDelPath.addCircle(mLeft + mRadius + mCircleStrokeWidth, mTop + mRadius + mCircleStrokeWidth, mRadius, Path.Direction.CW);
	    mDelRegion.setPath(mDelPath, getRegion());
	    canvas.drawPath(mDelPath, mDelPaint);
	    if (mCount > 0) {
		mDelPaint.setColor(mDelEnableFgColor);
	    } else {
		mDelPaint.setColor(mDelDisableFgColor);
	    }
	    mDelPaint.setStrokeWidth(mLineWidth);
	    //绘制删除按钮中的横线
	    canvas.drawLine(mLeft + mCircleStrokeWidth + mRadius / 2, mTop + mCircleStrokeWidth + mRadius, mLeft + mCircleStrokeWidth + mRadius / 2 + mRadius, mTop + mCircleStrokeWidth + mRadius, mDelPaint);
	    canvas.restore();

	    //绘制数量文本
	    //获取数量文本的最大位移距离
	    mNumPaint.setAlpha((int) (alphaMax * (1 - mDelAnimatorFraction)));
	    canvas.save();

	    canvas.translate((animOffsetMax - mRadius * 2 - mCircleStrokeWidth * 2 - mGapBetweenCircle / 2 + mNumPaint
		    .measureText(mCount + "") / 2) * mDelAnimatorFraction + mLeft + mRadius * 2 + mCircleStrokeWidth * 2 + mGapBetweenCircle / 2 - mNumPaint
		    .measureText(mCount + "") / 2, 0);
	    canvas.rotate((int) (animRotateMax * (1 - mDelAnimatorFraction)), mNumPaint
		    .measureText(mCount + "") / 2, mHeight / 2);
	    canvas.drawText(mCount + "", 0, mHeight / 2 - (mNumPaint.ascent() + mNumPaint
		    .descent()) / 2, mNumPaint);
	    canvas.restore();

	    //绘制添加按钮
	    if (mCount < 100) {
		mAddPaint.setColor(mAddEnableBgColor);
	    } else {
		mAddPaint.setColor(mAddDisableBgColor);
	    }
	    mAddPaint.setStrokeWidth(mCircleStrokeWidth);
	    int left = (int) (mLeft + animOffsetMax + mCircleStrokeWidth);
	    mAddPath.reset();
	    mAddPath.addCircle(left + mRadius, mTop + mCircleStrokeWidth + mRadius, mRadius, Path.Direction.CW);
	    mAddRegion.setPath(mAddPath, getRegion());
	    canvas.drawPath(mAddPath, mAddPaint);

	    //绘制添加按钮间的“+”
	    if (mCount < 100) {
		mAddPaint.setColor(mAddEnableFgColor);
	    } else {
		mAddPaint.setColor(mAddDisableFgColor);
	    }
	    mAddPaint.setStrokeWidth(mLineWidth);
	    canvas.drawLine(left + mRadius / 2, mTop + mCircleStrokeWidth + mRadius, left + mRadius / 2 + mRadius, mTop + mCircleStrokeWidth + mRadius, mAddPaint);
	    canvas.drawLine(left + mRadius, mTop + mCircleStrokeWidth + mRadius / 2, left + mRadius, mTop + mCircleStrokeWidth + mRadius / 2 + mRadius, mAddPaint);
	}
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	int width = MeasureSpec.getSize(widthMeasureSpec);
	int widthMode = MeasureSpec.getMode(widthMeasureSpec);
	int height = MeasureSpec.getSize(heightMeasureSpec);
	int heightMode = MeasureSpec.getMode(heightMeasureSpec);

	switch (widthMode) {
	    case MeasureSpec.EXACTLY:
		break;
	    case MeasureSpec.AT_MOST:
		int defaultWidth = (int) (getPaddingLeft() + mRadius * 2 + mGapBetweenCircle + mRadius * 2 + mCircleStrokeWidth * 4 + getPaddingRight());
		width = defaultWidth < width
			? defaultWidth
			: width;
		break;
	    case MeasureSpec.UNSPECIFIED:
		defaultWidth = (int) (getPaddingLeft() + mRadius * 2 + mGapBetweenCircle + mRadius * 2 + mCircleStrokeWidth * 4 + getPaddingRight());
		width = defaultWidth;
		break;
	}

	switch (heightMode) {
	    case MeasureSpec.EXACTLY:
		break;
	    case MeasureSpec.AT_MOST:
		int defaultHeight = (int) (getPaddingTop() + mRadius * 2 + mCircleStrokeWidth * 2 + getPaddingBottom());
		height = defaultHeight < height
			 ? defaultHeight
			 : height;
		break;
	    case MeasureSpec.UNSPECIFIED:
		defaultHeight = (int) (getPaddingTop() + mRadius * 2 + mCircleStrokeWidth * 2 + getPaddingBottom());
		width = defaultHeight;
		break;
	}

	setMeasuredDimension(width, height);
	//        cancelAnim();
	//        initUIByCount();
    }

    private void delClick() {
	if (mCount > 0) {
	    mCount--;
	    if (mCount == 0) {
		cancelAnim();
		if (mDelClospAnim != null && !mDelClospAnim.isRunning()) {
		    mDelClospAnim.start();
		}
	    } else {
		mDelAnimatorFraction = 0;
		invalidate();
	    }

	    if (mOnAddOrDelListner != null) {
		mOnAddOrDelListner.onDelClick(mCount);
	    }
	} else {

	}
    }

    private void addClick() {
	if (mCount < mMaxCount) {
	    mCount++;
	    if (mCount == 1) {
		cancelAnim();
		if (mHintClospAnim != null && !mHintClospAnim.isRunning()) {
		    mHintClospAnim.start();
		}
	    } else {
		mDelAnimatorFraction = 0;
		invalidate();
	    }

	    if (mOnAddOrDelListner != null) {
		mOnAddOrDelListner.onAddClick(mCount);
	    }

	    if (isBind) {
		bindToCartAnim();
	    }
	} else {

	}
	invalidate();
    }

    public boolean isAddFillMode() {
	return mIsAddFillMode;
    }

    public void setAddFillMode(boolean addFillMode) {
	mIsAddFillMode = addFillMode;
    }

    public int getAddEnableBgColor() {
	return mAddEnableBgColor;
    }

    public void setAddEnableBgColor(int addEnableBgColor) {
	mAddEnableBgColor = addEnableBgColor;
    }

    public int getAddEnableFgColor() {
	return mAddEnableFgColor;
    }

    public void setAddEnableFgColor(int addEnableFgColor) {
	mAddEnableFgColor = addEnableFgColor;
    }

    public int getAddDisableBgColor() {
	return mAddDisableBgColor;
    }

    public void setAddDisableBgColor(int addDisableBgColor) {
	mAddDisableBgColor = addDisableBgColor;
    }

    public int getAddDisableFgColor() {
	return mAddDisableFgColor;
    }

    public void setAddDisableFgColor(int addDisableFgColor) {
	mAddDisableFgColor = addDisableFgColor;
    }

    public boolean isDelFillMode() {
	return mIsDelFillMode;
    }

    public void setDelFillMode(boolean delFillMode) {
	mIsDelFillMode = delFillMode;
    }

    public int getDelEnableBgColor() {
	return mDelEnableBgColor;
    }

    public void setDelEnableBgColor(int delEnableBgColor) {
	mDelEnableBgColor = delEnableBgColor;
    }

    public int getDelEnableFgColor() {
	return mDelEnableFgColor;
    }

    public void setDelEnableFgColor(int delEnableFgColor) {
	mDelEnableFgColor = delEnableFgColor;
    }

    public int getDelDisableBgColor() {
	return mDelDisableBgColor;
    }

    public void setDelDisableBgColor(int delDisableBgColor) {
	mDelDisableBgColor = delDisableBgColor;
    }

    public int getDelDisableFgColor() {
	return mDelDisableFgColor;
    }

    public void setDelDisableFgColor(int delDisableFgColor) {
	mDelDisableFgColor = delDisableFgColor;
    }

    public float getRadius() {
	return mRadius;
    }

    public void setRadius(float radius) {
	mRadius = radius;
    }

    public float getCircleStrokeWidth() {
	return mCircleStrokeWidth;
    }

    public void setCircleStrokeWidth(float circleStrokeWidth) {
	mCircleStrokeWidth = circleStrokeWidth;
    }

    public float getLineWidth() {
	return mLineWidth;
    }

    public void setLineWidth(float lineWidth) {
	mLineWidth = lineWidth;
    }

    public float getGapBetweenCircle() {
	return mGapBetweenCircle;
    }

    public void setGapBetweenCircle(float gapBetweenCircle) {
	mGapBetweenCircle = gapBetweenCircle;
    }

    public float getNumTextSize() {
	return mNumTextSize;
    }

    public void setNumTextSize(float numTextSize) {
	mNumTextSize = numTextSize;
    }

    public int getMaxCount() {
	return mMaxCount;
    }

    public void setMaxCount(int maxCount) {
	mMaxCount = maxCount;
    }

    public int getCount() {
	return mCount;
    }

    public void setCount(int count) {
	mCount = count;
	cancelAnim();
	initUIByCount();
    }

    public boolean isIgnoreHintArea() {
	return mIgnoreHintArea;
    }

    public void setIgnoreHintArea(boolean ignoreHintArea) {
	mIgnoreHintArea = ignoreHintArea;
    }

    public String getHintText() {
	return mHintText;
    }

    public void setHintText(String hintText) {
	mHintText = hintText;
    }

    public int getHintBgColor() {
	return mHintBgColor;
    }

    public void setHintBgColor(int hintBgColor) {
	mHintBgColor = hintBgColor;
    }

    public int getHintFgColor() {
	return mHintFgColor;
    }

    public void setHintFgColor(int hintFgColor) {
	mHintFgColor = hintFgColor;
    }

    public float getHintTextSize() {
	return mHintTextSize;
    }

    public void setHintTextSize(float hintTextSize) {
	mHintTextSize = hintTextSize;
    }

    public float getHintBgRoundValue() {
	return mHintBgRoundValue;
    }

    public void setHintBgRoundValue(float hintBgRoundValue) {
	mHintBgRoundValue = hintBgRoundValue;
    }

    public int getPerAnimDuration() {
	return mPerAnimDuration;
    }

    public void setPerAnimDuration(int perAnimDuration) {
	mPerAnimDuration = perAnimDuration;
    }

    public int getReplenishTextColor() {
	return mReplenishTextColor;
    }

    public void setReplenishTextColor(int replenishTextColor) {
	mReplenishTextColor = replenishTextColor;
    }

    public float getReplenishTextSize() {
	return mReplenishTextSize;
    }

    public void setReplenishTextSize(float replenishTextSize) {
	mReplenishTextSize = replenishTextSize;
    }

    public String getReplenishText() {
	return mReplenishText;
    }

    public void setReplenishText(String replenishText) {
	mReplenishText = replenishText;
    }

    /**
     * 设置是否绑定购物车动画
     * @param isBind
     * @param context
     * @param startView
     * @param endView
     */

    public void setCartAnim(boolean isBind, Context context, @NonNull View startView,
			    @NonNull View endView) {
	this.isBind = isBind;
	this.context = context;
	this.startView = startView;
	this.endView = endView;
    }

    public void bindToCartAnim() {
	//创建购物车小球
	ImageView target = new ImageView(context);
	target.setImageResource(R.drawable.traint);
	//获取startView和endView在屏幕的坐标
	int startX = getLocation(startView, 0);
	int startY = getLocation(startView, 1);
	int endX = getLocation(endView, 0);
	int endY = getLocation(endView, 1);

	Drawable drawable = context.getResources().getDrawable(R.drawable.traint);
	int intrinsicWidth = 0;
	int intrinsicHeight = 0;
	if (drawable != null) {
	    intrinsicWidth = drawable.getIntrinsicWidth();
	    intrinsicHeight = drawable.getIntrinsicHeight();
	}

	if (startView instanceof ShopCartView) {
	    if (drawable != null) {
		startX = (int) (startX + mWidth - mCircleStrokeWidth - mRadius - intrinsicWidth / 2);
		startY = (int) (startY + mCircleStrokeWidth + mRadius - intrinsicHeight / 2);
	    }
	} else {
	    startX = startX + startView.getWidth() / 2 - intrinsicWidth / 2;
	    startY = startY + startView.getHeight() / 2 - intrinsicHeight / 2;
	}

	endX = endX + endView.getWidth() / 2 - intrinsicWidth / 2;
	endY = endY + endView.getHeight() / 2 - intrinsicHeight / 2;

	//将购物车小球加载入窗体相应位置
	traintImgToWindow(context, startX, startY, target);
	//执行购物车抛物线动画
	invokeAnim(target, startX, startY, endX, endY);
    }

    private void invokeAnim(final ImageView target, int startX, int startY, int endX, int endY) {
	Log.d("getLocation", startX + "," + startY + " " + endX + "," + endY);
	//抛物线动画分解为x轴匀速移动和y轴加速移动
	ObjectAnimator translateX = ObjectAnimator.ofFloat(target, "translationX", endX - startX);
	translateX.setInterpolator(new LinearInterpolator());

	ObjectAnimator translateY = ObjectAnimator.ofFloat(target, "translationY", endY - startY);
	translateY.setInterpolator(new AccelerateInterpolator());
	translateY.addListener(new AnimatorListenerAdapter() {
	    @Override
	    public void onAnimationStart(Animator animation) {
		//动画开始时显示小球
		target.setVisibility(VISIBLE);
	    }

	    @Override
	    public void onAnimationEnd(Animator animation) {
		//动画结束时隐藏小球
		target.setVisibility(GONE);
	    }
	});

	//缩放动画
	ObjectAnimator scaleX = ObjectAnimator.ofFloat(target, "scaleX", 1f, 0.4f);
	ObjectAnimator scaleY = ObjectAnimator.ofFloat(target, "scaleY", 1f, 0.4f);


	AnimatorSet animatorSet = new AnimatorSet();
	animatorSet.playTogether(translateX, translateY,scaleX,scaleY);
	animatorSet.setDuration(500);
	animatorSet.start();

    }

    /**
     * 将购物车小球添加屏幕相应位置
     * @param context
     * @param startX
     * @param startY
     * @param target
     */

    private void traintImgToWindow(Context context, int startX, int startY, ImageView target) {
	ViewGroup rootView = (ViewGroup) ((Activity) context).getWindow().getDecorView();
	LinearLayout layout = new LinearLayout(context);
	LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
	layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
	layout.setLayoutParams(layoutParams);
	rootView.addView(layout);

	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	params.leftMargin = startX;
	params.topMargin = startY;
	target.setLayoutParams(params);
	layout.addView(target);
    }

    /**
     * 获取控件在屏幕相应位置
     * @param target
     * @param i
     * @return
     */
    private int getLocation(View target, int i) {
	int[] location = new int[2];
	target.getLocationInWindow(location);
	return location[i];
    }

    public void setOnAddOrDelListner(OnAddOrDelListner onAddOrDelListner) {
	mOnAddOrDelListner = onAddOrDelListner;
    }

    public interface OnAddOrDelListner {
	void onAddClick(int count);

	void onDelClick(int count);
    }
}
