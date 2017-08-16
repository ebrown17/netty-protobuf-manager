import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
	final static Logger logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String... args){
	
	
		logger.info("Entering application.");
		Foo foo = new Foo();
		foo.doIt();
		logger.info("Exiting application.");
	}

	

}
 class Foo {
	  static final Logger logger = LoggerFactory.getLogger(Foo.class);
	  
	  public void doIt() {
	    logger.debug("Did it again!");
	  }
}
