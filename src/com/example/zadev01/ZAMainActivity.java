package com.example.zadev01;


import java.io.IOException;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.IEntityMatcher;
import org.andengine.entity.scene.IOnAreaTouchListener;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.ITouchArea;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.sensor.acceleration.AccelerationData;
import org.andengine.input.sensor.acceleration.IAccelerationListener;
import org.andengine.input.touch.TouchEvent;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.TextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import android.util.FloatMath;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

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
        this.mBitmapTextureAtlas = new BitmapTextureAtlas(this.getTextureManager(), 32, 32);
        this.mPlayerTiledTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "face_box.png", 0, 0, 1, 1);
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
        final float centerX = (CAMERA_WIDTH - this.mPlayerTiledTextureRegion.getWidth()) / 2;
        final float centerY = (CAMERA_HEIGHT - this.mPlayerTiledTextureRegion.getHeight()) / 2;
 
        // Create the sprite and add it to the scene.
        final Player oPlayer = new Player(centerX, centerY, this.mPlayerTiledTextureRegion, this.getVertexBufferObjectManager(), this.mPusher);
        final Player other = new Player(centerX, centerY, this.mPlayerTiledTextureRegion, this.getVertexBufferObjectManager(), this.mPusher);
        
        this.oPlayer = oPlayer; 
        this.other = other;
        this.mMainScene.attachChild(oPlayer);
        this.mMainScene.attachChild(other);
        
        return this.mMainScene;
    }
    @Override
    public boolean onSceneTouchEvent(final Scene pScene, final TouchEvent pSceneTouchEvent) {
    	final float x = pSceneTouchEvent.getX();
    	final float y = pSceneTouchEvent.getY();
    	/*float offx = (float) (x - this.oPlayer.getX());
    	float offy = (float) (y - this.oPlayer.getY());
    	if (offx <= 0)
    		return false;
    	
    	float realx = (float) (this.mCamera.getWidth() + this.oPlayer.getWidth() / 2.0f);
    	float ratio = (float) offy / (float) offx;
    	
    	final float distanceX = Math.abs(x - this.oPlayer.getX());
    	final float distanceY = Math.abs(y - this.oPlayer.getY());
    	final float distance = Math.min((float) Math.hypot(
    			(double) distanceX, (double) distanceY), Math.abs(800-this.oPlayer.getX()));
    	final double angleX = x - this.oPlayer.getX();

    	final double angleY = y - this.oPlayer.getY();
    	final float angle = (float) Math.toDegrees(Math.atan2(angleY,

    			angleX))

    			+ ANGLE_CONSTANT;*/
    	float boxX = this.oPlayer.getX(); 
    	float boxY = this.oPlayer.getY();

    	// v2 user touch
    	float touchX = pSceneTouchEvent.getX();
    	float touchY = pSceneTouchEvent.getY();     

    	float theta = (float) (180.0 / Math.PI * Math.atan2(touchY - boxY, touchX - boxX ));

    	this.oPlayer.angle = theta + (float)-90.00;
    	//this.oPlayer.setX(x);
    	//this.oPlayer.setY(y);
    	
    	//this.oPlayer.velocity = - 500;
    	ZAMainActivity.this.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				Toast.makeText(ZAMainActivity.this,
						   "x:" + String.valueOf(x) + "y:" + String.valueOf(y) ,
						   Toast.LENGTH_LONG).show();
			}
		
        });
    	return true;
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
	public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
			ITouchArea pTouchArea, float pTouchAreaLocalX,
			float pTouchAreaLocalY) {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void onAccelerationAccuracyChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onAccelerationChanged(AccelerationData pAccelerationData) {
		// TODO Auto-generated method stub
		
	}
}