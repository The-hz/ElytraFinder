/*
 * Decompiled with CFR 0.152.
 */
package com.door.hunt.utils;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.IntSupplier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Matrix3d;
import org.joml.Vector3d;

public class MathUtils {
    public static double s2(double x) {
        return x * x;
    }

    public static int s2(int x) {
        return x * x;
    }

    public static double squareSum(double ... x) {
        double sum = 0.0;
        for (double y : x) {
            sum += y * y;
        }
        return sum;
    }

    public static double squaredMagnitude(Box thi, Box other) {
        double d = Math.max(Math.max(thi.minX - other.maxX, other.minX - thi.maxX), 0.0);
        double e = Math.max(Math.max(thi.minY - other.maxY, other.minY - thi.maxY), 0.0);
        double f = Math.max(Math.max(thi.minZ - other.maxZ, other.minZ - thi.maxZ), 0.0);
        return d * d + e * e + f * f;
    }

    public static int sgn(int t) {
        return Integer.compare(t, 0);
    }

    public static double sgn(double t) {
        return Double.compare(t, 0.0);
    }

    public static double sgn(double t, double threshold) {
        return Math.abs(t) > threshold ? MathUtils.sgn(t) : 0.0;
    }

    public static boolean isInBox(Vec3d a2, Vec3d b, double range) {
        return MathUtils.isInBox(a2.subtract(b), range);
    }

    public static boolean isInBox(Vec3d a2, double range) {
        return Math.abs(a2.x) < range && Math.abs(a2.y) < range && Math.abs(a2.z) < range;
    }

    public static boolean isInXZRange(Vec3d a2, Vec3d b, double range) {
        return MathUtils.isInBox(a2.subtract(b), range);
    }

    public static boolean isInXZRange(Vec3d a2, double range) {
        return Math.abs(a2.x) < range && Math.abs(a2.z) < range;
    }

    public static Box getBlockBox(BlockPos pos) {
        return new Box(pos);
    }

    public static Box createBox(Vec3d vec3d, double ra) {
        return new Box(vec3d.subtract(ra, ra, ra), vec3d.add(ra, ra, ra));
    }

    public static String getDirectionName(int xSgn, int zSgn) {
        if (xSgn == 0 && zSgn == 0) {
            return "Center";
        }
        if (xSgn == 0 && zSgn == 1) {
            return "South";
        }
        if (xSgn == 0 && zSgn == -1) {
            return "North";
        }
        if (xSgn == 1 && zSgn == 0) {
            return "East";
        }
        if (xSgn == -1 && zSgn == 0) {
            return "West";
        }
        if (xSgn == 1 && zSgn == 1) {
            return "Southeast";
        }
        if (xSgn == 1 && zSgn == -1) {
            return "Northeast";
        }
        if (xSgn == -1 && zSgn == 1) {
            return "Southwest";
        }
        if (xSgn == -1 && zSgn == -1) {
            return "Northwest";
        }
        return "Unknown";
    }

