package in.succinct.beckn;

import com.venky.core.date.DateUtils;
import com.venky.core.util.MultiException;
import com.venky.core.util.ObjectHolder;
import com.venky.core.util.ObjectUtil;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class BecknObjectBase extends BecknAware<JSONObject> {
    public BecknObjectBase(JSONObject value) {
        super(value);
    }

    public BecknObjectBase(String payload) {
        super(payload);
    }
    public BecknObjectBase(){
        this(new JSONObject());
    }

    private transient Map<String, ObjectHolder<BecknAware>> attributeMap = new HashMap<>();

    public <T extends BecknAware> T get(Class<T> clazz,String name){
        return get(clazz,name,false);
    }
    public <T extends BecknAware> T get(Class<T> clazz,String name,boolean createIfAbsent){
        if (attributeMap().containsKey(name)){
            Object value = attributeMap().get(name).get();
            if (value != null && clazz.isAssignableFrom(value.getClass())){
                return (T)value;
            }
            return null;
        }
        JSONObject inner = getInner();
        JSONAware clazzInner = (JSONAware) inner.get(name);
        try {
            T t = null ;
            if (clazzInner != null || createIfAbsent){
                t = getObjectCreator().create(clazz);
                if (clazzInner != null) {
                    t.setInner(clazzInner);
                }else {
                    clazzInner = t.getInner();
                    inner.put(name,clazzInner);
                }
            }
            attributeMap().put(name,new ObjectHolder<>(t));
            return t;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public <T> T get(String key){
        return (T)getInner().get(key) ;
    }

    public <T> T get(String key, T returnIfNull){
        T ret = get(key);
        return ret != null ? ret : returnIfNull;
    }

    public <T extends BecknAware> void set(String key, T value){
        if (value == null){
            rm(key);
            return;
        }

        set(key,value.getInner());
        attributeMap().put(key,new ObjectHolder<>(value));
    }

    public <T extends Enum<T>> void setEnum(String key, T value) {
        setEnum(key,value,null);
    }
    public <T extends Enum<T>> void setEnum(String key, T value , EnumConvertor<T> convertor){
        set(key,convertor == null ? ( value == null ? null : value.name() ) : convertor.toString(value));
    }

    public <T extends Enum<T>> T getEnum(Class<T> enumClass, String key){
        return getEnum(enumClass, key, null);
    }
    public <T extends Enum<T>> T getEnum(Class<T> enumClass, String key, EnumConvertor<T> convertor){
        String value = get(key);
        return convertor == null ? (value == null ? null :  Enum.valueOf(enumClass,value)) : convertor.valueOf(value);
    }
    public static class EnumConvertor<T extends Enum<T>> {
        Class<T> enumClass;
        public EnumConvertor(Class<T> clazz){
            this.enumClass = clazz;
        }
        protected EnumConvertor(){
            ParameterizedType pt = (ParameterizedType)this.getClass().getGenericSuperclass();
            this.enumClass = (Class<T>) pt.getActualTypeArguments()[0];
        }
        @SuppressWarnings("unchecked")
        public Class<T> getEnumClass(){
            return enumClass;
        }

        public T valueOf(String enumRepresentation) {
            return enumRepresentation == null ? null : Enum.valueOf(getEnumClass(),enumRepresentation.replace('-','_'));
        }
        public String toString(T value){
            return value == null ? null : value.name().replace('_','-');
        }
    }
    public static class OrdinalBasedEnumConvertor<T extends Enum<T>> extends EnumConvertor<T> {
        public OrdinalBasedEnumConvertor(Class<T> clazz) {
            super(clazz);
        }

        protected OrdinalBasedEnumConvertor() {
            super();
        }

        public T valueOf(String enumRepresentation) {
            return getEnumClass().getEnumConstants()[Integer.parseInt(enumRepresentation) - 1];
        }
        public String toString(T value){
            String fmt = String.format("%%0%dd",getEnumClass().getEnumConstants().length + 1);
            return value == null ? null : String.format(fmt,value.ordinal() + 1);
        }
    }
    public void set(String key, JSONAware value){
        if (value == null) {
            rm(key);
            return;
        }

        getInner().put(key,value);
        attributeMap().remove(key);
    }
    public void set(String key, String value){
        if (value == null) {
            rm(key);
            return;
        }

        getInner().put(key,value);
    }
    public void set(String key, Boolean value){
        if (value == null ){
            rm(key);
            return;
        }
        getInner().put(key,value);
    }

    public void rm(String  key){
        getInner().remove(key);
        attributeMap().remove(key);
    }

    public static DateFormat TIMESTAMP_FORMAT_WITH_MILLS =  new SimpleDateFormat(DateUtils.ISO_8601_24H_FULL_FORMAT);
    public static DateFormat TIMESTAMP_FORMAT_WO_MILLIS = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static DateFormat TIMESTAMP_FORMAT =  TIMESTAMP_FORMAT_WITH_MILLS;

    public static final DateFormat[] TIMESTAMP_FORMATS = new DateFormat[] {TIMESTAMP_FORMAT_WITH_MILLS, TIMESTAMP_FORMAT_WO_MILLIS};

    public static final DateFormat DATE_FORMAT = new SimpleDateFormat(DateUtils.ISO_DATE_FORMAT_STR);
    public static final DateFormat APP_DATE_FORMAT = new SimpleDateFormat(DateUtils.APP_DATE_FORMAT_STR);

    public static final DateFormat TIME_FORMAT = new SimpleDateFormat(DateUtils.APP_TIME_FORMAT_STR);

    static {
        for (DateFormat t : TIMESTAMP_FORMATS ){
            t.setTimeZone(TimeZone.getTimeZone("UTC"));
        }
        TIME_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }


    public void set(String key, Date date, DateFormat format){
        if (date == null) {
            rm(key);
            return;
        }
        set(key,format.format(date));
    }

    public void set(String key, Number value){
        if (value == null){
            rm(key);
            return;
        }
        getInner().put(key,value);
    }
    public Date getTimestamp(String key){
        MultiException multiException = new MultiException();
        for (DateFormat f : TIMESTAMP_FORMATS){
            try {
                return getDate(key,f);
            }catch (Exception ex){
                multiException.add(ex);
            }
        }
        throw multiException;
    }
    public Date getDate(String key){
        return getDate(key,DATE_FORMAT);
    }
    public Date getTime(String key){
        return getDate(key,TIME_FORMAT);
    }
    public Date getDate(String key, DateFormat format){
        String value = get(key);
        if (value == null){
            return null;
        }

        try {
            return format.parse(value);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
    public Double getDouble(String key){
        return getDouble(key,0.0D);
    }
    public Double getDouble(String key,Double ifNull){
        Object o = get(key);
        return o == null ? ifNull : Double.valueOf(String.valueOf(o));
    }
    public Integer getInteger(String key) {
        return getInteger(key,0);
    }

    public Integer getInteger(String key,Integer ifNull){
        Object o = get(key);
        return o == null ? ifNull : Integer.valueOf(String.valueOf(o));
    }
    public Long getLong(String key) {
        return getLong(key,0L);
    }
    public Long getLong(String key, Long ifNull){
        Object o = get(key);
        return o == null ? ifNull : Long.valueOf(String.valueOf(o));
    }
    public Boolean getBoolean(String key){
        return getBoolean(key,false);
    }
    public Boolean getBoolean(String key,Boolean ifNull){
        Object o = get(key);
        return o == null ? ifNull : Boolean.valueOf(String.valueOf(o));
    }


    @Override
    public void setObjectCreator(BecknAwareCreator objectCreator) {
        super.setObjectCreator(objectCreator);
        attributeMap().clear();
    }

    @Override
    public void setInner(JSONObject value) {
        super.setInner(value);
        attributeMap().clear();
    }

    protected Map<String,ObjectHolder<BecknAware>> attributeMap(){
        if (attributeMap == null){
            synchronized (this) {
                if (attributeMap == null) {
                    attributeMap = new HashMap<>();
                }
            }
        }
        return attributeMap;
    }
    public boolean hasAdditionalProperties(){
        return false;
    }

    public <T extends  BecknObjectBase> void update(T from){
        update(from,true);
    }
    public <T extends  BecknObjectBase> void update(T from, boolean reset){
        update(from, getObjectCreator(),reset);
    }
    private <T extends  BecknObjectBase> void update(T fromSource , BecknAwareCreator boCreator){
        update(fromSource,boCreator,true);
    }
    private <T extends  BecknObjectBase> void update(T fromSource , BecknAwareCreator boCreator, boolean reset){
        if (!hasCommonAncestor(this,fromSource)){
            throw new IllegalArgumentException("Incompatible type of the parameter");
        }

        Class<? extends BecknObjectBase> sourceClass = fromSource.getClass();

        Map<String, Method> targetSetters = new HashMap<>();
        Map<String,Method> targetGetters = new HashMap<>();
        Map<String,Method> sourceGetters = new HashMap<>();
        for (Method m : sourceClass.getMethods()) {
            if ((m.getName().startsWith("get") || m.getName().startsWith("is")) && m.getParameterCount() == 0 && !m.getName().equals("getInner") && BecknObject.class.isAssignableFrom(m.getDeclaringClass())){
                sourceGetters.put(m.getName(),m);
            }
        }

        for (Method m : getClass().getMethods()) {
            if (!BecknObject.class.isAssignableFrom(m.getDeclaringClass())){
                continue;
            }
            if ((m.getName().startsWith("set") && m.getParameterCount() == 1 )) {
                String setterName = m.getName();

                String getterName = String.format("g%s",setterName.substring(1));
                Method sourceGetter = sourceGetters.get(getterName);

                if (sourceGetter == null){
                    getterName = String.format("is%s",setterName.substring(3));
                    sourceGetter = sourceGetters.get(getterName);

                }
                if (sourceGetter !=  null && hasCommonAncestor(m.getParameterTypes()[0],sourceGetter.getReturnType())){
                    targetSetters.put(m.getName(), m);
                }
            }else if ((m.getName().startsWith("get") || m.getName().startsWith("is")) && m.getParameterCount() == 0){
                targetGetters.put(m.getName(),m);
            }
        }

        for (Map.Entry<String,Method> entry : sourceGetters.entrySet()) {

            String getterName = entry.getKey();
            String setterName = getterName.startsWith("get") ? String.format("s%s",getterName.substring(1)) : String.format("set%s",getterName.substring(2));

            Method sourceGetter = entry.getValue();
            Method targetSetter = targetSetters.get(setterName);
            Method targetGetter = targetGetters.get(getterName);

            if (targetSetter == null || targetGetter == null) {
                continue;
            }

            Class<?> sourceFieldType = sourceGetter.getReturnType();
            Class<?> targetFieldType = targetSetter.getParameterTypes()[0];
            try {
                Object sourceField = sourceGetter.invoke(fromSource);
                Object targetField = targetGetter.invoke(this);
                if (sourceField == null){
                    continue;
                }
                if (targetField == null || reset){
                    if (BecknAware.class.isAssignableFrom(targetFieldType)){
                        targetField = boCreator.create(targetFieldType);
                    }else {
                        targetField = sourceField;
                    }
                }


                if (BecknObject.class.isAssignableFrom(sourceFieldType) && BecknObject.class.isAssignableFrom(targetFieldType)) {
                    if (((BecknObject)sourceField).hasAdditionalProperties() || sourceFieldType == targetFieldType){
                        ((BecknObject)targetField).setInner(((BecknObject)sourceField).getInner());
                    }else {
                        ((BecknObject) targetField).update((BecknObject) sourceField);
                    }
                } else if (BecknObjects.class.isAssignableFrom(sourceFieldType) && BecknObjects.class.isAssignableFrom(targetFieldType)){
                    BecknObjects sourceFields = (BecknObjects) sourceField;
                    BecknObjects targetFields = (BecknObjects) targetField;
                    for (Object o : sourceFields){
                        if (o instanceof BecknObject){
                            BecknObject listElement = (BecknObject) boCreator.create(o.getClass());
                            if (listElement.getClass() == o.getClass()){
                                listElement.setInner(((BecknObject) o).getInner());
                            }else {
                                listElement.update((BecknObject) o);
                                if (listElement instanceof BecknObjectWithId) {
                                    BecknObjectWithId lid = (BecknObjectWithId) listElement;
                                    BecknObjectsWithId lids = ((BecknObjectsWithId) targetFields);
                                    if (lids.get(lid.getId()) == null) {
                                        lids.add(lid);
                                    } else {
                                        lids.get(lid.getId()).update(lid);
                                    }
                                } else {
                                    targetFields.add(listElement);
                                }
                            }
                        }else {
                            targetFields.add(o);
                        }
                    }
                }
                targetSetter.invoke(this, targetField);
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }


    }

    public static boolean hasCommonAncestor(Class<?> targetType, Class<?> sourceType) {
        Class<?> commonAncestor = targetType;
        while (!commonAncestor.isAssignableFrom(sourceType)){
            commonAncestor = commonAncestor.getSuperclass();
        }
        if (!commonAncestor.isAssignableFrom(sourceType) || commonAncestor == BecknObjectBase.class || commonAncestor == Object.class ){
            return false;
        }


        return true;
    }


    public static  <T extends BecknObjectBase, S extends BecknObjectBase> boolean hasCommonAncestor(T target, S source){
        Class<?> commonAncestor = target.getClass();
        Class<?> sourceType = source.getClass();
        while (!commonAncestor.isAssignableFrom(sourceType)){
            commonAncestor = commonAncestor.getSuperclass();
        }
        if (commonAncestor == BecknObjectBase.class){
            return false;
        }
        return true;
    }

    protected String _flat(String s){
        return _flat(s," ");
    }
    protected String _flat(String s,String prefix){
        return prefix + (ObjectUtil.isVoid(s) ? " " : s) ;
    }
}
