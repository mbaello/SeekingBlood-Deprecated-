package sp18.cs370.seekingblood;

class Entity {
    // Since some enemies may fly, certain booleans may not apply to them.
    // This base class only considers grounded entities. Flying enemies should have an isFlying
    // variable in their own class.
    private boolean isFacingLeft;
    private boolean isOnGround;
    private float health;
    private int xVelocity;
    private int yVelocity;

    Entity() {
        health = 100.0f;
        xVelocity = 0;
        yVelocity = 0;
        isOnGround = true;
    }

    public boolean isFacingLeft() {
        return isFacingLeft;
    }

    public void setFacingLeft(boolean facingLeft) {
        isFacingLeft = facingLeft;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public int getxVelocity() {
        return xVelocity;
    }

    public void setxVelocity(int xVelocity) {
        this.xVelocity = xVelocity;
    }

    public int getyVelocity() {
        return yVelocity;
    }

    public void setyVelocity(int yVelocity) {
        this.yVelocity = yVelocity;
    }

    public void MoveSlowLeft() {

    }

    public void MoveSlowRight() {

    }

    public void MoveFastLeft() {

    }

    public void MoveFastRight() {

    }

}
