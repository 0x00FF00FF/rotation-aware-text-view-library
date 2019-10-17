package org.rares.ratv.rotationaware;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import org.rares.ratv.R;
import org.rares.ratv.rotationaware.animation.AnimationDTO;


/**
 * <hr />
 * A custom view that displays text according to view rotation, not exiting layout bounds while rotated. <br />
 * It does not rotate the whole canvas while keeping the layout dimensions like a normal {@link android.widget.TextView} would.
 * Contains a default animator that can rotate the view while keeping its aspect ratio according to the rotation.
 * This behaviour can be changed by supplying a target width and height. <br />
 * The default animator manipulates rotation, background color, text color, text size and layout width and height.
 * <ul>
 * Custom attributes (used mostly by the default animator):
 * <li>target_width</li>
 * <li>target_height</li>
 * <li>text_size</li>
 * <li>target_text_size</li>
 * <li>original_margin_left</li>
 * <li>original_margin_top</li>
 * <li>original_margin_right</li>
 * <li>original_margin_bottom</li>
 * <li>target_margin_left</li>
 * <li>target_margin_top</li>
 * <li>target_margin_right</li>
 * <li>target_margin_bottom</li>
 * <li>original_rotation</li>
 * <li>target_rotation</li>
 * <li>text_color</li>
 * <li>original_text_color</li>
 * <li>target_text_color</li>
 * <li>background_color</li>
 * <li>target_background_color</li>
 * <li>original_shadow_radius</li>
 * <li>target_shadow_radius</li>
 * <li>original_shadow_color</li>
 * <li>target_shadow_color</li>
 * </ul>
 * <hr />Does not extend TextView! <hr />
 *
 * @attr ref R.styleable#RotationAwareTextView_target_width
 * @attr ref R.styleable#RotationAwareTextView_target_height
 * @attr ref R.styleable#RotationAwareTextView_text_size
 * @attr ref R.styleable#RotationAwareTextView_target_text_size
 * @attr ref R.styleable#RotationAwareTextView_original_margin_left
 * @attr ref R.styleable#RotationAwareTextView_original_margin_top
 * @attr ref R.styleable#RotationAwareTextView_original_margin_right
 * @attr ref R.styleable#RotationAwareTextView_original_margin_bottom
 * @attr ref R.styleable#RotationAwareTextView_target_margin_left
 * @attr ref R.styleable#RotationAwareTextView_target_margin_top
 * @attr ref R.styleable#RotationAwareTextView_target_margin_right
 * @attr ref R.styleable#RotationAwareTextView_target_margin_bottom
 * @attr ref R.styleable#RotationAwareTextView_original_rotation
 * @attr ref R.styleable#RotationAwareTextView_target_rotation
 * @attr ref R.styleable#RotationAwareTextView_text_color
 * @attr ref R.styleable#RotationAwareTextView_original_text_color
 * @attr ref R.styleable#RotationAwareTextView_target_text_color
 * @attr ref R.styleable#RotationAwareTextView_background_color
 * @attr ref R.styleable#RotationAwareTextView_target_background_color
 * @attr ref R.styleable#RotationAwareTextView_original_shadow_radius
 * @attr ref R.styleable#RotationAwareTextView_target_shadow_radius
 * @attr ref R.styleable#RotationAwareTextView_original_shadow_color
 * @attr ref R.styleable#RotationAwareTextView_target_shadow_color
 */
@SuppressWarnings("SuspiciousNameCombination, unused")
public class RotationAwareTextView extends View {

    public final String TAG = RotationAwareTextView.class.getSimpleName();

    private int originalWidth = 400;
    private int originalHeight = 100;

    private int targetWidth = originalHeight;
    private int targetHeight = originalWidth;

    private int originalRotation = 0;
    private int targetRotation = -90;

    private int minTextSize = 20;   //

    private String text = "";

    private float pseudoRotation = 0;

    boolean clearOnDetach = false;

    private Layout mLayout;

    private Paint p = new Paint();
    private TextPaint textPaint = new TextPaint(p);

    private int defaultBackgroundColor = 0x88FFFFFF;
    private int defaultTextColor = 0xFF000000;
    private int backgroundColor = defaultBackgroundColor;

