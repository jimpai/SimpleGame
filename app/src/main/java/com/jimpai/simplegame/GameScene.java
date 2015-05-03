package com.jimpai.simplegame;

import android.content.Context;
import android.view.MotionEvent;

import org.cocos2d.actions.instant.CCCallFuncN;
import org.cocos2d.actions.interval.CCMoveTo;
import org.cocos2d.actions.interval.CCSequence;
import org.cocos2d.config.ccMacros;
import org.cocos2d.layers.CCColorLayer;
import org.cocos2d.layers.CCLayer;
import org.cocos2d.layers.CCScene;
import org.cocos2d.nodes.CCDirector;
import org.cocos2d.nodes.CCSprite;
import org.cocos2d.sound.SoundEngine;
import org.cocos2d.types.CGPoint;
import org.cocos2d.types.CGRect;
import org.cocos2d.types.CGSize;
import org.cocos2d.types.ccColor4B;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by jimpai on 15/5/2.
 */
public class GameScene extends CCLayer {

    protected ArrayList<CCSprite> _targets;
    protected ArrayList<CCSprite> _projectiles;
    protected int _projectilesDestroyed;
    protected int _winKillnum = 5;

    public static CCScene scene() {
        CCScene ccScene = CCScene.node();

        GameScene gameScene = new GameScene();

        ccScene.addChild(gameScene);

        return ccScene;

    }

    protected GameScene() {

        _targets = new ArrayList<CCSprite>();
        _projectiles = new ArrayList<CCSprite>();
        _projectilesDestroyed = 0;

        CCColorLayer ccColorLayerBg = CCColorLayer.node(ccColor4B.ccc4(255, 255, 255, 255));
        this.addChild(ccColorLayerBg);

        CCSprite ccSprite = CCSprite.sprite("Player.png");
        ccMacros.CCLOG("GameScene", "Width=" + CCDirector.sharedDirector().winSize().getWidth() + ", Height=" + CCDirector.sharedDirector().winSize().getHeight());
        ccSprite.setPosition(CCDirector.sharedDirector().winSize().getWidth() / 2, CCDirector.sharedDirector().winSize().getHeight() / 2);
        ccSprite.setScale(5f);

        this.schedule("gameLogic", 1.0f);
        this.schedule("update");
        this.addChild(ccSprite);
        Context context = CCDirector.sharedDirector().getActivity();

        SoundEngine.sharedEngine().playSound(context, R.raw.background_music_aac, true);

        ccMacros.CCLOG("GameScene", "constructor _targets.size is " + _targets.size());
        this.setIsTouchEnabled(true);
    }

    @Override
    public boolean ccTouchesEnded(MotionEvent event) {

        // Choose one of the touches to work with
        CGPoint location = CCDirector.sharedDirector().convertToGL(CGPoint.ccp(event.getX(), event.getY()));

        // Set up initial location of projectile
        CGSize winSize = CCDirector.sharedDirector().displaySize();
        CCSprite projectile = CCSprite.sprite("Projectile.png");
        projectile.setTag(2);
        _projectiles.add(projectile);

        projectile.setPosition(20, winSize.height / 2.0f);

        // Determine offset of location to projectile
        int offX = (int) (location.x - projectile.getPosition().x);
        int offY = (int) (location.y - projectile.getPosition().y);

        // Bail out if we are shooting down or backwards
        if (offX <= 0)
            return true;

        // Ok to add now - we've double checked position
        addChild(projectile);

        // Determine where we wish to shoot the projectile to
        int realX = (int) (winSize.width + (projectile.getContentSize().width / 2.0f));
        float ratio = (float) offY / (float) offX;
        int realY = (int) ((realX * ratio) + projectile.getPosition().y);
        CGPoint realDest = CGPoint.ccp(realX, realY);

        // Determine the length of how far we're shooting
        int offRealX = (int) (realX - projectile.getPosition().x);
        int offRealY = (int) (realY - projectile.getPosition().y);
        float length = (float) Math.sqrt((offRealX * offRealX) + (offRealY * offRealY));
        float velocity = 480.0f / 1.0f;
        float realMoveDuration = length / velocity;

        // Move projectile to actual enpoint
        projectile.runAction(CCSequence.actions(
                CCMoveTo.action(realMoveDuration, realDest),
                CCCallFuncN.action(this, "spriteMoveFinished")));

        Context context = CCDirector.sharedDirector().getActivity();
        SoundEngine.sharedEngine().playEffect(context, R.raw.pew_pew_lei);
        return true;//super.ccTouchesEnded(event);
    }

