package org.olumide.adebayo.puzzle;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

/*
@author: olumide marC adebayo: 374994
@description: simple puzzle game :)
 */

public class PuzzleActivity extends Activity implements View.OnTouchListener{

    FrameLayout frameLayout = null;
    ArrayList<ImageView> ivList = new ArrayList<ImageView>();

    int top =0;
    int left = 0;
    int pieceWidth=0;
    int pieceHeight=0;
    int width =0;
    int height = 0;

    int availablePieces = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puzzle);

        init();

    }

    private void init(){


        //get the REAL size of your device
        DisplayMetrics drm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getRealMetrics(drm);
        Log.d("r-size",drm.widthPixels+","+drm.heightPixels);
        //get the size - the bar space
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        Log.d("size",dm.widthPixels+","+dm.heightPixels);

        width = drm.widthPixels;
        height = drm.heightPixels;
        pieceWidth=width/5;
        pieceHeight=width/4;

        Log.d("Olu"," screen width is "+width);
        Log.d("Olu","piece width is "+pieceWidth);


        frameLayout = findViewById(R.id.frameLayoutid);
        frameLayout.setBackgroundColor(Color.BLACK);

        ImageView iv = new ImageView(PuzzleActivity.this);
        frameLayout.addView(iv);


        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width,width);
        params.setMargins(left,top,0,0);
        iv.setLayoutParams(params);

        iv.setImageResource(R.drawable.background);

        top =  iv.getDrawable().getIntrinsicWidth();


        Log.d("Olu","new top "+top);


        //set the pieces
        AssetManager manager = getAssets();
        try {
            String[] pieces = manager.list("ub");

            for(String _s: pieces) {

                String _tmp = _s;
                _tmp = _tmp.replaceAll(".jpeg","");



                _s = "ub/" + _s;
                InputStream stream = manager.open(_s);
                ImageView _iv = new ImageView(PuzzleActivity.this);

                _iv.setTag(_tmp);
                Drawable _d = Drawable.createFromStream(stream, null);

                _iv.setImageDrawable(_d);

                ivList.add(_iv);


            }
        }catch(Exception e){
            e.printStackTrace();
        }

        showPieces();
    }

    //will display 3 puzzle pieces at a time
    private void showPieces(){

        int i=0;
        int _left  = left;
        int _top = top;
        int leftOverlay = pieceWidth / 2;

        Random rand = new Random();


        while( ++i <= 3 && ivList.size()>0) {

            int _index = rand.nextInt(ivList.size());
            ImageView _iv = ivList.remove(_index);

            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(pieceWidth, pieceHeight);
            p.setMargins(_left, _top, 0, 0);
            _iv.setLayoutParams(p);
            _iv.setBackgroundColor(Color.WHITE);
            _iv.setPadding(2, 2, 2, 2);

            _left += leftOverlay + pieceWidth;

            _iv.setOnTouchListener(this);

            frameLayout.addView(_iv);
            availablePieces++;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x=0,y=0;
        FrameLayout.LayoutParams fofo;

        switch(event.getAction())        {
            case MotionEvent.ACTION_DOWN:
                x = event.getRawX();
                y = event.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                x = event.getRawX();
                y = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                x = event.getRawX();
                y = event.getRawY();
                break;
        }


        fofo = (FrameLayout.LayoutParams)v.getLayoutParams();
        fofo.setMargins((int)x,(int)y,0,0);
        v.setLayoutParams(fofo);

        checkPlacement(v, (int)x,(int)y);

        return true;

    }

    //check the placement of the current x,y cordinates
    //within the grid
    private void checkPlacement(View v,int x,int y){
        ImageView _iv = (ImageView) v;

        String _tag = v.getTag().toString();
        _tag = _tag.replaceAll("u","");
        int position = Integer.parseInt(_tag);
        position--;

        int gridCol = position % 5;
        int gridRow =       (int) Math.floor( position/5);

        int top = gridRow*pieceHeight;
        int left = gridCol*pieceWidth;

        int bottom = top +pieceHeight;
        int right = left +pieceWidth;

        Rect r = new Rect(left, top, right,bottom);
        Log.d("Olu","u is "+_tag);
        Log.d("Olu","y-("+r.top+","+r.bottom+")");
        Log.d("Olu","x-("+r.left+","+r.right+")");

        if( contains(r,x,y)){//belongs here

            Log.d("Olu","b4 avail pieces "+availablePieces);

            availablePieces--;

            Log.d("Olu","avail pieces "+availablePieces);

            if( availablePieces == 0){
                showPieces();
                if( availablePieces == 0){
                    endGame();
                    return;
                }
            }

            _iv.setOnTouchListener(null);

            //snap it
            FrameLayout.LayoutParams p = new FrameLayout.LayoutParams(pieceWidth, pieceHeight);
            p.setMargins(left,top,0,0);


            _iv.setLayoutParams(p);
           // _iv.setBackgroundColor(Color.WHITE);
            _iv.setPadding(0,0,0,0);
        }else{
            _iv.setBackgroundColor(Color.RED);
        }

        Log.d("Olu","row "+gridRow+" col"+gridCol);


    }

    //check if x,y is within the rect
    private boolean contains(Rect r,int x,int y){

        Log.d("Olu","[["+x+" , "+y+"]]");


        if( r.top <= y && r.bottom >= y){
            if( r.left <= x && r.right >= x){
                return true;
            }
        }
        return false;
    }



    private void endGame(){

        String str="Game Over";
        Toast toast = Toast.makeText(PuzzleActivity.this,str,Toast.LENGTH_LONG);
        int xOff = 0;
        int yOff = 0;

        toast.setGravity(Gravity.CENTER,xOff,yOff);
        toast.show();

    }


}
