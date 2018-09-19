package org.rares.ratv.rotationaware;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.annotation.StyleableRes;
import android.text.BoringLayout;
import android.text.Layout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import org.rares.ratv.R;
import org.rares.ratv.rotationaware.animation.AnimationDTO;
import org.rares.ratv.rotationaware.animation.DefaultRotationAnimatorHost;
import org.rares.ratv.rotationaware.animation.RotationAnimatorHost;
import org.rares.ratv.rotationaware.animation.RotationAwareUpdateListener;


/**
 *
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
 * <li>target_rotation</li>
 * <li>text_color</li>
 * <li>background_color</li>
 * <li>target_text_color</li>
 * <li>target_background_color</li>
 * <li>attach_default_animator</li>
 * </ul>
 * <hr />Does not extend TextView! <hr />
 *
 * @author rares
 */
@SuppressWarnings("SuspiciousNameCombination")
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

    private int targetTextColor = defaultTextColor;
    private int targetBackgroundColor = 0xFF303030;

    private int targetTextSize = minTextSize;
    private int textSize = 40;

    private boolean enableDefaultAnimator = true;
    private boolean enableDefaultClickListener = false;

    RotationAnimatorHost rotationAnimatorHost = null;
    private View.OnClickListener clickListener = null;
    private RotationAwareUpdateListener animationUpdateListener = null;

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
        textPaint.setTextSize(textSize);
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
            setTargetTextColor(a.getColor(R.styleable.RotationAwareTextView_target_text_color, targetTextColor));
            setTextSize(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_text_size, minTextSize));
            setTargetTextSize(a.getDimensionPixelSize(R.styleable.RotationAwareTextView_target_text_size, minTextSize));
            enableDefaultAnimator = a.getBoolean(R.styleable.RotationAwareTextView_attach_default_animator, true);
            enableDefaultClickListener = a.getBoolean(R.styleable.RotationAwareTextView_attach_default_click_listener, true);

            a.recycle();

            pseudoRotation = originalRotation;

            if (enableDefaultAnimator) {
                attachDefaultAnimator();
            }

            if (enableDefaultClickListener) {
                attachDefaultClickListener();
            }
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
        boolean changed = false;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int sizeW = MeasureSpec.getSize(widthMeasureSpec);
        int sizeH = MeasureSpec.getSize(heightMeasureSpec);

        if (originalWidth == ViewGroup.LayoutParams.MATCH_PARENT) {
            originalWidth = sizeW;
            changed = true;
        }
        if (originalWidth == ViewGroup.LayoutParams.WRAP_CONTENT) {
            originalWidth = (int) textPaint.measureText(text);
            changed = true;
        }
        if (originalHeight == ViewGroup.LayoutParams.MATCH_PARENT) {
            originalHeight = sizeH;
            changed = true;
        }
        if (originalHeight == ViewGroup.LayoutParams.WRAP_CONTENT) {
            originalHeight = (int) textPaint.getTextSize();
            changed = true;
        }

        if (widthMode == MeasureSpec.AT_MOST) {
            sizeW = Math.min((int) textPaint.measureText(text), sizeW);
        }

        if (heightMode == MeasureSpec.AT_MOST) {
            sizeH = Math.min((int) textPaint.getTextSize(), sizeH);
        }

        setMeasuredDimension(sizeW, sizeH);

        createLayout();

        if (changed && enableDefaultAnimator) {
            updateDefaultAnimator();
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        createLayout();
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
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.translate(
                (float) (Math.sin(Math.toRadians(-pseudoRotation))) * -mLayout.getHeight() / 2,
                (float) (Math.cos(Math.toRadians(pseudoRotation))) * -mLayout.getHeight() / 2);
        canvas.translate(canvas.getWidth() / 2, canvas.getHeight() / 2);
        canvas.rotate(getRotation());
        mLayout.draw(canvas);
        canvas.restore();
    }

    private AnimationDTO gatherAnimationData() {
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
        animationData.minTextSize = getTextSize();
        animationData.maxTextSize = getTargetTextSize();
        animationData.updateListener = animationUpdateListener;

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
        updateDefaultAnimator();
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
        createLayout();
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
        createLayout();
    }

    /**
     * Convenience method that directly sets paint color.
     *
     * @param color color in int format
     */
    public void setTextColor(int color) {
        textPaint.setColor(color);
        createLayout();
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
        updateDefaultAnimator();
    }

    /**
     * @return the width at the beginning of the rotation animation.
     */
    public int getOriginalWidth() {
        return originalWidth;
    }

    /**
     * @return the height at the beginning of the rotation animation.
     */
    public int getOriginalHeight() {
        return originalHeight;
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
        updateDefaultAnimator();
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
        updateDefaultAnimator();
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
        updateDefaultAnimator();
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
        updateDefaultAnimator();
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
    }

    /**
     * @return the text size at the end of the animation, in pixels.
     */
    public int getTargetTextSize() {
        return targetTextSize;
    }

    /**
     * Sets the size this control's text should be after rotation.
     *
     * @param targetTextSize size, in pixels.
     */
    public void setTargetTextSize(int targetTextSize) {
        this.targetTextSize = Math.max(minTextSize, targetTextSize);
        updateDefaultAnimator();
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
        createLayout();
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
     * @hide
     */
    public void setClearOnDetach(boolean clearOnDetach) {
        this.clearOnDetach = clearOnDetach;
    }

    /**
     * @return true if the default click listener is enabled.
     */
    public boolean isDefaultClickListenerEnabled() {
        return enableDefaultClickListener;
    }

    /**
     * Sets the value for the default click listener enabling flag.
     * Also enables or disables the default click listener.
     *
     * @param enableDefaultClickListener true for enabling,
     *                                   false for disabling
     *                                   the default click listener
     */
    public void setEnableDefaultClickListener(boolean enableDefaultClickListener) {
        this.enableDefaultClickListener = enableDefaultClickListener;
        if (enableDefaultClickListener) {
            attachDefaultClickListener();
        } else {
            detachClickListener();
        }
    }

    private void attachDefaultClickListener() {
        clickListener = new RotationAwareClickListener();
        setOnClickListener(clickListener);
    }

    private void detachClickListener() {
        clickListener = null;
        setOnClickListener(null);
    }

    /**
     * @return true if the default animator is enabled
     */
    public boolean isDefaultAnimatorEnabled() {
        return enableDefaultAnimator;
    }

    /**
     * Sets the value for the default animator enabling flag.
     * Also enables or disables the default animator.
     *
     * @param enableDefaultAnimator true for enabling,
     *                              false for disabling
     *                              the default animator
     */
    public void setEnableDefaultAnimator(boolean enableDefaultAnimator) {
        this.enableDefaultAnimator = enableDefaultAnimator;
        if (enableDefaultAnimator) {
            attachDefaultAnimator();
        } else {
            detachDefaultAnimator();
        }
    }

    private void updateDefaultAnimator() {
        if (enableDefaultAnimator && rotationAnimatorHost != null) {
            rotationAnimatorHost.updateAnimationData(gatherAnimationData());
        }
    }

    /**
     * Creates new rotation animators instances. <br />
     * Always call clear ({@link #detachDefaultAnimator}) when disposing them!
     */
    private void attachDefaultAnimator() {
        animationUpdateListener = new RotationAwareUpdateListener(this);

        AnimationDTO animationData = gatherAnimationData();

        rotationAnimatorHost = new DefaultRotationAnimatorHost(animationData);
    }

    /**
     * We are responsible for what we create.
     */
    private void detachDefaultAnimator() {
        if (animationUpdateListener != null) {
            animationUpdateListener.clear();
            animationUpdateListener = null;
        }
        if (rotationAnimatorHost != null) {
            rotationAnimatorHost.clear();
            rotationAnimatorHost = null;
        }
    }

    /**
     * create or update the layout.
     */
    private void createLayout() {
        BoringLayout.Metrics metrics = new BoringLayout.Metrics();
        metrics.width = originalWidth; //Math.max(originalWidth, originalHeight);
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
                    0,
                    Layout.Alignment.ALIGN_CENTER,
                    0F,
                    0F,
                    boringMetrics,
                    true);
        } else {
            mLayout = BoringLayout.make(
                    text,
                    textPaint,
                    0,
                    Layout.Alignment.ALIGN_CENTER,
                    0F,
                    0F,
                    boringMetrics,
                    true);
        }
    }

    /**
     * This kind of renders the view unusable.
     */
    private void clear() {
        if (clearOnDetach) {
            detachDefaultAnimator();
            detachClickListener();
            setBackgroundDrawable(null);
            mLayout = null;
            textPaint = null;
            p = null;
            text = null;
        }
    }
}