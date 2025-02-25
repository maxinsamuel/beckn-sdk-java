package in.succinct.beckn;

public class Intent extends BecknObject {
    public Intent(){
        super();
    }
    public Intent(String payload){
        super(payload);
    }

    public Descriptor getDescriptor(){
        return get(Descriptor.class, "descriptor");
    }
    public void setDescriptor(Descriptor descriptor){
        set("descriptor",descriptor);
    }

    public Fulfillment getFulfillment(){
        return get(Fulfillment.class, "fulfillment");
    }
    public void setFulfillment(Fulfillment fulfillment){
        set("fulfillment",fulfillment);
    }

    public Provider getProvider(){
        return get(Provider.class, "provider");
    }
    public void setProvider(Provider provider){
        set("provider",provider);
    }

    public Item getItem(){
        return get(Item.class, "item");
    }
    public void setItem(Item item){
        set("item",item);
    }

    public Tags getTags(){
        return get(Tags.class,"tags");
    }
    public void setTags(Tags tags){
        set("tags",tags);
    }

    public Category getCategory(){
        return get(Category.class,"category");
    }
    public void setCategory(Category category){
        set("category",category);
    }

    public Payment getPayment(){
        return get(Payment.class, "payment");
    }
    public void setPayment(Payment payment){
        set("payment",payment);
    }
}
