package de.fub.agg2graph.structs.frechet;

public class Pair<A, B> {
	public B b;
	public A a;

	public Pair(A a, B b) {
		this.a = a;
		this.b = b;
	}
	
	public A first() { return a; }
	public B second() { return b; }
}