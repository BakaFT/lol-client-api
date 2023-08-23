# this document assumes you are properly handling all cookies yourself

## REQUEST 1
```
POST https://auth.riotgames.com/api/v1/authorization HTTP/1.1
user-agent: RiotClient/70.0.0.247.1382 rso-auth (Windows;10;;Professional, x64)
Cache-Control: no-cache
Content-Type: application/json
Accept: application/json
{
    "acr_values": "",
    "claims": "",
    "client_id": "riot-client",
    "code_challenge": "",
    "code_challenge_method": "",
    "nonce": "HqDjhMCk-gwcA7vNoG-EsA",
    "redirect_uri": "http://localhost/redirect",
    "response_type": "token id_token",
    "scope": "openid link ban lol_region account"
}
```

if you dont have an asid cookie send the previous request again with the cookies it gave you, these should include `tdid, asid, did, clid`

## REQUEST 2
```
POST https://authenticate.riotgames.com/api/v1/login HTTP/1.1
user-agent: RiotClient/70.0.0.247.1382 rso-authenticator (Windows;10;;Professional, x64)
Accept: application/json
Cookie: tdid
Content-Type: application/json
{
    "apple": null,
    "campaign": null,
    "clientId": "riot-client",
    "code": null,
    "facebook": null,
    "gamecenter": null,
    "google": null,
    "language": "",
    "multifactor": null,
    "nintendo": null,
    "platform": "windows",
    "playstation": null,
    "remember": false,
    "riot_identity": {
        "campaign": null,
        "captcha": null,
        "language": "en_GB",
        "password": null,
        "remember": null,
        "state": "auth",
        "username": null
    },
    "riot_identity_signup": null,
    "rso": null,
    "sdkVersion": "23.8.0.1382",
    "type": "auth",
    "xbox": null
}
```

## REQUEST 3

the previous response (example visible below) body includes the hcaptcha sitekey and the rqdata

```
{
    "auth": {
        "auth_method": "riot_identity"
    },
    "captcha": {
        "hcaptcha": {
            "data": "THIS_VALUE_IS_RQDATA",
            "key": "THIS_VALUE_IS_SITEKEY"
        },
        "type": "hcaptcha"
    },
    "country": "deu",
    "platform": "windows",
    "type": "auth"
}
```

you now have to use either capmonster or capsolver (other services dont support this) to solve the hcaptcha enterprise captcha using the rqdata and sitekey on the websiteurl `https://authenticate.riotgames.com/api/v1/login`

```
PUT https://authenticate.riotgames.com/api/v1/login HTTP/1.1
user-agent: RiotClient/70.0.0.247.1382 rso-authenticator (Windows;10;;Professional, x64)
Accept: application/json
Cookie: __cflb, authenticator.sid, tdid
Content-Type: application/json
{
    "riot_identity": {
        "campaign": null,
        "captcha": "hcaptcha P1_...",
        "language": "en_GB",
        "password": "password_here",
        "remember": false,
        "state": null,
        "username": "username_here"
    },
    "type": "auth"
}
```

## REQUEST 4

the previous response includes a login token which you have to use for this request

```
POST https://auth.riotgames.com/api/v1/login-token HTTP/1.1
user-agent: RiotClient/70.0.0.247.1382 rso-auth (Windows;10;;Professional, x64)
Cache-Control: no-cache
Content-Type: application/json
Cookie: did, tdid, clid=ec1
Accept: application/json
{
    "authentication_type": "RiotAuth",
    "code_verifier": "",
    "login_token": "login_token_here",
    "persist_login": false
}
```

## REQUEST 5

the previous response includes a ssid cookie which is very important now for the last request, anything after this is as normal

```
POST https://auth.riotgames.com/api/v1/authorization HTTP/1.1
user-agent: RiotClient/70.0.0.247.1382 rso-auth (Windows;10;;Professional, x64)
Cache-Control: no-cache
Content-Type: application/json
Cookie: did, tdid, clid, ssid
Accept: application/json
{
    "acr_values": "",
    "claims": "",
    "client_id": "riot-client",
    "code_challenge": "",
    "code_challenge_method": "",
    "nonce": "dIIZ_afu0DfKKRdQc2KMLQ",
    "redirect_uri": "http://localhost/redirect",
    "response_type": "token id_token",
    "scope": "openid link ban lol_region account"
}
```

the response you get here should be familiar to you, everything after this is as before
