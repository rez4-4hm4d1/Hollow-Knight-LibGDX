package com.MReza.HallowKnight.models.player;

import com.MReza.HallowKnight.controllers.GameAssetManager;
import com.MReza.HallowKnight.controllers.ProfileManager;
import com.MReza.HallowKnight.models.GameObject;
import com.MReza.HallowKnight.models.config.AudioConfig;
import com.MReza.HallowKnight.models.config.KeyConfig;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import java.util.List;

public class Player extends GameObject {

    private float velocityX, velocityY;
    private boolean isAttacking = false;
    private Rectangle attackBox;
    private boolean isGrounded = false;
    private boolean wasGrounded = false;
    private boolean isLanding = false;
    private boolean isDashing = false;
    private boolean canDoubleJump = false;
    private boolean isDoubleJumping = false;

    private boolean isDead = false;
    private float deathTimer = 0f;
    private final float DEATH_DELAY = 2f;

    private boolean isFallingInPit = false;
    private float pitTimer = 0f;
    private final float PIT_DELAY = 1.0f;

    private float currentRespawnX = 100, currentRespawnY = 200;
    private float mapStartX = 100, mapStartY = 200;

    private int maxHealth = 5;
    private int currentHealth = 5;
    private int maxSoul = 99;
    private int currentSoul = 99;
    private final int SOUL_PER_HIT = 11;
    private final int SOUL_PER_HEAL = 33;

    private boolean isInvincible = false;
    private float invincibilityTimer = 0f;
    private final float INVINCIBILITY_DURATION = 1.5f;

    private boolean isFocusing = false;
    private boolean isFocusEnding = false;
    private float focusTimer = 0f;
    private final float FOCUS_DURATION = 1.5f;

    private boolean isGodMode = false;
    private boolean isNoclip = false;

    private Sound activeFocusSound;
    private long focusSoundId = -1;

    private final float GRAVITY = -1500f;
    private final float MOVE_SPEED = 300f;
    private final float JUMP_FORCE = 600f;
    private final float DOUBLE_JUMP_FORCE = 700f;
    private final float DASH_COOLDOWN = 2f;


    private float dashTimer = 0f;
    private float attackTimer = 0f;
    private float landingTimer = 0f;
    private float dashCooldownTimer = 0f;

    private int facingDirection = 1;

    private int deaths = 0;
    private int enemiesKilled = 0;
    private float playTime = 0f;

    private PlayerAnimationType currentAnimation = PlayerAnimationType.IDLE;
    private float stateTime = 0f;

    public Player(float x , float y){
        super(x, y, 50, 130);
        attackBox = new Rectangle();
        this.currentRespawnX = x;
        this.currentRespawnY = y;
        this.mapStartX = x;
        this.mapStartY = y;
    }

    @Override
    public void update(float delta, Array<Rectangle> solids) {
        if (isNoclip) {
            if (isInvincible) {
                invincibilityTimer -= delta;
                if (invincibilityTimer <= 0) isInvincible = false;
            }

            float flySpeed = 600f;

            if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
                this.y += flySpeed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                this.y -= flySpeed * delta;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                this.x += flySpeed * delta;
                facingDirection = 1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                this.x -= flySpeed * delta;
                facingDirection = -1;
            }

            hitbox.setPosition(this.x, this.y);
            return;
        }
        if (isDead) {
            currentAnimation = PlayerAnimationType.DEATH;
            stateTime += delta;
            deathTimer += delta;
            if (deathTimer >= DEATH_DELAY) {
                isDead = false;
                currentHealth = maxHealth;
                this.x = mapStartX;
                this.y = mapStartY;
                this.velocityX = 0;
                this.velocityY = 0;
                hitbox.setPosition(x, y);
            }
            return;
        }
        if (isFallingInPit) {
            pitTimer += delta;
            if (pitTimer >= PIT_DELAY) {
                isFallingInPit = false;
                currentHealth--;
                if (currentHealth <= 0) {
                    die();
                } else {
                    this.x = currentRespawnX;
                    this.y = currentRespawnY;
                    this.velocityX = 0;
                    this.velocityY = 0;
                    hitbox.setPosition(x, y);

                    isInvincible = true;
                    invincibilityTimer = INVINCIBILITY_DURATION;
                }
            }
            return;
        }
        wasGrounded = isGrounded;
        if (isDashing) {
            dashTimer -= delta;
            if (dashTimer <= 0) isDashing = false;
        }
        if (isAttacking) {
            attackTimer -= delta;
            if (attackTimer <= 0) isAttacking = false;
        }
        if (isInvincible) {
            invincibilityTimer -= delta;
            if (invincibilityTimer <= 0) isInvincible = false;
        }
        if (dashCooldownTimer > 0) dashCooldownTimer -= delta;