    protected void addTarget() {
        Random rand = new Random();
        CCSprite target = CCSprite.sprite("Target.png");
        target.setTag(1);
        _targets.add(target);
        ccMacros.CCLOG("GameScene", "addTarget _targets.size is " + _targets.size());
        ccMacros.CCLOG("GameScene", "Target be created!");
        CGSize winSiz = CCDirector.sharedDirector().displaySize();

        int minY = (int) (target.getContentSize().height);
        int maxY = (int) (winSiz.height - target.getContentSize().height);

        int rangeY = maxY - minY;
        int actualY = rand.nextInt(rangeY) + minY;
        if (actualY >= maxY) {
            actualY -= minY;
        }

        target.setPosition(winSiz.width + (target.getContentSize().width / 2.0f), actualY);
        addChild(target);

        int minDuration = 2;
        int maxDuration = 4;
        int rangeDuration = maxDuration - minDuration;
        int actualDuration = rand.nextInt(rangeDuration) + minDuration;

        CCMoveTo actionMove = CCMoveTo.action(actualDuration, CGPoint.ccp(-target.getContentSize().width / 2.0f, actualY));
        CCCallFuncN actionMoveDone = CCCallFuncN.action(this, "spriteMoveFinished");
        CCSequence actions = CCSequence.actions(actionMove, actionMoveDone);

        target.runAction(actions);
    }

    public void spriteMoveFinished(Object sender) {
        CCSprite sprite = (CCSprite) sender;
        if(sprite.getTag() == 1) {
            _projectilesDestroyed = 0;
            ccMacros.CCLOG("GameScene", "Target be destroyed!");
            _targets.remove(sprite);
            //if(_projectilesDestroyed > _winKillnum) {

            //}
            /*
            if (_targets.size() > 0) {
                for (CCSprite target : _targets) {
                    _targets.remove(target);
                    removeChild(target, true);
                }
            }
            */
            ccMacros.CCLOG("GameScene", "You Lose, boo, restart in 5 seconds");
            ccMacros.CCLOG("GameScene", "_targets.size is " + _targets.size());
            ccMacros.CCLOG("GameScene", "_projectile.size is " + _projectiles.size());
            CCDirector.sharedDirector().replaceScene(GameOverLayer.scene("You Lose, boo, restart in 5 seconds"));
        }
        else if (sprite.getTag() == 2)
            _projectiles.remove(sprite);

        this.removeChild(sprite, true);
    }

    public void gameLogic(float dt) {
        addTarget();
    }

    public void update(float dt) {
        ArrayList<CCSprite> projectilesToDelete = new ArrayList<CCSprite>();

        for(CCSprite projectile : _projectiles) {
            CGRect projectileRect = CGRect.make(projectile.getPosition().x - (projectile.getContentSize().width / 2.0f),
                    projectile.getPosition().y - (projectile.getContentSize().height / 2.0f),
                    projectile.getContentSize().width,
                    projectile.getContentSize().height);

            ArrayList<CCSprite> targetsToDelete = new ArrayList<CCSprite>();
            for (CCSprite target : _targets) {
                CGRect targetRect = CGRect.make(target.getPosition().x - (target.getContentSize().width / 2.0f),
                        target.getPosition().y - (target.getContentSize().height / 2.0f),
                        target.getContentSize().width,
                        target.getContentSize().height);

                if (CGRect.intersects(projectileRect, targetRect))
                    targetsToDelete.add(target);

            }

            for (CCSprite target : targetsToDelete) {
                _targets.remove(target);
                removeChild(target, true);
            }

            if (targetsToDelete.size() > 0)
                projectilesToDelete.add(projectile);
        }

        for (CCSprite projectile : projectilesToDelete) {
            _projectiles.remove(projectile);
            removeChild(projectile, true);
            if (++_projectilesDestroyed > _winKillnum) {
                _projectilesDestroyed = 0;
                ccMacros.CCLOG("GameScene", "You Win!");
                CCDirector.sharedDirector().replaceScene(GameOverLayer.scene("You Win!"));
            }
        }
    }
}
