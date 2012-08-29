package com.example.zadev01;


import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsFactory;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.texture.TextureOptions;

import android.hardware.SensorManager;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import android.hardware.SensorManager;
import android.util.FloatMath;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.emorym.android_pusher.Pusher;
import com.emorym.android_pusher.PusherCallback;
import com.example.zadev01.objects.Player;

import com.example.zadev01.server.ServerInterface;;
 
public class ZAMainActivity extends SimpleBaseGameActivity implements IAccelerationListener, IOnSceneTouchListener, IOnAreaTouchListener {
	// ==========================================================
	// Pusher Constants
	/* the log tag constant */
	private static final String LOG_TAG = "Pusher";

	private static final String PUSHER_APP_KEY = "0ad923c1f1da378fe30b";
	private static final String PUSHER_APP_SECRET = "5d4a3aa26a02cee66ef7";

	private static final String PUBLIC_CHANNEL = "test_channel";
	private static final String PRIVATE_CHANNEL = "test_channel";

	private Pusher mPusher;
	private static final float ANGLE_CONSTANT = 90;
	private EditText eventNameField;
	private EditText eventDataField;
	private EditText channelNameField;

	private Button sendButton;
	private ToggleButton togglePublicChannelButton;
	private ToggleButton togglePrivateChannelButton;

    // ===========================================================
    // Constants
    // ===========================================================
    public static final int CAMERA_WIDTH = 800;
    public static final int CAMERA_HEIGHT = 480;
    // ===========================================================
    // Fields
    // ===========================================================
 
    private Camera mCamera;
    private Scene mMainScene;
    public Player oPlayer;
    public Player other;
    
	private TiledTextureRegion mBoxFaceTextureRegion;
	private TiledTextureRegion mCircleFaceTextureRegion;

	private int mFaceCount = 0;

	private PhysicsWorld mPhysicsWorld;

	private float mGravityX;
	private float mGravityY;
    // ====================================
    // Textures
    // ==============
    private BitmapTextureAtlas mBitmapTextureAtlas;
    private TiledTextureRegion mPlayerTiledTextureRegion;
    // ===========================================================
    // Constructors
    // ===========================================================
 
    // ===========================================================
    // Getter & Setter
    // ===========================================================
 
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
 
