/**
 * Copyright (C) 2016 Robinhood Markets, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.robinhood.spark.sample;

import android.animation.Animator;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.robinhood.spark.SparkAdapter;
import com.robinhood.spark.SparkView;
import com.robinhood.spark.animation.LineSparkAnimator;
import com.robinhood.spark.animation.MorphSparkAnimator;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SparkView sparkView;
    private RandomizedAdapter adapter;
    private TextView scrubInfoTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sparkView = findViewById(R.id.sparkview);
        final MorphSparkAnimator morphSparkAnimator = new MorphSparkAnimator();
        morphSparkAnimator.setDuration(2000L);
        morphSparkAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        adapter = new RandomizedAdapter();
        sparkView.setAdapter(adapter);
        sparkView.setScrubListener(new SparkView.OnScrubListener() {
            @Override
            public void onScrubbed(Object value) {
                if (value == null) {
                    adapter.setDev(4);


                    sparkView.setSparkAnimator(morphSparkAnimator);
                    scrubInfoTextView.setText(R.string.scrub_empty);
                } else {
                    sparkView.setSparkAnimator(null);
                    adapter.setDev(1);
                    scrubInfoTextView.setText(getString(R.string.scrub_format, value));
                }
            }
        });

        findViewById(R.id.random_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adapter.randomize();
            }
        });

        ((CheckBox)findViewById(R.id.fillCheckBox)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if ( isChecked ) adapter.setDev(5);
                    //sparkView.setFillType(SparkView.FillType.DOWN);
                else adapter.setDev(1);
                    //sparkView.setFillType(SparkView.FillType.NONE);
            }
        });
        
        scrubInfoTextView = findViewById(R.id.scrub_info_textview);

        // set select
        Spinner animationSpinner = findViewById(R.id.animation_spinner);
        animationSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.animations)));
        animationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch(position) {
                    case 1:
                        LineSparkAnimator lineSparkAnimator = new LineSparkAnimator();
                        lineSparkAnimator.setDuration(2500L);
                        lineSparkAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                        sparkView.setSparkAnimator(lineSparkAnimator);
                        break;

                    case 2:
                        // set animator
                        MorphSparkAnimator morphSparkAnimator = new MorphSparkAnimator();
                        morphSparkAnimator.setDuration(2000L);
                        morphSparkAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

                        sparkView.setSparkAnimator(morphSparkAnimator);
                        break;

                    default:
                        sparkView.setSparkAnimator(null);
                        break;
                }

                adapter.randomize();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                sparkView.setSparkAnimator(null);
                adapter.randomize();
            }
        });
    }


    public static class RandomizedAdapter extends SparkAdapter {
        private final float[] yData;
        private final Random random;
        private int dev = 1;

        public RandomizedAdapter() {
            random = new Random();
            yData = new float[200];
            randomize();
        }

        public void randomize() {
            float previousRand = random.nextFloat();
            for (int i = 0, count = yData.length; i < count; i++) {
                float toAdd = previousRand;
                if(random.nextBoolean()) {
                    toAdd += random.nextFloat() / 10.0f;
                } else {
                    toAdd += random.nextFloat() / -10.0f;
                }
                yData[i] = toAdd;
                previousRand = yData[i];
            }
            notifyDataSetChanged();
        }

        public void setDev(int i) {
            if(dev == i) return;
            dev = i;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return yData.length / dev;
        }

        @NonNull
        @Override
        public Object getItem(int index) {
            return yData[index * dev];
        }

        @Override
        public float getY(int index) {
            return yData[index * dev];
        }
    }
}
