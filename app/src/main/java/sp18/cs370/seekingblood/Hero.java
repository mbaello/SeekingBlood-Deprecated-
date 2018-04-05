package sp18.cs370.seekingblood;

import android.graphics.Rect;
import android.widget.ImageView;

class Hero extends Entity {
    private boolean finishedJump;
    private ImageView heroView;
    private float stamina;
    private float reserve;

    Hero() {
        super();
        finishedJump = false;
        setxVelocity(0);
        setyVelocity(Constants.defaultJumpVelocity);
        stamina = 100.0f;
        reserve = 50.0f;
    }

    public boolean isFinishedJump() {
        return finishedJump;
    }

    public void setFinishedJump(boolean finishedJump) {
        this.finishedJump = finishedJump;
    }

    public ImageView getHeroView() {
        return heroView;
    }

    public void setHeroView(ImageView heroView) {
        this.heroView = heroView;
    }

    public float getHeroViewX() {
        return heroView.getX();
    }

    public void setHeroViewX(float x) {
        heroView.setX(x);
    }

    public float getHeroViewY() {
        return heroView.getY();
    }

    public void setHeroViewY(float y) {
        heroView.setY(y);
    }

    public float getStamina() {
        return stamina;
    }

    public void setStamina(float stamina) {
        this.stamina = stamina;
    }

    public float getReserve() {
        return reserve;
    }

    public void setReserve(float reserve) {
        this.reserve = reserve;
    }

    // The following move functions are being placed here temporarily, as they will probably be
    // implemented for the Entity class in the future. Since the character is currently only moving
    // horizontally, there is no need to check for upper bounds.
    protected void SprintRight(Rect movementRegion, int width) {
        int x = (int)this.getHeroViewX() + Constants.heroRunningSpeed;
        if(ValidMove(movementRegion, width, x) && this.isOnGround()) {
            this.setHeroViewX(x);
            this.setFacingLeft(false);
        }
    }
    
    protected void WalkRight(Rect movementRegion, int width) {
        int x = (int)this.getHeroViewX() + Constants.heroWalkingSpeed;
        if(ValidMove(movementRegion, width, x) && this.isOnGround()) {
            this.setHeroViewX(x);
            this.setFacingLeft(false);
        }
    }
    
    protected void WalkLeft(Rect movementRegion, int width) {
        int x = (int)this.getHeroViewX() - Constants.heroWalkingSpeed;
        if(ValidMove(movementRegion, width, x) && this.isOnGround()) {
            this.setHeroViewX(x);
            this.setFacingLeft(true);
        }
    }
    protected void SprintLeft(Rect movementRegion, int width) {
        int x = (int)this.getHeroViewX() - Constants.heroRunningSpeed;
        if(ValidMove(movementRegion, width, x) && this.isOnGround()) {
            this.setHeroViewX(x);
            this.setFacingLeft(true);
        }
    }

    protected boolean ValidMove(Rect movementRegion, int width, int x) {
        return ((!movementRegion.contains(x, 0)) && (x <= width));
    }

    // Handling ReachedGround may require an array of Rect objects that contains all Obstructables.
    // Obstructables will be a class referring to floors and platforms (obstructions).
    protected boolean ReachedGround(Rect floorRegion, int y) {
        return ((floorRegion.contains(0, y)));
    }

    protected void Jump(Rect floorRegion, Rect movementRegion, int width, int height) {
        int x = (int)this.getHeroViewX();
        int y = (int)this.getHeroViewY();
        x += this.getxVelocity();
        y += this.getyVelocity();
        this.setyVelocity(this.getyVelocity() + Constants.gravity);
        if(this.isOnGround()) {
            this.setOnGround(false);
            this.setHeroViewY(y);
            if(ValidMove(movementRegion, width, x))
                this.setHeroViewX(x);
        } else if (!ReachedGround(floorRegion, y)) {
            this.setHeroViewY(y);
            if(ValidMove(movementRegion, width, x))
                this.setHeroViewX(x);
        } else {
            this.setOnGround(true);
            this.setHeroViewY(height / 2);
            this.setyVelocity(Constants.defaultJumpVelocity);
            this.setxVelocity(0);
            finishedJump = true;
        }
    }
}
