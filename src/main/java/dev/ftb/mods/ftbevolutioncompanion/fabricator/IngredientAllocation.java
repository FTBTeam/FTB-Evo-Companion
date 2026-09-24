package dev.ftb.mods.ftbevolutioncompanion.fabricator;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.function.BiPredicate;

/** Integral allocation prevents overlapping item tags from counting the same items twice. */
public final class IngredientAllocation {
    private IngredientAllocation() {}

    public static int[] allocate(int[] available, int[] required, BiPredicate<Integer, Integer> matches) {
        int slots = available.length;
        int requirements = required.length;
        int sink = slots + requirements + 1;
        int[][] capacity = new int[sink + 1][sink + 1];
        int total = 0;
        for (int s = 0; s < slots; s++) capacity[0][1 + s] = available[s];
        for (int r = 0; r < requirements; r++) {
            capacity[1 + slots + r][sink] = required[r];
            total = Math.addExact(total, required[r]);
            for (int s = 0; s < slots; s++) {
                if (matches.test(s, r)) capacity[1 + s][1 + slots + r] = available[s];
            }
        }
        int flow = 0;
        while (flow < total) {
            int[] parent = new int[sink + 1];
            Arrays.fill(parent, -1);
            parent[0] = 0;
            ArrayDeque<Integer> queue = new ArrayDeque<>();
            queue.add(0);
            while (!queue.isEmpty() && parent[sink] == -1) {
                int node = queue.remove();
                for (int next = 1; next <= sink; next++) {
                    if (parent[next] == -1 && capacity[node][next] > 0) {
                        parent[next] = node;
                        queue.add(next);
                    }
                }
            }
            if (parent[sink] == -1) return null;
            int amount = total - flow;
            for (int n = sink; n != 0; n = parent[n]) amount = Math.min(amount, capacity[parent[n]][n]);
            for (int n = sink; n != 0; n = parent[n]) {
                capacity[parent[n]][n] -= amount;
                capacity[n][parent[n]] += amount;
            }
            flow += amount;
        }
        int[] consumed = new int[slots];
        for (int s = 0; s < slots; s++) consumed[s] = available[s] - capacity[0][1 + s];
        return consumed;
    }
}
