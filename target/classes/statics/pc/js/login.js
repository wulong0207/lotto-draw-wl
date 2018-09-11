(function($, yicai){
  "use strict"

  var user = yicai.user;
  var message = yicai.message;

  var CONSTANTS = {
    "DATA_LOGIN_KEY" : "data-login",
    "CALLBACK" : {
      "VALIDATE_USER_NAME_EXIST_SUCCESS" : "validate.user.name.exist.success",
      "VALIDATE_USER_NAME_EXIST_ERROR" : "validate.user.name.exist.error",
      "AJAX_LOGIN_SUCCESS" : "ajax.login.success",//
      "OTHER_LOGIN_SUCCESS" : "other.login.success",//第三方登录
      "OUT_LOGIN_SUCCESS" : "out.login.success",//对外回调使用
      "LOGIN_PASSWORD_ERROR" : "login.password.error",
      "LOGIN_VALID_CODE_ERROR" : "login.valid.code.error",
      "FETCH_ERROR" : "fetch.error",
      "LOGIN_CLOSE" : "login_close"
    },
    "URL" : {
      "VALID_PHONE" : baseData.apiPath + "/pc/v1.0/member/validate/number",
      "VALID_USER_NAME" : baseData.apiPath + "/pc/v1.0/passport/login/validate/username",
      "LOGIN_ACCOUNT" : baseData.apiPath + "/pc/v1.0/passport/login",
      "LOGIN_VALID_CODE" : baseData.apiPath + "/pc/v1.0/passport/login/code",
      "SEND_VALID_CODE" : baseData.apiPath + "/pc/v1.0/passport/get/code"
    },
    "SEND_TYPE" :{
      "LOGIN" : 1
    },
    "OTHER_LOGIN" : {
      "qq": 'https://graph.qq.com/oauth2.0/authorize?client_id=101380144&response_type=token&scope=all&redirect_uri=',
      "wechat": 'https://open.weixin.qq.com/connect/qrconnect?state=login&appid=wxda24076bd33c8e15&redirect_uri=',
      "sina": 'https://api.weibo.com/oauth2/authorize?client_id=201627274&response_type=code&redirect_uri=',
    },
    "COOKIE_OAUTH" : "oauth"
  }
  
  var Login = function() {
    this.userName = "";
    this.userNameError = {
      "level": "",
      "text": "",
    },
    this.password = "";
    this.passwordError = {
      "level": "",
      "text": "",
    },
    this.validCode = "";
    this.validCodeError = {
      "level": "",
      "text": "",
    };
    this.passwordShow = true;
    this.focusType = "";
    this.exsitValidNum = 0;
    this.userNameExsit = null;
    this.validCodeSend = false;
    this.validCodeSendTime = 0;
    this.validCodeSendTimes = 0;
    this.loginType = "";
    this.userAcc = "";
    this.remember = false;
    this.callbacks = {};
    this._init();
  }

  Login.prototype = {

    "_init" : function() {
      this.on(CONSTANTS.CALLBACK.VALIDATE_USER_NAME_EXIST_SUCCESS, Callbacks.VALIDATE_USER_NAME_EXIST_SUCCESS);
      this.on(CONSTANTS.CALLBACK.VALIDATE_USER_NAME_EXIST_ERROR, Callbacks.VALIDATE_USER_NAME_EXIST_ERROR);
      this.on(CONSTANTS.CALLBACK.AJAX_LOGIN_SUCCESS, Callbacks.AJAX_LOGIN_SUCCESS);
      this.on(CONSTANTS.CALLBACK.OTHER_LOGIN_SUCCESS, Callbacks.OTHER_LOGIN_SUCCESS);
      this.on(CONSTANTS.CALLBACK.LOGIN_PASSWORD_ERROR, Callbacks.LOGIN_PASSWORD_ERROR);
      this.on(CONSTANTS.CALLBACK.LOGIN_VALID_CODE_ERROR, Callbacks.LOGIN_VALID_CODE_ERROR);
      this.on(CONSTANTS.CALLBACK.FETCH_ERROR, Callbacks.FETCH_ERROR);
      this.on(CONSTANTS.CALLBACK.LOGIN_CLOSE, Callbacks.LOGIN_CLOSE_CLEAR_TIMER);
    },

    "regs":{
      email: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/i,
      url: /^(ftp|http|https):\/\/(\w+:{0,1}\w*@)?(\S+)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/,
      number: /^\s*(\-|\+)?(\d+|(\d*(\.\d*)))\s*$/,
    },

    "render" : function() {
      if(!yicai.isUndefined(this._render)) {
        return this._render;
      }
      this._render = new Login.Render(this);//初始化页面
      new Login.PageEvent(this);//初始化页面事件
      return this._render;
    },
    "on" : function(key, callback) {
      var callbacks = this.callbacks[key];
      if(!callbacks) {
        this.callbacks[key] = callbacks = $.Callbacks("unique");
      }
      callbacks.add(callback);
    },
    "trigger" : function() {
      var args = Array.prototype.slice.apply(arguments);
      var key = args.shift(0);
      args.push(this);
      var callbacks = this.callbacks[key];
      if(!yicai.isUndefined(callbacks)) {
          callbacks.fireWith(this, args).fired();
      }
      
    },
    "show" : function() {
      this.render().show();
    },
    "validate" : function() {
      if(!yicai.isUndefined(this._validator)) {
        return this._validator;
      }
      this._validator = new Login.Validator(this);
      return this._validator;
    },
    "close" : function() {
      this.render().close();
      window.clearInterval(this.sendCodeInterval);
      this.trigger(CONSTANTS.CALLBACK.LOGIN_CLOSE);
    },
    "doLogin" : function() {
      if(!this.validate().validLogin()) {
        return;
      }
      var userName = this.userName;
      var password = this.password;
      var passwordShow = this.passwordShow;
      var validCode = this.validCode;
      var isUserNameError = this.userNameError.level != "";
      var _this = this;
      if (userName && passwordShow && password && !isUserNameError) {//账户名密码登录
        $.ajax({
          "url": CONSTANTS.URL.LOGIN_ACCOUNT,
          "type": "POST",
          "contentType": yicai.CONSTANTS.API_CONTENT_TYPE,
          "headers": yicai.CONSTANTS.HEADERS,
          "data": JSON.stringify({
            userName: userName,
            password: password
          }),
          "dataType": "json",
          "success": function (result) {
            if(yicai.isSuccess(result)) {
              _this.trigger(CONSTANTS.CALLBACK.AJAX_LOGIN_SUCCESS, result.data);
            } else {
              _this.trigger(CONSTANTS.CALLBACK.LOGIN_PASSWORD_ERROR, result);
            }            
          },
          error:function (XMLHttpRequest, textStatus, errorThrown) {
            _this.trigger(CONSTANTS.FETCH_ERROR, {message: errorThrown});
          }
        })
      } else if(userName && !passwordShow && validCode && !isUserNameError) {
        $.ajax({
          "url": CONSTANTS.URL.LOGIN_VALID_CODE,
          "type": "POST",
          "contentType": yicai.CONSTANTS.API_CONTENT_TYPE,
          "headers" : yicai.CONSTANTS.HEADERS,
          "data": JSON.stringify({
            userName: userName,
            code: validCode,
            sendType: 1,
          }),
          "dataType" : "json",
          "success" : function (result) {
            if(yicai.isSuccess(result)) {
              _this.trigger(CONSTANTS.CALLBACK.AJAX_LOGIN_SUCCESS, result.data);
            } else {
              _this.trigger(CONSTANTS.CALLBACK.LOGIN_VALID_CODE_ERROR, result);
            }            
          },
          "error":function (XMLHttpRequest, textStatus, errorThrown) {
            _this.trigger(CONSTANTS.FETCH_ERROR, {message: errorThrown});
          }
        })
      }
    },
    "doOtherLogin" : function(type) {
      var url = CONSTANTS.OTHER_LOGIN[type];
      if(!url) {
        return;
      }
      if(type == "qq") {
        url += encodeURIComponent(baseData.webCpUrl + '/oauth?state=login&backUrl=' + encodeURIComponent(location.href));
      } else if(type == "wechat") {
        url += encodeURIComponent(baseData.webCpUrl + '/oauth1?backUrl=' + encodeURIComponent(location.href));
        url += '&response_type=code&scope=snsapi_login#wechat_redirect'
      } else if(type == "sina") {
        url += encodeURIComponent(baseData.webCpUrl + '/oauth2?state=login&backUrl=' + encodeURIComponent(location.href))
      }
      $.cookie(CONSTANTS.COOKIE_OAUTH, JSON.stringify({
        type: 'login',
        backUrl: location.href || '',
        close: true,
        protocol: location.protocol
      }),{path: "/", domain : yicai.CONSTANTS.DOMAIN});
      var open = window.open(
        url,
        'oauth',
        'toolbar=yes, location=no, directories=no, status=no, menubar=yes, scrollbars=yes, resizable=no, copyhistory=yes, width=500, height=600');
      window.clearInterval(this.timer);
      var _this = this;
      this.timer = setInterval(function(){
        try{
          var token = user.getToken();
          if(!yicai.isUndefined(token)) {
            _this.trigger(CONSTANTS.CALLBACK.OTHER_LOGIN_SUCCESS);
          }
        }catch(e){
          console.error(e);
        }
      }, 800);
    },
    "sendValidCode" : function() {
      if(this.passwordShow || this.validCodeSendTime > 0 || this.validCodeSend) {
        //密码登录、验证码已发送、验证码倒计时中
        return;
      }
      var userName = $.trim($("#userName").val());
      this.userName = userName;
      var data = {"sendType" : CONSTANTS.SEND_TYPE.LOGIN, "userName" : userName}
      //获取验证码
      var _this = this;
      _this.validCodeSend = true;
      $.ajax({
        "url" : CONSTANTS.URL.SEND_VALID_CODE,
        "type" : "POST",
        "contentType" : yicai.CONSTANTS.API_CONTENT_TYPE,
        "headers" : yicai.CONSTANTS.HEADERS,
        "data" : JSON.stringify(data),
        "dataType" : "json",
        "success" : function(result) {
          if(yicai.isSuccess(result)) {
            var text = "";
            if(_this.loginType == "email") {
              text = "验证码已发送到你的邮箱，15分钟内输入有效";
            } else if(_this.loginType = "phone") {
              text = "验证码已发送到你的手机，15分钟内输入有效";
            }
            _this.validCodeError = {
              level: "",
              text: text,
            }
            //启动倒计时
            _this.validCodeSendTime = 60;
            _this.sendCodeInterval = window.setInterval(function() {
              _this.validCodeSendTime--;
              if(_this.validCodeSendTime <= 0) {
                _this.validCodeSend = false;
                window.clearInterval(_this.sendCodeInterval);
              }
              _this.render().showValidCodeSendTime();
            }, 1000);
            if(result.data.count == 8) {
              var $msg = $("<div></div>");
              $msg.append("<span>验证码每天只能获取10次，您已获取8次！</span><br/>");
              $msg.append('<span style="color:#ff7a0d;">请检查您的邮箱是否设置了拦截</span><br/>');
              $msg.append('<span>或联系客服</span>');
              message.alert($msg.html());
            }
          } else {
            _this.validCodeSend = false;
            _this.validCodeError = {
              level: "error",
              text: result.message,
            }
          }
          _this.render().showValidCodeTip();
        },
        "error" : function(XMLHttpRequest, textStatus, errorThrown) {
          message.info(errorThrown);
        }
      });
    },
    "onSuccess" : function(callback) {//添加登录成功回调
      this.on(CONSTANTS.CALLBACK.OUT_LOGIN_SUCCESS, callback);
    }

  }

  Login.Render = function(login) {
    this.login = login;
    this._init();
  }

  Login.Render.prototype = {
    "_init" : function() {
      var html = loginView();
      this.login.$element = $(html).appendTo("body");
      var _login = this.login;
      var userStatus = user.getStatus() || {};
      if(!yicai.isUndefined(userStatus.remember) && !yicai.isUndefined(userStatus.remember.userName)) {
        _login.userName = userStatus.remember.userName;
        $("#remember").prop("checked", true);
        $("#userName").val(_login.userName);
        window.setTimeout(function() {
          $("#userName").focus().blur();
        }, 50);
      }
      $("body").data(CONSTANTS.DATA_LOGIN_KEY, this.login);
    },
    "show" : function() {
      this.login.$element.show();
    },
    "close" : function() {
      this.login.$element.remove();
      $("body").removeData(CONSTANTS.DATA_LOGIN_KEY);
    },
    "focusUserName" : function() {
      $("#userName").focus();
    },
    "focusPassword" : function() {
      $("#password").focus();
    },
    "focusValidCode" : function() {
      $("#validCode").focus();
    },
    "changeLoginType" : function(){
      if(this.login.passwordShow) {
        $("input[name=password]").closest("div.input-form").show();
        $("input[name=validCode]").closest("div.input-form").hide();
        this.login.render().focusPassword();
      } else {
        $("input[name=password]").closest("div.input-form").hide();
        $("input[name=validCode]").closest("div.input-form").show();
      }
    },
    "showNameTip" : function() {
      var userNameError = this.login.userNameError;
      var $userNameTip = $("#userNameTip");
      var $userName = $("#userName");
      $userNameTip.html($.trim(userNameError.text));
      if(userNameError.level) {
        $userNameTip.addClass("error");
        $userName.addClass("error");
      } else {
        $userNameTip.removeClass("error");
      }
      if(this.login.focusType == "userName") {
        this.focusUserName();
      }
    },
    "showPasswordTip" : function() {
      var passwordError = this.login.passwordError;
      var $passwordTip = $("#passwordTip");
      var $password = $("#password");
      $passwordTip.html($.trim(passwordError.text));
      if(passwordError.level) {
        $passwordTip.addClass("error");
        $password.addClass("error");
      } else {
        $passwordTip.removeClass("error");
      }
      if(this.login.focusType == "password") {
        this.focusPassword();
      }
    },
    "showValidCodeTip" : function() {
      var validCodeError = this.login.validCodeError;
      var $validCodeTip = $("#validCodeTip");
      var $validCode = $("#validCode");
      $validCodeTip.html($.trim(validCodeError.text));
      if(validCodeError.level) {
        $validCodeTip.addClass("error");
        $validCode.addClass("error");
      } else {
        $validCodeTip.removeClass("error");
      }
      if(this.login.focusType == "validCode") {
        this.focusValidCode();
      }
    },
    "showValidCodeSendTime" : function() {
      var _login = this.login;
      var $btn2 = $("#validCodeSendTime").closest("button");
      var $btn1 = $btn2.prev("button");
      if(_login.validCodeSendTime > 0) {//倒计时
        $btn1.hide();
        $btn2.show();
        $("#validCodeSendTime").text(_login.validCodeSendTime);
      } else {
        $btn1.text("重新获取验证码").show();
        $btn2.hide();
        $("#validCodeSendTime").text(0);
      }
    }
  }

  Login.PageEvent = function(login) {
    this.login = login;
    this._init();
  }
  Login.PageEvent.prototype = {
    "_init" : function() {
      var _this = this;
      for(var key in _this){
        var obj = _this[key];
        if(key.indexOf("_bind") == 0 && $.isFunction(obj)) {
          obj.apply(_this);
        }
      }
    },
    "_bindUserName" : function() {
      var login = this.login;

      $("#userNameLabel").click(function () {//获取用户名焦点
        $(this).hide();
        $("#userName").focus();
        if ($("#password").val() == "") {
          $("#passwordLabel").show();
        }
      });

      $("#userName").focus(function () {//用户名获取焦点
        $("#userNameLabel").hide();
      });

      $("#userName").blur(function (event) {//用户名失去焦点
        if ($(this).val() == "") {
          $("#userNameLabel").show();
          $("#userNameTip").html("").removeClass("error");
        } else {
          if (event.target.name == "userName") {
            var exsitValidNum = login.exsitValidNum;
            if ($(this).val() && exsitValidNum <= 10) {//失焦验证
              login.exsitValidNum = exsitValidNum + 1
            } else {
              login.userNameExsit = null
            }
          }
          login.focusType = ""//清空
          login.validate().checkUserNameExist();
        }
      });

      $("#userName").keyup(function () {//用户名输入中
        if(event.keyCode != '13') {
          $(this).removeClass("error");
          login.userNameError = {
            level : "",
            text : ""
          }
          login.render().showNameTip();
          login.userNameExsit = false;
        }
      });

      $("#userName").keypress(function(event) {
        if(event.keyCode == '13') {
          event.preventDefault();
          $("#msLoginSubmit").click();
          return false;
        }
      });
    },
    "_bindPassword" : function() {
      var login = this.login;

      $("#passwordLabel").click(function () {//获取密码焦点
        $(this).hide();
        $("#password").focus();
      })

      $("#password").focus(function () {//密码获得焦点
        $("#passwordLabel").hide();
      });

      $("#password").blur(function () {//密码失去焦点
        if ($(this).val() == "") {
          $("#passwordLabel").show();
          login.focusType = ""//清空
        }
      });

      $("#password").keyup(function () {//密码输入中
        if(event.keyCode != '13') {
          $(this).removeClass("error");
          login.passwordError = {
            level : "",
            text : ""
          }
          login.render().showPasswordTip();
          login.userNameExsit = false;
        }
      });
      $("#password").keypress(function(event) {
        if(event.keyCode == '13') {
          event.preventDefault();
          event.stopPropagation();
          $("#msLoginSubmit").click();
          return false;
        }
      });
    },
    "_bindValidCode" : function() {
      var _login = this.login;
      $("#validCodeLabel").click(function () {//获取验证码焦点
        $(this).hide();
        $("#validCode").focus();
      })
      $("#validCode").focus(function () {
        $("#validCodeLabel").hide();
      });
      $("#validCode").keyup(function () {//验证码输入中
        if(event.keyCode != '13') {
          $(this).removeClass("error");
          _login.validCodeError = {
            level : "",
            text : ""
          }
          _login.render().showValidCodeTip();
          _login.userNameExsit = false;
        }
      });
      $("#validCode").blur(function () {//验证码框失去焦点
        if ($(this).val() == "") {
          $("#validCodeLabel").show();
        }
      });
      $("#sendValidCode").click(function() {
        _login.sendValidCode();
      });
      $("#validCode").keypress(function(event) {
        if(event.keyCode == '13') {
          event.preventDefault();
          event.stopPropagation();
          $("#msLoginSubmit").click();
          return false;
        }
      });
    },
    "_bindClose" : function() {
      var login = this.login;
      login.$element.find(".icon-close").click(function(){
        login.close();
      });
    },
    "_bindLogin" : function() {
      var login = this.login;

      $("#msLoginSubmit").click(function () {
        login.userName = $.trim($("#userName").val());
        if($.trim($("#password").val()) != "") {
          login.password = crypto($.trim($("#password").val()));
        }
        login.validCode = $.trim($("#validCode").val());
        login.remember = $("#remember").is(":checked");
        login.doLogin();
      });
    },
    "_bindOtherLogin" : function() {
      var login = this.login;
      //点击快捷登录小图标
      login.$element.on("click", ".link-QQ", function () {
        login.doOtherLogin("qq");
      })
      login.$element.on("click", ".link-wechat", function () {
        login.doOtherLogin("wechat");
      })
      login.$element.on("click", ".link-sina", function () {
        login.doOtherLogin("sina");
      })
      
      //点击免密码登录
      login.$element.on("click", "#loginTypeClick-em", function () {
        login.passwordShow = false;
        login.loginType = "email";
        login.render().changeLoginType();
      })
      login.$element.on("click", "#loginTypeClick-mob", function () {
        login.passwordShow = false;
        login.loginType = "phone";
        login.render().changeLoginType();
      })
    }

  }

  Login.Validator = function(login) {
    this.login = login;
    this._init();
  }

  Login.Validator.prototype = {
    "_init": function(){},
    "checkUserNameExist" : function() {
      var login = this.login;
      var userName = $.trim($("#userName").val());
      var code = "";
      $.ajax({
        "url": CONSTANTS.URL.VALID_USER_NAME,
        "type": "POST",
        "contentType": yicai.CONSTANTS.API_CONTENT_TYPE,
        "headers": yicai.CONSTANTS.HEADERS,
        "data": JSON.stringify({
          userName: userName,
          code: code,
          sendType: 1,
        }),
        "dataType": "json",
        "success": function (result) {
          if(yicai.isSuccess(result)) {
            login.trigger(CONSTANTS.CALLBACK.VALIDATE_USER_NAME_EXIST_SUCCESS, result.data);
          } else {
            login.trigger(CONSTANTS.CALLBACK.VALIDATE_USER_NAME_EXIST_ERROR, result);
          }
        },
        "error": function (XMLHttpRequest, textStatus, errorThrown) {
          login.trigger(CONSTANTS.CALLBACK.VALIDATE_USER_NAME_EXIST_ERROR, {"message": errorThrown});
        },
      });
    },
    "validPhone" : function(mob) {
      var success = false;
      if (mob) {
        $.ajax({
          "url": CONSTANTS.URL.VALID_PHONE,
          "type": "GET",
          "async": false,
          "headers":yicai.CONSTANTS.HEADERS,
          "data": {mobile: mob},
          "dataType" : "json",
          "success": function (result) {
            if(yicai.isSuccess(result)) {
              success = true;
            }
          }
        });
      }
      return success;
    },
    "validLogin" : function() {
      var _login = this.login;
      var userName = _login.userName;
      var password = _login.password;
      var passwordShow = _login.passwordShow;
      var validCode = _login.validCode;
      if(!userName) {
        _login.focusType = "userName";
        _login.userNameError = {
          level: "error",
          text: "账户名不能为空",
        }
        _login.render().showNameTip();
        return false;
      }else if(passwordShow && !password) {
        _login.focusType = "password";
        _login.passwordError = {
          level: "error",
          text: "密码不能为空",
        }
        _login.render().showPasswordTip();
        return false;
      }else if(!passwordShow && !validCode) {
        _login.focusType = "validCode";
        _login.validCodeError = {
          level: "error",
          text: "验证码不能为空",
        }
        _login.render().showValidCodeTip();
        return false;
      }
      return true;
    }
  }





  //************渲染登录窗口
  function loginView() {
    var View = '<div id="loginMessage" class="message" style="display: none">' + '<div class="message-overlay"></div>';
    View += '<div class="message-main">' + '<div class="message-head"><i class="icon icon-close"></i></div>';
    View += loginBody();
    View += '</div></div>';
    
    return View;
  }
  
  function loginBody() {
    var msbd = '<div class="message-body">' +
        '<div class="login-mode LoginM">' +
        '<img style="width: 160px ; margin: -30px auto 30px;display: block" src="' + baseData.contextPath + '/resources/img/login/LOGO.png"/>';
    
    msbd += nameView();
    msbd += passwordView();
    msbd += codeView();
    msbd += LoginSubmit()
    msbd += LoginOther();
    msbd += '</div></div>';
    return msbd;
  }
  
  function nameView() {//用户名输入框
    var namebox = '<div class="input-form special">' + '<div class="input-prebox">' + '<label class="input-icon"><i class="username"></i></label>';
    
    namebox += '<span id="userNameLabel" class="ph-label">' + '手机号/邮箱/账户名' + '</span>';
    namebox += '<input name="userName" type="text" style="display: none " disabled autoComplete="off"/>'
    namebox += '<input id="userName" class="input-normal" name="userName" type="text" ' + 'value=""'
        + '/>'
    
    namebox += '<p id="userNameTip" class="input-tip error"></p></div></div>';
    return namebox;
  }
  
  function passwordView() {//密码输入框
    var passwordbox = '<div class="input-form special">' + '<div class="input-prebox">' + '<label class="input-icon"><i class="password"></i></label>';
    
    passwordbox += '<span id="passwordLabel" class="ph-label">' + '请输入您的登录密码' + '</span>';
    passwordbox += '<input name="password" type="text" style="display: none " disabled autoComplete="off"/>'
    passwordbox += '<input id="password" class="input-normal" name="password" type="password" ' + 'value=""'
        + '/>'
    
    passwordbox += '<p id="passwordTip" class="input-tip error"></p></div></div>';
    return passwordbox;
  }
  
  function codeView() {//验证码输入框
    
    var codebox = '<div class="input-form"  style="display: none">' + '<div class="input-prebox">';
    
    codebox += '<span id="validCodeLabel" class="ph-label">' + '6位数字' + '</span>';
    codebox += '<input name="validCode" type="text" style="display: none " disabled autoComplete="off"/>'
    codebox += '<input id="validCode" class="input-normal" name="validCode" type="text" ' + 'value=""' + '/>'
    
    codebox += '<button id="sendValidCode" class="btn-valid">获取验证码</button>'
    codebox += '<button class="btn-valid default" style="display: none"><em id="validCodeSendTime">60</em>秒后重新获取</button>'
    
    codebox += '<p id="validCodeTip" class="input-tip error"></p></div></div>';
    return codebox;
  }
  
  function LoginSubmit() {//登录按钮
    var LoginBtn = '<div id="msLoginSubmit" class="input-form">' +
        '<a class="submit mt10">确认登录</a>' +
        '</div>';
    return LoginBtn;
  }
  
  function LoginOther() {//记住账户、忘记密码、免费注册、登录方式
    var other = '<p class="tar pt15 pb15 fs12">' +
        '<label class="fl"><input id="remember" name="remember" class="radio_ck" type="checkbox" />记住账户</label>' +
        '<a target="_blank" href="'+baseData.webCpUrl+'/findpwd?flag=1">忘记密码</a>' +
        '<span class="mr5 ml5">|</span>' +
        '<a target="_blank" href="'+baseData.webCpUrl+'/register">免费注册</a>' +
        '</p>'
    other += '<p class="aicon-list fs12 pt10">' + '其他登录方式：' +
        '<a id="oginQQ" class="link-QQ"></a>' +
        '<a id="loginWechat" class="link-wechat"></a>' +
        '<a id="loginSina" class="link-sina"></a>' +
        '</p>'
    
    return other;
  }
  //************渲染登录窗口 结束

  //************加密
  function crypto(password, salt, keySize, iterations) {
    if(yicai.isUndefined(salt)){
      salt = '2f1e131cc3009026cf8991da3fd4ac38';
    }
    if(yicai.isUndefined(keySize)) {
      keySize = 512;
    }
    if(yicai.isUndefined(iterations)) {
      iterations = 50;
    }
    return CryptoJS.PBKDF2(password, CryptoJS.enc.Utf8.parse(salt), {
      keySize: keySize / 32,
      iterations: iterations
    }).toString();
  }
  //************加密 end

  //************回调
  var Callbacks = {
    "VALIDATE_USER_NAME_EXIST_SUCCESS" : function(data) {
      var login = this;
      var email = login.regs.email;
      var userName = $.trim($("#userName").val());
      login.userNameExsit = true;
      var isSetPwd = data["set_pwd"] == 1;
      login.userNameError = {
        "level" : "",
        "text" : ""
      }
      if(!isSetPwd && email.test(userName)) {
        login.loginType = "email";
        login.passwordShow = false;
        login.userNameError.text = "您的账号还未设置登录密码,您可设置登录密码";
      } else if(!isSetPwd && login.validate().validPhone(userName)) {
        login.loginType = "phone";
         login.passwordShow = false;
         login.userNameError.text = "您的账号还未设置登录密码,您可设置登录密码";
      } else{
        login.loginType = "acc";
        login.passwordShow = true;
      }
      login.render().showNameTip();
      login.render().changeLoginType();
    },
    "VALIDATE_USER_NAME_EXIST_ERROR" : function(result) {
      var login = this;
      var userName = $.trim($("#userName").val());
      var errorCode = result.errorCode || "";
      if (errorCode == 40119) {
        login.userNameError = {
          level: "error",
          text: "该账号不存在",
        }
      } else if (errorCode == 40190) {
        var unPwdMsg = '<span>该账户名绑定手机号' + result.data.mob + '，立即' + '<a class="blue" id="loginTypeClick-mob">' + '手机免密登录' + '</a></span>';
        login.userNameError = {
          level: "",
          text: unPwdMsg,
        }
      } else if (errorCode == 40191) {
        var unPwdMsg = '<span>该账户名绑定邮箱' + result.data.em + '，立即' + '<a class="blue" id="loginTypeClick-em">' + '邮箱免密登录' + '</a></span>';
        login.userNameError = {
          level: "",
          text: unPwdMsg,
        }
      } else if (errorCode == 40192) {
        var unPwdMsg = '<span>该账户名第三方登录，立即使用<a class="blue link-QQ">QQ登录</a></span>';
        login.userNameError = {
          level: "",
          text: unPwdMsg,
        }
      } else if (errorCode == 40193) {
        
        var unPwdMsg = '<span>该账户名第三方登录，立即使用<a class="blue link-wechat">微信登录</a></span>';
        login.userNameError = {
          level: "",
          text: unPwdMsg,
        }
      } else if (errorCode == 40195) {
        var unPwdMsg = '<span>该账户名第三方登录，立即使用<a class="blue link-wechat">微博登录</a></span>';
        login.userNameError = {
          level: "",
          text: unPwdMsg,
        }
      } else {
        login.userNameError = {
          level: "error",
          text: result.message,
        };
      }
      login.render().showNameTip();
    },
    "AJAX_LOGIN_SUCCESS" : function(data) {
      user.login(data);
      if(this.remember) {
        user.updateStatus({
          "remember":{
            "userName": this.userName,
            "password": this.password
          }
        });
      } else {
        user.updateStatus({"remember":null});
      }
      $("body").trigger("user.refresh");
      this.close();
      this.trigger(CONSTANTS.CALLBACK.OUT_LOGIN_SUCCESS);
    },
    "OTHER_LOGIN_SUCCESS" : function() {
      if(this.remember) {
        user.updateStatus({
          "remember":{
            "userName": this.userName,
            "password": this.password
          }
        });
      } else {
        user.updateStatus({"remember":null});
      }
      $("body").trigger("user.refresh");
      this.close();
      this.trigger(CONSTANTS.CALLBACK.OUT_LOGIN_SUCCESS);
    },
    "LOGIN_PASSWORD_ERROR" : function(result){
      var login = this;
      var errorCode = result.errorCode || "";
      if(errorCode == 40141) {
        login.passwordError = {
          level: "error",
          text: '您输入的密码和账户名不匹配已超过10次!今天已经不能再次尝试,请联系客服'
        }
        this.render().showPasswordTip();
      }else if(errorCode == 40128 || errorCode == 40109) {
        var msg = '您输入的密码与帐户名不匹配!是否<a class="blue" href="' + baseData.webCpUrl + '/findpwd?flag=1">忘记密码</a>';
        login.passwordError = {
          level: "error",
          text: msg
        }
        this.render().showPasswordTip();
      }else {
        login.passwordError = {
          level: "error",
          text: result.message
        }
        this.render().showPasswordTip();
      }
    },
    "LOGIN_VALID_CODE_ERROR" : function(result){
      var login = this;
      var errorCode = result.errorCode || "";
      login.validCodeError = {
        level: "error",
        text: result.message
      }
      this.render().showValidCodeTip();
    },
    "FETCH_ERROR" : function(msg){
      message.info(msg);
    },
    "LOGIN_CLOSE_CLEAR_TIMER" : function() {
      window.clearInterval(this.timer);
    }
  }


  yicai.login = function() {
    var login = $("body").data(CONSTANTS.DATA_LOGIN_KEY);
    if(yicai.isUndefined(login)) {
      login = new Login();
    }
    login.show();
    return login;
  }

})(jQuery, YiCai)
//************回调 end
