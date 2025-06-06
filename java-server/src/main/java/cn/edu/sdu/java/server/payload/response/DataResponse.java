package cn.edu.sdu.java.server.payload.response;

import lombok.Getter;
import lombok.Setter;

/*
 * DataResponse 前端HTTP请求返回数据对象
 * Integer code 返回代码 0 正确返回 1 错误返回信息
 * Object data 返回数据对象
 * String msg 返回正确错误信息
 */
@Setter
@Getter
public class DataResponse {
    private Integer code;
    private Object data;
    private String msg;
    public DataResponse(){

    }

    public DataResponse(Integer code, Object data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static DataResponse success(Object data) {
        return new DataResponse(0, data, "操作成功");
    }

    public static DataResponse error(String errorMsg) {
        return new DataResponse(-1, errorMsg, null);
    }

    // 错误响应（带错误码）
    public static DataResponse error(int code, String errorMsg) {
        return new DataResponse(code, errorMsg, null);
    }
}
