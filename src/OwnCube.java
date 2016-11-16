/*
 * Copyright (c) 2007 Sun Microsystems, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in
 *   the documentation and/or other materials provided with the
 *   distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL
 * NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF
 * USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR
 * ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL,
 * CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND
 * REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF THE USE OF OR
 * INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS BEEN ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 *
 */
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;

/**
 * Simple color-per-vertex cube with a different color for each face
 */
public class OwnCube extends Shape3D {
	private static final float[] verts = {
			// front face
			1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
			// back face
			-1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
			// right face
			1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
			// left face
			-1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
			// top face
			1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
			// bottom face
			-1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, };

	double scale;

	private float[] colors(Color3f color) {
		float r1 = color.x, g1 = color.y, b1 = color.z;
		float r2 = r1 * 0.7f, g2 = g1 * 0.7f, b2 = b1 * 0.7f;
		float r3 = r1 * 0.4f, g3 = g1 * 0.4f, b3 = b1 * 0.4f;
		return new float[]{
				// front face (red)
				r1, g1, b1, r1, g1, b1, r1, g1, b1, r1, g1, b1,
				// back face (green)
				r1, g1, b1, r1, g1, b1, r1, g1, b1, r1, g1, b1,
				// right face (blue)
				r2, g2, b2, r2, g2, b2, r2, g2, b2, r2, g2, b2,
				// left face (yellow)
				r2, g2, b2, r2, g2, b2, r2, g2, b2, r2, g2, b2,
				// top face (magenta)
				r3, g3, b3, r3, g3, b3, r3, g3, b3, r3, g3, b3,
				// bottom face (cyan)
				r3, g3, b3, r3, g3, b3, r3, g3, b3, r3, g3, b3,
		};
	}
	
	/**
	 * Constructs a color cube with the specified scale. The corners of the
	 * color cube are [-scale,-scale,-scale] and [scale,scale,scale].
	 * 
	 * @param scale
	 *            the scale of the cube
	 */
	public OwnCube(double scale, Color3f color) {
		QuadArray cube = new QuadArray(24, QuadArray.COORDINATES | QuadArray.COLOR_3);
		scale /= 2;
		float scaledVerts[] = new float[verts.length];
		for (int i = 0; i < verts.length; i++)
			scaledVerts[i] = verts[i] * (float) scale;

		cube.setCoordinates(0, scaledVerts);
		cube.setColors(0, colors(color));

		this.setGeometry(cube);

		this.scale = scale;
	}

	/**
	 * @deprecated ColorCube now extends shape so it is no longer necessary to
	 *             call this method.
	 */
	public Shape3D getShape() {
		return this;
	}

	/**
	 * Returns the scale of the Cube
	 *
	 * @since Java 3D 1.2.1
	 */
	public double getScale() {
		return scale;
	}
}