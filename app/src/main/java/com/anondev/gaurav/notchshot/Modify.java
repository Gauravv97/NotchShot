package com.anondev.gaurav.notchshot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.TransitionOptions;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class Modify extends AppCompatActivity {
    ImageView imageView;
    SeekBar s1,s2,s3,s4;
    LinearLayout linearLayout;
    Toolbar toolbar;
    ImageButton save;
    FrameLayout frameLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        imageView=findViewById(R.id.imageView_modify);
        frameLayout=findViewById(R.id.NewLayout);
        s1=findViewById(R.id.EdgeSeekbar);
        s2=findViewById(R.id.NotchWidth);
        s3=findViewById(R.id.NotchRoundBar);
        s4=findViewById(R.id.NotchHeight);
        linearLayout=findViewById(R.id.Notch);
        save=findViewById(R.id.Save);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                frameLayout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                Bitmap bitmap = Bitmap.createBitmap(frameLayout.getMeasuredWidth(), frameLayout.getMeasuredHeight(),
                            Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                frameLayout.layout(0, 0, frameLayout.getMeasuredWidth(), frameLayout.getMeasuredHeight());
                frameLayout.draw(canvas);
                String path=Save();
                FileOutputStream fos = null;
                File file=new File(path);
                try {

                    fos = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);

                }
                catch (FileNotFoundException e) {}
                Uri uri = FileProvider.getUriForFile(Modify.this, "com.anondev.gaurav.notchshot.fileprovider", file);
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("image/jpg");
                Modify.this.startActivity(Intent.createChooser(intent, "Share Your shot"));
                Modify.this.finish();

            }
        });
        setBitmap(5);
        setNotch(5,5,5);
        s1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setBitmap(i);

            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        s3.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setNotch(i,s2.getProgress(),s4.getProgress());

            }



            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        s2.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setNotch(s3.getProgress(),i,s4.getProgress());

            }



            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        s4.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                setNotch(s3.getProgress(),s2.getProgress(),i);

            }



            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
    private void setBitmap(int i){
        final Bitmap bitmap=roundCornerImage(BitmapFactory.decodeFile(getIntent().getExtras().getString("path")),i*15);
        Glide.with(this)
                .load(bitmap)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        bitmap.recycle();
                        return false;
                    }

                })
                .into(imageView);

    }
    void setNotch(int i,int j,int k){
        GradientDrawable shape =  new GradientDrawable();
        ViewGroup.LayoutParams l=linearLayout.getLayoutParams();
        l.width=j*40;
        l.height=k*10;
        linearLayout.setLayoutParams(l);
        shape.setCornerRadii( new float[]{0,0,0,0,i*8,i*8,i*8,i*8} );
        shape.setColor(Color.BLACK);
        linearLayout.setBackground(shape);
    }
    public Bitmap roundCornerImage(Bitmap raw, float round) {
        int width = raw.getWidth();
        int height = raw.getHeight();
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(result);
        canvas.drawARGB(0, 0, 0, 0);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);

        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        canvas.drawRoundRect(rectF, round, round, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(raw, rect, rect, paint);

        return result;
    }
    public String Save(){
        try {File image = File.createTempFile(
                "NotchShot",  /* prefix */
                ".jpg",         /* suffix */
                getExternalFilesDir("Pictures")      /* directory */
        );
            return image.getAbsolutePath();
        } catch (IOException ex) {
            // Error occurred while creating the File
        }
        return null;
    }
}
