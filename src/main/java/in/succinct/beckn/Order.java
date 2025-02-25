package in.succinct.beckn;

import in.succinct.beckn.Order.Status.StatusConverter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.Date;

public class Order extends BecknObjectWithId {
    public Order() {
        super();
    }
    public Order(String payload){
        super(payload);
    }

    public Provider getProvider(){
        return get(Provider.class,"provider");
    }
    public void setProvider(Provider provider){
        set("provider",provider);
    }

    public Location getProviderLocation(){
        return get(Location.class,"provider_location");
    }

    public void setProviderLocation(Location location){
        set("provider_location",location);
    }

    public Items getItems(){
        return get(Items.class,"items");
    }
    public void setItems(Items  items){
        set("items",items);
    }

    public AddOns getAddOns(){
        return get(AddOns.class, "add_ons");
    }
    public void setAddOns(AddOns add_ons){
        set("add_ons",add_ons);
    }

    public Offers getOffers(){
        return get(Offers.class, "offers");
    }
    public void setOffers(Offers offers){
        set("offers",offers);
    }


    public Billing getBilling(){
        return get(Billing.class,"billing");
    }
    public void setBilling( Billing billing){
        set("billing",billing);
    }

    public Fulfillment getFulfillment(){
        return get(Fulfillment.class,"fulfillment");
    }
    public void setFulfillment(Fulfillment fulfillment){
        set("fulfillment",fulfillment);
    }

    public Fulfillments getFulfillments(){
        return get(Fulfillments.class, "fulfillments");
    }
    public void setFulfillments(Fulfillments fulfillments){
        set("fulfillments",fulfillments);
    }

    public Quote getQuote(){
        return get(Quote.class,"quote");
    }
    public void setQuote(Quote quote){
        set("quote",quote);
    }

    public Payment getPayment(){
        return get(Payment.class,"payment");
    }
    public void setPayment(Payment payment){
        set("payment",payment);
    }

    public Date getCreatedAt(){
        return getTimestamp("created_at");
    }
    public Date getUpdatedAt(){
        return getTimestamp("updated_at");
    }
    public void setCreatedAt(Date date){
        set("created_at",date,TIMESTAMP_FORMAT);
    }
    public void setUpdatedAt(Date date){
        set("updated_at",date,TIMESTAMP_FORMAT);
    }
    public void setState(Status state){
        setEnum("state", state, new StatusConverter());
    }
    public Status getState(){
        return getEnum(Status.class, "state", new StatusConverter());
    }



    public Documents getDocuments(){
        return get(Documents.class,"documents");
    }
    public void setDocuments(Documents documents){
        set("documents",documents);
    }

    public boolean isExtendedAttributesDisplayed(){
        return true;
    }

    public Cancellation getCancellation(){
        return extendedAttributes.get(Cancellation.class, "cancellation");
    }
    public void setCancellation(Cancellation cancellation){
        extendedAttributes.set("cancellation",cancellation);
    }


    public enum Status {
        Awaiting_Agent_Acceptance,
        Reaching_Pickup_Location,
        Reached_Pickup_Location,

        Out_for_delivery,
        Created,
        Accepted,
        In_progress,
        Completed,
        Cancelled;

        public static class StatusConverter extends EnumConvertor<Status> {

        }
    }
    public static class Orders extends BecknObjectsWithId<Order> {
        public Orders() {
        }

        public Orders(JSONArray array) {
            super(array);
        }

        public Orders(String payload) {
            super(payload);
        }
    }
}
