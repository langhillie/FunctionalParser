import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class Node {
    public ArrayList<Node> children = new ArrayList<Node>();
    public Node parent;

    public ArrayList<Object> list = new ArrayList<Object>();
    public Object val;
    public Object tmp;
    public String op;

    public HashMap<String, Object> frame = new HashMap<>();

    public Node() {

    }
}
