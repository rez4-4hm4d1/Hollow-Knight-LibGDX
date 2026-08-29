package com.MReza.HallowKnight.models.enemies;

import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.models.player.Player;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public class HuskHornhead extends Enemy{

    private final float WALK_SPEED = 100f;
    private final float ATTACK_SPEED = 350f;
    private final float WALK_DURATION = 3.0f;
    private final float REST_DURATION = 1.5f;

    private Rectangle visionBox;
    private final float VISION_WIDTH = 300f;
    private final float VISION_HEIGHT = 120f;

    private Rectangle enemyAttackBox;

    public enum HuskState {
        WALK,
        REST,
        ATTACK,
    }
    private HuskState currentState;

    public HuskHornhead(float x, float y, Player player) {
        super(x, y, 110, 150, 3, player);
        this.currentState = HuskState.WALK;
        this.visionBox = new Rectangle();
        this.enemyAttackBox = new Rectangle();
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
        updateVisionBox();

        if (currentState != HuskState.ATTACK){
            if (!player.isDead() && visionBox.overlaps(player.getHitbox())){
                startAttacking();
            }
        }

        switch (currentState){
            case WALK :
                velocityX = WALK_SPEED * facingDirection;
                if (stateTimer >= WALK_DURATION) {
                    currentState = HuskState.REST;
                    stateTimer = 0f;
                    velocityX = 0;
                }
                break;
            case REST:
                velocityX = 0;
                if (stateTimer >= REST_DURATION) {
                    currentState = HuskState.WALK;
                    stateTimer = 0;
                    facingDirection = facingDirection * -1;
                }
                break;
            case ATTACK:
                velocityX = ATTACK_SPEED * facingDirection;
                break;
        }

        if (currentState == HuskState.ATTACK) {
            float attackX = (facingDirection == 1) ? x + width : x - 60;
            enemyAttackBox.set(attackX, y, 60, height - 20);
        }
        else {
            enemyAttackBox.set(0, 0, 0, 0);
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
    private void startAttacking() {
        currentState = HuskState.ATTACK;
        stateTimer = 0f;
    }


    @Override
    protected void onWallHit() {
        if (currentState == HuskState.ATTACK){
            currentState = HuskState.REST;
            stateTimer = 0f;
        }
        else if (currentState == HuskState.WALK){
            facingDirection = facingDirection * -1;
        }
    }

    private void checkEdge(Array<Rectangle> solids){
        if (velocityY != 0) return;

        float sensorX = (facingDirection == 1) ? x + width + 5 : x - 15;
        float sensorY = y - 10;
        groundSensor.set(sensorX, sensorY,10, 10);

        boolean isGroundAhead = false;
        for (Rectangle rect : solids){
            if (groundSensor.overlaps(rect)){
                isGroundAhead = true;
                break;
            }
        }
        if (!isGroundAhead){
            if (currentState == HuskState.ATTACK){
                currentState = HuskState.REST;
                stateTimer = 0f;
                velocityX = 0;
            }
            else if (currentState == HuskState.WALK){
                facingDirection = facingDirection * -1;
                velocityX = WALK_SPEED * facingDirection;
            }
        }
    }

    public TextureRegion getFrame() {
        EnemyAnimationType currentAnim;

        if (isDead) {
            currentAnim = EnemyAnimationType.HUSK_DEATH;
        }
        else {
            switch (currentState) {
                case ATTACK:
                    currentAnim = EnemyAnimationType.HUSK_ATTACK;
                    break;
                case WALK:
                    currentAnim = EnemyAnimationType.HUSK_WALK;
                    break;
                case REST:
                    currentAnim = EnemyAnimationType.HUSK_IDLE;
                    break;
                default:
                    currentAnim = EnemyAnimationType.HUSK_WALK;
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
    protected void die(){
        super.die();
        stateTimer = 0f;
    }

    @Override
    protected List<Sound> getDamageSound() {
        return GameAssetManager.getEnemySound(EnemySoundType.HUSK_DAMAGE);
    }

    @Override
    protected List<Sound> getDeathSound() {
        return GameAssetManager.getEnemySound(EnemySoundType.HUSK_DEATH);
    }

    public Rectangle getVisionBox() { return visionBox; }
    public HuskState getCurrentState() { return currentState; }
    public Rectangle getEnemyAttackBox() {
        return enemyAttackBox;
    }
}
