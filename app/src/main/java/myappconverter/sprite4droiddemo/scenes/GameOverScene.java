package myappconverter.sprite4droiddemo.scenes;

import android.graphics.Color;
import android.view.MotionEvent;

import com.myappconverter.java.coregraphics.CGPoint;
import com.myappconverter.java.coregraphics.CGSize;
import com.myappconverter.java.foundations.NSSet;
import com.myappconverter.java.foundations.NSString;
import com.myappconverter.java.spritekit.SKLabelNode;
import com.myappconverter.java.spritekit.SKScene;
import com.myappconverter.java.spritekit.SKView;
import com.myappconverter.uikit.myappclasses.MyAppTouch;

/**
 * Created by myAppconveter on 28/10/2015.
 */
public class GameOverScene extends SKScene{

    @Override
    public SKScene initWithSize(CGSize _size) {


        SKLabelNode label = SKLabelNode.labelNodeWithFontNamed(SKLabelNode.class, new NSString("jandles"));
        label.setText(new NSString("Game Over"));
        label.setColor(Color.GREEN);
        label.setPosition(CGPoint.make(getFrame().getSize().width / 2, getFrame().getSize().height / 2));
        addChild(label);

        return this;
    }


    @Override
    public void touchesBeganWithEvent(NSSet<MyAppTouch> touches, MotionEvent event) {
        MainScene restart = (MainScene)MainScene.sceneWithSize(MainScene.class, getSize());
        SKView view = getView();
        view.presentScene(restart);
    }
}
