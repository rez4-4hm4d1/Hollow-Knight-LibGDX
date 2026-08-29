package com.MReza.HallowKnight.models.enemies;

import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.models.GameWorld;
import com.MReza.HallowKnight.models.player.Achievement;
import com.MReza.HallowKnight.models.player.Player;
import com.MReza.HallowKnight.view.screens.GameScreen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.Collections;
import java.util.List;

public class FalseKnight extends Enemy {

    public enum BossState {
        SLEEP,
        IDLE,
        MACE_SLAM,
        CHARGE_RUN,
        OFFENSIVE_LEAP,
        DEFENSIVE_LEAP,
        POWER_SLAM,
        STUNNED
    }

    private BossState currentState;
    private BossState lastState;
    private GameScreen gameScreen;

    private GameWorld world;

    private float baseRunSpeed = 305;
    private float currentRunSpeed = baseRunSpeed;
    private float jumpForce = 850f;
    private float speedMultiplier = 1.0f;
    private float decisionDelay = 1.0f;

    private Rectangle maceHitbox;
    private Rectangle headHitbox;
    private Rectangle shockwaveBox;
    private float shockwaveSpeed = 400f;
    private int shockwaveDirection = 3;
    private boolean hasSlammed = false;

    private boolean isPhase2 = false;
    private int recentDamage = 0;
    private float damageTimer = 0f;

    private float initialX, initialY;

    public FalseKnight(float x, float y, Player player, GameScreen gameScreen, GameWorld world) {
        super(x, y, 190, 250, 50, player);
        this.gameScreen = gameScreen;
        this.world = world;
        this.initialX = x;
        this.initialY = y;

        this.currentState = BossState.SLEEP;
        this.lastState = BossState.SLEEP;
        this.maceHitbox = new Rectangle(0, 0, 0, 0);
        this.headHitbox = new Rectangle(0, 0, 0, 0);
        this.shockwaveBox = new Rectangle(0, 0, 0, 0);

        INVINCIBILITY_DURATION = 0.5f;
    }

    @Override
    public void update(float delta, Array<Rectangle> solids) {
        if (isDead) {
            stateTimer += delta;
            velocityX = 0;
            return;
        }

        if (!world.isBossFightActive()) {
            applyPhysicsAndCollision(delta, solids);
            return;
        }

        if (currentState == BossState.SLEEP) {
            changeState(BossState.IDLE);
        }

        updateInvincibility(delta);
        stateTimer += delta;

        if (recentDamage > 0) {
            damageTimer += delta;
            if (damageTimer > 1.0f) {
                recentDamage = 0;
            }
        }

        if (shockwaveBox.width > 0) {
            shockwaveBox.x += (shockwaveSpeed * shockwaveDirection) * delta;
        }

        switch (currentState) {
            case IDLE:
                velocityX = 0;
                if (stateTimer > (decisionDelay / speedMultiplier)) {
                    decideNextMove();
                }
                break;

            case MACE_SLAM:
                velocityX = 0;
                if (stateTimer > (0.5f / speedMultiplier) && !hasSlammed) {
                    hasSlammed = true;
                    gameScreen.triggerCameraShake(0.3f, 5f);
                    createMaceHitbox(400, 80);
                }
                if (stateTimer > (0.6f / speedMultiplier)) {
                    maceHitbox.set(0, 0, 0, 0);
                }

                if (stateTimer > (1.0f / speedMultiplier)) {
                    changeState(BossState.IDLE);
                }
                break;

            case CHARGE_RUN:
                velocityX = (player.getX() < this.x + this.width) ? -currentRunSpeed : currentRunSpeed;
                facingDirection = (velocityX > 0) ? 1 : -1;

                if (Math.abs(player.getX() - this.x) < 320) {
                    changeState(BossState.MACE_SLAM);
                }
                break;

            case STUNNED:
                velocityX = 0;
                float headW = 70;
                float headH = 70;

                if (facingDirection == 1) {
                    headHitbox.set(this.x + this.width - 20, this.y + 50, headW, headH);
                } else {
                    headHitbox.set(this.x - headW + 20, this.y + 50, headW, headH);
                }

                if (stateTimer > 4.0f) {
                    isPhase2 = true;
                    speedMultiplier = 1.75f;
                    currentRunSpeed = baseRunSpeed * speedMultiplier;
                    headHitbox.set(0,0,0,0);
                    changeState(BossState.IDLE);
                }
                break;

            case OFFENSIVE_LEAP:
            case DEFENSIVE_LEAP:
            case POWER_SLAM:
                break;
        }

        float prevVelocityY = velocityY;

        applyPhysicsAndCollision(delta, solids);

        if (currentState == BossState.OFFENSIVE_LEAP ||
            currentState == BossState.DEFENSIVE_LEAP ||
            currentState == BossState.POWER_SLAM) {

            if (prevVelocityY < 0 && velocityY == 0) {
                if (!hasSlammed) {
                    hasSlammed = true;
                    velocityX = 0;
                    stateTimer = 0f;

                    if (currentState == BossState.POWER_SLAM) {
                        gameScreen.triggerCameraShake(1.0f, 10f);
                        shockwaveDirection = facingDirection;
                        shockwaveBox.set(this.x + (facingDirection == 1 ? this.width : -212), this.y, 212, 212);
                    } else {
                        gameScreen.triggerCameraShake(0.2f, 3f);
                    }
                }
            }

            if (hasSlammed) {
                if (stateTimer > (1.2f / speedMultiplier)) {
                    shockwaveBox.set(0, 0, 0, 0);
                    changeState(BossState.IDLE);
                }
            }
        }
    }

