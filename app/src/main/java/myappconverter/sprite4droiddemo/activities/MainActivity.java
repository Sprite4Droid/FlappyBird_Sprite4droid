package myappconverter.sprite4droiddemo.activities;

import org.cocos2dx.lib.Sprite4DroidActivity;

import android.os.Bundle;

import com.myappconverter.java.coregraphics.CGSize;
import com.myappconverter.java.spritekit.SKView;

import myappconverter.sprite4droiddemo.scenes.MainScene;


public class MainActivity extends Sprite4DroidActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void initWithSize(CGSize size) {

        //TODO create and start your first Scene
        MainScene scene = (MainScene) MainScene.sceneWithSize(MainScene.class, size);
        SKView.getInstance().presentScene(scene);

    }

}