        boolean wantToFocus = Gdx.input.isKeyPressed(KeyConfig.FOCUS) && isGrounded && !isDashing && !isAttacking;
        if (wantToFocus && currentSoul >= SOUL_PER_HEAL && currentHealth < maxHealth) {
            if (!isFocusing){
                isFocusing = true;
                isFocusEnding = false;
                startFocusSound();
            }
            velocityX = 0;
            focusTimer += delta;

            float currentFocusDuration = FOCUS_DURATION;
            if (ProfileManager.isCharmEquipped(CharmType.QUICK_FOCUS.name())) {
                currentFocusDuration = FOCUS_DURATION / 2;
            }
            if (focusTimer >= currentFocusDuration) {
                stopFocusSound();
                playSound(GameAssetManager.getPlayerSound(PlayerSoundType.GET_HEAL));
                currentHealth++;
                currentSoul -= SOUL_PER_HEAL;
                focusTimer = 0f;

                if (currentHealth >= maxHealth || currentSoul < SOUL_PER_HEAL){
                    isFocusing = false;
                    isFocusEnding = true;
                }
                else  {
                    startFocusSound();
                }
            }
        }
        else {
            if (isFocusing) {
                isFocusing = false;
                focusTimer = 0f;
                isFocusEnding = true;
                stopFocusSound();
            }
        }

        if (!isDashing && !isFocusing) {
            velocityX = 0;
            if (Gdx.input.isKeyPressed(KeyConfig.MOVE_LEFT)) {
                velocityX = -MOVE_SPEED;
                facingDirection = -1;
            }
            else if (Gdx.input.isKeyPressed(KeyConfig.MOVE_RIGHT)) {
                velocityX = MOVE_SPEED;
                facingDirection = 1;
            }

            if (Gdx.input.isKeyJustPressed(KeyConfig.JUMP)) {
                if (isGrounded){
                    velocityY = JUMP_FORCE;
                    isGrounded = false;
                    isDoubleJumping = false;
                }
                else if (canDoubleJump){
                    velocityY = DOUBLE_JUMP_FORCE;
                    canDoubleJump = false;
                    isDoubleJumping = true;
                    stateTime = 0f;
                }
            }

            if (Gdx.input.isKeyJustPressed(KeyConfig.DASH) && !isDashing && dashCooldownTimer <= 0) {
                isDashing = true;
                dashTimer = 0.3f;

                float currentDashCooldown = DASH_COOLDOWN;
                if (ProfileManager.isCharmEquipped(CharmType.DASH_MASTER.name())) {
                    currentDashCooldown *= 0.5f;
                }
                dashCooldownTimer = currentDashCooldown;
                velocityX = 850f * facingDirection;
            }

            if (Gdx.input.isKeyJustPressed(KeyConfig.ATTACK) && !isAttacking) {
                isAttacking = true;

                float attackDuration = 0.3f;
                if (ProfileManager.isCharmEquipped(CharmType.QUICK_SLASH.name())) {
                    attackDuration = 0.15f;
                }
                attackTimer = attackDuration;

                playSound(GameAssetManager.getPlayerSound(PlayerSoundType.SLASH));
            }
        }

        //gravity

        if (!isDashing){
            velocityY += GRAVITY * delta;
        } else {
            velocityY = 0;
        }

        x += velocityX * delta;
        hitbox.setPosition(x,y);

        for (Rectangle rect : solids){
            if (hitbox.overlaps(rect)){
                if (velocityX > 0){
                    x = rect.x - width;
                }
                else if (velocityX < 0){
                    x = rect.x + rect.width;
                }
                velocityX = 0;
                hitbox.setPosition(x, y);
            }
        }

        boolean onGroundThisFrame = false;
        y += velocityY * delta;
        hitbox.setPosition(x, y);

