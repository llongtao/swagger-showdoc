**简要描述：**
- 微信登录

**请求URL：**
- `http://api.aexp.top/auth/api/v2/user/auth/wxLogin`

**请求方式：**
- POST


**请求实体：**[WxLoginReq](#WxLoginReq)
**请求body：**
```json
{
  "jsCode" : "string"
}
```

**响应实体：**[ResponseInfoOfWxLoginResp](#ResponseInfoOfWxLoginResp)
**响应示例**
```json
{
  "data" : {
    "expiredAt" : "",
    "token" : "string",
    "uid":"string"
  },
  "messageCode" : "string",
  "message" : "string",
  "statusCode" : 1,
  "timestamp" : ""
}
```

###数据模型

<a name="WxLoginReq"></a>**WxLoginReq**

|参数名|必选|类型|说明|示例|
|:---|:---|:---|:---|:---|
|jsCode|false|string|js_code|""|

<a name="WxLoginResp"></a>**ZzLoginResp**

|参数名|必选|类型|说明|示例|
|:---|:---|:---|:---|:---|
|expiredAt|false|string|过期时间|""|
|token|false|string|令牌|""|
|uid|false|string|用户id|""|

<a name="ResponseInfoOfWxLoginResp"></a>**ResponseInfoOfWxLoginResp**

|参数名|必选|类型|说明|示例|
|:---|:---|:---|:---|:---|
|data|false|[WxLoginResp](#WxLoginResp)|响应数据|""|
|message|false|string|反馈信息|""|
|messageCode|true|string|反馈信息码|""|
|statusCode|true|integer|状态码|200|
|timestamp|false|string|时间戳|""|