    private void decideNextMove() {
        facingDirection = (player.getX() > this.x) ? 1 : -1;
        float distToPlayer = Math.abs(player.getX() - this.x);
        BossState nextState = BossState.IDLE;

        if (recentDamage >= 3 && MathUtils.randomBoolean(0.7f)) {
            nextState = BossState.DEFENSIVE_LEAP;
            recentDamage = 0;
        }
        else {
            if (distToPlayer <= 320) {
                nextState = MathUtils.randomBoolean(0.7f) ? BossState.MACE_SLAM : BossState.OFFENSIVE_LEAP;
            }
            else if (distToPlayer > 400 && distToPlayer < 800) {
                if (isPhase2 && MathUtils.randomBoolean(0.7f)) {
                    nextState = BossState.POWER_SLAM;
                }
                else {
                    nextState = MathUtils.randomBoolean(0.6f) ? BossState.CHARGE_RUN : BossState.OFFENSIVE_LEAP;
                }
            } else {
                nextState = MathUtils.randomBoolean(0.6f) ? BossState.CHARGE_RUN : BossState.OFFENSIVE_LEAP;
            }
        }

        if (nextState == lastState) {
            if (distToPlayer > 200) {
                nextState = BossState.CHARGE_RUN;
            }
            else nextState = BossState.MACE_SLAM;
        }

        changeState(nextState);
    }

    private void changeState(BossState newState) {
        this.lastState = this.currentState;
        this.currentState = newState;
        this.stateTimer = 0f;
        this.hasSlammed = false;

        this.maceHitbox.set(0, 0, 0, 0);

        if (newState == BossState.POWER_SLAM) {
            velocityY = jumpForce * 1.2f;
            velocityX = (player.getX() < this.x) ? -currentRunSpeed : currentRunSpeed;
            facingDirection = (velocityX > 0) ? 1 : -1;
        }
        else if (newState == BossState.OFFENSIVE_LEAP) {
            velocityY = jumpForce;
            velocityX = (player.getX() < this.x) ? -currentRunSpeed : currentRunSpeed;
            facingDirection = (velocityX > 0) ? 1 : -1;
        }
        else if (newState == BossState.DEFENSIVE_LEAP) {
            velocityY = jumpForce * 0.8f;
            velocityX = (player.getX() < this.x) ? currentRunSpeed : -currentRunSpeed;
            facingDirection = (player.getX() < this.x) ? -1 : 1;
        }
    }

    public void resetBoss() {
        this.x = initialX;
        this.y = initialY;
        this.hp = maxHp;
        this.isDead = false;
        this.isPhase2 = false;
        this.speedMultiplier = 1.0f;
        this.currentRunSpeed = baseRunSpeed;
        this.hasSlammed = false;
        this.maceHitbox.set(0, 0, 0, 0);
        this.headHitbox.set(0, 0, 0, 0);
        this.shockwaveBox.set(0, 0, 0, 0);
        this.currentState = BossState.SLEEP;
        this.lastState = BossState.SLEEP;

        this.hitbox.setPosition(this.x, this.y);
    }

