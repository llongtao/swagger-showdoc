package top.aexp.swaggershowdoc.server.models;

import lombok.Data;

@Data
public class BaseResponse {
    boolean succeed;
    String message;


    public BaseResponse() {
    }

    public BaseResponse(boolean succeed) {
        this.succeed = succeed;
    }

    public BaseResponse(boolean succeed, String message) {
        this.succeed = succeed;
        this.message = message;
    }

    public static BaseResponse ok(){
        return new BaseResponse(true);
    }

    public static BaseResponse error(String msg){
        return new BaseResponse(false,msg);
    }
}
