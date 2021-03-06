package edu.umd.hcil.impressionistpainter434;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.ImageView;

import java.text.MessageFormat;
import java.util.Random;

/**
 * Created by jon on 3/20/2016.
 */
public class ImpressionistView extends View {

    private ImageView _imageView;

    private Canvas _offScreenCanvas = null;
    private Bitmap _offScreenBitmap = null;
    private Paint _paint = new Paint();

    private int _alpha = 150;
    private int _defaultRadius = 25;
    private Point _lastPoint = null;
    private long _lastPointTime = -1;
    private boolean _useMotionSpeedForBrushStrokeSize = true;
    private Paint _paintBorder = new Paint();
    private BrushType _brushType = BrushType.Square;
    private float _minBrushRadius = 5;

    private VelocityTracker velocity = null;
    private boolean _invertColor = false;
    private boolean _clickDraw = false;

    public ImpressionistView(Context context) {
        super(context);
        init(null, 0);
    }

    public ImpressionistView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public ImpressionistView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    /**
     * Because we have more than one constructor (i.e., overloaded constructors), we use
     * a separate initialization method
     * @param attrs
     * @param defStyle
     */
    private void init(AttributeSet attrs, int defStyle){

        // Set setDrawingCacheEnabled to true to support generating a bitmap copy of the view (for saving)
        // See: http://developer.android.com/reference/android/view/View.html#setDrawingCacheEnabled(boolean)
        //      http://developer.android.com/reference/android/view/View.html#getDrawingCache()
        this.setDrawingCacheEnabled(true);

        _paint.setColor(Color.RED);
        _paint.setAlpha(_alpha);
        _paint.setAntiAlias(true);
        _paint.setStyle(Paint.Style.FILL);
        _paint.setStrokeWidth(4);

        _paintBorder.setColor(Color.BLACK);
        _paintBorder.setStrokeWidth(3);
        _paintBorder.setStyle(Paint.Style.STROKE);
        _paintBorder.setAlpha(50);

        //_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));
    }

    public Bitmap get_offScreenBitmap() {
        return _offScreenBitmap;
    }

    @Override
    protected void onSizeChanged (int w, int h, int oldw, int oldh){

        Bitmap bitmap = getDrawingCache();
        Log.v("onSizeChanged", MessageFormat.format("bitmap={0}, w={1}, h={2}, oldw={3}, oldh={4}", bitmap, w, h, oldw, oldh));
        if(bitmap != null) {
            _offScreenBitmap = getDrawingCache().copy(Bitmap.Config.ARGB_8888, true);
            _offScreenCanvas = new Canvas(_offScreenBitmap);
        }
    }

    /**
     * Sets the ImageView, which hosts the image that we will paint in this view
     * @param imageView
     */
    public void setImageView(ImageView imageView){
        _imageView = imageView;
    }

    /**
     * Sets the brush type. Feel free to make your own and completely change my BrushType enum
     * @param brushType
     */
    public void setBrushType(BrushType brushType){
        _brushType = brushType;
    }

    public void set_invertColor(boolean invertColor) { _invertColor = invertColor; }

    public void set_clickDraw(boolean clickDraw) { _clickDraw = clickDraw;}

