package com.example.zadev01.objects;

import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.widget.Toast;

import com.emorym.android_pusher.Pusher;
import com.emorym.android_pusher.PusherCallback;
import com.example.zadev01.ZAMainActivity;
import java.lang.Math;
public class Player extends GameObject {
 
    // ===========================================================
    // Constants
    // ===========================================================
 
    // ===========================================================
    // Fields
    // ===========================================================
 
    // ===========================================================
    // Constructors
    // ===========================================================
    public Player(final float pX, final float pY, final TiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, Pusher mPusher) {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager, mPusher);
    }
 
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
 
    @Override
    public void move() {
    	if(true){
    		
	        this.mPhysicsHandler.setVelocityX((float)0.000 - (float)Math.sin(Math.toRadians(this.angle))*(float)50.00);
	        this.mPhysicsHandler.setVelocityY((float)Math.cos(Math.toRadians(this.angle))*(float)50.00);
    	}
    	else{
    		this.mPhysicsHandler.setVelocityX(this.velocity);
    	}
        OutOfScreenX();
    }
    
    private void OutOfScreenX() {
        if (mX > ZAMainActivity.CAMERA_WIDTH) { // OutOfScreenX (right)
            mX = 0;
        } else if (mX < 0) { // OutOfScreenX (left)
            mX = ZAMainActivity.CAMERA_WIDTH;
        }
    }
    // ===========================================================
    // Methods
    // ===========================================================
 
}