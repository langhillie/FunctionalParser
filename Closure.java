import java.util.ArrayList;
import java.util.HashMap;


// Holds pointer to start and end of function, and the reference frame used for the function.
public class Closure {
	
	// Reference to subroutine
	public int start;
	
	// Params
	public ArrayList<Object> params = new ArrayList<Object>();
	
	// Referencing environment (maybe not necessary here)
	public HashMap<String, Object> frame = new HashMap<>();
	

    public String toString() {
    	return "<lambda>";
    }
	
	
}
