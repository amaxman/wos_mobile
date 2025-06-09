package wos.mobile.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.util.AttributeSet;

import wos.mobile.R;


public class CustomFab extends androidx.appcompat.widget.AppCompatImageButton {

    private Context ctx;
    private int bgColor;
    private int bgColorPressed;

    public CustomFab(Context context) {
        super(context);
        this.ctx = context;
        init(null);
    }

    public CustomFab(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.ctx = context;
        init(attrs);
    }

    public CustomFab(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.ctx = context;
        init(attrs);
    }

    private Drawable createButton(int color) {
        OvalShape oShape = new OvalShape();
        ShapeDrawable sd = new ShapeDrawable(oShape);
        setWillNotDraw(false);
        sd.getPaint().setColor(color);

        ShapeDrawable sd1 = new ShapeDrawable(oShape);

        sd1.setShaderFactory(new ShapeDrawable.ShaderFactory() {
            @Override
            public Shader resize(int width, int height) {
                LinearGradient lg = new LinearGradient(0,0,0, height,
                        new int[] {
                                Color.WHITE,
                                Color.GRAY,
                                Color.DKGRAY,
                                Color.BLACK
                        }, null, Shader.TileMode.REPEAT);

                return lg;
            }
        });

        LayerDrawable ld = new LayerDrawable(new Drawable[] { sd1, sd });
        ld.setLayerInset(0, 5, 5, 0, 0);
        ld.setLayerInset(1, 0, 0, 5, 5);

        return ld;
    }

    private void init(AttributeSet attrSet) {
        Resources.Theme theme = ctx.getTheme();
        TypedArray arr = theme.obtainStyledAttributes(attrSet, R.styleable.Fab, 0, 0);
        try {
            setBgColor(arr.getColor(R.styleable.Fab_bg_color, Color.TRANSPARENT));
            setBgColorPressed(arr.getColor(R.styleable.Fab_bg_color_pressed, Color.TRANSPARENT));
            StateListDrawable sld = new StateListDrawable();

            sld.addState(new int[] {android.R.attr.state_pressed}, createButton(bgColorPressed));
            sld.addState(new int[] {}, createButton(bgColor));
            setBackground(sld);
        }

        catch(Throwable t) {}
        finally {
            arr.recycle();
        }

    }

    public void setBgColor(int color) {
        this.bgColor = color;
    }

    public void setBgColorPressed(int color) {
        this.bgColorPressed = color;
    }
}
