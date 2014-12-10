package a2;

public final class Error extends Throwable {
	private static final long serialVersionUID = 1L; // sigh
	Error(String msg) {
		super(msg);
	}
}
