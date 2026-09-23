package dev.ftb.mods.ftbevolutioncompanion.pyramid;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.ArrayList;
import java.util.List;

public final class PyramidLayout {
    public static final int RADIUS = 2;
    public static final int HEIGHT = 6;
    private static final int WIDTH = RADIUS * 2 + 1;
    private static final int CELL_COUNT = WIDTH * WIDTH * HEIGHT;

    public enum Bay {
        NONE, ITEM, FLUID, ENERGY
    }

    private static final List<Vec3i> CELLS = new ArrayList<>();
    private static final VoxelShape[][] SHAPES = new VoxelShape[4][CELL_COUNT];

    static {
        for (int y = 0; y < HEIGHT; y++) {
            for (int z = -RADIUS; z <= RADIUS; z++) {
                for (int x = -RADIUS; x <= RADIUS; x++) {
                    CELLS.add(new Vec3i(x, y, z));
                }
            }
        }
        List<int[]>[] boxes = parseBoxes();
        for (Direction facing : Direction.Plane.HORIZONTAL) {
            VoxelShape[] shapes = SHAPES[facing.get2DDataValue()];
            for (int i = 0; i < CELL_COUNT; i++) {
                VoxelShape shape = Shapes.empty();
                for (int[] box : boxes[i]) {
                    shape = Shapes.or(shape, rotatedBox(box, facing));
                }
                shapes[i] = shape.optimize();
            }
        }
    }

    private PyramidLayout() {
    }

    public static List<Vec3i> cells() {
        return CELLS;
    }

    public static List<BlockPos> positions(BlockPos core, Direction facing) {
        List<BlockPos> positions = new ArrayList<>(CELL_COUNT);
        for (Vec3i cell : CELLS) {
            positions.add(core.offset(toWorld(cell, facing)));
        }
        return positions;
    }

    public static Vec3i toWorld(Vec3i model, Direction facing) {
        return switch (facing) {
            case SOUTH -> new Vec3i(-model.getX(), model.getY(), -model.getZ());
            case EAST -> new Vec3i(-model.getZ(), model.getY(), model.getX());
            case WEST -> new Vec3i(model.getZ(), model.getY(), -model.getX());
            default -> model;
        };
    }

    public static Vec3i toModel(Vec3i world, Direction facing) {
        return switch (facing) {
            case SOUTH -> new Vec3i(-world.getX(), world.getY(), -world.getZ());
            case EAST -> new Vec3i(world.getZ(), world.getY(), -world.getX());
            case WEST -> new Vec3i(-world.getZ(), world.getY(), world.getX());
            default -> world;
        };
    }

    public static boolean contains(Vec3i model) {
        return Math.abs(model.getX()) <= RADIUS && Math.abs(model.getZ()) <= RADIUS && model.getY() >= 0 && model.getY() < HEIGHT;
    }

    public static Bay bay(Vec3i model) {
        if (model.getY() != 0) return Bay.NONE;
        int x = model.getX();
        int z = model.getZ();
        if (Math.abs(z) == RADIUS && Math.abs(x) == 1) return Bay.ITEM;
        if (x == -RADIUS && Math.abs(z) == 1) return Bay.FLUID;
        if (x == RADIUS && Math.abs(z) == 1) return Bay.ENERGY;
        return Bay.NONE;
    }

    public static VoxelShape shape(Vec3i model, Direction facing) {
        if (!contains(model) || facing.getAxis() == Direction.Axis.Y) return Shapes.block();
        return SHAPES[facing.get2DDataValue()][index(model)];
    }

    private static int index(Vec3i model) {
        return (model.getX() + RADIUS) + WIDTH * ((model.getZ() + RADIUS) + WIDTH * model.getY());
    }

    private static VoxelShape rotatedBox(int[] box, Direction facing) {
        int x0 = box[0];
        int z0 = box[2];
        int x1 = box[3];
        int z1 = box[5];
        return switch (facing) {
            case SOUTH -> Block.box(16 - x1, box[1], 16 - z1, 16 - x0, box[4], 16 - z0);
            case EAST -> Block.box(16 - z1, box[1], x0, 16 - z0, box[4], x1);
            case WEST -> Block.box(z0, box[1], 16 - x1, z1, box[4], 16 - x0);
            default -> Block.box(x0, box[1], z0, x1, box[4], z1);
        };
    }

    @SuppressWarnings("unchecked")
    private static List<int[]>[] parseBoxes() {
        List<int[]>[] boxes = new List[CELL_COUNT];
        for (int i = 0; i < CELL_COUNT; i++) {
            boxes[i] = new ArrayList<>();
        }
        for (String entry : PyramidShapeData.CELLS.split(";")) {
            if (entry.isBlank()) continue;
            String[] parts = entry.split("=");
            String[] key = parts[0].trim().split(" ");
            Vec3i cell = new Vec3i(Integer.parseInt(key[0]), Integer.parseInt(key[1]), Integer.parseInt(key[2]));
            for (String box : parts[1].split(",")) {
                String[] values = box.trim().split(" ");
                int[] bounds = new int[6];
                for (int i = 0; i < 6; i++) {
                    bounds[i] = Integer.parseInt(values[i]);
                }
                boxes[index(cell)].add(bounds);
            }
        }
        return boxes;
    }
}
