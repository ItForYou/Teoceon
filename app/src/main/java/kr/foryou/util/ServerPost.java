package kr.foryou.util;

public class ServerPost {
    public String getSuccess() {
        return success;
    }
    public void setSuccess(String success) {
        this.success = success;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getWinBoolean() {
        return winBoolean;
    }

    public void setWinBoolean(String winBoolean) {
        this.winBoolean = winBoolean;
    }
    public String getItemCount() {return itemCount;}

    public void setItemCount(String itemCount) {this.itemCount = itemCount;}

    public String getOrderCount() {return orderCount;}

    public void setOrderCount(String orderCount) {this.orderCount = orderCount;}
    String success;
    String message;
    String winBoolean;



    String itemCount;
    String orderCount;
}
