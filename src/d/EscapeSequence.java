package d;

public interface EscapeSequence
{
	public String encode(String s);

	public String decode(String s, boolean allowerror);
}