        for (Rectangle rect : solids){
            if (hitbox.overlaps(rect)){
                if (velocityY > 0){
                    y = rect.y - height;
                }
                else if (velocityY < 0){
                    y = rect.y + rect.height;
                    onGroundThisFrame = true;
                }
                velocityY = 0;
                hitbox.setPosition(x, y);
            }
        }
        isGrounded = onGroundThisFrame;

        if (isGrounded){
            canDoubleJump = true;
            isDoubleJumping = false;
        }

        if (!wasGrounded && isGrounded) {
            isLanding = true;
            landingTimer = 0.2f;
        }
        if (isLanding) {
            landingTimer -= delta;
            if (landingTimer <= 0) isLanding = false;
        }
        if (isAttacking) {
            float attackX = (facingDirection == 1) ? x + width : x - 100;
            attackBox.set(attackX, y, 100, height);
        }
        else {
            attackBox.set(0, 0, 0, 0);
        }
        PlayerAnimationType nextAnimation = PlayerAnimationType.IDLE;
        if (isDashing) {
            nextAnimation = PlayerAnimationType.DASH;
        }
        else if (isAttacking) {
            nextAnimation = PlayerAnimationType.SLASH;
        }
        else if (isFocusEnding){
            nextAnimation = PlayerAnimationType.FOCUS_END;

            if (GameAssetManager.getPlayerAnimation(PlayerAnimationType.FOCUS_END).isAnimationFinished(stateTime)){
                isFocusEnding = false;
            }
        }
        else if (isFocusing){
            if (currentAnimation != PlayerAnimationType.FOCUS_START && currentAnimation != PlayerAnimationType.FOCUS){
                nextAnimation = PlayerAnimationType.FOCUS_START;
            }
            else if (currentAnimation == PlayerAnimationType.FOCUS_START){
                if (GameAssetManager.getPlayerAnimation(PlayerAnimationType.FOCUS_START).isAnimationFinished(stateTime)){
                    nextAnimation = PlayerAnimationType.FOCUS;
                }
                else {
                    nextAnimation = PlayerAnimationType.FOCUS_START;
                }
            }
            else {
                nextAnimation = PlayerAnimationType.FOCUS;
            }
        }
        else if (!isGrounded) {
            if (velocityY > 0) {
                if (isDoubleJumping){
                    nextAnimation = PlayerAnimationType.DOUBLE_JUMP;
                }
                else {
                    nextAnimation = PlayerAnimationType.AIRBORNE;
                }
            }
            else
            { // velocityY < 0
                nextAnimation = PlayerAnimationType.FALL;
                isDoubleJumping = false;
            }
        }

