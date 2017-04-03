package learner;

import java.util.ArrayList;
import java.util.TreeSet;

public class Attribute {
    private String _name;

    private boolean _isDiscrete;

    private Integer _columnNumber;

    private TreeSet<String> _values;

    public double maxValue = Double.MIN_VALUE;

    public double minValue = Double.MAX_VALUE;

    public double k = 0.0;

    public Attribute(Integer columnNumber, String name, boolean isDiscrete){
        _columnNumber = columnNumber;
        _name = name;
        _isDiscrete = isDiscrete;
        _values = new TreeSet<String>();
    }

    public Integer getColumn(){
        return _columnNumber;
    }

    public String getName() {
        return _name;
    }

    public boolean isDiscrete() {
        return _isDiscrete;
    }

    public ArrayList<String> getValues() {
        return new ArrayList<String>(_values);
    }

    public void addValue(String val) {
        _values.add(val);
    }

    public void clearValues() {
        _values.clear();
    }
}

