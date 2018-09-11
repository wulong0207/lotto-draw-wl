(function($, yicai){
  "use strict"

  var CONSTANTS = {
    "LOGIN_MODEL" : {
      "MODAL" : "modal",
      "PAGE" : "page"
    },
    "COOKIE_USER_INFO" : "userInfo",
    "COOKIE_TOKEN" : "token",
    "COOKIE_STATUS" : "status",
    "LOGIN_URL" : baseData.webCpUrl + "/login?backUrl="
  }

  var defaults = {
    exdays : 14
  }

  var User = function() {}
  User.prototype = {
    "getToken"  : function(toLogin){
      var token = $.cookie(CONSTANTS.COOKIE_TOKEN);
      if(token) {
        return token;
      }
      if(toLogin) {
        window.location.href = CONSTANTS.LOGIN_URL + encodeURIComponent(window.location.href);
      }
    },
    "logout" : function() {
      $.cookie(CONSTANTS.COOKIE_TOKEN, null, { expires: -1, path: "/", domain: yicai.CONSTANTS.DOMAIN});
      var userStatus = this.getStatus();
      if(!yicai.isUndefined(userStatus.remember) && !yicai.isUndefined(userStatus.remember.userName)) {
        this.updateStatus({"remember":{"userName" : userStatus.remember.userName }});
      }
      this.updateStatus({})
      window.location.reload();
    },
    "login" : function(userInfo, autoLogin) {
      if(!userInfo) {
        return;
      }
      var expiredays = autoLogin ? defaults.exdays : 2 / 24;// 有效期
      var expires = +new Date() + expiredays * 24 * 60 * 60 * 1000;
      $.cookie(CONSTANTS.COOKIE_TOKEN, userInfo["tk"], { 
        expires: expires, 
        path: "/",
        domain: yicai.CONSTANTS.DOMAIN
      });
    },
    "getStatus" : function(){
      var cookieStatus = $.cookie(CONSTANTS.COOKIE_STATUS);
      if(!yicai.isUndefined(cookieStatus)) {
        try{
          cookieStatus = $.parseJSON(decodeURIComponent(cookieStatus));
        }catch(e){
        }
      }
      return cookieStatus || {};
    },
    "updateStatus" : function(status) {
      var cookieStatus = this.getStatus();
      cookieStatus = cookieStatus || {};
      cookieStatus = $.extend(cookieStatus, status);
      $.cookie(CONSTANTS.COOKIE_STATUS, JSON.stringify(cookieStatus), {
        expires: defaults.exdays * 24 * 60 * 60 * 1000,
        path : "/",
        domain: yicai.CONSTANTS.DOMAIN
      });
    }
  }

  yicai.user = new User();
})(jQuery, YiCai)