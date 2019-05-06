package official.com.windowdetailssharingapp.drawing;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import official.com.windowdetailssharingapp.R;
import official.com.windowdetailssharingapp.core.db.LocalStorage;

public class DrawingActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawingView mDrawingView;
    private ImageButton currPaint;
    private float smallBrush, mediumBrush, largeBrush;

    private int index;
    private String doorWindow;
    private String project;
    private String property;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_sketch);

        // Configure action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Get intent extras
        Intent i = getIntent();
        index = i.getIntExtra("index", -1);
        property = i.getStringExtra("property");
        doorWindow = i.getStringExtra("doorWindow");
        project = i.getStringExtra("project");
        if (index == -1 || property == null) {
            finish();
            return;
        }

        this.setTitle(property);

        mDrawingView = (DrawingView) findViewById(R.id.drawing);
        mDrawingView.setBaseBitmap(LocalStorage.getBitmap(index, doorWindow, project));

        // Getting the initial paint color.
        LinearLayout paintLayout = (LinearLayout) findViewById(R.id.paint_colors);

        // 0th child is white color, so selecting first child to give black as initial color.
        currPaint = (ImageButton) paintLayout.getChildAt(1);

        currPaint.setImageDrawable(getResources().getDrawable(R.drawable.pallet_pressed));

        Button drawButton = findViewById(R.id.buttonBrush);
        drawButton.setOnClickListener(this);
        Button eraseButton = findViewById(R.id.buttonErase);
        eraseButton.setOnClickListener(this);
        Button newButton = findViewById(R.id.buttonNew);
        newButton.setOnClickListener(this);

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        // Set the initial brush size
        mDrawingView.setBrushSize(smallBrush);

    }

    /**
     * Method is called when color is clicked from pallet.
     *
     * @param view ImageButton on which click took place.
     */
    public void paintClicked(View view) {
        if (view != currPaint) {
            // Update the color
            ImageButton imageButton = (ImageButton) view;
            String colorTag = imageButton.getTag().toString();
            mDrawingView.setColor(colorTag);
            // Swap the backgrounds for last active and currently active image button.
            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.pallet_pressed));
            currPaint.setImageDrawable(getResources().getDrawable(R.drawable.pallet));
            currPaint = (ImageButton) view;
            mDrawingView.setErase(false);
            mDrawingView.setBrushSize(mDrawingView.getLastBrushSize());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.buttonBrush:
                // Show brush size chooser dialog
                showBrushSizeChooserDialog();
                break;
            case R.id.buttonErase:
                // Show eraser size chooser dialog
                showEraserSizeChooserDialog();
                break;
            case R.id.buttonNew:
                // Show new painting alert dialog
                showNewPaintingAlertDialog();
                break;
        }
    }

    private void showBrushSizeChooserDialog() {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setContentView(R.layout.dialog_drawing_brush);
        brushDialog.setTitle("Brush size:");
        ImageButton smallBtn = brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.setBrushSize(smallBrush);
                mDrawingView.setLastBrushSize(smallBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumBtn = brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.setBrushSize(mediumBrush);
                mDrawingView.setLastBrushSize(mediumBrush);
                brushDialog.dismiss();
            }
        });

        ImageButton largeBtn = brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.setBrushSize(largeBrush);
                mDrawingView.setLastBrushSize(largeBrush);
                brushDialog.dismiss();
            }
        });
        mDrawingView.setErase(false);
        brushDialog.show();
    }

    private void showEraserSizeChooserDialog() {
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle("Eraser size:");
        brushDialog.setContentView(R.layout.dialog_drawing_brush);
        ImageButton smallBtn = brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.setErase(true);
                mDrawingView.setBrushSize(smallBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton mediumBtn = brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.setErase(true);
                mDrawingView.setBrushSize(mediumBrush);
                brushDialog.dismiss();
            }
        });
        ImageButton largeBtn = brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawingView.setErase(true);
                mDrawingView.setBrushSize(largeBrush);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }

    private void showNewPaintingAlertDialog() {
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle("New drawing");
        newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
        newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mDrawingView.reset();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    private void showSavePaintingConfirmationDialog() {
        AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
        saveDialog.setTitle("Save Drawing");
        saveDialog.setMessage("Do you want to save sketch for the '" + property + "' property?");
        saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Get canvas bitmap
                mDrawingView.setDrawingCacheEnabled(true);
                Bitmap bitmap = mDrawingView.getDrawingCache();

                // Save bitmap to file
                boolean saved = LocalStorage.saveBitmap(bitmap, index, doorWindow, project);
                if (saved) {
                    Toast.makeText(DrawingActivity.this,
                            "Sketch saved!", Toast.LENGTH_SHORT)
                            .show();

                    finish();
                } else {
                    Toast.makeText(DrawingActivity.this,
                            "Failed to save. Check app permissions.", Toast.LENGTH_SHORT)
                            .show();
                }
                // Destroy the current cache.
                mDrawingView.destroyDrawingCache();
            }
        });
        saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        saveDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_property_sketch, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        // noinspection SimplifiableIfStatement

        if (id == R.id.action_save_drawing) {

            showSavePaintingConfirmationDialog();
            return true;
        }

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
