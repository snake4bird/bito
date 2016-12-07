package bito.util.pool;

public interface Poolable
{
	void hold();

	void free();

	boolean reusable();

	void destroy();
}
