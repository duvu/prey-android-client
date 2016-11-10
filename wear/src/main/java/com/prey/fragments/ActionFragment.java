package com.prey.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.prey.R;

/**
 * Created by oso on 22-09-16.
 */

public class ActionFragment extends Fragment {

    private ImageView mPhoto;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.actions_fragment, container, false);
        // mPhoto = (ImageView) view.findViewById(R.id.photo);

        ImageView sound=(ImageView)view.findViewById(R.id.sound);

        sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Vibrator vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
                long[] vibrationPattern = {0, 500, 50, 300};
                //-1 - don't repeat
                final int indexInPatternToRepeat = -1;
                vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);
                */
            }
        });
        return view;
    }

    public void setBackgroundImage(Bitmap bitmap) {
        mPhoto.setImageBitmap(bitmap);
    }
}
