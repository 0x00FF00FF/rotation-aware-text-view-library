package org.rares.ratv.rotationaware.animation;

/**
 * Data transfer object for sending animation values around.
 */
public class AnimationDTO {
    public int minRotation;
    public int maxRotation;
    public int minWidth;
    public int maxWidth;
    public int minHeight;
    public int maxHeight;
    public int minTextSize;
    public int maxTextSize;
    public int minTextColor;
    public int maxTextColor;
    public int minBackgroundColor;
    public int maxBackgroundColor;
    public int minMarginLeft;
    public int minMarginTop;
    public int minMarginRight;
    public int minMarginBottom;
    public int maxMarginLeft;
    public int maxMarginTop;
    public int maxMarginRight;
    public int maxMarginBottom;

    public int minShadowColor;
    public int maxShadowColor;
    public int minShadowRadius;
    public int maxShadowRadius;

    @Override
    public String toString() {
        return AnimationDTO.class.getSimpleName() + "#" + this.hashCode() +
                "\nminRotation: " + minRotation +
                "\nmaxRotation: " + maxRotation +
                "\nminWidth: " + minWidth +
                "\nmaxWidth: " + maxWidth +
                "\nminHeight: " + minHeight +
                "\nmaxHeight: " + maxHeight +
                "\nminMarginLeft: " + minMarginLeft +
                "\nminMarginTop: " + minMarginTop +
                "\nminMarginRight: " + minMarginRight +
                "\nminMarginBottom: " + minMarginBottom +
                "\nmaxMarginLeft: " + maxMarginLeft +
                "\nmaxMarginTop: " + maxMarginTop +
                "\nmaxMarginRight: " + maxMarginRight +
                "\nmaxMarginBottom: " + maxMarginBottom +
                "\nminTextSize: " + minTextSize +
                "\nmaxTextSize: " + maxTextSize +
                "\nminTextColor: " + minTextColor +
                "\nmaxTextColor: " + maxTextColor +
                "\nminShadowRadius: " + minShadowRadius +
                "\nmaxShadowRadius: " + maxShadowRadius +
                "\nminShadowColor: " + minShadowColor +
                "\nmaxShadowColor: " + maxShadowColor +
                "\nminBackgroundColor: " + minBackgroundColor +
                "\nmaxBackgroundColor: " + maxBackgroundColor;
    }
}