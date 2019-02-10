package comm;

public interface Handler<I> {
  public void sendMessage(I message);
}
