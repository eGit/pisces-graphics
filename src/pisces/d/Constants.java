/*
 * Pisces Graphics
 * Copyright (C) 2010 John Pritchard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * CLASSPATH Exception
 * 
 * Linking this library statically or dynamically with other modules
 * is making a combined work based on this library. Thus, the terms
 * and conditions of the GNU General Public License cover the whole
 * combination.
 * 
 * As a special exception, the copyright holders of this library give
 * you permission to link this library with independent modules to
 * produce an executable, regardless of the license terms of these
 * independent modules, and to copy and distribute the resulting
 * executable under terms of your choice, provided that you also meet,
 * for each linked independent module, the terms and conditions of the
 * license of that module. An independent module is a module which is
 * not derived from or based on this library. If you modify this
 * library, you may extend this exception to your version of the
 * library, but you are not obligated to do so. If you do not wish to
 * do so, delete this exception statement from your version.
 * 
 * The CLASSPATH Exception is discussed briefly at
 * <http://www.gnu.org/software/classpath/license.html>.
 */
/*
 * Copyright 2007 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */
package pisces.d;

/**
 * 
 */
public interface Constants {

    /**
     * Path commands
     */
    public static final byte COMMAND_MOVE_TO  = 0;
    public static final byte COMMAND_LINE_TO  = 1;
    public static final byte COMMAND_QUAD_TO  = 2;
    public static final byte COMMAND_CUBIC_TO = 3;
    public static final byte COMMAND_CLOSE    = 4;

    /**
     * Arc types
     */
    public static final int ARC_OPEN = 0;
    public static final int ARC_CHORD = 1;
    public static final int ARC_PIE = 2;

    /**
     * Winding rules
     */
    public static final int WIND_EVEN_ODD = 0;
    public static final int WIND_NON_ZERO = 1;

    /**
     * Stroke join style
     */
    public static final int JOIN_MITER = 0;
    public static final int JOIN_ROUND = 1;
    public static final int JOIN_BEVEL = 2;

    /**
     * Stroke end cap style
     */
    public static final int CAP_BUTT = 0;
    public static final int CAP_ROUND = 1;
    public static final int CAP_SQUARE = 2;

    /**
     * Common Math constants, epsilon for values 0.0 to 10.0.
     */
    public final static double EPS = (1e-8);

    public final static double EPS_M2 = (EPS*2.0);
    public final static double EPS_D2 = (EPS/2.0);
    public final static double EPS_1D2 = (1.0 - EPS_D2);

    public final static double Zero = (0.0+EPS);

    public final static double PI = Math.PI;
    public final static double PI_D2 = (PI / 2.0);
    public final static double PI_M2 = (PI * 2.0);
    public final static double PI_D3 = (PI / 3.0);
    /**
     * Multiply Degrees for Radians
     */
    public final static double Radians = (PI / 180.0);
    /**
     * 1.414
     */
    public static final double SQRT_TWO = Math.sqrt(2.0);
}