    private int originalTextColor = defaultTextColor;
    private int targetTextColor = defaultTextColor;
    private int targetBackgroundColor = 0xFF303030;

    private int targetTextSize = minTextSize;
    private int textSize = 40;
    private int originalTextSize = textSize;

    private int originalMarginLeft = 0;
    private int originalMarginTop = 0;
    private int originalMarginRight = 0;
    private int originalMarginBottom = 0;

    private int targetMarginLeft = 0;
    private int targetMarginTop = 0;
    private int targetMarginRight = 0;
    private int targetMarginBottom = 0;

    private int originalShadowRadius = 0;
    private int targetShadowRadius = 0;
    private int originalShadowColor = 0;
    private int targetShadowColor = 0;

    private int shadowRadius = 0;
    private int shadowColor = 0;

    private View.OnClickListener clickListener = null;

    public final static int GRAVITY_CENTER = 0;
    public final static int GRAVITY_START = 1;
    public final static int GRAVITY_END = 2;

    private int gravity = GRAVITY_CENTER;

    private TextUtils.TruncateAt truncateAt = TextUtils.TruncateAt.END;
    private boolean ellipsize = false;

    //    canvas center, layout center, used in onDraw
    private final PointF cc = new PointF();
    private final PointF lc = new PointF();


    public RotationAwareTextView(Context context) {
        super(context);
        init(context, null);
    }

    public RotationAwareTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public RotationAwareTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * The place where all initialization takes place. <br />
     * Sets default values and, if possible, applies values from xml configuration.
     *
     * @param context information about the environment
     * @param attrs   collection of xml-defined attributes
     */
    private void init(Context context, AttributeSet attrs) {

        setBackgroundColor(defaultBackgroundColor);
        setTextColor(defaultTextColor);
        textPaint.setTextSize(originalTextSize);
        textPaint.setAntiAlias(true);
        textPaint.setTypeface(Typeface.DEFAULT);
        targetWidth = (int) (textPaint.getTextSize() * 2);

        if (attrs != null) {

            int[] systemAttrs = {
                    android.R.attr.layout_width,
                    android.R.attr.layout_height,
            };

            @StyleableRes int index = 0; // hmm...
            TypedArray a = context.obtainStyledAttributes(attrs, systemAttrs);

            originalWidth = a.getLayoutDimension(index++, originalWidth);
            originalHeight = a.getLayoutDimension(index, originalHeight);

            a.recycle();

            a = context.obtainStyledAttributes(attrs, R.styleable.RotationAwareTextView);

            setTargetWidth(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_target_width, getTargetWidth()));
            setTargetHeight(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_target_height, getTargetHeight()));

            setTargetRotation(a.getInt(R.styleable.RotationAwareTextView_target_rotation, getTargetRotation()));
            setOriginalRotation(a.getInt(R.styleable.RotationAwareTextView_original_rotation, getOriginalRotation()));

            setBackgroundColor(a.getColor(R.styleable.RotationAwareTextView_background_color, defaultBackgroundColor));
            setTargetBackgroundColor(a.getColor(R.styleable.RotationAwareTextView_target_background_color, targetBackgroundColor));

            setTextColor(a.getColor(R.styleable.RotationAwareTextView_text_color, textPaint.getColor()));
            setOriginalTextColor(a.getColor(R.styleable.RotationAwareTextView_original_text_color, textPaint.getColor()));
            setTargetTextColor(a.getColor(R.styleable.RotationAwareTextView_target_text_color, targetTextColor));