    public static List<BlockPos> getOccupiedBlockPositions(Box box) {
        int minX = (int)Math.floor(box.minX);
        int maxX = (int)Math.ceil(box.maxX) - 1;
        int minY = (int)Math.floor(box.minY);
        int maxY = (int)Math.ceil(box.maxY) - 1;
        int minZ = (int)Math.floor(box.minZ);
        int maxZ = (int)Math.ceil(box.maxZ) - 1;
        ArrayList<BlockPos> positions = new ArrayList<BlockPos>((maxX - minX + 1) * (maxY - minY + 1) * (maxZ - minZ + 1));
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
                for (int z = minZ; z <= maxZ; ++z) {
                    positions.add(new BlockPos(x, y, z));
                }
            }
        }
        return positions;
    }

    public static List<BlockPos> getOccupiedBlockPositions(Vec3d min, Vec3d max) {
        return MathUtils.getOccupiedBlockPositions(new Box(min, max));
    }

    public static Vec3d getVerticalWithSameXZ(Vec3d vec3d) {
        Vec3d direction = vec3d.normalize();
        return (direction.y != 0.0 ? new Vec3d(direction.x, -(MathUtils.s2(direction.x) + MathUtils.s2(direction.z)) / direction.y, direction.z) : new Vec3d(0.0, 1.0, 0.0)).normalize();
    }

    public static Vec3d getVerticalWithSameY(Vec3d vec3d) {
        Vec3d dir = vec3d.normalize();
        double dx = dir.x;
        double dz = dir.z;
        if (Math.abs(dx) < 1.0E-8 && Math.abs(dz) < 1.0E-8) {
            return new Vec3d(1.0, 0.0, 0.0);
        }
        return new Vec3d(dz, 0.0, -dx).normalize();
    }

    public static Pair<Vec3d, Vec3d> getTangentWithSameXZ(Vec3d center, double range, Vec3d point) {
        return MathUtils.getTangentWithSameXZ(range, point.subtract(center));
    }

    public static Pair<Vec3d, Vec3d> getTangentWithSameXZ(double range, Vec3d point) {
        double r2 = MathUtils.s2(range);
        double len = point.length();
        if (MathUtils.s2(len) <= r2) {
            Vec3d vec3d = MathUtils.getVerticalWithSameXZ(point);
            return Pair.of(vec3d, vec3d.negate());
        }
        double cutLine = r2 / len;
        double cutLen = Math.sqrt(r2 - MathUtils.s2(cutLine));
        Vec3d verticals = MathUtils.getVerticalWithSameXZ(point);
        Vec3d cutPoint = point.normalize().multiply(cutLine);
        return Pair.of(cutPoint.add(verticals.multiply(cutLen)).subtract(point), cutPoint.subtract(verticals.multiply(cutLen)).subtract(point));
    }

    public static Pair<Vec3d, Vec3d> getTangentWithSamePlate(Vec3d center, double range, Vec3d point) {
        return MathUtils.getTangentWithSamePlate(range, point.subtract(center));
    }

    public static Pair<Vec3d, Vec3d> getTangentWithSamePlate(double range, Vec3d point) {
        double r2 = MathUtils.s2(range);
        double len = point.length();
        if (MathUtils.s2(len) <= r2) {
            Vec3d vec3d = MathUtils.getVerticalWithSameY(point);
            return Pair.of(vec3d, vec3d.negate());
        }
        double cutLine = r2 / len;
        double cutLen = Math.sqrt(r2 - MathUtils.s2(cutLine));
        Vec3d verticals = MathUtils.getVerticalWithSameY(point);
        Vec3d cutPoint = point.normalize().multiply(cutLine);
        return Pair.of(cutPoint.add(verticals.multiply(cutLen)).subtract(point), cutPoint.subtract(verticals.multiply(cutLen)).subtract(point));
    }

    public static List<Vec3i> create2DPointListInRange(double i, int x) {
        ArrayList<Vec3i> list = new ArrayList<Vec3i>();
        int range = (int)i;
        for (int y = -range; y <= range; ++y) {
            for (int z = -range; z <= range; ++z) {
                list.add(new Vec3i(y, x, z));
            }
        }
        list.sort(Comparator.comparingDouble(v -> v.getX() * v.getX() + v.getZ() * v.getZ()));
        return list;
    }

    public static List<Vec3i> create3DPointListInRange(double i) {
        ArrayList<Vec3i> list = new ArrayList<Vec3i>();
        int range = (int)i;
        for (int x = -range; x <= range; ++x) {
            for (int y = -range; y <= range; ++y) {
                for (int z = -range; z <= range; ++z) {
                    list.add(new Vec3i(x, y, z));
                }
            }
        }
        list.sort(Comparator.comparingDouble(s -> s.getX() * s.getX() + s.getZ() * s.getZ() + s.getY() * s.getY()));
        return list;
    }

    public static Vec3d linearInterpolation(Vec3d[] vec3ds, int ticksLater) {
        if (ticksLater <= 0 || vec3ds.length < 3) {
            return vec3ds[vec3ds.length - 1];
        }
        if (vec3ds[0] == null || vec3ds[1] == null || vec3ds[2] == null) {
            return vec3ds[2];
        }
        Vec3d velocity1 = vec3ds[2].subtract(vec3ds[1]);
        Vec3d velocity2 = vec3ds[1].subtract(vec3ds[0]);
        Vec3d acceleration = velocity1.subtract(velocity2);
        double t = ticksLater;
        return vec3ds[2].add(velocity1.multiply(t)).add(acceleration.multiply(0.5 * t * t));
    }

    public static Vec3d quadraticPolynomialFit(Vec3d[] positions, int ticksLater) {
        if (ticksLater <= 0 || positions.length < 3) {
            return positions[positions.length - 1];
        }
        if (positions[0] == null || positions[1] == null || positions[2] == null) {
            return positions[2];
        }
        double[] times = new double[]{-2.0, -1.0, 0.0};
        double[] xVals = new double[3];
        double[] yVals = new double[3];
        double[] zVals = new double[3];
        for (int i = 0; i < 3; ++i) {
            xVals[i] = positions[i].x;
            yVals[i] = positions[i].y;
            zVals[i] = positions[i].z;
        }
        double[] xCoeffs = MathUtils.solveQuadratic(times, xVals);
        double[] yCoeffs = MathUtils.solveQuadratic(times, yVals);
        double[] zCoeffs = MathUtils.solveQuadratic(times, zVals);
        double t = ticksLater;
        double t2 = t * t;
        return new Vec3d(xCoeffs[0] * t2 + xCoeffs[1] * t + xCoeffs[2], yCoeffs[0] * t2 + yCoeffs[1] * t + yCoeffs[2], zCoeffs[0] * t2 + zCoeffs[1] * t + zCoeffs[2]);
    }

    private static double[] solveQuadratic(double[] t, double[] p) {
        double t0 = t[0];
        double t1 = t[1];
        double t2 = t[2];
        double p0 = p[0];
        double p1 = p[1];
        double p2 = p[2];
        double det = t0 * t0 * (t1 - t2) + t0 * (t2 * t2 - t1 * t1) + (t1 * t1 * t2 - t1 * t2 * t2);
        double detA = p0 * (t1 - t2) + t0 * (p2 - p1) + (p1 * t2 - p2 * t1);
        double detB = t0 * t0 * (p1 - p2) + p0 * (t2 * t2 - t1 * t1) + (t1 * t1 * p2 - t2 * t2 * p1);
        double detC = t0 * t0 * (t1 * p2 - t2 * p1) + t0 * (t2 * t2 * p1 - t1 * t1 * p2) + p0 * (t1 * t1 * t2 - t1 * t2 * t2);
        double a2 = detA / det;
        double b = detB / det;
        double c = detC / det;
        return new double[]{a2, b, c};
    }

    public static Vec3d linearPrediction(Vec3d[] vec3ds, int ticksLater) {
        if (ticksLater <= 0) {
            return vec3ds[vec3ds.length - 1];
        }
        int datapoints = 0;
        for (int i = vec3ds.length - 1; i >= 0 && vec3ds[i] != null; --i) {
            ++datapoints;
        }
        if (datapoints < 2) {
            return vec3ds[vec3ds.length - 1];
        }
        Vec3d[] vec3ds1 = new Vec3d[datapoints];
        System.arraycopy(vec3ds, vec3ds.length - datapoints, vec3ds1, 0, datapoints);
        vec3ds = vec3ds1;
        double[] x = new double[vec3ds.length];
        double[] y = new double[vec3ds.length];
        double[] z = new double[vec3ds.length];
        double[] arg = new double[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            x[i] = vec3ds[i].x;
            y[i] = vec3ds[i].y;
            z[i] = vec3ds[i].z;
            arg[i] = -vec3ds.length + 1 + i;
        }
        Linear xl = MathUtils.linearRegression(arg, x);
        Linear yl = MathUtils.linearRegression(arg, y);
        Linear zl = MathUtils.linearRegression(arg, z);
        return new Vec3d(xl.f(ticksLater), yl.f(ticksLater), zl.f(ticksLater));
    }

    public static Vec3d quadraticPrediction(Vec3d[] vec3ds, int ticksLater) {
        if (ticksLater <= 0) {
            return vec3ds[vec3ds.length - 1];
        }
        int datapoints = 0;
        for (int i = vec3ds.length - 1; i >= 0 && vec3ds[i] != null; --i) {
            ++datapoints;
        }
        if (datapoints < 4) {
            return vec3ds[vec3ds.length - 1];
        }
        Vec3d[] vec3ds1 = new Vec3d[datapoints];
        System.arraycopy(vec3ds, vec3ds.length - datapoints, vec3ds1, 0, datapoints);
        vec3ds = vec3ds1;
        double[] x = new double[vec3ds.length];
        double[] y = new double[vec3ds.length];
        double[] z = new double[vec3ds.length];
        double[] arg = new double[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            x[i] = vec3ds[i].x;
            y[i] = vec3ds[i].y;
            z[i] = vec3ds[i].z;
            arg[i] = -vec3ds.length + 1 + i;
        }
        MathFunction xl = MathUtils.quadraticRegression(arg, x);
        MathFunction yl = MathUtils.quadraticRegression(arg, y);
        MathFunction zl = MathUtils.quadraticRegression(arg, z);
        return new Vec3d(xl.f(ticksLater), yl.f(ticksLater), zl.f(ticksLater));
    }

    public static Linear linearRegression(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumX2 = 0.0;
        for (int i = 0; i < n; ++i) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }
        double denominator = (double)n * sumX2 - sumX * sumX;
        if (Math.abs(denominator) < 1.0E-10) {
            return new Linear(0.0, sumY / (double)n);
        }
        double slope = ((double)n * sumXY - sumX * sumY) / denominator;
        double intercept = (sumY - slope * sumX) / (double)n;
        return new Linear(slope, intercept);
    }

    public static MathFunction quadraticRegression(double[] x, double[] y) {
        int n = x.length;
        double sumX = 0.0;
        double sumX2 = 0.0;
        double sumX3 = 0.0;
        double sumX4 = 0.0;
        double sumY = 0.0;
        double sumXY = 0.0;
        double sumX2Y = 0.0;
        for (int i = 0; i < n; ++i) {
            double xi = x[i];
            double xi2 = xi * xi;
            double xi3 = xi2 * xi;
            double xi4 = xi3 * xi;
            sumX += xi;
            sumX2 += xi2;
            sumX3 += xi3;
            sumX4 += xi4;
            sumY += y[i];
            sumXY += xi * y[i];
            sumX2Y += xi2 * y[i];
        }
        Matrix3d m = new Matrix3d(sumX4, sumX3, sumX2, sumX3, sumX2, sumX, sumX2, sumX, (double)n);
        Vector3d v = new Vector3d(sumX2Y, sumXY, sumY);
        if (Math.abs(m.determinant()) > 1.0E-6) {
            Matrix3d minv = m.invert();
            Vector3d result = minv.transform(v);
            return new Quadratic(result.x, result.y, result.z);
        }
        return MathUtils.linearRegression(x, y);
    }

    public static class Linear
    implements MathFunction {
        final double a;
        final double b;

        public Linear(double a2, double b) {
            this.a = a2;
            this.b = b;
        }

        @Override
        public double f(double x) {
            return this.a * x + this.b;
        }
    }

    public static interface MathFunction {
        public double f(double var1);
    }

    public static class Quadratic
    implements MathFunction {
        final double a;
        final double b;
        final double c;

        public Quadratic(double a2, double b, double c) {
            this.a = a2;
            this.b = b;
            this.c = c;
        }

        @Override
        public double f(double x) {
            return this.a * x * x + this.b * x + this.c;
        }
    }

    public static class NVPredictor {
        private final Vec3d[] pointList;
        private final IntSupplier supplier;

        public NVPredictor(Vec3d[] historyStack, IntSupplier currentIndex) {
            this.pointList = historyStack;
            this.supplier = currentIndex;
        }

        public Vec3d compute(int ticksLater) {
            Vec3d v3d;
            int idx = this.supplier.getAsInt();
            Vec3d currentPos = this.pointList[idx];
            if (currentPos == null) {
                return null;
            }
            int len = this.pointList.length;
            ArrayList<Vec3d> points = new ArrayList<Vec3d>();
            points.add(currentPos);
            for (int i = 1; i < len && (v3d = this.pointList[(idx - i + len) % len]) != null; ++i) {
                points.add(0, v3d);
            }
            Vec3d result = null;
            if (!points.isEmpty()) {
                result = currentPos;
            }
            if (points.size() < 2) {
                return result;
            }
            ArrayList<Vec3d> diff = new ArrayList<Vec3d>();
            Vec3d oldV = null;
            for (Vec3d v : points) {
                if (oldV == null) {
                    oldV = v;
                    continue;
                }
                diff.add(v.subtract(oldV));
                oldV = v;
            }
            if (diff.size() >= 2) {
                Vec3d d = new Vec3d(0.0, 0.0, 0.0);
                for (Vec3d v : diff) {
                    d = d.add(v).multiply(0.5);
                }
                return result.add(d.multiply((double)ticksLater));
            }
            if (diff.size() == 1) {
                return currentPos.add(((Vec3d)diff.get(0)).multiply((double)ticksLater));
            }
            return result;
        }
    }
}