    /**
     * Clears the painting
     */
    public void clearPainting(){
        _offScreenBitmap.eraseColor(Color.TRANSPARENT);
        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(_offScreenBitmap != null) {
            canvas.drawBitmap(_offScreenBitmap, 0, 0, _paint);
        }

        // Draw the border. Helpful to see the size of the bitmap in the ImageView
        canvas.drawRect(getBitmapPositionInsideImageView(_imageView), _paintBorder);
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent){

        //TODO
        //Basically, the way this works is to listen for Touch Down and Touch Move events and determine where those
        //touch locations correspond to the bitmap in the ImageView. You can then grab info about the bitmap--like the pixel color--
        //at that location

        float touchX = motionEvent.getX();
        float touchY = motionEvent.getY();

        Random rand = new Random();

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (_brushType == BrushType.VelocityCircle || _brushType == BrushType.VelocityRectangle) {
                    if (!_clickDraw) {
                        if (velocity == null) {
                            velocity = VelocityTracker.obtain();
                        } else {
                            velocity.clear();
                        }

                        velocity.addMovement(motionEvent);
                    }
                } else if (_brushType == BrushType.Line) {
                    if (_clickDraw) {
                        Bitmap imageViewBitmap = _imageView.getDrawingCache();
                        Rect border = getBitmapPositionInsideImageView(_imageView);

                        for (int i = 0; i <= 400; i++) {
                            int randX = rand.nextInt(border.right);
                            int randY = rand.nextInt(border.bottom);

                            if (border.contains(randX, randY)) {
                                int pixelColor = imageViewBitmap.getPixel(randX, randY);
                                if (!_invertColor) {
                                    _paint.setColor(pixelColor);
                                } else {
                                    _paint.setColor(0xFFFFFFFF - pixelColor);
                                }
                                _paint.setAlpha(200);

                                _offScreenCanvas.drawLine(randX, randY, randX + 50, randY + 50, _paint);
                            }
                        }

                        invalidate();
                    }
                } else if (_brushType == BrushType.Dots) {
                    if (_clickDraw) {
                        Bitmap imageViewBitmap = _imageView.getDrawingCache();
                        Rect border = getBitmapPositionInsideImageView(_imageView);

                        for (int i = 0; i <= 3000; i++) {
                            int randX = rand.nextInt(border.right);
                            int randY = rand.nextInt(border.bottom);

                            if (border.contains(randX, randY)) {
                                int pixelColor = imageViewBitmap.getPixel(randX, randY);
                                if (!_invertColor) {
                                    _paint.setColor(pixelColor);
                                } else {
                                    _paint.setColor(0xFFFFFFFF - pixelColor);
                                }
                                _paint.setAlpha(200);

                                _offScreenCanvas.drawPoint((float) randX, (float) randY, _paint);
                            }
                        }

                        invalidate();
                    }
                } else if (_brushType == BrushType.Square) {
                    if (_clickDraw) {
                        Bitmap imageViewBitmap = _imageView.getDrawingCache();
                        Rect border = getBitmapPositionInsideImageView(_imageView);

                        for (int i = 0; i <= 100; i++) {
                            int randX = rand.nextInt(border.right);
                            int randY = rand.nextInt(border.bottom);

                            if (border.contains(randX, randY)) {
                                int pixelColor = imageViewBitmap.getPixel(randX, randY);
                                if (!_invertColor) {
                                    _paint.setColor(pixelColor);
                                } else {
                                    _paint.setColor(0xFFFFFFFF - pixelColor);
                                }
                                _paint.setAlpha(200);

                                _offScreenCanvas.drawRect((float) randX, (float) randY, (float) randX + 75, (float) randY + 75, _paint);
                            }
                        }

                        invalidate();
                    }
                } else if (_brushType == BrushType.Circle) {
                    if (_clickDraw) {
                        Bitmap imageViewBitmap = _imageView.getDrawingCache();
                        Rect border = getBitmapPositionInsideImageView(_imageView);

                        for (int i = 0; i <= 100; i++) {
                            int randX = rand.nextInt(border.right);
                            int randY = rand.nextInt(border.bottom);

                            if (border.contains(randX, randY)) {
                                int pixelColor = imageViewBitmap.getPixel(randX, randY);
                                if (!_invertColor) {
                                    _paint.setColor(pixelColor);
                                } else {
                                    _paint.setColor(0xFFFFFFFF - pixelColor);
                                }
                                _paint.setAlpha(200);

                                _offScreenCanvas.drawCircle((float) randX, (float) randY, 50, _paint);
                            }
                        }

                        invalidate();
                    }
                } else if (_brushType == BrushType.Caret) {
                    if (_clickDraw) {
                        Bitmap imageViewBitmap = _imageView.getDrawingCache();
                        Rect border = getBitmapPositionInsideImageView(_imageView);

                        for (int i = 0; i <= 200; i++) {
                            int randX = rand.nextInt(border.right);
                            int randY = rand.nextInt(border.bottom);

                            if (border.contains(randX, randY)) {
                                int pixelColor = imageViewBitmap.getPixel(randX, randY);
                                if (!_invertColor) {
                                    _paint.setColor(pixelColor);
                                } else {
                                    _paint.setColor(0xFFFFFFFF - pixelColor);
                                }
                                _paint.setAlpha(200);

                                _offScreenCanvas.drawLine((float) randX, (float) randY, (float) randX + 50, (float) randY + 50, _paint);
                                _offScreenCanvas.drawLine((float) randX, (float) randY, (float) randX - 50, (float) randY + 50, _paint);
                            }
                        }

                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_MOVE:
                if (!_clickDraw) {
                    Bitmap imageViewBitmap = _imageView.getDrawingCache();
                    Rect border = getBitmapPositionInsideImageView(_imageView);

                    if (border.contains((int) touchX, (int) touchY)) {
                        int pixelColor = imageViewBitmap.getPixel((int) touchX, (int) touchY);
                        if (!_invertColor) {
                            _paint.setColor(pixelColor);
                        } else {
                            _paint.setColor(0xFFFFFFFF - pixelColor);
                        }
                        _paint.setAlpha(200);

                        if (_brushType == BrushType.Square) {
                            _offScreenCanvas.drawRect(touchX, touchY, touchX + 75, touchY + 75, _paint);
                        } else if (_brushType == BrushType.Circle) {
                            _offScreenCanvas.drawCircle(touchX, touchY, 50, _paint);
                        } else if (_brushType == BrushType.Line) {
                            _offScreenCanvas.drawLine(touchX, touchY, touchX + 50, touchY + 50, _paint);
                        } else if (_brushType == BrushType.VelocityCircle) {
                            velocity.addMovement(motionEvent);
                            velocity.computeCurrentVelocity(1000);

                            _offScreenCanvas.drawCircle(touchX, touchY, 25 + (velocity.getXVelocity() + velocity.getYVelocity()) / 200, _paint);
                        } else if (_brushType == BrushType.VelocityRectangle) {
                            velocity.addMovement(motionEvent);
                            velocity.computeCurrentVelocity(1000);

                            _offScreenCanvas.drawRect(touchX, touchY, touchX + 50 + velocity.getXVelocity() / 50, touchY + 50 + velocity.getYVelocity() / 50, _paint);
                        } else if (_brushType == BrushType.Caret) {
                            _offScreenCanvas.drawLine(touchX, touchY, touchX + 50, touchY + 50, _paint);
                            _offScreenCanvas.drawLine(touchX, touchY, touchX - 50, touchY + 50, _paint);
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }

        invalidate();

        return true;
    }




    /**
     * This method is useful to determine the bitmap position within the Image View. It's not needed for anything else
     * Modified from:
     *  - http://stackoverflow.com/a/15538856
     *  - http://stackoverflow.com/a/26930938
     * @param imageView
     * @return
     */
    private static Rect getBitmapPositionInsideImageView(ImageView imageView){
        Rect rect = new Rect();

        if (imageView == null || imageView.getDrawable() == null) {
            return rect;
        }

        // Get image dimensions
        // Get image matrix values and place them in an array
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int widthActual = Math.round(origW * scaleX);
        final int heightActual = Math.round(origH * scaleY);

        // Get image position
        // We assume that the image is centered into ImageView
        int imgViewW = imageView.getWidth();
        int imgViewH = imageView.getHeight();

        int top = (int) (imgViewH - heightActual)/2;
        int left = (int) (imgViewW - widthActual)/2;

        rect.set(left, top, left + widthActual, top + heightActual);

        return rect;
    }
}

