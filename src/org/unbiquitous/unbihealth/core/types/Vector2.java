package org.unbiquitous.unbihealth.core.types;

/**
 * Holds 3 dimensional vectorial data.
 * 
 * @author Luciano Santos
 */
public class Vector2 {
	public double x = 0;
	public double y = 0;

	public Vector2() {
	}

	public Vector2(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double sqrMagnitude() {
		return x * x + y * y;
	}

	public double magnitude() {
		return Math.sqrt(sqrMagnitude());
	}

	public Vector2 multiply(double d) {
		return multiply(this, d);
	}

	public Vector2 add(Vector2 other) {
		return add(this, other);
	}

	public Vector2 subtract(Vector2 other) {
		return subtract(this, other);
	}

	public Vector3 toVector3() {
		return new Vector3(x, y, 0);
	}

	public Vector3 toVector3(double z) {
		return new Vector3(x, y, z);
	}

	public static Vector2 multiply(final Vector2 v, double d) {
		return new Vector2(v.x * d, v.y * d);
	}

	public static Vector2 add(final Vector2 v1, final Vector2 v2) {
		return new Vector2(v1.x + v2.x, v1.y + v2.y);
	}

	public static Vector2 subtract(final Vector2 v1, final Vector2 v2) {
		return add(v1, multiply(v2, -1));
	}

	public static Vector2 zero() {
		return new Vector2();
	}

	public static Vector2 one() {
		return new Vector2(1, 1);
	}

	public static Vector2 right() {
		return new Vector2(1, 0);
	}

	public static Vector2 up() {
		return new Vector2(0, 1);
	}

	public static Vector2 left() {
		return new Vector2(-1, 0);
	}

	public static Vector2 down() {
		return new Vector2(0, -1);
	}
}