        else if (velocityX != 0) {
            if (currentAnimation != PlayerAnimationType.RUN_START && currentAnimation != PlayerAnimationType.RUN){
                nextAnimation = PlayerAnimationType.RUN_START;
            }
            else if (currentAnimation == PlayerAnimationType.RUN_START){
                if (GameAssetManager.getPlayerAnimation(PlayerAnimationType.RUN_START).isAnimationFinished(stateTime)){
                    nextAnimation = PlayerAnimationType.RUN;
                }
                else {
                    nextAnimation = PlayerAnimationType.RUN_START;
                }
            }
            else {
                nextAnimation = PlayerAnimationType.RUN;
            }
        }
        else if (isLanding) {
            nextAnimation = PlayerAnimationType.LANDING;
        }
        if (currentAnimation != nextAnimation) {
            currentAnimation = nextAnimation;
            stateTime = 0f;
        }
        stateTime += delta;
    }

    private void startFocusSound() {
        if (AudioConfig.isSfxOn() && activeFocusSound == null) {
            List<Sound> sounds = GameAssetManager.getPlayerSound(PlayerSoundType.FOCUS);
            if (sounds != null && !sounds.isEmpty()) {
                activeFocusSound = sounds.get((int) (Math.random() * sounds.size()));
                focusSoundId = activeFocusSound.loop(1.0f);
            }
        }
    }


    private void stopFocusSound() {
        if (activeFocusSound != null) {
            activeFocusSound.stop(focusSoundId);
            activeFocusSound = null;
        }
    }
    public void playSound(List<Sound> sounds){
        if (AudioConfig.isSfxOn() && sounds != null && !sounds.isEmpty()){
            int randomIndex = (int) (Math.random() * sounds.size());
            sounds.get(randomIndex).play(1.0f);
        }
    }
    public TextureRegion getFrame() {
        boolean isLooping = (currentAnimation != PlayerAnimationType.RUN_START &&
            currentAnimation != PlayerAnimationType.LANDING &&
            currentAnimation != PlayerAnimationType.FOCUS_START &&
            currentAnimation != PlayerAnimationType.FOCUS_END &&
            currentAnimation != PlayerAnimationType.DEATH );
        TextureRegion frame = GameAssetManager.getPlayerAnimation(currentAnimation).getKeyFrame(stateTime, isLooping);

        boolean needsFlip = (facingDirection == 1);
        if (frame.isFlipX() != needsFlip) {
            frame.flip(true, false);
        }

        return frame;
    }

    public void addPlayTime(float delta) { this.playTime += delta; }
    public void incrementKills() { this.enemiesKilled++; }
    public void incrementDeaths() { this.deaths++; }

    public void takeDamage(int amount){
        if (isGodMode) {
            return;
        }
        if (isInvincible || isDead) return;;

        currentHealth -= amount;
        stopFocusSound();
        isFocusing = false;
        focusTimer = 0f;

        if (currentHealth <= 0){
            die();
        }
        else {
            isInvincible = true;
            invincibilityTimer = INVINCIBILITY_DURATION;
            playSound(GameAssetManager.getPlayerSound(PlayerSoundType.TAKE_DAMAGE));
        }
    }
    public void fallInPit() {
        isFallingInPit = true;
        pitTimer = 0f;

        velocityX = 0;
        velocityY = 0;

        stopFocusSound();
        isFocusing = false;
        isFocusEnding = false;
        focusTimer = 0f;
    }
    public void addSoul(){
        int soulToAdd = SOUL_PER_HIT;
        if (ProfileManager.isCharmEquipped(CharmType.SOUL_CATCHER.name())) {
            soulToAdd += 10;
        }
        currentSoul += soulToAdd;

        if (currentSoul > maxSoul) {
            currentSoul = maxSoul;
        }
    }
    private void die(){
        isDead = true;
        deathTimer = 0f;
        stateTime = 0f;
        velocityX = 0;
        velocityY = 0;

        stopFocusSound();
        isFocusing = false;
        isFocusEnding = false;

        incrementDeaths();
        playSound(GameAssetManager.getPlayerSound(PlayerSoundType.DEATH));
    }
    public void setRespawnPoint(float x, float y) {
        this.currentRespawnX = x;
        this.currentRespawnY = y;
    }

    public void setMapStart(float x, float y) {
        this.mapStartX = x;
        this.mapStartY = y;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        this.hitbox.setPosition(x, y);
    }
    public float getStateTime() {
        return stateTime;
    }

    public int getFacingDirection() {
        return facingDirection;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public float getVelocityX() {
        return velocityX;
    }

    public float getVelocityY() {
        return velocityY;
    }

    public Rectangle getAttackBox() {
        return attackBox;
    }

    public boolean isGrounded() {
        return isGrounded;
    }

    public boolean isWasGrounded() {
        return wasGrounded;
    }

    public boolean isLanding() {
        return isLanding;
    }

    public boolean isDashing() {
        return isDashing;
    }

    public boolean isInvincible() {
        return isInvincible;
    }

    public float getInvincibilityTimer() {
        return invincibilityTimer;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getCurrentSoul() {
        return currentSoul;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public void setCurrentSoul(int currentSoul) {
        this.currentSoul = currentSoul;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public int getMaxSoul() {
        return maxSoul;
    }

    public boolean isDead() { return isDead; }

    public boolean isFallingInPit() {
        return isFallingInPit;
    }

    public void setGodMode(boolean godMode) {
        this.isGodMode = godMode;
    }

    public void setNoclip(boolean noclip) {
        this.isNoclip = noclip;
    }

    public void emergencyHeal() {
        if (this.currentHealth < maxHealth) {
            this.currentHealth += 1;
        }
    }
    public int getDeaths() { return deaths; }
    public int getEnemiesKilled() { return enemiesKilled; }
    public float getPlayTime() { return playTime; }

    public void setDeaths(int deaths) { this.deaths = deaths; }
    public void setEnemiesKilled(int enemiesKilled) { this.enemiesKilled = enemiesKilled; }
    public void setPlayTime(float playTime) { this.playTime = playTime; }

    public void refillSoul() {
        this.currentSoul = maxSoul;
    }
}

