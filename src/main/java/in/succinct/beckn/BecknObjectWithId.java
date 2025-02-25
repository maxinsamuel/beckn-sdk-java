package in.succinct.beckn;

import com.venky.core.util.ObjectUtil;
import org.json.simple.JSONObject;

public class BecknObjectWithId extends BecknObject {
    public BecknObjectWithId(){
        super();
    }
    public BecknObjectWithId(String payload){
        super(payload);
    }
    public BecknObjectWithId(JSONObject object){
        super(object);
    }

    public String getId(){
        return get("id");
    }
    public void setId(String id){
        if (!ObjectUtil.isVoid(id) && !ObjectUtil.isVoid(getId()) && !ObjectUtil.equals(getId(),id)){
            throw new RuntimeException("Id cannot be changed!");
        }
        set("id",id);
    }


}
