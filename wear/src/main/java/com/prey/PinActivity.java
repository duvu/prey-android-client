package com.prey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;

/**
 * Created by oso on 29-09-16.
 */

public class PinActivity extends Activity {

    private static float sideIndexX;
    private static float sideIndexY;

    private ImageView[][] gridPointsImage;
    private int[][][] gridPoints;
    private TextView[][] gridPointsText;

    private String[][] gridPassword;

    private int positionCounter;

    private final int x=0;
    private final int y=1;

    private String clave="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sequence_layout);


        findViewById(R.id.sequenceLayout).setOnTouchListener(new PinActivity.C03351());


        this.gridPassword = (String[][]) Array.newInstance(String.class, new int[]{3, 3});
        this.gridPassword[0][0] = "A";
        this.gridPassword[0][1] = "B";
        this.gridPassword[0][2] = "C";
        this.gridPassword[1][0] = "D";
        this.gridPassword[1][1] = "E";
        this.gridPassword[1][2] = "F";
        this.gridPassword[2][0] = "G";
        this.gridPassword[2][1] = "H";
        this.gridPassword[2][2] = "I";


        this.gridPointsImage = (ImageView[][]) Array.newInstance(ImageView.class, new int[]{3, 3});
        this.gridPointsImage[0][0] = (ImageView) findViewById(R.id.grid11Image);
        this.gridPointsImage[0][1] = (ImageView) findViewById(R.id.grid12Image);
        this.gridPointsImage[0][2] = (ImageView) findViewById(R.id.grid13Image);
        this.gridPointsImage[1][0] = (ImageView) findViewById(R.id.grid21Image);
        this.gridPointsImage[1][1] = (ImageView) findViewById(R.id.grid22Image);
        this.gridPointsImage[1][2] = (ImageView) findViewById(R.id.grid23Image);
        this.gridPointsImage[2][0] = (ImageView) findViewById(R.id.grid31Image);
        this.gridPointsImage[2][1] = (ImageView) findViewById(R.id.grid32Image);
        this.gridPointsImage[2][2] = (ImageView) findViewById(R.id.grid33Image);


        gridPoints=new int[3][3][2];
        gridPoints[0][0][x]=106;
        gridPoints[0][0][y]=106;
        gridPoints[1][0][x]=106;
        gridPoints[1][0][y]=212;
        gridPoints[2][0][x]=106;
        gridPoints[2][0][y]=320;

        gridPoints[0][1][x]=212;
        gridPoints[0][1][y]=106;
        gridPoints[1][1][x]=212;
        gridPoints[1][1][y]=212;
        gridPoints[2][1][x]=212;
        gridPoints[2][1][y]=320;

        gridPoints[0][2][x]=320;
        gridPoints[0][2][y]=106;
        gridPoints[1][2][x]=320;
        gridPoints[1][2][y]=212;
        gridPoints[2][2][x]=320;
        gridPoints[2][2][y]=320;


        this.gridPointsText = (TextView[][]) Array.newInstance(TextView.class, new int[]{3, 3});
        this.gridPointsText[0][0] = (TextView) findViewById(R.id.grid11Text);
        this.gridPointsText[0][1] = (TextView) findViewById(R.id.grid12Text);
        this.gridPointsText[0][2] = (TextView) findViewById(R.id.grid13Text);
        this.gridPointsText[1][0] = (TextView) findViewById(R.id.grid21Text);
        this.gridPointsText[1][1] = (TextView) findViewById(R.id.grid22Text);
        this.gridPointsText[1][2] = (TextView) findViewById(R.id.grid23Text);
        this.gridPointsText[2][0] = (TextView) findViewById(R.id.grid31Text);
        this.gridPointsText[2][1] = (TextView) findViewById(R.id.grid32Text);
        this.gridPointsText[2][2] = (TextView) findViewById(R.id.grid33Text);



        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int final_i = i;
                int final_j = j;
                this.gridPointsImage[i][j].setOnTouchListener(new PinActivity.C03362());
                this.gridPointsText[i][j].setOnTouchListener(new PinActivity.C03373());
            }
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int screenWidth = metrics.widthPixels;
        Log.i("PREY", "Actual Screen Height = " + screenHeight + " Width = " + screenWidth);



    }

    class C03351 implements View.OnTouchListener {
        C03351() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            PinActivity.sideIndexX = event.getX();
            PinActivity.sideIndexY = event.getY();

            //Log.i("PREY","action:"+event.getAction()+" x:"+sideIndexX+" y:"+sideIndexY);
            int gri_i = -1;
            int gri_j = -1;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (PinActivity.this.inRegion(event.getRawX(), event.getRawY(), i, j)) {
                        gri_i = i;
                        gri_j = j;
                        break;
                    }
                }
            }
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN://0
                    Log.i("PREY","action down");
                    break;
                case MotionEvent.ACTION_UP://1
                    Log.i("PREY","action up");
                    resetPoints();
                    break;
                case MotionEvent.ACTION_MOVE://2
                    //Log.i("PREY","action move");
                    if (gri_i == -1 || gri_j == -1) {
                        return false;
                    } else {
                        if(!clave.contains(gridPassword[gri_i][gri_j])) {
                            clave += gridPassword[gri_i][gri_j];
                            Log.i("PREY","clave:"+clave);
                        }
                        if ("ADGH".equals(clave)){
                            Intent startIntent = new Intent(getApplicationContext(), ListDevicesActivity.class);
                            startActivity(startIntent);
                        }

                        PinActivity.this.gridPointsImage[gri_i][gri_j].setImageResource(R.drawable.pattern_pin_selected);
                        //  LoginActivity.this.password = LoginActivity.this.password + LoginActivity.this.gridPassword[gri_i][gri_j];
                        PinActivity.this.gridPointsText[gri_i][gri_j].setText(BuildConfig.FLAVOR + PinActivity.this.positionCounter);
                    }

                    break;
                case MotionEvent.ACTION_CANCEL://3
                    Log.i("PREY","action cancel");
                    break;
                default:
                    break;
            }

            // Log.i("PREY","gri_i:"+gri_i+" gri_j:"+gri_j);




            return false;
        }
    }

    public void resetPoints(){
        for (int i=0;i<3;i++){
            for(int j=0;j<3;j++){
                PinActivity.this.gridPointsImage[i][j].setImageResource(R.drawable.pattern_pin);
                PinActivity.this.gridPointsText[i][j].setText(BuildConfig.FLAVOR );
            }
        }
        clave="";
    }

    private boolean inRegion(float xxx, float yyy, int i ,int j) {


        //Log.i("PREY","inRegion x:"+xxx+" y:"+yyy+" i:"+i+" j:"+j);
        int xx=gridPoints[i][j][x];
        int yy=gridPoints[i][j][y];
        int xx1=xx-106;
        int yy1=yy-106;
        if(xxx<xx && xxx>xx1   && yyy<yy && yyy>yy1){
            return true;
        }
        return false;
    }

    class C03362 implements View.OnTouchListener {

        C03362() {
        }


        public boolean onTouch(View v, MotionEvent event) { return false; }
    }


    class C03373 implements View.OnTouchListener {
        C03373() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }


}