    private void createMaceHitbox(float w, float h) {
        float maceX = (facingDirection == 1) ? this.x + this.width : this.x - w;
        maceHitbox.set(maceX, this.y, w, h);
    }

    @Override
    protected void onWallHit() {
        if (currentState == BossState.CHARGE_RUN) {
            changeState(BossState.DEFENSIVE_LEAP);
        } else if (currentState == BossState.OFFENSIVE_LEAP ||
            currentState == BossState.DEFENSIVE_LEAP ||
            currentState == BossState.POWER_SLAM) {
            velocityX = 0;
        }
    }

    @Override
    public boolean takeDamage(int damage) {
        if (currentState == BossState.STUNNED) {
            return super.takeDamage(damage);
        }

        boolean hitSuccessful = super.takeDamage(damage);

        if (hitSuccessful) {
            if (currentState == BossState.IDLE || currentState == BossState.SLEEP) {
                facingDirection = (player.getX() > this.x) ? 1 : -1;
            }

            recentDamage += damage;
            damageTimer = 0f;

            if (hp <= maxHp / 2 && !isPhase2 && currentState != BossState.STUNNED) {
                changeState(BossState.STUNNED);
                recentDamage = 0;
            }
        }
        return hitSuccessful;
    }

    @Override
    protected void die() {
        super.die();
        stateTimer = 0f;
        gameScreen.triggerAchievementNotification(Achievement.FALSE_KNIGHT);
    }

    public float getStateTimer() {
        return stateTimer;
    }

    public TextureRegion getFrame() {
        Animation<TextureRegion> animation;
        boolean loop = true;

        if (isDead){
            animation = GameAssetManager.getEnemyAnimation(EnemyAnimationType.FALSE_KNIGHT_DEATH);
            loop = false;
        }
        else {
            switch (currentState) {
                case SLEEP:
                    animation = GameAssetManager.getEnemyAnimation(EnemyAnimationType.FALSE_KNIGHT_SLEEP);
                    break;
                case IDLE:
                    animation = GameAssetManager.getEnemyAnimation(EnemyAnimationType.FALSE_KNIGHT_IDLE);
                    break;
                case CHARGE_RUN:
                    animation = GameAssetManager.getEnemyAnimation(EnemyAnimationType.FALSE_KNIGHT_RUN);
                    break;
                case MACE_SLAM:
                    animation = GameAssetManager.getEnemyAnimation(EnemyAnimationType.FALSE_KNIGHT_MACE_SLAM);
                    loop = false;
                    break;
                case OFFENSIVE_LEAP:
                case DEFENSIVE_LEAP:
                    animation = GameAssetManager.getEnemyAnimation(EnemyAnimationType.FALSE_KNIGHT_JUMP);
                    loop = false;
                    break;
                case POWER_SLAM:
                    animation = GameAssetManager.getEnemyAnimation(EnemyAnimationType.FALSE_KNIGHT_POWER_SLAM);
                    loop = false;
                    break;
                case STUNNED:
                    animation = GameAssetManager.getEnemyAnimation(EnemyAnimationType.FALSE_KNIGHT_STUNNED);
                    break;
                default:
                    animation = GameAssetManager.getEnemyAnimation(EnemyAnimationType.FALSE_KNIGHT_IDLE);
                    break;
            }
//            System.out.println(currentState); // for debug
        }

        TextureRegion frame = animation.getKeyFrame(stateTimer, loop);

        boolean needsFlip = (facingDirection == 1);
        if (frame.isFlipX() != needsFlip) {
            frame.flip(true, false);
        }

        return frame;
    }

    public int getShockwaveDirection() {
        return shockwaveDirection;
    }

    @Override
    protected List<Sound> getDamageSound() { return GameAssetManager.getEnemySound(EnemySoundType.FALSE_KNIGHT_DAMAGE); }
    @Override
    protected List<Sound> getDeathSound() { return GameAssetManager.getEnemySound(EnemySoundType.FALSE_KNIGHT_DEATH); }

    public BossState getCurrentState() { return currentState; }
    public Rectangle getMaceHitbox() { return maceHitbox; }
    public Rectangle getHeadHitbox() { return headHitbox; }
    public Rectangle getShockwaveBox() { return shockwaveBox; }
    public boolean isPhase2() { return isPhase2; }
}
