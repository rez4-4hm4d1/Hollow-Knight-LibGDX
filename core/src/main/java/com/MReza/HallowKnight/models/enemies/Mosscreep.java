package com.MReza.HallowKnight.models.enemies;

import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.models.player.Player;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public class Mosscreep extends Enemy {

    private final float WALK_SPEED = 100f;

    public Mosscreep(float x, float y, Player player) {
        super(x, y, 60, 40, 2, player);
        this.facingDirection = 1;
    }

    @Override
    public void update(float delta, Array<Rectangle> solids) {
        if (isDead) {
            stateTimer += delta;
            velocityX = 0;
            applyPhysicsAndCollision(delta, solids);
            return;
        }

        updateInvincibility(delta);
        stateTimer += delta;

        velocityX = WALK_SPEED * facingDirection;

        applyPhysicsAndCollision(delta, solids);

        checkEdge(solids);
    }

    @Override
    protected void onWallHit() {
        facingDirection *= -1;
    }

    private void checkEdge(Array<Rectangle> solids) {
        if (velocityY != 0) return;

        float sensorX = (facingDirection == 1) ? x + width + 5 : x - 15;
        float sensorY = y - 10;
        groundSensor.set(sensorX, sensorY, 10, 10);

        boolean isGroundAhead = false;
        for (Rectangle rect : solids) {
            if (groundSensor.overlaps(rect)) {
                isGroundAhead = true;
                break;
            }
        }

        if (!isGroundAhead) {
            facingDirection *= -1;
        }
    }

    public TextureRegion getFrame() {
        EnemyAnimationType currentAnim;
        if (this.isDead){
            currentAnim = EnemyAnimationType.MOSSCREEP_DEATH;
        }
        else {
             currentAnim = EnemyAnimationType.MOSSCREEP_WALK;
        }
        TextureRegion frame = GameAssetManager.getEnemyAnimation(currentAnim).getKeyFrame(stateTimer, !isDead);

        boolean needsFlip = (facingDirection == 1);
        if (frame.isFlipX() != needsFlip) {
            frame.flip(true, false);
        }
        return frame;
    }

    @Override
    protected List<Sound> getDamageSound() {
        return GameAssetManager.getEnemySound(EnemySoundType.MOSSCREEP_DAMAGE);
    }

    @Override
    protected List<Sound> getDeathSound() {
        return GameAssetManager.getEnemySound(EnemySoundType.MOSSCREEP_DEATH);
    }
}