    @Override
    public EngineOptions onCreateEngineOptions() {
        this.mCamera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        this.mCamera.setCameraSceneRotation((float)90.00);
        ServerInterface x = new ServerInterface();
        try {
			x.CreateUser();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), this.mCamera);
    }
    @Override
    protected void onCreateResources() {
        // Load all the textures this game needs.
        //this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32);
        //this.mPlayerTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_box.png", 0, 0, 1, 1);
        //this.mBitmapTextureAtlas.load();
		//BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 64, 64, TextureOptions.BILINEAR);
		this.mBoxFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_box_tiled.png", 0, 0, 2, 1); // 64x32
		this.mCircleFaceTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_circle_tiled.png", 0, 32, 2, 1); // 64x32
		this.mBitmapTextureAtlas.load();
    }
 
    @Override
    protected Scene onCreateScene() {
    	this.initializePusher();
    	this.mEngine.registerUpdateHandler(new FPSLogger()); // logs the frame rate
        
        // Create Scene and set background colour to (1, 1, 1) = white
        this.mMainScene = new Scene();
        this.mMainScene.setBackground(new Background(1, 1, 1));
        this.mMainScene.setOnSceneTouchListener(this);

        // Centre the player on the camera.
        //final float centerX = (CAMERA_WIDTH - this.mPlayerTiledTextureRegion.getWidth()) / 2;
        //final float centerY = (CAMERA_HEIGHT - this.mPlayerTiledTextureRegion.getHeight()) / 2;
		this.mEngine.registerUpdateHandler(new FPSLogger());

		this.mPhysicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), false);

		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Rectangle ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle roof = new Rectangle(0, 0, CAMERA_WIDTH, 2, vertexBufferObjectManager);
		final Rectangle left = new Rectangle(0, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);
		final Rectangle right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT, vertexBufferObjectManager);

		final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, left, BodyType.StaticBody, wallFixtureDef);
		PhysicsFactory.createBoxBody(this.mPhysicsWorld, right, BodyType.StaticBody, wallFixtureDef);

		this.mMainScene.attachChild(ground);
		this.mMainScene.attachChild(roof);
		this.mMainScene.attachChild(left);
		this.mMainScene.attachChild(right);

		this.mMainScene.registerUpdateHandler(this.mPhysicsWorld);
		this.mMainScene.setOnAreaTouchListener(this);


        // Create the sprite and add it to the scene.
        //final Player oPlayer = new Player(centerX, centerY, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager(), this.mPusher);
        //final Player other = new Player(centerX, centerY, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager(), this.mPusher);
        
        this.oPlayer = oPlayer; 
        this.other = other;
        this.mMainScene.attachChild(oPlayer);
        this.mMainScene.attachChild(other);
        
        return this.mMainScene;
    }
	@Override
	public boolean onAreaTouched( final TouchEvent pSceneTouchEvent, final ITouchArea pTouchArea,final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
		if(pSceneTouchEvent.isActionDown()) {
			final AnimatedSprite face = (AnimatedSprite) pTouchArea;
			this.jumpFace(face);
			return true;
		}

		return false;
	}

	@Override
	public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
		if(this.mPhysicsWorld != null) {
			if(pSceneTouchEvent.isActionDown()) {
				this.addFace(pSceneTouchEvent.getX(), pSceneTouchEvent.getY());
				return true;
			}
		}
		return false;
	}

	@Override
	public void onAccelerationAccuracyChanged(final AccelerationData pAccelerationData) {

	}

	@Override
	public void onAccelerationChanged(final AccelerationData pAccelerationData) {
		this.mGravityX = pAccelerationData.getX();
		this.mGravityY = pAccelerationData.getY();

		final Vector2 gravity = Vector2Pool.obtain(this.mGravityX, this.mGravityY);
		this.mPhysicsWorld.setGravity(gravity);
		Vector2Pool.recycle(gravity);
	}

    protected void initializePusher(){
    	this.mPusher = new Pusher(PUSHER_APP_KEY, PUSHER_APP_SECRET);
    	this.mPusher.bind("connection_established",new PusherCallback() {
			@Override
	        public void onEvent(String eventName, JSONObject eventData, String channelName) {    
					ZAMainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(ZAMainActivity.this,
									   "Received\nEvent: ",
									   Toast.LENGTH_LONG).show();
						}
					
			        });
			    }
		});
    	this.mPusher.connect();
    	this.mPusher.subscribe(PUBLIC_CHANNEL);
    	this.mPusher.mLocalChannels.get(PUBLIC_CHANNEL).bind("my_event",new PusherCallback() {
			@Override
	        public void onEvent(String eventName, JSONObject eventData, String channelName) {    
					final JSONObject eD = eventData;
					ZAMainActivity.this.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							Toast.makeText(ZAMainActivity.this,
									   "Public Channel Event" + eD,
									   Toast.LENGTH_LONG).show();
						}
					
			        });
			    }
		});
    	
    }
	@Override
	public void onResumeGame() {
		super.onResumeGame();

		this.enableAccelerationSensor(this);
	}

	@Override
	public void onPauseGame() {
		super.onPauseGame();

		this.disableAccelerationSensor();
	}
	
	private void addFace(final float pX, final float pY) {
		this.mFaceCount++;

		final AnimatedSprite face;
		final Body body;

		final FixtureDef objectFixtureDef = PhysicsFactory.createFixtureDef(1, 0.5f, 0.5f);

		if(this.mFaceCount % 2 == 1){
			face = new AnimatedSprite(pX, pY, this.mBoxFaceTextureRegion, this.getVertexBufferObjectManager());
			body = PhysicsFactory.createBoxBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);
		} else {
			face = new AnimatedSprite(pX, pY, this.mCircleFaceTextureRegion, this.getVertexBufferObjectManager());
			body = PhysicsFactory.createCircleBody(this.mPhysicsWorld, face, BodyType.DynamicBody, objectFixtureDef);
		}

		this.mPhysicsWorld.registerPhysicsConnector(new PhysicsConnector(face, body, true, true));

		face.animate(new long[]{200,200}, 0, 1, true);
		face.setUserData(body);
		this.mMainScene.registerTouchArea(face);
		this.mMainScene.attachChild(face);
	}

	private void jumpFace(final AnimatedSprite face) {
		final Body faceBody = (Body)face.getUserData();

		final Vector2 velocity = Vector2Pool.obtain(this.mGravityX * -50, this.mGravityY * -50);
		faceBody.setLinearVelocity(velocity);
		Vector2Pool.recycle(velocity);
	}

}