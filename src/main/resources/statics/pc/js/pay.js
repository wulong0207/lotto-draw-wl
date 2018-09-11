(function($, yicai){

  var message = yicai.message;
  var user = yicai.user;
  
  var CONSTANTS = {
    "CLIENT_TYPE" : 1,
    "URL_TO_PAY" : baseData.apiPath + "/pc/v1.0/payCenter/toPay",
    "URL_PAY" : baseData.apiPath + "/pc/v1.0/payCenter/pay",
    "URL_CHECK_PAY" : baseData.apiPath + "/pc/v1.0/payCenter/payResult",
    "COOKIE_USER_INFO" : baseData.COOKIE_USER_INFO,
    "HB_CAN_USE" : 1,
    "PAY_TYPE_FLAG_CAN_USE" : 1,
    "PAY_TYPE_FLAG_PAUSE" : 0,
    "TRUE_NUM" : 1,
    "HB_TYPE_KEY" : {
      "YH" : 1,
      "MJ" : 2,
      "CJ" : 3,
      "JJ" : 4,
      "DLB" : 5,
      "SJ" : 6
    },
    "HB_TYPE" : {
      1:"充值红包",
      2:"满减红包",
      3:"彩金红包",
      4:"加奖红包",
      5:"大礼包",
      6:"随机红包"
    },
    "BANK_TYPE" : {
      1 : "储蓄卡",
      2 : "信用卡",
      3 : "第三方支付"
    },
    "PAY_DATA_KEY" : "payData",
    "PAY_BANK_ID" : "payBankId",
    "PAY_DATA_TYPE_KEY" : "payDataType",
    "PAY_DATA_TYPE" :{
      "HB": "HB"/*红包*/, "WALLET": "WALLET"/*余额*/, "METHOD": "METHOD"/*渠道*/
    },
    "CHANNEL_TYPE" : {
        10 : {
            name: '支付宝',
            color: "#2091c6",
            tips: ["1. 打开支付宝选[扫一扫]", "2. 在打开的页面选[立即支付]", "3. 在弹出熟悉的界面完成操作即可"]
        },
        11 : {
            name: '微信',
            color: "#40af36",
            tips: ["1. 打开微信右上[+]选[扫一扫]", "2. 在打开的页面选[立即付款]", "3. 在弹出熟悉的界面完成操作即可"]
        },
        12 : {
            name: 'QQ',
            color: "#2091c6",
            tips: ["1. 打开手机QQ右上[+]选[扫一扫]", "2. 在打开的页面选[立即付款]", "3. 在弹出熟悉的界面完成操作即可"]
        },
        14 : {
            name: '京东',
            color: "#2091C6",
            tips: ["1. 打开手机京东左上[+]选[扫一扫]", "2. 在打开的页面选[立即付款]", "3. 在弹出熟悉的界面完成操作即可"]
        }

    }
  }

  var defaults = {
    "channelId" : yicai.CONSTANTS.HEADERS.cId,
    "clientType" : CONSTANTS.CLIENT_TYPE
  };
  
  var Pay  = function() {
    this._render = null;
    this._event = null;
    this.result = {};
    this.callbacks = {};//回调
    this.paySuccess = true;
    this.payData.hbCode = null;
    this.payData.wallet = 0;
    this.payData.bankCardId = null;
    this.payData.bankId = null;
    this.payData.bankMoney = null;
    this.payData._pay = this;
    this._init();
  };
  
  Pay.prototype = {
    "_init" : function() {
      // 跳转支付成功回调
      this.on("success.toPay", onToPaySuccess);
      // 跳转支付失败回调
      this.on("error.toPay", onToPayError);
      // 调用支付请求成功回调
      this.on("success.pay", onPaySuccess);
      // 调用支付请求失败回调
      this.on("error.pay", onPayError);
      // 关闭
      this.on("destroy", function() {
        this.stopCheckPay();
      })
    },
    "payData" : {
      "hbCode" : null,
      "wallet" : 0,
      "bankCardId" : null,
      "bankId" : null,
      "bankMoney" : 0,
      "judgeBankMoney" : 0,
      "_pay" : null,
      "getHbData" : function() {
        if(yicai.isUndefined(this.hbCode)) {
          return null;
        }
        var toPayResult = this._pay.getToPayResult() || {};
        if(!yicai.isSuccess(toPayResult)) {
          return null;
        }
        var hbList = toPayResult.data.cl || [];
        var hbData = null;
        for(var i = 0; i < hbList.length; i++) {
          hbData = hbList[i];
          if(hbData["r_c"] == this.hbCode) {
            return hbData;
          }
        }
        return hbData;
      },
      "getHbPayMoney" : function() {
        var hbData = this.getHbData();
        return yicai.isUndefined(hbData) ? 0 : getHbMoney(hbData, this._pay.getOrderPrice);
      },
      "getWallet" : function() {
        return this.wallet;
      },
      "getBankData" : function() {
        if(yicai.isUndefined(this.bankCardId) && yicai.isUndefined(this.bankId) ) {
          return null;
        }
        var toPayResult = this._pay.getToPayResult() || {};
        if(!yicai.isSuccess(toPayResult)) {
          return null;
        }
        var bankList = toPayResult.data.ptl || [];
        var bankData = null;
        for(var i = 0; i < bankList.length; i++) {
          bankData = bankList[i];
          if(bankData["b_i"] == this.bankId && bankData["b_c_i"] == this.bankCardId) {
            return bankData;
          }
        }
        return bankData;
      },
      /**
      * 设置使用的红包编号, 判断红包是否可使用，可使用返回该红包数据
      */
      "setHbCode" : function(hbCode) {
        if(!yicai.isUndefined(hbCode) && hbCode == this.hbCode) {
          return this.getHbData();
        }
        if(yicai.isUndefined(hbCode)) {
          this.hbCode = null;
          return null;
        }
        var toPayResult = this._pay.getToPayResult() || {};
        if(!yicai.isSuccess(toPayResult)) {
          return null;
        }
        var hbData = null;
        var hbList = toPayResult.data.cl || [];
        for(var i = 0; i < hbList.length; i++) {
          hbData = hbList[i];
          if(hbData["r_c"] == hbCode && hbData["r_s"] == CONSTANTS.HB_CAN_USE) {
            this.hbCode = hbData["r_c"];
            return hbData;
          }
        }
        return null;
      },
      /**
      * 使用余额，返回已使用的剩余的余额
      */
      "setWallet" : function(money) {
        var toPayResult = this._pay.getToPayResult() || {};
        if(!yicai.isSuccess(toPayResult)) {
          return money;
        }
        var userWallet = toPayResult.data["uw"];
        var userTotalBalance = userWallet["tot_c_b"];
        if(userTotalBalance <= 0) {
          return money;
        }else if(money <= 0) {
          this.wallet = 0;
          return 0;
        } else if(userTotalBalance >= money) {
          this.wallet = money;
          return 0;
        } else {
          this.wallet = userTotalBalance;
          return yicai.subNumber(money, userTotalBalance);
        }
      },
      /**
      * 使用银行支付，返回已使用的银行数据
      */
      "setBankCardId" : function(bankCardId, bankId, money) {
        money = yicai.isUndefined(money) ? 0 : money;
        if(money <= 0 || (yicai.isUndefined(bankId) && yicai.isUndefined(bankCardId))) {
          this.bankCardId = null;
          this.bankId = null;
          this.bankMoney = 0;
          return;
        }
        var toPayResult = this._pay.getToPayResult() || {};
        if(!yicai.isSuccess(toPayResult)) {
          return 0;
        }
        var bankList = toPayResult.data.ptl || [];
        var bankData = null;
        for(var i = 0; i < bankList.length; i++) {
          bankData = bankList[i];
          if(bankData["b_i"] == bankId && bankData["b_c_i"] == bankCardId && bankData["flag"] == CONSTANTS.PAY_TYPE_FLAG_CAN_USE) {
            this.bankCardId = bankCardId;
            this.bankId = bankId;
            this.bankMoney = money;
            return bankData;
          }
        }
        return null;
      },
      "refreshJudgeBankMoney": function() {
        var orderPrice = this._pay.getOrderPrice();
        var remainMoney = orderPrice;
        var hbData = this.getHbData();
        if(!yicai.isUndefined(hbData)) {
          var hbMoney = getHbMoney(hbData, orderPrice);
          remainMoney = yicai.subNumber(remainMoney, hbMoney);
          if(remainMoney <= 0) {//红包足额支付
            this.judgeBankMoney = orderPrice;
            return;
          }
        }
        if(this.getWallet() > 0) {
          remainMoney = yicai.subNumber(remainMoney, this.getWallet());
          if(remainMoney <= 0) {
            this.judgeBankMoney = this.getWallet();
            return;
          }
        }
        this.judgeBankMoney = remainMoney;
      }
    },

    "toPay" : function() {
      if(arguments.length == 0) {
        throw "arguments is missing!";
      }
      var token = user.getToken(true);
      if(yicai.isUndefined(token)) {
        return;
      }
      var arg1 = arguments[0];
      var param = arg1;
      if($.isFunction(arg1)) {
        param = arg1();
      }
      var data;
      if($.type(param) == "string") {
        data = $.extend({}, defaults, {"orderCode" : arg1, "token" : token});
      } else if($.isPlainObject(param)){
        data = $.extend({}, defaults, param, {"token" : token});
      }
      this.orderCode = data.orderCode;
      var _pay = this;
      $.ajax({
        "url" : CONSTANTS.URL_TO_PAY,
        "type" : "GET",
        "headers" : yicai.CONSTANTS.HEADERS,
        "data" : data,
        "dataType" : "json",
        "success" : function(result) {
          _pay.result["toPay"] = result;
          if(yicai.isSuccess(result)){
            _pay.trigger("success.toPay");
          } else {
            _pay.trigger("error.toPay");
          }
        },
        "error" : function(XMLHttpRequest, textStatus, errorThrown) {
          message.info(errorThrown);
        }
      });
    },
    "pay" : function() {
      var $payOrder = this.$element;
      var payData =this.payData;
      var token = user.getToken(true);
      if(yicai.isUndefined(token)) {
        this.isPaying = false;
        return;
      }
      var payParam = {"token" : token, "channelId" : yicai.CONSTANTS.HEADERS.cId, "clientType" : CONSTANTS.CLIENT_TYPE};
      payParam.orderCode = this.orderCode;
      payParam.returnUrl = baseData.webCpUrl + "/uc/record";
      var payFlag = false;
      //红包
      var hbData = payData.getHbData();
      if(!yicai.isUndefined(hbData)) {
        payFlag = true;
        payParam.redCode = hbData["r_c"];
      }
      //余额
      if(payData.wallet > 0) {
        payFlag = true;
        payParam.balance = payData.wallet;
      }
      //银行卡
      var bankData = payData.getBankData()
      if(!yicai.isUndefined(bankData)) {
        payFlag = true;
        payParam.bankId = bankData["b_i"];
        payParam.bankCardId = bankData["b_c_i"];
        payParam.payAmount =payData.bankMoney;
        //是否切换快捷、网银支付
        if(bankData["b_t"] == 1 || bankData["b_t"] == 2) {
          if($payOrder.find(".pay-confirm .pay-tip input[type=checkbox]").is(":checked")) {
            payParam.change=1;
          }
        }
      }
      if(!payFlag) {
        message.info("请选择支付方式");
        this.isPaying = false;
        return;
      }
      // 请求支付
      var _pay = this;
      $.ajax({
        "url" : CONSTANTS.URL_PAY,
        "type" : "POST",
        "contentType":"application/json",
        "headers" : yicai.CONSTANTS.HEADERS,
        "data":JSON.stringify(payParam),
        "dataType":"json",
        "success": function(result){
          _pay.isPaying = false;
          _pay.result["pay"] = result;
          if(yicai.isSuccess(result)){
            _pay.trigger("success.pay");
          } else {
            _pay.trigger("error.pay");
          }
        },
        "error": function(XMLHttpRequest, textStatus, errorThrown) {
          _pay.isPaying = false;
          message.info(errorThrown);
        }
      });
    },
    "checkPay" : function() {
      var payResult = this.getPayResult();
      if(yicai.isUndefined(payResult) || yicai.isUndefined(payResult.data["transCode"])) {
        return;
      }
      var token = user.getToken();
      if(yicai.isUndefined(token)) {
        return;
      }
      var param = {"token" : token, "transCode" : payResult.data["transCode"]};
      var _pay = this;
      $.ajax({
        "url" : CONSTANTS.URL_CHECK_PAY,
        "type" : "GET",
        "headers" : yicai.CONSTANTS.HEADERS,
        "data" : param,
        "dataType" : "json",
        "success": function(result){
          if(yicai.isSuccess(result) && result.data["p_s"] == 2) {
            _pay.paySuccess = true;
            _pay.render().showSuccess();
          }else{
            _pay.checkTimeOut = window.setTimeout(function() {
              _pay.checkPay();
            }, 2000);
          }
        },
        "error": function(XMLHttpRequest, textStatus, errorThrown) {
          _pay.checkTimeOut = window.setTimeout(function() {
            _pay.checkPay();
          }, 2000);
        }
      });
    },
    "stopCheckPay" : function() {
      if(!yicai.isUndefined(this.checkTimeOut)) {
        window.clearTimeout(this.checkTimeOut);
      }
    },

    "getToPayResult" : function() {
      return this.result["toPay"];
    },
    "getToPayBankData" : function(bankId, bankCardId) {
      var toPayResult = this.getToPayResult();
      if(!yicai.isSuccess(toPayResult)) {
        return null;
      }
      var bankList = toPayResult.data.ptl || [];
      var bankData = null;
      for(var i = 0; i < bankList.length; i++) {
        bankData = bankList[i];
        if(bankData["b_i"] == bankId && bankData["b_c_i"] == bankCardId) {
          return bankData;
        }
      }
    },
    "getPayResult" : function() {
      return this.result["pay"];
    },
    "on" : function(key, callback) {
      var callbacks = this.callbacks[key];
      if(!callbacks) {
        this.callbacks[key] = callbacks = $.Callbacks("unique");
      }
      callbacks.add(callback);
    },
    "trigger" : function(key, params) {
      var args = Array.prototype.slice.apply(arguments);
      var key = args.shift(0);
      args.push(this);
      var callbacks = this.callbacks[key];
      if(!yicai.isUndefined(callbacks)) {
        callbacks.fireWith(this, args).fired();
      }
    },
    "render" : function() {
      if(!yicai.isUndefined(this._render)) {
        return this._render;
      }
      this._render = new Pay.Render(this);
      return this._render;
    },
    "event" : function() {
      if(!yicai.isUndefined(this._event)) {
        return this._event;
      }
      this._event = new Pay.PageEvent(this);
      return this._event;
    },
    "getOrderPrice" : function() {
      var toPayResult = this.getToPayResult();
      if(yicai.isSuccess(toPayResult)) {
        return toPayResult.data.od["o_a"];
      }
      return 0;
    },
    "useHb" : function(hbCode) {
      var _pay = this;
      var payData = _pay.payData;
      var orderPrice = this.getOrderPrice();
      var toPayResult = this.getToPayResult();

      var remainMoney = orderPrice;//剩余待支付金额
      var hbData = payData.setHbCode(hbCode);
      if(!yicai.isUndefined(hbData)) {//可以使用红包
        var hbMoney = getHbMoney(hbData, orderPrice);//红包使用金额
        remainMoney = yicai.subNumber(remainMoney, hbMoney);
      }
      remainMoney = payData.setWallet(remainMoney);
      // 银行支付
      var bankData = getCanUseBankCard(_pay, remainMoney);
      if(!yicai.isUndefined(bankData)) {
        payData.setBankCardId(bankData["b_c_i"], bankData["b_i"], remainMoney);
      } else {
        payData.setBankCardId(null, null, remainMoney);
      }
      //
      payData.refreshJudgeBankMoney();
      _pay.render().refresh();
    },
    "useWallet" : function() {
      var _pay = this;
      var payData = _pay.payData;
      var orderPrice = this.getOrderPrice();
      var toPayResult = this.getToPayResult();
      var remainMoney = orderPrice;//剩余待支付金额

      if(!yicai.isSuccess(toPayResult)) {
        return;
      }
      var userWallet = toPayResult.data["uw"];
      var userTotalBalance = userWallet["tot_c_b"];
      if(userTotalBalance <= 0) {
        message.info("您账户余额为零！");
      }

      payData.setWallet(0);
      remainMoney = orderPrice;
      var hbData = payData.getHbData();
      if(!yicai.isUndefined(hbData)) {
        var hbMoney = getHbMoney(hbData, orderPrice);//红包使用金额
        if(hbMoney >= remainMoney) {//已使用的红包足额支付则取消使用
          payData.setHbCode(null);
        }else{
          remainMoney = yicai.subNumber(remainMoney, hbMoney);
        }
      }
      // 重新使用余额支付剩余金额
      remainMoney = payData.setWallet(remainMoney);
      // 银行支付
      var bankData = getCanUseBankCard(_pay, remainMoney);
      if(!yicai.isUndefined(bankData)) {
        payData.setBankCardId(bankData["b_c_i"], bankData["b_i"], remainMoney);
      } else {
        payData.setBankCardId(null, null, remainMoney);
      }
      //
      payData.refreshJudgeBankMoney();
      //刷新
      _pay.render().refresh();
    },
    "cancelUseWallet" : function() {
      var _pay = this;
      var payData = _pay.payData;
      var orderPrice = this.getOrderPrice();
      var toPayResult = this.getToPayResult();
      var remainMoney = orderPrice;//剩余待支付金额

      payData.setWallet(0);
      // 红包
      var hbData = payData.getHbData();
      if(!yicai.isUndefined(hbData)) {
        var hbMoney = getHbMoney(hbData, orderPrice);//红包使用金额
        if(hbMoney >= remainMoney) {//已使用的红包足额支付则取消使用
          payData.setHbCode(null);
        }else{
          remainMoney = yicai.subNumber(remainMoney, hbMoney);
        }
      }
      // 银行支付
      var bankData = getCanUseBankCard(_pay, remainMoney);
      if(!yicai.isUndefined(bankData)) {
        payData.setBankCardId(bankData["b_c_i"], bankData["b_i"], remainMoney);
      } else {
        payData.setBankCardId(null, null, remainMoney);
      }
      //
      payData.refreshJudgeBankMoney();
      _pay.render().refresh();
    },
    "useBank" : function(bankCardId, bankId) {
      var _pay = this;
      var payData = _pay.payData;
      var orderPrice = this.getOrderPrice();
      var toPayResult = this.getToPayResult();
      var remainMoney = orderPrice;//剩余待支付金额

      // 判断银行卡是否可用
      var bankData = null;
      var bankList = toPayResult.data.ptl || [];
      for(var i = 0; i < bankList.length; i++) {
        if(bankList[i]["b_c_i"] == bankCardId && bankList[i]["b_i"] == bankId) {
          bankData = bankList[i];
          break;
        }
      }
      if(bankData == null || bankData["flag"] != CONSTANTS.PAY_TYPE_FLAG_CAN_USE) {
        return;
      }
      // 红包
      var hbData = payData.getHbData();
      if(!yicai.isUndefined(hbData)) {
        var hbMoney = getHbMoney(hbData, orderPrice);//红包使用金额
        if(hbMoney >= remainMoney) {//已使用的红包足额支付则取消使用
          payData.setHbCode(null);
        }else{
          remainMoney = yicai.subNumber(remainMoney, hbMoney);
        }
      }
      // 余额
      var wallet = payData.getWallet();
      if(wallet > 0) {//如果有使用余额支付、且余额足额、取消使用余额支付
        var tmpRemainMoney = payData.setWallet(remainMoney);
        if(tmpRemainMoney <= 0) {
          payData.setWallet(0)
        } else {
          remainMoney = tmpRemainMoney;
        }
      }
      // 银行卡
      if(isCanUseBankCard(bankData, remainMoney)) {
        payData.setBankCardId(bankCardId, bankId, remainMoney);
      }else{
        bankData = getCanUseBankCard(_pay, remainMoney);
        if(!yicai.isUndefined(bankData)) {
          payData.setBankCardId(bankData["b_c_i"], bankData["b_i"], remainMoney);
        }
      }
      //
      payData.refreshJudgeBankMoney();
      _pay.render().refresh();
    },
    "destroy" : function() {
      this.toPayResult = null;
      this._event = null;
      this._render.destroy();
      this._render = null;
      if(this.paySuccess) {
        this.trigger("out.pay.success");
      }
      this.trigger("destroy");
    },
    "onSuccess" : function(callback) {
      this.on("out.pay.success", callback);
    }
  };

  /**
  * 渲染
  */
  Pay.Render = function(_pay) {
    this._pay = _pay;

  };

  Pay.Render.prototype = {
    "_init" : function() {
      var _pay = this._pay;
      // 初始化数据
      var data = _pay.getToPayResult().data || {};
      var orderInfo = data.od || {};
      var hbList = data.cl || [];
      var userWallet = data.uw || {};
      var payTypeList = data.ptl || [];

      var $payOrder = $("#payOrder");
      if($payOrder.length > 0) {
        var oldPay = $payOrder.data(CONSTANTS.PAY_DATA_KEY);
        if(!yicai.isUndefined(oldPay) && !yicai.isUndefined(oldPay.destroy)) {
          oldPay.destroy();
        }
        $payOrder.remove();
      }
      $payOrder = $('<div id="payOrder" class="pay-order"></div>').appendTo('body');
      $payOrder.data(CONSTANTS.PAY_DATA_KEY, _pay);

      // 支付内容
      var $payContent = $('<div class="pay-content"></div>').appendTo($payOrder);
      $('<div class="pay-head"><strong class="title">确认支付</strong><span class="right"><i class="i-close"></i></span></div>').appendTo($payContent);
      var $payMain = $('<div class="pay-main"></div>').appendTo($payContent);
      // pay-info
      var $payInfo = $('<div class="pay-info"></div>').appendTo($payMain);
      $payInfo.append('<strong>'+orderInfo["l_n"]+'<strong>').append('<i>-</i>').append('<em>' + orderInfo["l_i"] + '期</em>').append('<i>-</i>')
      $payInfo.append('<em>' + orderInfo["b_t_n"] + '</em>').append('<span class="price"><em>￥'+ orderInfo["o_a"] + '</em></span>');
      // pay-hb 红包
      var $payHb = $('<div class="pay-hb"></div>').appendTo($payMain);
      $payHb.append('<div class="pay-hb-default"id="payHB"><span class="title">红包</span><span class="des">不使用红包</span><span class="right"><em class="price">￥0.00</em></span><i class="i-arrow"></i></div>');
      var $payHbMore = $('<div class="pay-hb-more" style="display: none;"></div>').appendTo($payHb);
      if(hbList.length == 0) {
        $payHbMore.append('<div class="pay-hb-no"><i></i><span>没有可用红包！</span></div>');
      } else {
        var $ul = $('<ul class="pay-list" id="hbList"></ul>').appendTo($payHbMore);
        var $li = $('<li class="pay-list-item selected"><span>不使用红包</span><span class="right"><i class="i-radio"></i></span></li>');
        $li.appendTo($ul);
        $.each(hbList, function(i, hb) {
          $li = $('<li class="pay-list-item"></li>').appendTo($ul);
          $li.data(CONSTANTS.PAY_DATA_KEY, hb["r_c"]);
          $li.append('<span>' + hb["r_n"] + '</span>');
          if(hb.r_s == CONSTANTS.HB_CAN_USE) {
            $li.append('<span class="hb-type">&lt;' + CONSTANTS.HB_TYPE[hb["r_t"]] + '&gt;</span>');
            $li.append('<span class="right"><em class="hb-time">' + hb["r_r"] + '</em><i class="i-radio"></i></span>');
          } else {
            $li.append('<span class="hb-tip">' + hb["u_t"] + '</span>');
            $li.append('<span class="hb-type">&lt;' + CONSTANTS.HB_TYPE[hb["r_t"]] + '&gt;</span>');
            $li.append('<span class="right"><em class="hb-time">有效期至' + hb["o_t_t"] + '</em></span>');
          }
        });
      }
      // pay-methods 其它支付渠道
      {
        var $payMethods = $('<div class="pay-methods"></div>').appendTo($payMain);
        $payMethods.append('<div class="pay-methods-default"id="payMethods"><span class="title">支付方式</span><div class="des"><div class="des-list"><p class="des-item" style="display:none"><em>余额支付</em><em class="price"></em></p><p class="des-item" style="display:none"><em></em><em class="price"></em></p></div></div><span class="right"><i class="i-arrow"></i></span></div>');
        var $payMethodMore = $('<div class="pay-methods-more" style="display: none;"><div>').appendTo($payMethods);
        var $ul = $('<ul class="pay-list" id="methodsList"></ul>').appendTo($payMethodMore);
        //账户余额
        var $li = $('<li class="pay-list-item"><span class="methods-icon"><i class="i-count"></i></span><span class="methods-des">' + userWallet["p_n"] + '￥' + userWallet["tot_c_b"] + '</span><span class="right"><i class="i-checkbox"></i></span></li>');
        $li.appendTo($ul);
        //
        $.each(payTypeList, function(i, payType){
          $li = $('<li class="pay-list-item"></li>').appendTo($ul);
          $li.data(CONSTANTS.PAY_DATA_KEY, payType["b_c_i"]);
          $li.data(CONSTANTS.PAY_BANK_ID, payType["b_i"]);
          var $span = $('<span class="methods-icon"></span>');
          if(payType["b_lg"] || payType["s_lg"]) {
            $("<img>",{"src":payType["b_lg"] || payType["s_lg"]}).appendTo($span);
          }
          $li.append($span);
          if(payType["flag"] == CONSTANTS.PAY_TYPE_FLAG_CAN_USE) {
            var methodDes = getMethodPayDesc(payType);
            $li.append('<span class="methods-des">' + methodDes + '</span>');
            $li.append('<span class="fr"></span>');
            $li.append('<span class="right"><i class="i-radio"></i></span>'); 
          }else{
            $li.addClass("pay-methods-no");
            $li.append('<span class="methods-des">' + payType["b_n"] + '</span>');
            var rs = payType["r_s"];
            if(payType["flag"] == CONSTANTS.PAY_TYPE_FLAG_PAUSE) {
              rs = "正在维护，客官请使用其他支付"; 
            }
            $li.append('<span class="fr">' + rs + '</span>');
            $li.append('<span class="right"></span>');
          }
        });
        $ul.append('<li class="pay-list-item add-bankcard"style="display: block;"><span class="methods-icon"><i class="i-addCard"></i></span><span class="methods-des">添加银行卡</span></li>');
      }
      // pay-confirm
      $payMain.append('<div class="pay-confirm"><div class="pay-btn"><a id="btnConfirm">确认付款</a></div><div class="pay-tip"><input type="checkbox" /><span></span></div><div class="pay-time"></div></div>');
      // 扫码
      $payOrder.append('<div class="pay-qrcode" style="display: none;"><i class="i-close"></i><div class="pay-title">请用手机<em class="green"></em>扫码支付<em class="red"></em>元</div><div class="pay-code"><img src="' + baseData.contextPath +'/resources/img/newcard.png"/><span>操作指引：</span><p></p></div><div class="pay-time"></div></div>');
      // 支付成功提示
      $payOrder.append('<div class="pay-success" style="display: none;"><i class="i-close"></i><div class="pay-head">温馨提示</div><div class="pay-body"><i class="i-success"></i><span class="text"><b>恭喜你，已支付成功！</b>祝您中大奖。</span></div><div class="pay-bottom"><a class="btn-grey">查看方案详情</a><a class="btn-blue">再次购买</a></div></div>');
      // 表单跳转
      $payOrder.append('<div id="zhifu-content" style="display: none;"></div>').hide();
      //
      $("body").addClass("pay-overflow");
      this._pay.$element = $payOrder;
      return $payOrder;
    },
    "destroy": function() {
      this._pay.$element.remove();
      $("body").removeClass("pay-overflow");
    },
    // 使用红包、余额、银行(QQ、微信等三方支付)支付后调用，重新渲染
    "refresh" : function() {
      var $payOrder = this._pay.$element;
      var _pay = this._pay;
      // 红包
      var hbData = _pay.payData.getHbData();
      var $payHb = $payOrder.find(".pay-hb-default");
      var $payHbList = $payOrder.find(".pay-hb-more .pay-list");
      $payHbList.find(".pay-list-item.selected").removeClass("selected");
      if(yicai.isUndefined(hbData)) {//不使用红包
        var toPayResult = _pay.getToPayResult();
        var hbList = toPayResult.data.cl || [];
        if(hbList.length > 0) {
          $payHb.find(".des").html("不使用红包");
          $payHb.find(".right").empty().html('<em class="price">￥0.00</em>');
          $payHbList.find(".pay-list-item:eq(0)").addClass("selected");
        } else { 
          $payHb.find(".des").html("");
          $payHb.find(".right").empty().html('<em class="c999">无可用红包</em>');
        }
      } else {
        $payHb.find(".des").html(hbData["r_n"] + '<em>&lt;' + CONSTANTS.HB_TYPE[hbData["r_t"]] + '&gt;</em>');
        $payHb.find(".right").empty().html('<em>' + hbData["r_r"] + '</em>');
        $.each($payHbList.find(".pay-list-item"), function() {
          var hbCode = $(this).data(CONSTANTS.PAY_DATA_KEY);
          if(hbData["r_c"] == hbCode) {
            $(this).addClass("selected");
            return false;
          }
        });
      }
      // 余额
      var wallet = _pay.payData.getWallet();
      var $payWallet = $payOrder.find(".pay-methods-default .des-list .des-item:eq(0)");
      var $payWalletItem = $payOrder.find(".pay-methods-more .pay-list .pay-list-item:eq(0)");
      if(wallet == 0) {//不使用余额
        $payWallet.find("em.price").html("￥0.00");
        $payWallet.hide();
        $payWalletItem.removeClass("selected");
      } else {
        $payWallet.find("em.price").html("￥" + wallet);
        $payWallet.show();
        $payWalletItem.addClass("selected");
      }
      // 银行
      var bankData = _pay.payData.getBankData();
      var $payBank = $payOrder.find(".pay-methods-default .des-list .des-item:eq(1)");
      var $payBankList = $payOrder.find(".pay-methods-more .pay-list");
      $payBankList.find(".pay-list-item:gt(0).selected").removeClass("selected");
      if(yicai.isUndefined(bankData)) {
        var orderPrice = _pay.getOrderPrice();
        if(wallet == 0 && yicai.subNumber(orderPrice, _pay.payData.getHbPayMoney()) >= 0) {
          $payBank.find("em:first").html("");
          $payBank.find("em.price").html("请选择支付方式");
          $payBank.show();
        }else{
          $payBank.find("em:first").html("");
          $payBank.find("em.price").html("");
          $payBank.hide();
        }
        $payOrder.find(".pay-confirm .pay-btn a").html("确认付款");
        $payOrder.find(".pay-confirm .pay-tip").hide();
      } else {
        $payBank.find("em:first").html(getMethodPayDesc(bankData));
        $payBank.find("em.price").html("￥" + _pay.payData.bankMoney);
        $payBank.show();
        $.each($payBankList.find(".pay-list-item:gt(0)"), function() {
          var bankCardId = $(this).data(CONSTANTS.PAY_DATA_KEY);
          var bankId = $(this).data(CONSTANTS.PAY_BANK_ID);
          if(bankData["b_c_i"] == bankCardId && bankData["b_i"] == bankId) {
            $(this).addClass("selected");
            return false;
          }
        });
        // 快捷支付
        var $payTip = $payOrder.find(".pay-confirm .pay-tip");
        if(bankData["b_t"] == 1 || bankData["b_t"] == 2) {
          if(bankData["o_b"] == 0) {
            $payTip.find("span").html("使用快捷支付，便捷的快速支付方式");
          } else {
            $payTip.find("span").html("使用网银支付，享受安全无忧的支付方式");
          }
          $payTip.show();
        }else{
          $payTip.hide();
        }
        $payOrder.find(".pay-confirm .pay-btn a").html(bankData["b_n"] +"&nbsp;&nbsp;&nbsp;支付" + _pay.payData.bankMoney + "元");
      }
      var canUseBankCount = 0;
      $.each($payBankList.find(".pay-list-item:gt(0):not(.add-bankcard)"), function() {
        var bankCardId = $(this).data(CONSTANTS.PAY_DATA_KEY);
        var bankId = $(this).data(CONSTANTS.PAY_BANK_ID);
        var toPayBankData = _pay.getToPayBankData(bankId, bankCardId);
        if(yicai.isUndefined(toPayBankData) || toPayBankData["flag"] != CONSTANTS.PAY_TYPE_FLAG_CAN_USE) {
          return true;
        }
        if(_pay.payData.judgeBankMoney < toPayBankData["m_l"]) {//小于最小支付金额
          $(this).find(".methods-des").html(toPayBankData["b_n"]);
          var rs = "满" +  toPayBankData["m_l"] + "元以上订单可使用"
          $(this).find(".fr").html(rs).show();
          $(this).addClass("pay-methods-no");
          $(this).find(".right").hide();
        }else if(_pay.payData.judgeBankMoney > toPayBankData["t_l"]){//超过最大支付金额
          $(this).find(".methods-des").html(toPayBankData["b_n"]);
          var rs = "金额超" +  toPayBankData["t_l"] + "元建议使用银行卡支付"
          $(this).find(".fr").html(rs).show();
          $(this).addClass("pay-methods-no");
          $(this).find(".right").hide();
        }else{
          canUseBankCount++;
          $(this).find(".methods-des").html(getMethodPayDesc(toPayBankData));
          $(this).find(".fr").html("").hide();
          $(this).removeClass("pay-methods-no");
          $(this).find(".right").show();
        }
      });
      if(canUseBankCount == 0) {
        $payBankList.find(".add-bankcard").show();
      }else{
        $payBankList.find(".add-bankcard").hide();
      }
      _pay.event()._bindPayBank();
    },
    "payTime" : function() {
      var payTime = this._pay.payTime;
      var hour = Math.floor(payTime/3600);
      var minute = Math.floor((payTime-hour*3600)/60);
      var second = payTime-hour*3600-minute*60;
      var html = "订单";
      if(hour > 0) {
        html += '<em>' + yicai.lpad(hour) + '</em>小时';
      }
      if(minute > 0 || hour > 0) {
        html += '<em>' + yicai.lpad(minute) + '</em>分';
      }
      html += '<em>' + yicai.lpad(second) + '</em>秒后截止投注';
      this._pay.$element.find(".pay-time").html(html);
    },
    "showSuccess" : function() {
      var $payOrder = this._pay.$element;
      $payOrder.find(">div:not(.pay-success)").hide();
      $payOrder.find(">.pay-success").show();
      $payOrder.find(".pay-success .pay-bottom a:eq(0)").prop("href", baseData.webCpUrl + "/order/" + this._pay.orderCode);
    },
    "showQrcode" : function(){
      var $payOrder = this._pay.$element;
      var $payQrcode = $payOrder.find(".pay-qrcode")
      $payOrder.find(">div:not(.pay-qrcode)").hide();
      var payResult = this._pay.getPayResult();
      var $title = $payQrcode.find(".pay-title")
      var channelType = CONSTANTS.CHANNEL_TYPE[payResult.data["channel"]];
      $title.find("em:eq(0)").html(channelType.name).css("color", channelType.color);
      $title.find("em:eq(1)").html(this._pay.payData.bankMoney || 0);
      $payQrcode.find("img").prop("src", "data:image/png;base64," + payResult.data["qrStream"]);
      $payQrcode.find(".pay-code p").append(channelType.tips.join("<br>"));
      $payOrder.find(">.pay-qrcode").show();
    }
  }

  /**
  * 事件(页面元素事件)
  */
  Pay.PageEvent = function(_pay) {
    this._pay = _pay;
  }

  Pay.PageEvent.prototype = {
    "_init" : function() {
      var _this = this;
      for(var key in _this){
        var obj = _this[key];
        if(key.indexOf("_bind") == 0 && $.isFunction(obj)) {
          obj.apply(_this);
        }
      }
    },
    "_bindHb" : function() {
      var $payOrder = this._pay.$element;
      //红包
      $payOrder.find(".pay-hb-default").click(function() {
        //红包支付显示隐藏
        var $this =  $(this);
        var $arrow = $this.find(".i-arrow").toggleClass("active");
        var $payHbMore = $payOrder.find(".pay-hb-more");
        if($payHbMore.is(":hidden")){
          $payHbMore.show();
        }else{
          $payHbMore.hide();
        }
        var $payMethodMore = $payOrder.find(".pay-methods-more");
        if($payMethodMore.is(":visible")){
          $payMethodMore.hide();
          $payOrder.find(".pay-methods-default .i-arrow").removeClass("active");
        }
      });
    },
    "_bindWalletAndBank" : function() {
      // 余额和银行支付显示隐藏
      var $payOrder = this._pay.$element;
      $payOrder.find(".pay-methods-default").click(function() {
        var $this =  $(this);
        var $arrow = $this.find(".i-arrow").toggleClass("active");
        var $payMethodMore = $payOrder.find(".pay-methods-more");
        if($payMethodMore.is(":hidden")) {
          $payMethodMore.show();
        }else{
          $payMethodMore.hide();
        }
        var $payHbMore = $payOrder.find(".pay-hb-more");
        if($payHbMore.is(":visible")) {
          $payHbMore.hide();
          $payOrder.find(".pay-hb-default .i-arrow").removeClass("active");
        }
      });
    },
    "_bindPayHb" : function(){
      var _pay = this._pay;
      var $payOrder = this._pay.$element;
      //使用(不使用)红包
      $payOrder.find(".pay-hb-more .pay-list li:has(.i-radio)").click(function() {
        if(!$(this).hasClass("selected")) {
          var hbCode = $(this).data(CONSTANTS.PAY_DATA_KEY);
          _pay.useHb(hbCode);
        }
        $payOrder.find(".pay-hb-more:visible").hide();
      });
    },
    "_bindPayWallet" : function() {
      var _pay = this._pay;
      var $payOrder = this._pay.$element;
      //使用(不使用)余额
      $payOrder.find(".pay-methods-more .pay-list li:eq(0)").click(function() {
        if($(this).hasClass("selected")) {
          _pay.cancelUseWallet();
        }else{
          _pay.useWallet();
        }
        $payOrder.find(".pay-methods-more:visible").hide();
      });
    },
    "_bindPayBank" : function() {
      var _pay = this._pay;
      var $payOrder = this._pay.$element;
      // 使用 银行支付
      $payOrder.find(".pay-methods-more .pay-list li:gt(0):not(.add-bankcard)").unbind("click");
      $payOrder.find(".pay-methods-more .pay-list li:gt(0):not(.add-bankcard,.pay-methods-no)").click(function() {
        if(!$(this).hasClass("selected")) {
          var bankCardId = $(this).data(CONSTANTS.PAY_DATA_KEY);
          var bankId = $(this).data(CONSTANTS.PAY_BANK_ID);
          _pay.useBank(bankCardId, bankId);
        }
        $payOrder.find(".pay-methods-more:visible").hide();
      });
    },
    "_bindPayBtn" : function() {
      var _pay = this._pay;
      var $payOrder = this._pay.$element;
      $payOrder.find(".pay-confirm .pay-btn").click(function() {
        if(_pay.isPaying) {
          return;
        }
        _pay.isPaying = true;
        _pay.pay();
      });
    },
    "_bindClose" : function() {
      var _pay = this._pay;
      var $payOrder = this._pay.$element;
      $payOrder.find(".i-close").click(function() {
        _pay.destroy();
      });
      $payOrder.find(".pay-success .pay-bottom a:eq(1)").click(function(){
        _pay.destroy();
      });
    },
    "_bindAddBankCard" : function() {
      var _pay = this._pay;
      $(".add-bankcard").click(function() {
        window.location.href = baseData.webCpUrl + "/update/card/" + _pay.orderCode;
      });
    }
  }

  /**
  * 跳转支付成功回调
  */
  var onToPaySuccess = function(_pay) {
    var $payOrder = _pay.render()._init();
    _pay.event()._init();
    _pay.render().refresh();
    $payOrder.show();
    // 初始选择支付方式
    var toPayResult = _pay.getToPayResult();
    var hbCode = getFirstHbCode(toPayResult);
    if(yicai.isUndefined(hbCode)) {
      var userWallet = toPayResult.data["uw"];
      var userTotalBalance = userWallet["tot_c_b"];
      if(userTotalBalance > 0) {
        _pay.useWallet();
      } else {
        _pay.cancelUseWallet();
      }
    } else {
      _pay.useHb(hbCode);
    }
    // 倒计时
    _pay.payTime = toPayResult.data["lpt"];
    var interval = window.setInterval(function() {
      _pay.payTime = _pay.payTime - 1;
      _pay.render().payTime();
      if(_pay.payTime <= 0) {
        _pay.destroy();
        _pay.stopChekPay();
        _pay.trigger("pay.timeout");
      }
    }, 1000)
    _pay.on("destroy", function() {
      window.clearInterval(interval);
    })
    _pay.render().payTime();
  }

  /**
  * 跳转支付失败回调
  */
  var onToPayError = function(_pay) {
    message.info(_pay.getToPayResult().message);
  }

  /**
  * 调用支付请求成功回调
  */
  var onPaySuccess = function(_pay) {
    var $payOrder = _pay.$element;
    var payResult = _pay.getPayResult();
    var data = payResult.data;
    if(data.type == 1 && data.formLink) {// form表单提交跳转
      message.loading("正在跳转到第三方支付...", {close:false});
      var $zhifuContent = $("#zhifu-content");
      $zhifuContent.html(data.formLink);
      $zhifuContent.find("form").submit();
    }else if(data.type == 2 && data.formLink) {// 二维码
      _pay.render().showQrcode();
      _pay.checkPay();
    }else if(data.type == 4 && data.formLink){// 跳转地址
      message.loading("正在跳转到第三方支付...", {close:false});
      window.location.href = data.formLink;
    } else {
      _pay.paySuccess = true;
      _pay.render().showSuccess();
    }
  }

  /**
  * 调用支付请求失败回调
  */
  var onPayError = function(_pay) {
    var btns = [];
    var errorCode = _pay.getPayResult().errorCode;
    if(errorCode == "40133") {//账号未实名认证
      btns.push({
        "id": "ok",
        "callback": function(_message) {
          _message.close();
          window.location.href = baseData.webCpUrl + "/update/realid";
        }
      });
      btns.push({
        "id": "close"
      });
    }
    if(btns.length > 0) {
      message.info(_pay.getPayResult().message, {
        "btns": btns
      });
    } else {
      message.info(_pay.getPayResult().message);
    }
    
  }

  function getMethodPayDesc(bankData) {
    var methodDes = bankData["b_n"];
    if(bankData["b_t"] == 1 || bankData["b_t"] == 2) {
      methodDes += bankData["c_c"] || "";
      methodDes += CONSTANTS.BANK_TYPE[bankData["b_t"]];
      methodDes += " | ";
      methodDes += ['网银','快捷'][bankData["o_b"]]
    }
    return methodDes;
  }

  function getHbMoney(hbPayData, price){
    var hbBalance = 0.00;
    if(hbPayData["r_t"] == CONSTANTS.HB_TYPE_KEY.CJ) {//彩金
      hbBalance = hbPayData["r_b"];
    }else {
      hbBalance = hbPayData["r_v"];
    }
    return hbBalance;
  }

  function isCanUseBankCard(bankData, remainMoney) {
    return !yicai.isUndefined(bankData) && bankData["flag"] == CONSTANTS.PAY_TYPE_FLAG_CAN_USE && remainMoney >= bankData["m_l"] && remainMoney <= bankData["t_l"];
  }

  function getCanUseBankCard(_pay, remainMoney) {
    var toPayResult = _pay.getToPayResult();
    if(remainMoney <= 0) {
      return null;
    }
    //余额不足支付
    var bankData = null;
    var bankList = toPayResult.data.ptl || [];
    for(var i = 0; i < bankList.length; i++) {
      bankData = bankList[i];
      if(isCanUseBankCard(bankData, remainMoney)) {
        return bankData;
      }
    }
    return null;
  }

  function getFirstHbCode(param) {
    var hbList = $.isArray(param) ? param : (param.data.cl || []);
    for(var i = 0; i < hbList.length; i++) {
      if(hbList[i]["r_s"] == CONSTANTS.HB_CAN_USE) {
        return hbList[i]["r_c"];
      }
    }
    return null;
  }

  yicai.pay = function(orderCode) {
    var pay = new Pay();
    pay.toPay(orderCode);
    return pay;
  }
})(jQuery, YiCai)