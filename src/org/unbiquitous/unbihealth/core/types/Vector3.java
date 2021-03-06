package org.unbiquitous.unbihealth.core.types;

/**
 * Holds 3 dimensional vectorial data.
 * 
 * @author Luciano Santos
 */
public class Vector3 {
	public double x = 0;
	public double y = 0;
	public double z = 0;

	public Vector3() {
	}

	public Vector3(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double sqrMagnitude() {
		return x * x + y * y + z * z;
	}

	public double magnitude() {
		return Math.sqrt(sqrMagnitude());
	}

	public Vector3 multiply(double d) {
		return multiply(this, d);
	}

	public Vector3 add(Vector3 other) {
		return add(this, other);
	}

	public Vector3 subtract(Vector3 other) {
		return subtract(this, other);
	}

	public static Vector3 multiply(final Vector3 v, double d) {
		return new Vector3(v.x * d, v.y * d, v.z * d);
	}

	public static Vector3 add(final Vector3 v1, final Vector3 v2) {
		return new Vector3(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}

	public static Vector3 subtract(final Vector3 v1, final Vector3 v2) {
		return add(v1, multiply(v2, -1));
	}

	public static Vector3 zero() {
		return new Vector3();
	}

	public static Vector3 one() {
		return new Vector3(1, 1, 1);
	}
	
	public static Vector3 right() {
		return new Vector3(1, 0, 0);
	}
	
	public static Vector3 up() {
		return new Vector3(0, 1, 0);
	}
	
	public static Vector3 forward() {
		return new Vector3(0, 0, 1);
	}
	
	public static Vector3 left() {
		return new Vector3(-1, 0, 0);
	}
	
	public static Vector3 down() {
		return new Vector3(0, -1, 0);
	}
	
	public static Vector3 back() {
		return new Vector3(0, 0, -1);
	}
}
