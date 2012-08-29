package com.example.zadev01.objects;

import org.andengine.engine.handler.physics.PhysicsHandler;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.texture.region.ITiledTextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.emorym.android_pusher.Pusher;
 
public abstract class GameObject extends AnimatedSprite {
 
    // ===========================================================
    // Constants
    // ===========================================================
 
    // ===========================================================
    // Fields
    // ===========================================================
 
    public PhysicsHandler mPhysicsHandler;
    public Pusher mPusher;
    public int velocity;
    public float angle;
    // ===========================================================
    // Constructors
    // ===========================================================
 
    public GameObject(final float pX, final float pY, final ITiledTextureRegion pTiledTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, Pusher p) {
        super(pX, pY, pTiledTextureRegion, pVertexBufferObjectManager);
    }
 
    // ===========================================================
    // Getter & Setter
    // ===========================================================
 
    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================
 
    // ===========================================================
    // Methods
    // ===========================================================
 
    public abstract void move();
}