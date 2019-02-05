package com.aruns.muzikplayer;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
Button btn_next,btn_previous,btn_pause;
TextView songTextLabel;
SeekBar seekBar;
static MediaPlayer mymediaplayer;
int position;
String s;
ArrayList<File> mySongs;
Thread updateseekbar;
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        btn_next=(Button)findViewById(R.id.next);
        btn_previous=(Button)findViewById(R.id.back);
        btn_pause=(Button)findViewById(R.id.pause);
        songTextLabel=(TextView)findViewById(R.id.songlabel);
        seekBar=(SeekBar)findViewById(R.id.seekbar);
        updateseekbar=new Thread(){
            @Override
            public void run() {
               int totalduration=mymediaplayer.getDuration();
               int currentposition=0;
               while(currentposition<totalduration)
               {
                   try {
                       sleep(500);
                       currentposition=mymediaplayer.getCurrentPosition();
                       seekBar.setProgress(currentposition);
                   }catch (InterruptedException e)
                   {
                       e.printStackTrace();
                   }
               }
            }
        };

        if(mymediaplayer!=null) {
            mymediaplayer.stop();
            mymediaplayer.reset();
            mymediaplayer.release();
            mymediaplayer=null;
        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mySongs=(ArrayList)bundle.getParcelableArrayList("songs");
        s=mySongs.get(position).getName().toString();
        String songName=i.getStringExtra("songname");

        songTextLabel.setText(songName);
        songTextLabel.setSelected(true);

        position=bundle.getInt("pos",0);

        Uri u=Uri.parse(mySongs.get(position).toString());
        mymediaplayer=MediaPlayer.create(getApplicationContext(),u);
        mymediaplayer.start();
        seekBar.setMax(mymediaplayer.getDuration());


        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        updateseekbar.start();

        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary),PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mymediaplayer.seekTo(seekBar.getProgress());
            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                seekBar.setMax(mymediaplayer.getDuration());
                if(mymediaplayer.isPlaying())
                {
                    btn_pause.setBackgroundResource(R.drawable.icon_play);
                    mymediaplayer.pause();
                }else
                {
                    btn_pause.setBackgroundResource(R.drawable.icon_pause);
                    mymediaplayer.start();
                }
            }
        });

        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mymediaplayer.stop();
                mymediaplayer.release();
                position=((position+1)%mySongs.size());
                Uri u=Uri.parse(mySongs.get(position).toString());
                mymediaplayer=MediaPlayer.create(getApplicationContext(),u);
                s=mySongs.get(position).getName().toString();
                songTextLabel.setText(s);
                mymediaplayer.start();
            }
        });

        btn_previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mymediaplayer.stop();
                mymediaplayer.release();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u=Uri.parse(mySongs.get(position).toString());
                mymediaplayer=MediaPlayer.create(getApplicationContext(),u);
                s=mySongs.get(position).getName().toString();
                songTextLabel.setText(s);
                mymediaplayer.start();

            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home)
        {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}