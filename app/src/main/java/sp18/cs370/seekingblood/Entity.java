package sp18.cs370.seekingblood;

class Entity {
    // Since some enemies may fly, certain booleans may not apply to them.
    // This base class only considers grounded entities. Flying enemies should have an isFlying
    // variable in their own class.
    private boolean isFacingLeft;
    private boolean isOnGround;
    private double health;
    private double xVelocity;
    private double yVelocity;

    Entity() {
        health = 100.0;
        xVelocity = 0.0;
        yVelocity = 0.0;
        isOnGround = true;
    }

    public boolean isOnGround() {
        return isOnGround;
    }

    public void setOnGround(boolean onGround) {
        isOnGround = onGround;
    }

    public double getHealth() {
        return health;
    }

    public void setHealth(double health) {
        this.health = health;
    }

    public double getXVelocity() {
        return xVelocity;
    }

    public void setXVelocity(double xVelocity) {
        this.xVelocity = xVelocity;
    }

    public double getYVelocity() {
        return yVelocity;
    }

    public void setYVelocity(double yVelocity) {
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
