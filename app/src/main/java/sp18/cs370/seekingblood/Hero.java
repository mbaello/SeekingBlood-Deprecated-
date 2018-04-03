package sp18.cs370.seekingblood;

class Hero extends Entity {
    private double stamina;
    private double reserve;

    Hero() {
        super();
        setXVelocity(6);
        setYVelocity(6);
        stamina = 100.0;
        reserve = 50.0;
    }

    public double getStamina() {
        return stamina;
    }

    public void setStamina(double stamina) {
        this.stamina = stamina;
    }

    public double getReserve() {
        return reserve;
    }

    public void setReserve(double reserve) {
        this.reserve = reserve;
    }
}
