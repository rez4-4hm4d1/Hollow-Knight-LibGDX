package com.MReza.HallowKnight.models.enemies;

import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.models.player.Player;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public class Mosquito extends Enemy {

    public enum MosquitoState {
        PATROLLING,
        TARGETING,
        ATTACK
    }

    private MosquitoState currentState;

    private Rectangle visionBox;
    private final float VISION_RADIUS = 700f;

    private float targetX, targetY;
    private float diveTimer = 0f;
    private final float TARGETING_DELAY = 0.5f;
    private final float ATTACK_SPEED = 450f;
    private final float MAX_ATTACK_TIME = 0.7f;

    public Mosquito(float x, float y, Player player) {
        super(x, y, 45, 45, 2, player);
        this.currentState = MosquitoState.PATROLLING;
        this.visionBox = new Rectangle();
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

        visionBox.set(x + width / 2f - VISION_RADIUS / 2f, y + height / 2f - VISION_RADIUS / 2f, VISION_RADIUS, VISION_RADIUS);

        switch (currentState) {
            case PATROLLING:
                velocityX = 0;
                velocityY = 0;

                if (!player.isDead() && visionBox.overlaps(player.getHitbox())) {
                    currentState = MosquitoState.TARGETING;
                    stateTimer = 0f;

                    targetX = player.getX() + (player.getWidth() / 2f);
                    targetY = player.getY() + (player.getHeight() / 2f);
                }
                break;

            case TARGETING:
                velocityX = 0;
                velocityY = 0;

                facingDirection = (targetX > this.x ) ? 1 : -1;

                if (stateTimer >= TARGETING_DELAY) {
                    currentState = MosquitoState.ATTACK;
                    diveTimer = 0f;

                    float startX = x + width / 2f;
                    float startY = y + height / 2f;
                    float angle = MathUtils.atan2(targetY - startY, targetX - startX);

                    velocityX = MathUtils.cos(angle) * ATTACK_SPEED;
                    velocityY = MathUtils.sin(angle) * ATTACK_SPEED;
                }
                break;

            case ATTACK:
                diveTimer += delta;

                if (diveTimer >= MAX_ATTACK_TIME) {
                    resetToPatrol();
                }
                break;
        }

        applyFlyingPhysicsAndCollision(delta, solids);
    }

    private void applyFlyingPhysicsAndCollision(float delta, Array<Rectangle> solids) {
        x += velocityX * delta;
        hitbox.setPosition(x, y);

        for (Rectangle rect : solids) {
            if (hitbox.overlaps(rect)) {
                if (velocityX > 0) x = rect.x - width;
                else if (velocityX < 0) x = rect.x + rect.width;
                onWallHit();
                hitbox.setPosition(x, y);
            }
        }

        y += velocityY * delta;
        hitbox.setPosition(x, y);

        for (Rectangle rect : solids) {
            if (hitbox.overlaps(rect)) {
                if (velocityY > 0) y = rect.y - height;
                else if (velocityY < 0) y = rect.y + rect.height;
                onWallHit();
                hitbox.setPosition(x, y);
            }
        }
    }

    @Override
    protected void onWallHit() {
        if (currentState == MosquitoState.ATTACK) {
            resetToPatrol();
        }
    }

    private void resetToPatrol() {
        currentState = MosquitoState.PATROLLING;
        stateTimer = 0f;
        velocityX = 0;
        velocityY = 0;
    }

    public TextureRegion getFrame() {
        EnemyAnimationType currentAnim;

        if (isDead) {
            currentAnim = EnemyAnimationType.MOSQUITO_DEATH;
        }
        else {
            switch (currentState) {
                case ATTACK:
                    currentAnim = EnemyAnimationType.MOSQUITO_ATTACK;
                    break;
                case TARGETING:
                case PATROLLING:
                default:
                    currentAnim = EnemyAnimationType.MOSQUITO_FLY;
                    break;
            }
        }

        boolean isLooping = !isDead;
        TextureRegion frame = GameAssetManager.getEnemyAnimation(currentAnim).getKeyFrame(stateTimer, isLooping);

        boolean needsFlip = (facingDirection == 1);
        if (frame.isFlipX() != needsFlip) {
            frame.flip(true, false);
        }
        return frame;
    }

    @Override
    protected List<Sound> getDamageSound() {
        return GameAssetManager.getEnemySound(EnemySoundType.MOSQUITO_DAMAGE);
    }

    @Override
    protected List<Sound> getDeathSound() {
        return GameAssetManager.getEnemySound(EnemySoundType.MOSQUITO_DEATH);
    }
}
