package me.dakto101.util;

import javax.annotation.Nullable;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.World;
import org.bukkit.util.Vector;

public class ParticleEffect {

	/**
	 * Create circle effect
	 * @param loc location
	 * @param angle (in degree)
	 * @param radius
	 * @param particle
	 * @param vector
	 */
	public static void createCircleEffect(final Location loc, final int angle, final double radius, 
								final Particle particle, final Vector vector, final DustOptions color) {
		World w = loc.getWorld();
		double xx = vector.getX();
		double yy = vector.getY();
		double zz = vector.getZ();
		
		double rad = 2*Math.PI/(360/angle);
		for (int t = 0; t <= 360/angle; t++) {
			double x = loc.getX() + radius*Math.cos(rad*t);
			double y = loc.getY();
			double z = loc.getZ() + radius*Math.sin(rad*t);
			if (color == null) w.spawnParticle(particle, x, y, z, 0, xx, yy, zz);
			else w.spawnParticle(particle, x, y, z, 0, xx, yy, zz, color);
			
			
		}
	}
	
	
	/**
	 * Create particle with vector(x, y, z). If x or y or z is null then random (-randomRange; randomRange).
	 * @param loc location
	 * @param amount
	 * @param particle
	 * @param x
	 * @param y
	 * @param z
	 * @param randomRange random range of random vector
	 * @param color dust color
	 */
	public static void createVectorParticle(final Location loc, final int amount, 
				final Particle particle,@Nullable final Double x,@Nullable final Double y,@Nullable final Double z, 
				final double randomRange, final DustOptions color) {
		World w = loc.getWorld();
		for (int i = 0; i < amount; i++) {
			w.spawnParticle(particle, loc, 0, x == null ? Math.random() * randomRange - randomRange * 0.5 : x, 
					y == null ? Math.random() * randomRange - randomRange * 0.5 : y, 
					z == null ? Math.random() * randomRange - randomRange * 0.5 : z, color);
		}
	}
	
	/**
	 * Create nearby particle with x y z nearby
	 * @param loc location
	 * @param amount
	 * @param particle
	 * @param x
	 * @param y
	 * @param z
	 * @param v particle vector
	 * @param color dust color
	 */
	public static void createNearbyParticle(final Location loc, final int amount, 
				final Particle particle, final double x, final double y, final double z, final Vector v, final DustOptions color) {
		World w = loc.getWorld();
		for (int i = 0; i < amount; i++) {
			w.spawnParticle(particle, loc.getX() + Math.random() * x * 2 - x, loc.getY() + Math.random() * y * 2 - y, 
					loc.getZ() + Math.random() * z * 2 - z, 0, v.getX(), v.getY(), v.getZ(), color);
		}
	}
	
}
