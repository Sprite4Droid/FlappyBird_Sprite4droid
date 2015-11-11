package myappconverter.sprite4droiddemo.scenes;

import com.myappconverter.java.coregraphics.CGPoint;
import com.myappconverter.java.coregraphics.CGSize;
import com.myappconverter.java.coregraphics.CGVector;
import com.myappconverter.java.foundations.NSArray;
import com.myappconverter.java.foundations.NSMutableArray;
import com.myappconverter.java.foundations.NSSet;
import com.myappconverter.java.foundations.NSString;
import com.myappconverter.java.foundations.SEL;
import com.myappconverter.java.ios.include.*;
import com.myappconverter.java.spritekit.SKAction;
import com.myappconverter.java.spritekit.SKColor;
import com.myappconverter.java.spritekit.SKNode;
import com.myappconverter.java.spritekit.SKPhysicsBody;
import com.myappconverter.java.spritekit.SKPhysicsContact;
import com.myappconverter.java.spritekit.SKPhysicsContactDelegate;
import com.myappconverter.java.spritekit.SKScene;
import com.myappconverter.java.spritekit.SKSpriteNode;
import com.myappconverter.java.spritekit.SKTexture;
import com.myappconverter.java.spritekit.SKTransition;
import com.myappconverter.java.spritekit.SKView;
import com.myappconverter.java.spritekit.internals.SKTransitionBase;
import com.myappconverter.uikit.myappclasses.MyAppTouch;

import android.graphics.Color;
import android.util.Log;
import android.view.MotionEvent;

public class MainScene extends SKScene implements SKPhysicsContactDelegate{


    SKSpriteNode _bird;
    int      _skyColor;
    static final int  kVerticalPipeGap = 100;
    static  int birdCategory = 1 << 0;
    static  int worldCategory = 1 << 1;
    static  int pipeCategory = 1 << 2;
    SKTexture _pipeTexture1;
    SKTexture _pipeTexture2;
    SKAction _moveAndRemovePipes;
    SKNode    _movingGround;
    SKNode    _movingSky;
    SKNode    _movingObstacle;
    double lastSpawnTimeInterval;
    double lastUpdateTimeInterval;

