package com.jimpai.simplegame;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;

import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.opengl.CCGLSurfaceView;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.ccColor4B;


public class MainActivity extends Activity {
    private CCGLSurfaceView mCCGLSurfaceView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mCCGLSurfaceView = new CCGLSurfaceView(this);

        setContentView(mCCGLSurfaceView);
    }

    @Override
    protected void onStart() {
        super.onStart();


        CCDirector ccDirector = CCDirector.sharedDirector();

        ccDirector.attachInView(mCCGLSurfaceView);

        ccDirector.setScreenSize(800, 1200);

        ccDirector.setDisplayFPS(true);

        ccDirector.setAnimationInterval(1.0f / 60.0f);

        CCScene ccScene = GameScene.scene();

        ccDirector.runWithScene(ccScene);
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        SoundEngine.sharedEngine().preloadEffect(this, R.raw.pew_pew_lei);
        SoundEngine.sharedEngine().preloadSound(this, R.raw.background_music_aac);
        SoundEngine.sharedEngine().resumeSound();
        CCDirector.sharedDirector().resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        SoundEngine.sharedEngine().pauseSound();
        CCDirector.sharedDirector().pause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundEngine.sharedEngine().realesAllSounds();
        SoundEngine.sharedEngine().realesAllEffects();
        CCDirector.sharedDirector().end();
        ccMacros.CCLOG("MainActivity", "onDestroy");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