            setOriginalTextSize(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_text_size, minTextSize));
            setTargetTextSize(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_target_text_size, minTextSize));

            setOriginalMarginLeft(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_original_margin_left, originalMarginLeft));
            setOriginalMarginTop(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_original_margin_top, originalMarginTop));
            setOriginalMarginRight(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_original_margin_right, originalMarginRight));
            setOriginalMarginBottom(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_original_margin_bottom, originalMarginBottom));

            setTargetMarginLeft(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_target_margin_left, originalMarginLeft));
            setTargetMarginTop(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_target_margin_top, originalMarginTop));
            setTargetMarginRight(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_target_margin_right, originalMarginRight));
            setTargetMarginBottom(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_target_margin_bottom, originalMarginBottom));

            setOriginalShadowRadius(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_original_shadow_radius, originalShadowRadius));
            setTargetShadowRadius(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_target_shadow_radius, targetShadowRadius));

            setOriginalShadowColor(a.getColor(R.styleable.RotationAwareTextView_original_shadow_color, originalShadowColor));
            setTargetShadowColor(a.getColor(R.styleable.RotationAwareTextView_target_shadow_color, targetShadowColor));

            a.recycle();

            setTextSize(getOriginalTextSize());
            pseudoRotation = originalRotation;

        }
    }


    /**
     * Replaces original measure code to set original width and original height, so that animation could run properly. <br />
     * Supports MATCH_PARENT and WRAP_CONTENT configurations.
     *
     * @param widthMeasureSpec  horizontal space requirements as imposed by the parent.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);

        if (originalWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
            originalWidth = sizeW;
        }
        if (originalWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            originalWidth = (int) textPaint.measureText(text);
        }
        if (originalHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            originalHeight = sizeH;
        }
        if (originalHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            originalHeight = (int) (textPaint.getTextSize() * 1.25);
        }

        if (widthMode == MeasureSpec.AT_MOST) {
            sizeW = Math.min((int) textPaint.measureText(text), sizeW);
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            sizeH = (int) Math.min((int) textPaint.getTextSize() * 1.25, sizeH);
        }

        setMeasuredDimension(sizeW, sizeH);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        createLayout(right - left);
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        clear();
    }

    /**
     * The place where the magic happens. <br />
     * The canvas is rotated by current rotation value. <br />
     * The text is always drawn in the middle of the canvas. <br />
     * Because of the differences between layout size and
     * canvas size, after rotation, the two center points
     * would not coincide. As such, the canvas is adjusted by a factor
     * composed the layout height and sine/cosine of rotation angle. <br />
     *
     * @param canvas the object on which drawing calls are made.
     */
    @SuppressLint("CanvasSize")
    @SuppressWarnings("IntegerDivisionInFloatingPointContext")
    @Override
    protected void onDraw(Canvas canvas) {
        cc.x = canvas.getWidth() / 2;
        cc.y = canvas.getHeight() / 2;
        lc.x = mLayout.getWidth() / 2;
        lc.y = mLayout.getHeight() / 2;

//        drawMiddle(canvas, true);
        canvas.rotate(pseudoRotation, cc.x, cc.y);
        canvas.save();
        canvas.translate(cc.x - lc.x, cc.y - lc.y);
        mLayout.draw(canvas);
//        drawMiddle(canvas, false);
        canvas.restore();
    }

    private void drawMiddle(Canvas canvas, boolean forCanvas) {
        Paint paint = new Paint();
        paint.setStrokeWidth(2);
        paint.setColor(0xFF00AA00);
        if (forCanvas) {
            canvas.drawLine(
                    (float) (0),
                    (float) (canvas.getHeight() / 2),
                    (float) (canvas.getWidth()),
                    (float) (canvas.getHeight() / 2),
                    paint);
            canvas.drawLine(
                    (float) (canvas.getWidth() / 2),
                    (float) (0),
                    (float) (canvas.getWidth() / 2),
                    (float) (canvas.getHeight()),
                    paint);
        } else {
            paint.setColor(0xFF0000AA);
            paint.setStrokeWidth(4);
            canvas.drawLine(
                    (float) (0),
                    (float) (mLayout.getHeight() / 2),
                    (float) (mLayout.getWidth()),
                    (float) (mLayout.getHeight() / 2),
                    paint);
            canvas.drawLine(
                    (float) (mLayout.getWidth() / 2),
                    (float) (0),
                    (float) (mLayout.getWidth() / 2),
                    (float) (mLayout.getHeight()),
                    paint);
        }
    }

    /**
     * @return an animation data transfer object
     * containing the cached values.
     */
    public AnimationDTO gatherAnimationData() {
        AnimationDTO animationData = new AnimationDTO();

        animationData.minRotation = getOriginalRotation();
        animationData.maxRotation = getTargetRotation();
        animationData.minWidth = getOriginalWidth();
        animationData.maxWidth = getTargetWidth();
        animationData.minHeight = getOriginalHeight();
        animationData.maxHeight = getTargetHeight();
        animationData.minTextColor = getTextPaint().getColor();
        animationData.maxTextColor = getTargetTextColor();
        animationData.minBackgroundColor = getBackgroundColor();
        animationData.maxBackgroundColor = getTargetBackgroundColor();
        animationData.minTextSize = getOriginalTextSize();
        animationData.maxTextSize = getTargetTextSize();

        animationData.minMarginLeft = getOriginalMarginLeft();
        animationData.minMarginTop = getOriginalMarginTop();
        animationData.minMarginRight = getOriginalMarginRight();
        animationData.minMarginBottom = getOriginalMarginBottom();
        animationData.maxMarginLeft = getTargetMarginLeft();
        animationData.maxMarginTop = getTargetMarginTop();
        animationData.maxMarginRight = getTargetMarginRight();
        animationData.maxMarginBottom = getTargetMarginBottom();

        animationData.minShadowColor = originalShadowColor;
        animationData.maxShadowColor = targetShadowColor;
        animationData.minShadowRadius = originalShadowRadius;
        animationData.maxShadowRadius = targetShadowRadius;
        return animationData;
    }

    /**
     * Resets the view rotation, width and height.
     */
    public void reset() {
        setRotation(0);
        ViewGroup.LayoutParams lp = getLayoutParams();
        lp.width = originalWidth;
        lp.height = originalHeight;
        setLayoutParams(lp);
    }

    /**
     * This view can not be rotated in the normal android way,
     * only its text is going to be rotated.<br />
     * Increasing values result in clockwise rotation.
     *
     * @return the angle (in degrees) at which the text is
     * currently rotated.
     */
    @Override
    public final float getRotation() {
        return pseudoRotation;
    }

    /**
     * This view can not be rotated in the normal android way,
     * only its text is going to be rotated. <br />
     * Increasing values result in clockwise rotation.
     *
     * @param rotation the angle (in degrees) to rotate the text to
     */
    @Override
    public final void setRotation(float rotation) {
        pseudoRotation = rotation;
    }

    /**
     * Sets the end rotation that will be achieved after animation.<br />
     * Increasing values result in clockwise rotation.
     *
     * @param targetRotation the angle (in degrees) that the text will
     *                       be after animation.
     */
    public void setTargetRotation(int targetRotation) {
        this.targetRotation = targetRotation;
    }

    /**
     * Gets the end rotation achieved after animation.<br />
     * Increasing values result in clockwise rotation.
     *
     * @return an angle (in degrees)
     */
    public int getTargetRotation() {
        return targetRotation;
    }

    /**
     * @return the drawn text.
     */
    public String getText() {
        return text;
    }

    /**
     * Sets the text to be drawn.
     *
     * @param txt text to be drawn.
     */
    public void setText(String txt) {
        if (getText().equals(txt)) {
            return;
        }
        this.text = txt;
        invalidate();
    }

    /**
     * @return the paint object used for text drawing.
     */
    public TextPaint getTextPaint() {
        return textPaint;
    }

    /**
     * Sets the paint object used for text drawing.
     *
     * @param textPaint paint object used for text drawing.
     */
    public void setTextPaint(TextPaint textPaint) {
        this.textPaint = textPaint;
        invalidate();
    }

    /**
     * Convenience method that directly sets paint color.
     *
     * @param color color in int format
     */
    public void setTextColor(int color) {
        textPaint.setColor(color);
        invalidate();
    }

    /**
     * @return the color of the text at the beginning of the animation
     */
    public int getOriginalTextColor() {
        return originalTextColor;
    }

    /**
     * @param originalTextColor the color of the text at the beginning of the animation.
     */
    public void setOriginalTextColor(int originalTextColor) {
        this.originalTextColor = originalTextColor;
    }

    /**
     * @return the height at the end of the rotation animation.
     */
    public int getTargetHeight() {
        return targetHeight;
    }

    /**
     * @param targetHeight the height at the end of the rotation animation.
     */
    public void setTargetHeight(int targetHeight) {
        this.targetHeight = targetHeight;
    }

    /**
     * @return the width at the beginning of the rotation animation.
     */
    public int getOriginalWidth() {
        return originalWidth;
    }

    /**
     * Handle width care :)
     *
     * @param originalWidth the width at start of initial animation.
     */
    public void setOriginalWidth(int originalWidth) {
        this.originalWidth = originalWidth;
    }

    /**
     * @return the height at the beginning of the rotation animation.
     */
    public int getOriginalHeight() {
        return originalHeight;
    }

    /**
     * @param originalHeight the height at the start of the initial animation.
     */
    public void setOriginalHeight(int originalHeight) {
        this.originalHeight = originalHeight;
    }

    /**
     * @return the width at the end of the rotation animation.
     */
    public int getTargetWidth() {
        return targetWidth;
    }

    /**
     * @param targetWidth the width at the end of the rotation animation.
     */
    public void setTargetWidth(int targetWidth) {
        this.targetWidth = targetWidth;
    }

    /**
     * @return the rotation at the beginning of the animation.
     */
    public int getOriginalRotation() {
        return originalRotation;
    }

    /**
     * @param originalRotation the rotation at the beginning of the animation.
     */
    public void setOriginalRotation(int originalRotation) {
        this.originalRotation = originalRotation;
    }

    /**
     * @return pixel value for respective margin
     */
    public int getOriginalMarginLeft() {
        return originalMarginLeft;
    }

    /**
     * @param originalMarginLeft pixel value for respective margin
     */
    public void setOriginalMarginLeft(int originalMarginLeft) {
        this.originalMarginLeft = originalMarginLeft;
    }

    /**
     * @return pixel value for respective margin
     */
    public int getOriginalMarginTop() {
        return originalMarginTop;
    }

    /**
     * @param originalMarginTop pixel value for respective margin
     */
    public void setOriginalMarginTop(int originalMarginTop) {
        this.originalMarginTop = originalMarginTop;
    }

    /**
     * @return pixel value for respective margin
     */
    public int getOriginalMarginRight() {
        return originalMarginRight;
    }

    /**
     * @param originalMarginRight pixel value for respective margin
     */
    public void setOriginalMarginRight(int originalMarginRight) {
        this.originalMarginRight = originalMarginRight;
    }

    /**
     * @return pixel value for respective margin
     */
    public int getOriginalMarginBottom() {
        return originalMarginBottom;
    }

    /**
     * @param originalMarginBottom pixel value for respective margin
     */
    public void setOriginalMarginBottom(int originalMarginBottom) {
        this.originalMarginBottom = originalMarginBottom;
    }

    /**
     * @return pixel value for respective margin
     */
    public int getTargetMarginLeft() {
        return targetMarginLeft;
    }

    /**
     * @param targetMarginLeft pixel value for respective margin
     */
    public void setTargetMarginLeft(int targetMarginLeft) {
        this.targetMarginLeft = targetMarginLeft;
    }

    /**
     * @return pixel value for respective margin
     */
    public int getTargetMarginTop() {
        return targetMarginTop;
    }

    /**
     * @param targetMarginTop pixel value for respective margin
     */
    public void setTargetMarginTop(int targetMarginTop) {
        this.targetMarginTop = targetMarginTop;
    }

    /**
     * @return pixel value for respective margin
     */
    public int getTargetMarginRight() {
        return targetMarginRight;
    }

    /**
     * @param targetMarginRight pixel value for respective margin
     */
    public void setTargetMarginRight(int targetMarginRight) {
        this.targetMarginRight = targetMarginRight;
    }

    /**
     * @return pixel value for respective margin
     */
    public int getTargetMarginBottom() {
        return targetMarginBottom;
    }

    /**
     * @param targetMarginBottom pixel value for respective margin
     */
    public void setTargetMarginBottom(int targetMarginBottom) {
        this.targetMarginBottom = targetMarginBottom;
    }

    /**
     * @return the color of the text at the end of the animation.
     */
    public int getTargetTextColor() {
        return targetTextColor;
    }

    /**
     * @param targetTextColor the color of the text at the end of the animation.
     */
    public void setTargetTextColor(int targetTextColor) {
        this.targetTextColor = targetTextColor;
    }

    /**
     * @return background color at the end of the animation.
     */
    public int getTargetBackgroundColor() {
        return targetBackgroundColor;
    }

    /**
     * @param targetBackgroundColor background color at the end of the animation.
     */
    public void setTargetBackgroundColor(int targetBackgroundColor) {
        this.targetBackgroundColor = targetBackgroundColor;
    }

    /**
     * @return current background color
     */
    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        super.setBackgroundColor(backgroundColor);
        this.backgroundColor = backgroundColor;
        invalidate();
    }

    /**
     * @return the text size at the end of the animation, in pixels.
     */
    public int getTargetTextSize() {
        return targetTextSize;
    }

    /**
     * Sets the size this control's text should be after initial rotation.
     *
     * @param targetTextSize size, in pixels.
     */
    public void setTargetTextSize(int targetTextSize) {
        this.targetTextSize = Math.max(minTextSize, targetTextSize);
    }

    /**
     * @return text size at view creation (target size for reverse animation)
     */
    public int getOriginalTextSize() {
        return originalTextSize;
    }

    /**
     * @param originalTextSize target text size for reverse animation
     */
    public void setOriginalTextSize(int originalTextSize) {
        this.originalTextSize = Math.max(minTextSize, originalTextSize);
    }

    public int getShadowRadius() {
        return shadowRadius;
    }

    public void setShadowRadius(int shadowRadius) {
        this.shadowRadius = shadowRadius;
    }

    public int getShadowColor() {
        return shadowColor;
    }

    public void setShadowColor(int shadowColor) {
        this.shadowColor = shadowColor;
        invalidate();
    }

    public void setShadowLayer() {
        getTextPaint().setShadowLayer(shadowRadius, 0, 0, shadowColor);
        invalidate();
    }

    /**
     * @return the starting value for the shadow radius (0 means no shadow)
     */
    public int getOriginalShadowRadius() {
        return originalShadowRadius;
    }

    /**
     * @param originalShadowRadius the starting value for the
     *                             shadow radius (0 means no shadow)
     */
    public void setOriginalShadowRadius(int originalShadowRadius) {
        this.originalShadowRadius = originalShadowRadius;
    }

    /**
     * @return the end value for the shadow radius (0 means no shadow)
     */
    public int getTargetShadowRadius() {
        return targetShadowRadius;
    }

    /**
     * @param targetShadowRadius the end value for the shadow
     *                           radius (0 means no shadow)
     */
    public void setTargetShadowRadius(int targetShadowRadius) {
        this.targetShadowRadius = targetShadowRadius;
    }

    /**
     * @return the color used for drawing the shadow in the
     * target position
     */
    public int getTargetShadowColor() {
        return targetShadowColor;
    }

    /**
     * @param targetShadowColor the color used for drawing the
     *                          shadow in the target position
     */
    public void setTargetShadowColor(int targetShadowColor) {
        this.targetShadowColor = targetShadowColor;
    }

    /**
     * @return the color used for drawing the shadow in the
     * original position
     */
    public int getOriginalShadowColor() {
        return originalShadowColor;
    }

    /**
     * @param shadowColor the color used for drawing the shadow
     *                    in the original position
     */
    public void setOriginalShadowColor(int shadowColor) {
        this.originalShadowColor = shadowColor;
    }

    /**
     * @return current text size, in pixels.
     */
    public int getTextSize() {
        return textSize;
    }

    /**
     * Sets the size this control's text should be.
     *
     * @param textSize size, in pixels.
     */
    public void setTextSize(float textSize) {
        this.textSize = (int) Math.max(minTextSize, textSize);
        textPaint.setTextSize(this.textSize);
    }

    /**
     * @return true if the view is configured
     * to clear everything on view detachment
     */
    public boolean isClearOnDetach() {
        return clearOnDetach;
    }

    /**
     * Configure the view to clear its variables
     * on view detachment.
     *
     * @param clearOnDetach true to enable, false to disable
     */
    private void setClearOnDetach(boolean clearOnDetach) {
        this.clearOnDetach = clearOnDetach;
    }

    /**
     * Rudimentary implementation of horizontal gravity
     *
     * @return one of:<br />
     * GRAVITY_CENTER = 0;<br />
     * GRAVITY_START = 1;<br />
     * GRAVITY_END = 2;<br />
     */
    public int getGravity() {
        return gravity;
    }

    /**
     * Rudimentary implementation of horizontal gravity
     *
     * @param gravity <br />
     *                GRAVITY_CENTER = 0;<br />
     *                GRAVITY_START = 1;<br />
     *                GRAVITY_END = 2;<br />
     */
    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    /**
     * @return true if text will be truncated if it is longer than the width
     * of its containing view
     */
    public boolean isEllipsize() {
        return ellipsize;
    }

    /**
     * @param ellipsize set to true to truncate the text
     *                  if it is longer than the width
     *                  of its containing view
     */
    public void setEllipsize(boolean ellipsize) {
        this.ellipsize = ellipsize;
    }

    /**
     * @return TruncateAt showing the position where the text will be truncated.
     * Check {@link TextUtils.TruncateAt} for more info.
     * {@link TextUtils.TruncateAt#MARQUEE} is not supported.
     */
    public TextUtils.TruncateAt getEllipsizeMode() {
        return truncateAt;
    }

    /**
     * @param truncateAt showing the position where the text will be truncated.
     *                   Check {@link TextUtils.TruncateAt} for more info.
     *                   {@link TextUtils.TruncateAt#MARQUEE} is not supported.
     */
    public void setEllipsizeMode(TextUtils.TruncateAt truncateAt) {
        this.truncateAt = truncateAt;
    }

    /**
     * Request this view to layout text again with the width supplied by this method.
     *
     * @param width how many pixels should the text occupy
     */
    public void requestTextLayout(int width) {
        createLayout(width);
    }

    /**
     * Request a layout without supplying any width.
     */
    public void requestInternalLayout() {
        int width = getWidth();
        if (width == 0) {
            width = getMeasuredWidth();
        }
        if (width == 0) {
            width = originalWidth;
        }
        if (width <= 0) {
            width = 0;
        }
        try {
            createLayout(width);
        } catch (Exception x) {
            Log.e(TAG, "requestInternalLayout: >>> ERROR <<< ", x);
        }
    }

    public int getLayoutWidth() {
        return mLayout.getWidth();
    }

    public int getLayoutHeight() {
        return mLayout.getHeight();
    }

    /**
     * Create or update the layout.
     *
     * @param width the width of the container (outer width for the boring layout)
     */
    private void createLayout(int width) {
        BoringLayout.Metrics metrics = new BoringLayout.Metrics();
        metrics.width = (int) textPaint.measureText(text); //Math.max(originalWidth, originalHeight);
        metrics.top = 0; // only this is used

        BoringLayout.Metrics boringMetrics = BoringLayout.isBoring(text, textPaint, metrics);
//        Log.v(TAG, "createLayout: is it boring? " + (boringMetrics == null ? " no." : " yes."));

        /*
        source 	        CharSequence: the text to render
        paint 	        TextPaint: the default paint for the layout
        outerWidth 	    int: the wrapping width for the text
        align 	        Layout.Alignment: whether to left, right, or center the text
        spacingMult 	float: this value is no longer used by BoringLayout
        spacingAdd 	    float: this value is no longer used by BoringLayout
        metrics 	    BoringLayout.Metrics: information about FontMetrics and line width
        includePad 	    boolean: set whether to include extra space beyond font ascent and descent
                            which is needed to avoid clipping in some scripts
        */

        if (mLayout != null) {
            mLayout = ((BoringLayout) mLayout).replaceOrMake(
                    text,
                    textPaint,
                    width,
                    getAlignmentFromGravity(gravity),
                    0F,
                    0F,
                    boringMetrics,
                    true,
                    ellipsize ? truncateAt : null,
                    (int) (width - textPaint.measureText("W")));
        } else {
            mLayout = BoringLayout.make(
                    text,
                    textPaint,
                    width,
                    getAlignmentFromGravity(gravity),
                    0F,
                    0F,
                    boringMetrics,
                    true,
                    ellipsize ? truncateAt : null,
                    (int) (width - textPaint.measureText("W")));
        }
    }

    private Layout.Alignment getAlignmentFromGravity(int gravity) {
        if (gravity == GRAVITY_START) {
            return Layout.Alignment.ALIGN_NORMAL;
        }
        if (gravity == GRAVITY_END) {
            return Layout.Alignment.ALIGN_OPPOSITE;
        }
        return Layout.Alignment.ALIGN_CENTER;
    }

    /**
     * This kind of renders the view unusable.
     */
    private void clear() {
        if (clearOnDetach) {
            setBackgroundDrawable(null);
            mLayout = null;
            textPaint = null;
            p = null;
            text = null;
        }
    }
}