    @Override
    public SKScene initWithSize(CGSize _size) {
        super.initWithSize(_size);


        this.getPhysicsWorld().setGravity(CGVector.CGVectorMake(0, -600));
        this.getPhysicsWorld().setContactDelegate(this);

        SKTexture birdTexture1 = SKTexture.textureWithImageNamed(new NSString("Bird1"));
        SKTexture birdTexture2 = SKTexture.textureWithImageNamed(new NSString("Bird2"));
        birdTexture1.setFilteringMode(SKTexture.SKTextureFilteringMode.SKTextureFilteringNearest);
        birdTexture2.setFilteringMode(SKTexture.SKTextureFilteringMode.SKTextureFilteringNearest);
        _bird = SKSpriteNode.spriteNodeWithTexture(SKSpriteNode.class, birdTexture1);
        _bird.setScale(2);
        _bird.setPosition(CGPoint.make(this.frame().size.getWidth() / 4, this.getFrame().size.getHeight() / 2));

        this.addChild(_bird);


        NSMutableArray<SKTexture> sequence = new NSMutableArray<SKTexture>();
        sequence.addObject(birdTexture1);
        sequence.addObject(birdTexture2);
        SKAction flap = SKAction.repeatActionForever(SKAction.animateWithTexturesTimePerFrame(sequence, 0.2));

        _bird.runAction(flap);
        _bird.setPhysicsBody(SKPhysicsBody.bodyWithCircleOfRadius(_bird.getSize().height / 2));
        _bird.getPhysicsBody().setDynamic(true);
        _bird.getPhysicsBody().setAllowsRotation(false);

        _bird.getPhysicsBody().setCategoryBitMask(birdCategory);
        _bird.getPhysicsBody().setCollisionBitMask(worldCategory | pipeCategory);
        _bird.getPhysicsBody().setContactTestBitMask(worldCategory | pipeCategory);

        _skyColor = Color.argb(255, 113, 197, 207);
        setBackgroundColor(_skyColor);

        _movingGround = SKNode.node(SKNode.class);
        addChild(_movingGround);

        _movingObstacle= SKNode.node(SKNode.class);
        addChild(_movingObstacle);

        _movingSky = SKNode.node(SKNode.class);
        _movingSky.setZPosition(-20);
        addChild(_movingSky);



        // Create ground

        SKTexture groundTexture = SKTexture.textureWithImageNamed(new NSString("Ground"));
        groundTexture.setFilteringMode(SKTexture.SKTextureFilteringMode.SKTextureFilteringNearest);

        SKSpriteNode dummy = SKSpriteNode.spriteNodeWithColorSize(SKSpriteNode.class, Color.TRANSPARENT, groundTexture.getSize());
        dummy.setPosition(CGPoint.make(0, groundTexture.getSize().height));
        dummy.setPhysicsBody(SKPhysicsBody.bodyWithRectangleOfSize(CGSize.make(getFrame().getSize().width, groundTexture.getSize().height * 2)));
        dummy.getPhysicsBody().setDynamic(false);
        dummy.getPhysicsBody().setCategoryBitMask(worldCategory);
        addChild(dummy);


        SKAction moveGroundSprite = SKAction.moveByXYDuration(-groundTexture.getSize().width*2, 0, 0.02 * groundTexture.getSize().width*2);
        SKAction resetGroundSprite = SKAction.moveByXYDuration(groundTexture.getSize().width*2, 0, 0);
        NSMutableArray<SKAction> actions = new NSMutableArray<SKAction>();
        actions.addObject(moveGroundSprite);
        actions.addObject(resetGroundSprite);
        SKAction moveGroundSpritesForever = SKAction.repeatActionForever(SKAction.sequence(actions));

        for( int i = 0; i < 2 + getFrame().size.width / ( groundTexture.getSize().width * 2 ); ++i ) {
            SKSpriteNode sprite = SKSpriteNode.spriteNodeWithTexture(SKSpriteNode.class, groundTexture);
            sprite.setScale(2);
            sprite.setPosition(CGPoint.make(i * sprite.getSize().width, sprite.getSize().height / 2));
            //sprite.runAction(moveGroundSpritesForever);
            _movingGround.addChild(sprite);
            _movingGround.runAction(moveGroundSpritesForever);
        }

        // Create skyline

        SKTexture skylineTexture = SKTexture.textureWithImageNamed(new NSString("Skyline"));
        skylineTexture.setFilteringMode(SKTexture.SKTextureFilteringMode.SKTextureFilteringNearest);

        SKAction moveSkylineSprite = SKAction.moveByXYDuration(-skylineTexture.getSize().width*2, 0,0.1 * skylineTexture.getSize().width*2);
        SKAction resetSkylineSprite = SKAction.moveByXYDuration(skylineTexture.getSize().width*2, 0, 0);
        NSMutableArray<SKAction> _actions = new NSMutableArray<SKAction>();
        _actions.addObject(moveSkylineSprite);
        _actions.addObject(resetSkylineSprite);
        SKAction moveSkylineSpritesForever = SKAction.repeatActionForever(SKAction.sequence(_actions));

        for( int i = 0; i < 2+ this.getFrame().size.width / ( skylineTexture.getSize().width * 2 ); ++i ) {
            SKSpriteNode sprite = SKSpriteNode.spriteNodeWithTexture(SKSpriteNode.class, skylineTexture);
            sprite.setScale(2);
            sprite.setZPosition(-20);
            sprite.setPosition(CGPoint.make(i * sprite.getSize().width, sprite.getSize().height / 2 + groundTexture.getSize().height * 2));
            _movingSky.addChild(sprite);
            _movingSky.runAction(moveSkylineSpritesForever);
        }

        // Create pipes

        _pipeTexture1 = SKTexture.textureWithImageNamed(new NSString("Pipe1"));
        _pipeTexture1.setFilteringMode(SKTexture.SKTextureFilteringMode.SKTextureFilteringNearest);
        _pipeTexture2 = SKTexture.textureWithImageNamed(new NSString("Pipe2"));
        _pipeTexture2.setFilteringMode(SKTexture.SKTextureFilteringMode.SKTextureFilteringNearest);



        float distanceToMove = getFrame().size.width + 2 * _pipeTexture1.getSize().width;
        SKAction movePipes = SKAction.moveByXYDuration(-distanceToMove, 0, 0.01*distanceToMove);
        SKAction removePipes = SKAction.moveByXYDuration(distanceToMove, 0, 0);
        NSMutableArray<SKAction> list = new NSMutableArray<SKAction>();
        list.addObject(movePipes);
        list.addObject(removePipes);
        _moveAndRemovePipes = SKAction.sequence(list);
        SKAction sendBlocks = SKAction.repeatActionForever(_moveAndRemovePipes);

        SKNode pipePair = SKNode.node(SKNode.class);
        pipePair.setPosition(CGPoint.make(getFrame().size.width + _pipeTexture1.getSize().width, 0));
        pipePair.setZPosition(-10);

        float y = com.myappconverter.java.ios.include.Math.arc4random() % (int)( getFrame().size.height / 3 );

        SKSpriteNode pipe1 = SKSpriteNode.spriteNodeWithTexture(SKSpriteNode.class, _pipeTexture1);
        pipe1.setScale(2);
        pipe1.setPosition(CGPoint.make(0, y));
        pipe1.setPhysicsBody(SKPhysicsBody.bodyWithRectangleOfSize(pipe1.getSize()));
        pipe1.getPhysicsBody().setDynamic(false);
        pipe1.getPhysicsBody().setCategoryBitMask(pipeCategory);
        pipe1.getPhysicsBody().setContactTestBitMask(birdCategory);
        pipePair.addChild(pipe1);

        SKSpriteNode pipe2 = SKSpriteNode.spriteNodeWithTexture(SKSpriteNode.class, _pipeTexture2);
        pipe2.setScale(2);
        pipe2.setPosition(CGPoint.make(0, y + pipe1.getSize().height + kVerticalPipeGap));
        pipe2.setPhysicsBody(SKPhysicsBody.bodyWithRectangleOfSize(pipe2.getSize()));
        pipe2.getPhysicsBody().setDynamic(false);
        pipe2.getPhysicsBody().setCategoryBitMask(pipeCategory);
        pipe2.getPhysicsBody().setContactTestBitMask(birdCategory);
        pipePair.addChild(pipe2);
        pipePair.runAction(sendBlocks);

        addChild(pipePair);


        return this;
    }






    float clamp(float min, float max, float value) {
        if( value > max ) {
            return max;
        } else if( value < min ) {
            return min;
        } else {
            return value;
        }
    }


    @Override
    public void update(double currentTime) {
        _bird.setZRotation(clamp(-1, 0.5f, _bird.getPhysicsBody().getVelocity().dy * (_bird.getPhysicsBody().getVelocity().dy < 0 ? 0.001f : 0.0013f)));
    }



    @Override
    public void touchesBeganWithEvent(NSSet<MyAppTouch> touches, MotionEvent event) {

        _bird.getPhysicsBody().applyImpulse(CGVector.CGVectorMake(0,8000));

    }

    @Override
    public void touchesEndedWithEvent(NSSet<MyAppTouch> touches, MotionEvent event) {

    }

    @Override
    public void didBeginContact(SKPhysicsContact skPhysicsContact) {

        GameOverScene finish = (GameOverScene)GameOverScene.sceneWithSize(GameOverScene.class, getSize());
        SKView view = getView();
        view.presentScene(finish);
    }

    @Override
    public void didEndContact(SKPhysicsContact skPhysicsContact) {

    }
}