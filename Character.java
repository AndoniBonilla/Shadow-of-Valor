package FightingGame;

import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class Character {
    private String name;
    private int health;
    private int attackPower;
    private int defensePower;
    private String specialAbilityName;
    private Rectangle characterSprite;

    public Character(String name, int health, int attackPower, int defensePower, String specialAbilityName, Color color) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
        this.defensePower = defensePower;
        this.specialAbilityName = specialAbilityName;
        this.characterSprite = new Rectangle(30, 30, color);
    }

    public Rectangle getCharacterSprite() {
        return characterSprite;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getAttackPower() {
        return attackPower;
    }

    public int getDefensePower() {
        return defensePower;
    }

    public void move(double dx, double dy) {
        characterSprite.setX(characterSprite.getX() + dx);
        characterSprite.setY(characterSprite.getY() + dy);
    }

    public void attack(Character opponent) {
        System.out.println(name + " attacks " + opponent.getName() + "!");
        opponent.takeDamage(attackPower);
    }

    public void useSpecialAbility(Character opponent) {
        System.out.println(name + " uses " + specialAbilityName + "!");
    }

    public void takeDamage(int amount) {
        health -= Math.max(0, amount - defensePower);
        System.out.println(name + " takes " + (amount - defensePower) + " damage. Health now: " + health);
    }
}
