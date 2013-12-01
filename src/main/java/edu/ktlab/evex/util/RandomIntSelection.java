package edu.ktlab.evex.util;

public class RandomIntSelection {
	private int min;
	private int max;
	private boolean[] selected;

	public RandomIntSelection(int min, int max) {
		this.min = min;
		this.max = max;
		selected = new boolean[max - min + 1];
	}

	public int nextInt() {
		int next = (int) (Math.random() * (max + 1));
		if (next < min || selected[next - min])
			return nextInt();
		selected[next - min] = true;

		return next;
	}

	public int[] nextInt(int n) {
		if (n > selected.length)
			n = selected.length;
		int[] res = new int[n];
		for (int i = 0; i < n; i++)
			res[i] = nextInt();
		return res;
	}

	public static void main(String[] args) {
		RandomIntSelection random = new RandomIntSelection(1, 20);

		for (int i : random.nextInt(10))
			System.out.println(i);
	}
}
