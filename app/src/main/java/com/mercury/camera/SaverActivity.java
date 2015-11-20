package com.mercury.camera;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SaverActivity extends Activity {

    public static final String PICTURE_EXTRA = "pictureFile";
    public static final String ORIENTATION_EXTRA = "orientationInt";

    private Bitmap mImage;

    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.saver_activity);
        File temp = (File) getIntent().getSerializableExtra(PICTURE_EXTRA);
        int orientation = getIntent().getIntExtra(ORIENTATION_EXTRA, 0);
        try {
            mImage = rotateBitmap(BitmapFactory.decodeStream(new FileInputStream(temp)), -orientation);
            ImageView imageView = (ImageView) findViewById(R.id.image_view);
            imageView.setImageBitmap(mImage);
        } catch (IOException e) {
            e.printStackTrace();
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.saver_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.saver_menu_save:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                final EditText dialogEditText = new EditText(this);
                dialogEditText.setText("Photo.jpg");
                builder.setView(dialogEditText)
                        .setTitle(R.string.enter_name)
                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                writeToFile(dialogEditText.getText().toString());
                                dialog.dismiss();
                            }
                        }).
                        setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void writeToFile(String s) {
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), s);
        try {
            FileOutputStream fOut = new FileOutputStream(path);
            mImage.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
            fOut.close();
            Toast.makeText(this, "Saved", Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
