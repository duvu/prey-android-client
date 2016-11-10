package com.prey;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.reflect.Array;

public class LoginActivity extends Activity    {
    private Button one, two, three, four, five, six, seven, eight, nine, zero;
    private boolean isTouch = false;
    private String clave="";
    float initialX, initialY;
    private static final String  TAG = "PREY";

    ImageView[][] gridPointsImage;
    TextView[][] gridPointsText;
    private static float sideIndexX;
    private static float sideIndexY;

    static boolean PRESSING = false;
    static boolean READ_FROM_DB = false;

    private String password;
    private int positionCounter;

    String[][] gridPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sequence_layout);

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

        findViewById(R.id.sequenceLayout).setOnTouchListener(new C03351());

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int final_i = i;
                int final_j = j;
                this.gridPointsImage[i][j].setOnTouchListener(new C03362());
                this.gridPointsText[i][j].setOnTouchListener(new C03373());
            }
        }
/*
        one = (Button) findViewById(R.id.one);
        two = (Button) findViewById(R.id.two);
        three = (Button) findViewById(R.id.three);
        four = (Button) findViewById(R.id.four);
        five = (Button) findViewById(R.id.five);
        six = (Button) findViewById(R.id.six);
        seven = (Button) findViewById(R.id.seven);
        eight = (Button) findViewById(R.id.eight);
        nine = (Button) findViewById(R.id.nine);
        zero = (Button) findViewById(R.id.zero);


        try {
            one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clave+="1";
                    Log.i("PREY","clave:"+clave);
                }
            });

            two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clave+="2";
                    Log.i("PREY","clave:"+clave);
                }
            });

            three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clave+="3";
                    Log.i("PREY","clave:"+clave);
                }
            });

            four.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clave+="4";
                    Log.i("PREY","clave:"+clave);
                }
            });

            five.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clave+="5";
                }
            });

            six.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clave+="6";
                    Log.i("PREY","clave:"+clave);
                }
            });

            seven.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clave+="7";
                    Log.i("PREY","clave:"+clave);
                }
            });

            eight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clave+="8";
                    Log.i("PREY","clave:"+clave);
                }
            });

            nine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clave+="9";
                    Log.i("PREY","clave:"+clave);
                }
            });

            zero.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clave+="0";
                    Log.i("PREY","clave:"+clave);
                }
            });

        } catch (Exception e) {

        }*/


    }


    private boolean inRegion(float x, float y, View v) {
        this.gridPointsImage[0][0].hasFocus();
        int[] mCoordBuffer = new int[2];
        v.getLocationOnScreen(mCoordBuffer);
        if (((float) (mCoordBuffer[0] + v.getWidth())) <= x || ((float) (mCoordBuffer[1] + v.getHeight())) <= y || ((float) mCoordBuffer[0]) >= x || ((float) mCoordBuffer[1]) >= y) {
            return false;
        }
        return true;
    }

    public void resetPassword(View view) {
        this.password = BuildConfig.FLAVOR;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.gridPointsImage[i][j].setImageResource(R.drawable.pattern_pin);
                this.gridPointsText[i][j].setText(BuildConfig.FLAVOR);
            }
        }
    }

    public void reloadPassword(View view) {
        resetPassword(view);
        if (false){//this.settings.getPasswordType().equals("pattern")) {
            READ_FROM_DB = true;
            this.password = "oso";
            int counter = 1;
            for (char letter : this.password.toCharArray()) {
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 3; j++) {
                        if (this.gridPassword[i][j].toCharArray()[0] == letter) {
                            this.gridPointsImage[i][j].setImageResource(R.drawable.pattern_pin_selected);
                            this.gridPointsText[i][j].setText(BuildConfig.FLAVOR + counter);
                            counter++;
                        }
                    }
                }
            }
        }
    }

    class C03362 implements View.OnTouchListener {
        C03362() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }


    class C03373 implements View.OnTouchListener {
        C03373() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            return false;
        }
    }


    class C03351 implements View.OnTouchListener {
        C03351() {
        }

        public boolean onTouch(View v, MotionEvent event) {
            LoginActivity.sideIndexX = event.getX();
            LoginActivity.sideIndexY = event.getY();
            int gri_i = -1;
            int gri_j = -1;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    if (LoginActivity.this.inRegion(event.getRawX(), event.getRawY(), LoginActivity.this.gridPointsImage[i][j])) {
                        gri_i = i;
                        gri_j = j;
                        break;
                    }
                }
            }
            if (event.getAction() == 0) {
                if (LoginActivity.PRESSING) {
                    LoginActivity.this.reloadPassword(null);
                    LoginActivity.PRESSING = false;

                } else if (gri_i == -1 || gri_j == -1) {
                    return false;
                } else {
                    /*if (LoginActivity.READ_FROM_DB || LoginActivity.this.password.length() == 0) {
                        LoginActivity.this.resetPassword(null);
                        LoginActivity.this.positionCounter = 0;
                        LoginActivity.PRESSING = true;
                        LoginActivity.READ_FROM_DB = false;
                    }*/
                    if (true){//!LoginActivity.this.password.contains(LoginActivity.this.gridPassword[gri_i][gri_j]) && LoginActivity.this.gridPointsText[gri_i][gri_j].getText().equals(BuildConfig.FLAVOR)) {
                        LoginActivity.this.positionCounter = LoginActivity.this.positionCounter + 1;
                        LoginActivity.this.gridPointsImage[gri_i][gri_j].setImageResource(R.drawable.pattern_pin_selected);
                        //  LoginActivity.this.password = LoginActivity.this.password + LoginActivity.this.gridPassword[gri_i][gri_j];
                        LoginActivity.this.gridPointsText[gri_i][gri_j].setText(BuildConfig.FLAVOR + LoginActivity.this.positionCounter);
                    }
                }
                return true;
            } else if (event.getAction() == 1) {
                LoginActivity.PRESSING = false;
                return true;
            } else if (!LoginActivity.PRESSING) {
                return false;
            } else {
                if (gri_i == -1 || gri_j == -1) {
                    return false;
                }
                if (!LoginActivity.this.password.contains(LoginActivity.this.gridPassword[gri_i][gri_j]) && LoginActivity.this.gridPointsText[gri_i][gri_j].getText().equals(BuildConfig.FLAVOR)) {
                    LoginActivity.this.positionCounter = LoginActivity.this.positionCounter + 1;
                    LoginActivity.this.password = LoginActivity.this.password + LoginActivity.this.gridPassword[gri_i][gri_j];
                    LoginActivity.this.gridPointsImage[gri_i][gri_j].setImageResource(R.drawable.pattern_pin_selected);
                    LoginActivity.this.gridPointsText[gri_i][gri_j].setText(BuildConfig.FLAVOR + LoginActivity.this.positionCounter);
                }
                return false;
            }
        }
    }
}
