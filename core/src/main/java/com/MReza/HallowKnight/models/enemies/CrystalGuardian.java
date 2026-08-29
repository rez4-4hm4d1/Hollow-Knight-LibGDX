package com.MReza.HallowKnight.models.enemies;

import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.models.player.Player;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public class CrystalGuardian extends Enemy {

    private final float ENRAGED_SPEED = 400f;
    private final float LASER_PREP_TIME = 0.5f;
    private final float LASER_DURATION = 1.2f;
    private final float TOTAL_SHOOT_DURATION = LASER_PREP_TIME + LASER_DURATION;
    private final float ENRAGED_DURATION = 1.8f;

    private Rectangle visionBox;
    private final float VISION_WIDTH = 600f;
    private final float VISION_HEIGHT = 180f;

    private Rectangle laserBox;
    private final float LASER_WIDTH = 800f;
    private final float LASER_HEIGHT = 50f;

    public enum GuardianState {
        IDLE,
        LASER,
        ENRAGED
    }

    private GuardianState currentState;
    private float enragedTimer = 0f;

    public CrystalGuardian(float x, float y, Player player) {
        super(x, y, 120, 160, 5, player);
        this.currentState = GuardianState.IDLE;
        this.visionBox = new Rectangle();
        this.laserBox = new Rectangle();
    }

    @Override
    public void update(float delta, Array<Rectangle> solids) {
        if (isDead) {
            stateTimer += delta;
            velocityX = 0;
            return;
        }

        updateInvincibility(delta);
        stateTimer += delta;

        switch (currentState) {
            case IDLE:
                velocityX = 0;
                updateVisionBox();

                if (!player.isDead() && visionBox.overlaps(player.getHitbox())) {
                    currentState = GuardianState.LASER;
                    stateTimer = 0f;
                }
                break;

            case LASER:
                velocityX = 0;

                if (stateTimer >= LASER_PREP_TIME && stateTimer <= TOTAL_SHOOT_DURATION) {
                    float laserX = (facingDirection == 1) ? x + width : x - LASER_WIDTH;
                    float laserY = y + (height / 2f) - (LASER_HEIGHT / 2f);
                    laserBox.set(laserX, laserY + 10, LASER_WIDTH, LASER_HEIGHT);
                } else {
                    laserBox.set(0, 0, 0, 0);
                }

                if (stateTimer > TOTAL_SHOOT_DURATION) {
                    currentState = GuardianState.ENRAGED;
                    enragedTimer = 0f;
                    laserBox.set(0, 0, 0, 0);
                }
                break;

            case ENRAGED:
                enragedTimer += delta;
                laserBox.set(0, 0, 0, 0);


                float enemyCenter = this.x + (this.width / 2f);
                float playerCenter = player.getX() + (player.getWidth() / 2f);

                float diffX = playerCenter - enemyCenter;

                float deadZone = 30f;

                if (Math.abs(diffX) > deadZone) {
                    if (diffX > 0) {
                        facingDirection = 1;
                    } else {
                        facingDirection = -1;
                    }
                }

                velocityX = ENRAGED_SPEED * facingDirection;

                if (enragedTimer >= ENRAGED_DURATION) {
                    currentState = GuardianState.IDLE;
                    stateTimer = 0f;
                    velocityX = 0;
                }
                break;
        }

        applyPhysicsAndCollision(delta, solids);
        checkEdge(solids);
    }

    private void updateVisionBox() {
        if (facingDirection == 1) {
            visionBox.set(x + width, y, VISION_WIDTH, VISION_HEIGHT);
        } else {
            visionBox.set(x - VISION_WIDTH, y, VISION_WIDTH, VISION_HEIGHT);
        }
    }

    @Override
    protected void onWallHit() {
        if (currentState == GuardianState.ENRAGED) {
            facingDirection = facingDirection * -1;
            currentState = GuardianState.IDLE;
        }
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
            if (currentState == GuardianState.ENRAGED) {
                facingDirection = facingDirection * -1;
                currentState = GuardianState.IDLE;
            }
        }
    }

    public TextureRegion getFrame() {
        EnemyAnimationType currentAnim;

        if (isDead) {
            currentAnim = EnemyAnimationType.GUARDIAN_DEATH;
        } else {
            switch (currentState) {
                case LASER:
                    currentAnim = EnemyAnimationType.GUARDIAN_SHOOT;
                    break;
                case ENRAGED:
                    currentAnim = EnemyAnimationType.GUARDIAN_RUN;
                    break;
                case IDLE:
                default:
                    currentAnim = EnemyAnimationType.GUARDIAN_IDLE;
                    break;
            }
        }

        boolean isLooping = !isDead && currentState != GuardianState.LASER;
        TextureRegion frame = GameAssetManager.getEnemyAnimation(currentAnim).getKeyFrame(stateTimer, isLooping);

        boolean needsFlip = (facingDirection == 1);
        if (frame.isFlipX() != needsFlip) {
            frame.flip(true, false);
        }
        return frame;
    }

    @Override
    protected void die() {
        super.die();
        stateTimer = 0f;
    }

    @Override
    protected List<Sound> getDamageSound() {
        return GameAssetManager.getEnemySound(EnemySoundType.GUARDIAN_DAMAGE);
    }

    @Override
    protected List<Sound> getDeathSound() {
        return GameAssetManager.getEnemySound(EnemySoundType.GUARDIAN_DEATH);
    }

    public Rectangle getVisionBox() { return visionBox; }
    public Rectangle getLaserBox() { return laserBox; }
    public GuardianState getCurrentState() { return currentState; }

    public float getStateTimer() {
        return stateTimer;
    }

}
