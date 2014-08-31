package chookin.utils;

public class Pair<K, V> {
	private K key;
	private V value;

	public K getKey() {
		return this.key;
	}

	public Pair() {
	}

	public Pair(K key, V value) {
		this.key = key;
		this.value = value;
	}

	public void setKey(K key) {
		this.key = key;
	}

	public V getValue() {
		return this.value;
	}

	public void setValue(V value) {
		this.value = value;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object other) {
		return other instanceof Pair && equals(key, ((Pair<K, V>) other).key)
				&& equals(value, ((Pair<K, V>) other).value);
	}

	@Override
	public int hashCode() {
		if (key == null)
			return (value == null) ? 0 : value.hashCode() + 1;
		else if (value == null)
			return key.hashCode() + 2;
		else
			return key.hashCode() * 17 + value.hashCode();
	}

	@Override
	public String toString() {
		return "{" + key + "," + value + "}";
	}

	private static boolean equals(Object x, Object y) {
		return (x == null && y == null) || (x != null && x.equals(y));
	}
}
