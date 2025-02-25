package in.succinct.beckn;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.util.Comparator;
import java.util.Iterator;

public class BecknObjects<T> extends BecknAware<JSONArray> implements  Iterable<T>{
    Class<T> clazz ;
    public BecknObjects(){
        this(new JSONArray());
    }

    public BecknObjects(JSONArray value) {
        super(value);
        ParameterizedType pt = (ParameterizedType)getClass().getGenericSuperclass();
        this.clazz = (Class<T>) pt.getActualTypeArguments()[0];;
    }

    public BecknObjects(String payload) {
        super(payload);
        ParameterizedType pt = (ParameterizedType)getClass().getGenericSuperclass();
        this.clazz = (Class<T>) pt.getActualTypeArguments()[0];;
    }

    public void add(T t){
        if (t instanceof BecknObject){
            getInner().add(((BecknObject)t).getInner());
        }else {
            getInner().add(t);
        }
    }
    public void remove(T t){
        if (t instanceof BecknObject){
            getInner().remove(((BecknObject)t).getInner());
        }else {
            getInner().remove(t);
        }
    }
    public void clear(){
        getInner().clear();
    }

    public T get(int index){
        Object element = getInner().get(index);
        if (element == null){
            return null;
        }
        try {
            T t ;
            if (BecknObject.class.isAssignableFrom(clazz)){
                t = getObjectCreator().create(clazz);
                ((BecknObject)t).setInner((JSONObject) element);
            }else {
                t = clazz.getConstructor(String.class).newInstance(String.valueOf(element));
            }
            return t;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    public int size(){
        return getInner().size();
    }

    @Override
    public boolean equals(Object o) {
        if ( o != null && getClass() == o.getClass() ){
            BecknObjects other = (BecknObjects)o;
            return (other.clazz == clazz && other.getInner().equals(getInner()));
        }
        return false;
    }


    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            int i = 0;
            @Override
            public boolean hasNext() {
                return i < size();
            }

            @Override
            public T next() {
                return get(i++);
            }
        };
    }

    public T min(Comparator<T> comparator){
        int size = size();
        if (size == 0){
            return null;
        }
        T min = get(0);
        for (int i = 1 ; i < size ; i ++){
            T t = get(i);
            if (comparator.compare(min,t) > 0){
                min = t;
            }
        }
        return min;
    }
}
