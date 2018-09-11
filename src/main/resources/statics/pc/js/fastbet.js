(function($, yicai) {
  var user = yicai.user;
  var login = yicai.login;
  var message = yicai.message;
  var pay = yicai.pay;

  var CONSTANTS = {
    "URL" : {
      "FAST" : baseData.apiPath + "/pc/v1.0/operate/fast",
      "ADDORDER" : baseData.apiPath + "/pc/v1.0/order/addOrder",
      "BETTINGMUL": baseData.apiPath + "/pc/v1.0/lottery/bettingMul/"
    },
    "POSITION" : 4,//位置，开奖公告
    "CAllBACK" : {
      "INIT_FAST_SUCCESS" : "init.fast.success",
      "INIT_FAST_ERROR" : "init.fast.error",
      "FAST_TIMEOUT" : "fastbet.timeout",
      "ADDORDER_SUCCESS" : "addorder.success",
      "ADDORDER_ERROR" : "addorder.error"
    },
    "TIME_HUG" : 300,
    "POKER":{1:"A", 2: "2", 3: "3", 4: "4", 5: "5", 6: "6", 7: "7", 8: "8", 9: "9", 10 : "10", 11: "J", 12: "Q", 13: "K"}
  }

  var LotteryChildCategory = {
    "x115" : {//规则 彩种ID+code
      1:{//任八
        "name" : "任八",
        "code" : "08",
        "num" : 8
      },
      2:{//任二
        "name" : "任二",
        "code" : "02",
        "num" : 2
      },
      3:{//任三
        "name" : "任三",
        "code" : "03",
        "num" : 3
      },
      4:{//任四
        "name" : "任四",
        "code" : "04",
        "num" : 4
      },
      5:{//任五
        "name" : "任五",
        "code" : "05",
        "num" : 5
      },
      6:{//任六
        "name" : "任六",
        "code" : "06",
        "num" : 6
      },
      7:{//任七
        "name" : "任七",
        "code" : "07",
        "num" : 7
      },
      8:{//前一
        "name" : "前一",
        "code" : "09",
        "num" : 1
      },
      9:{//前二
        "name" : "前二直选",
        "code" : "11",
        "num" : 2,
        "sort" : false
      },
      10:{//组二
        "name" : "前二组选",
        "code" : "10",
        "num" : 2
      },
      11:{//前三
        "name" : "前三直选",
        "code" : "13",
        "num" : 3,
        "sort" : false
      },
      12:{//组三
        "name" : "前三组选",
        "code" : "12",
        "num" : 3
      }
    },
    "k3":{
      1:{//二不同号
        "name" : "二不同号",
        "code" : "04",
        "num" : 2
      },
      2:{//三同号通选
        "name" : "三同号通选",
        "code" : "07",
        "num" : 3
      },
      3:{//三同号单选
        "name" : "三同号单选",
        "code" : "06",
        "num" : 3,
        "sort" : false
      },
      4:{//三连号通选
        "name" : "三连号通选",
        "code" : "08",
        "num" : 3,
      },
      5:{//三不同号
        "name" : "三不同号",
        "code" : "05",
        "num" : 3,
      },
      6:{//二同号复选
        "name" : "二同号复选",
        "code" : "03",
        "num" : 2,
      },
      7:{//二同号单选
        "name" : "二同号单选",
        "code" : "02",
        "num" : 3,
        "sort" : false
      },
      8:{//和值
        "name" : "和值",
        "code" : "01",
        "num" : 1,
        "min" : 3,
        "max" : 18
      }
    },
    "ssc" : {
      1:{//一星
        "name" : "一星",
        "code" : "08",
        "num" : 1
      }
    },
    "kl10" : {
      1:{//前一数投
        "name" : "前一数投",
        "code" : "01",
        "num" : 1,
        "max" : 18
      },
      2:{//任选二
        "name" : "任选二",
        "code" : "03",
        "num" : 2
      }
    },
    "poker" : {
      1:{//任选二
        "name" : "任选一",
        "code" : "01",
        "num" : 1
      }
    }
  }



  var defaults = {
    "lotteryCode" : 0,
    "element": "#fastbet",
    "joinMark" : "|",//号码连接符号
    "groupJoinMark" : "|", //红蓝号码连接符
    "reloadOnTimeout" : true
  }

  function randomNum(min, max) {
    return Math.floor(Math.random()*(max-min + 1)) + min;
  }

  function getMaxMultiple(endTimeCount, betMultipleModel) {
    var maxMultiple = 999;
    if(betMultipleModel && betMultipleModel.length > 0) {
      var tempEndTime = 0;
      for(var i = 0; i < betMultipleModel.length; i++) {
        var bettingMul = betMultipleModel[i];
        if(bettingMul.endTime <= endTimeCount) {
          maxMultiple = bettingMul.multipleNum;
          break;
        }
      }
    }
    return maxMultiple;
  }

  var Fastbet = function(option){
    this.option = $.extend({}, defaults, option);
    this.callbacks ={};//回调
    this.fastbetModel ={}; //快投数据模型
    this.unitJackpotMoney = 5000000;//500万
    this.lotteryCode = null;//彩种
    this.lotteryChildCode = null;//子玩法
    this.lotteryChildName = null;//子玩法名称
    this.issueCode = null;//彩期
    this.enable =false;//是否允许购买
    this.unitAmount =2; //单价
    this.rules ={};//球规则;
    this.endTimeCount = 1;
    this.$element = null;
    this.balls = {};
    this.planContent = {};
    this.multiple = 1;//投注倍数
    this.isRandoming = false;
    this.isRandomed = false;
    this.buyType = 1;
    this.isDltAdd = 0;
    this.betMultipleModel = [];
    this.maxMultiple = 999;
  }

  Fastbet.prototype = {
   
    "_init":function() {
      this.lotteryCode = this.option.lotteryCode;
      this.$element = $(this.option.element);
      this.lotteryChildCode= this.option.lotteryChildCode;

      this.on(CONSTANTS.CAllBACK.INIT_FAST_SUCCESS, Callbacks.INIT_FAST_SUCCESS);
      this.on(CONSTANTS.CAllBACK.INIT_FAST_ERROR, Callbacks.INIT_FAST_ERROR);
      this.on(CONSTANTS.CAllBACK.ADDORDER_SUCCESS, Callbacks.ADDORDER_SUCCESS);
      this.on(CONSTANTS.CAllBACK.ADDORDER_ERROR, Callbacks.ADDORDER_ERROR);
      this.on(CONSTANTS.CAllBACK.FAST_TIMEOUT, Callbacks.FAST_TIMEOUT);
      this.initFastbet();
    },
    "initFastbet":function() {
      var _this = this;
      var data = {
        "lotteryCode":_this.lotteryCode,
        "position":CONSTANTS.POSITION,
        "t": new Date().getTime()
      }
      $.ajax({
        "url" : CONSTANTS.URL.FAST,
        "type" : "GET",
        "headers" : yicai.CONSTANTS.HEADERS,
        "data" : data,
        "dataType": "json",
        "success" : function(result) {
          var isSuccess = yicai.isSuccess(result);
          if(isSuccess && result.data && result.data.length > 0) {
            _this.fastbetModel = result.data[0];
            _this.trigger(CONSTANTS.CAllBACK.INIT_FAST_SUCCESS, result);
          } else if(isSuccess && (!result.data || result.data.length ==0)) {
            _this.trigger(CONSTANTS.CAllBACK.INIT_FAST_ERROR, result);
          } else if(!isSuccess && result.errorCode == 10008){//数据不存在
            _this.trigger(CONSTANTS.CAllBACK.INIT_FAST_ERROR, result);
          } else {
            message.info(result.message);
          }
        }
      });
    },
    "setRules" : function() {

    },
    "random" : function() {
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
    "render" : function() {
      if(this._render) {
        return this._render;
      }
      this._render = this._createRender();
      return this._render;
    },
    "_createRender": function() {
      return new Fastbet.Render(this);
    },
    "calAmount": function(){
      if(!this.isRandomed) {
        return 0;
      }
      if(this.isDltAdd == 1) {
        this.unitAmount = 3;
      }else{
        this.unitAmount = 2;
      }
      return this.unitAmount * this.multiple;
    },
    "formatBall" : function(ball, max) {
      return max > 10 ? yicai.lpad(ball, 2) :  ball.toString();
    },
    "submit" : function() {
      var _this = this;
      if(_this.isRandoming) {//正在摇号
        return;
      }
      if(!_this.isRandomed) {
        message.info("请先摇一注号码再投注！", {
          "btns" : [{
            "id" : "ok",
            "name" : "摇一注",
            "callback" : function(message) {
              message.close();
              _this.random();
            }
          },{
            "id": "close"
          }]
        });
        return;
      }
      if(this.endTimeCount <= 0) {
        message.info("彩期销售已截止！");
        return;
      }
      var amount = _this.unitAmount * _this.multiple;
      if(isNaN(amount) || amount <= 0) {
        return;
      }
      var token = user.getToken();
      if(!token) {
        login().onSuccess(function() {
          _this.submit();
        });
        return;
      }
      if(_this.multiple > _this.maxMultiple) {
        message.info("当前您的倍数已超出最高投注倍数", {
          btns:[
            {
              "id": "ok",
              "callback": function(_message) {
                _this.multiple = _this.maxMultiple;
                _this.render().multiple();
                _message.close();
              }
            },
            {
              "id":"close"
            }
          ]
        });
        return;
      }
      var data = {
        "channelId" : yicai.CONSTANTS.HEADERS.cId,
        "isDltAdd" : _this.isDltAdd,
        "lotteryCode" : _this.lotteryCode,
        "lotteryIssue" : _this.issueCode,
        "buyType" : _this.buyType,
        "multipleNum" : _this.multiple,
        "orderAmount" : _this.calAmount(),
        "orderDetailList" : [{
          "amount" : _this.unitAmount,
          "buyNumber" : 1,//单个方案投注注数
          "codeWay" : 2,//1：手选；2：机选；3：上传
          "contentType" : 1, //玩法 根据每个彩种不同， 1：单式；2：复式；3：胆拖；4：混合
          "lotteryChildCode" : _this.lotteryChildCode,//子玩法
          "multiple": 1,//  单个方案投注倍数
          "planContent" : _this.planContent//投注内容
        }],
        "token" : token,
        "platform" : yicai.CONSTANTS.HEADERS.pId
      }

      var _this = this;
      $.ajax({
        "url": CONSTANTS.URL.ADDORDER,
        "type": "POST",
        "contentType": yicai.CONSTANTS.API_CONTENT_TYPE,
        "headers" : yicai.CONSTANTS.HEADERS,
        "data": JSON.stringify(data),
        "dataType": "json",
        "success": function(result) {
          if(yicai.isSuccess(result)) {
            _this.trigger(CONSTANTS.CAllBACK.ADDORDER_SUCCESS, result.data);
          } else {
            _this.trigger(CONSTANTS.CAllBACK.ADDORDER_ERROR, result);
          }
        },
        "error": function(XMLHttpRequest, textStatus, errorThrown) {
          message.info(errorThrown);
        }
      });
    }
  }

  // 页面渲染
  Fastbet.Render = function(fastbet){
    this._fastbet = fastbet;
  }
  Fastbet.Render.prototype = {
    "_init": function(){
      var _fastbet = this._fastbet;
      var $element = _fastbet.$element;
      var fastbetModel = _fastbet.fastbetModel;
      //奖池
      if(!yicai.isUndefined(fastbetModel.jackpotAmount)) {
        $element.find(".jackpot-num").text(Math.floor(fastbetModel.jackpotAmount/_fastbet.unitJackpotMoney));
        $element.find(".jackpot-money").text(yicai.formatMoney(_fastbet.unitJackpotMoney));
      }
      //彩期
      $element.find(".issue-code").text(_fastbet.issueCode);
      //子玩法
      $element.find(".lottoy-child-name").text(_fastbet.lotteryChildName);
      this._initLottoBox();
      //倒计时
      this.showSaleEndTime();
      //投注倍数
      this.multiple();
      // 加载页面事件
      new Fastbet.PageEvent(_fastbet);
    },
    "_initLottoBox" : function() {

    },
    "showSaleEndTime" : function() {
      var endTimeCount = this._fastbet.endTimeCount;
      var day = Math.floor(endTimeCount/(24*3600));
      var hour = Math.floor((endTimeCount-day*24*3600)/3600);
      var minute = Math.floor((endTimeCount-day*24*3600-hour*3600)/60);
      var second = endTimeCount-day*24*3600-hour*3600-minute*60;
      var html = '';
      if(day > 0) {
        html += day + "天";
      }
      html += yicai.lpad(hour) + "小时";
      html += yicai.lpad(minute) + "分";
      html += yicai.lpad(second) + "秒"
      this._fastbet.$element.find(".end-time").html(html);
    },
    "show" : function() {
      this._fastbet.$element.show();
    },
    "hide" : function() {
      this._fastbet.$element.hide();
    },
    "price" : function(){
      var $element = this._fastbet.$element;
      if($element.find(".addbet input[type=checkbox]").is(":checked")){
        this._fastbet.isDltAdd = 1;
      } else {
        this._fastbet.isDltAdd = 0;
      }
      $element.find(".total-price").text(this._fastbet.calAmount());
    },
    "multiple" : function() {
      var _fastbet = this._fastbet;
      var $minus = _fastbet.$element.find(".multiple-div .icon-minus");
      var $add = _fastbet.$element.find(".multiple-div .icon-add");
      var $input = _fastbet.$element.find(".multiple-div input.multiple-input");
      $input.val(_fastbet.multiple);
      if(_fastbet.multiple <= 1){
        $minus.addClass("false");
        $add.removeClass("false");
      } else if(_fastbet.multiple >= _fastbet.maxMultiple) {
        $minus.removeClass("false");
        $add.addClass("false");
      } else {
        $minus.removeClass("false");
        $add.removeClass("false");
      }
      this.price();
    }
  }

  
  // 回调处理
  var Callbacks = {
    "INIT_FAST_SUCCESS" : function(result) {
      var fastbetModel = this.fastbetModel;
      this.issueCode = fastbetModel.issueCode;
      this.enable = true;
      var endTimeCount = Math.floor((yicai.getTime(fastbetModel.saleEndTime)-result.serviceTime)/1000);
      this.endTimeCount = endTimeCount < 0 ? 0 : endTimeCount;
      this.multiple = fastbetModel.multiple || 1;
      this.setRules();

      this.render()._init();
      this.render().show();

      var _this  = this;
      if(endTimeCount > 0) {
        this.timer = window.setInterval(function() {
          _this.endTimeCount--;
          _this.render().showSaleEndTime();
          _this.maxMultiple = getMaxMultiple(_this.endTimeCount, _this.betMultipleModel);
          if(_this.endTimeCount <= 0) {
            window.clearInterval(_this.timer);
            _this.trigger(CONSTANTS.CAllBACK.FAST_TIMEOUT);
          }
        },1000);
        _this.render().showSaleEndTime();
        //查询投注限制
        $.ajax({
          "url" : CONSTANTS.URL.BETTINGMUL + _this.lotteryCode,
          "type" : "GET",
          "headers" : yicai.CONSTANTS.HEADERS,
          "dataType" : "json",
          "success" : function(_result) {
            if(yicai.isSuccess(_result)) {
              _this.betMultipleModel = _result.data;
            }
          }
        });
      }
      this.random();
    },
    "INIT_FAST_ERROR" : function(result) {
      this.render().hide();
    },
    "ADDORDER_SUCCESS" : function(data) {
      var _this = this;
      pay(data["oc"]).onSuccess(function() {
        _this.random();
      });
    },
    "ADDORDER_ERROR" : function(result) {
      var errorCode = result.errorCode;
      if(errorCode == 40118) {//重新登录
        login();
        return;
      } else {
        message.info(result.message);
      }
    },
    "FAST_TIMEOUT" : function() {
      if(this.option.reloadOnTimeout) {
        window.setTimeout(function() {
          window.location.reload();
        }, 1500);
      }      
    }
  }

  Fastbet.PageEvent = function(fastbet){
    this._fastbet = fastbet;
    this._init();
  };
  Fastbet.PageEvent.prototype = {
    "_init" : function() {
      var _this = this;
      for(var key in _this){
        var obj = _this[key];
        if(key.indexOf("_bind") == 0 && $.isFunction(obj)) {
          obj.apply(_this);
        }
      }
    },
    "_bindRandom" : function() {
      var _fastbet = this._fastbet;
      this._fastbet.$element.find(".handle-random, .handle-right").click(function() {
        _fastbet.random();
      });
    },
    "_bindSubmit" : function() {
      var _fastbet = this._fastbet;
      this._fastbet.$element.find(".handle-submit").click(function() {
        _fastbet.submit();
      });
    },
    "_bindMultiple" : function() {
      var _fastbet = this._fastbet;
      //倍数
      var $minus = _fastbet.$element.find(".multiple-div .icon-minus");
      var $add = _fastbet.$element.find(".multiple-div .icon-add");
      var $input = _fastbet.$element.find(".multiple-div input.multiple-input");
      _fastbet.$element.find(".multiple-div .icon-minus").click(function(){
        var $this = $(this);
        if($this.is(".false")) {
          return;
        }
        _fastbet.multiple--;
        _fastbet.render().multiple();
      });
      _fastbet.$element.find(".multiple-div .icon-add").click(function(){
        var $this = $(this);
        if($this.is(".false")) {
          return;
        }
        _fastbet.multiple++;
        _fastbet.render().multiple();
      });
      _fastbet.$element.find(".multiple-div input.multiple-input").keyup(function(){
        var val = $(this).val();
        val = val.replace(/[^\d]/g,'');
        if(val == "") {
          val = 1;
        }
        if(val < 1) {
          val = 1;
        } else if(val > _fastbet.maxMultiple) {
          val = _fastbet.maxMultiple;
        }
        _fastbet.multiple = val;
      });
      _fastbet.$element.find(".multiple-div input.multiple-input").blur(function(){
        _fastbet.render().multiple();
      });
    },
    "_bindDltAdd" : function() {
      var _fastbet = this._fastbet;
      _fastbet.$element.find(".addbet input[type=checkbox]").click(function() {
        _fastbet.render().price();
      });
    }
  }

  //************* 数字类快投
  var FastbetNum = function(option) {
    Fastbet.call(this, option);
  }
  FastbetNum.prototype = new Fastbet();
  FastbetNum.prototype.setRules = function() {
    this.rules = [
      {"type": "red", "min" : 1, "max": 33, "num": 6, "repeat" : false},
      {"type":"blue", "min" : 1, "max": 16, "num": 1, "repeat" : false}
    ];
  }
  FastbetNum.prototype._createRender = function() {
    return new FastbetNum.Render(this);
  }
  FastbetNum.prototype.random = function(){
    if(this.isRandoming) {
      return;
    }
    var _this = this;
    _this.balls = {};
    this.isRandoming = true;
    this.isRandomed = false;
    this.planContent = "";
    var rules = this.rules;
    var planArr = [];
    $.each(rules, function(i, rule){
      var ballArr = [];
      if(!rule.repeat) {
        var flags = {};
        for(var i = 0; i < rule.num; i++) {
          var ball ;
          while(flags[ball = randomNum(rule.min, rule.max)]){
          }
          flags[ball] = true;
          ballArr.push(ball);
        }
      } else {
        for(var i = 0; i < rule.num; i++) {
          ballArr.push(randomNum(rule.min, rule.max));
        }
      }
      var sortFlag = yicai.isUndefined(rule.sort) ? !rule.repeat : rule.sort;
      if(sortFlag) {
        ballArr.sort(function(next,prev) {
          return next - prev;
        });
      }
      for(var i = 0; i < ballArr.length; i++) {
        ballArr[i] = _this.formatBall(ballArr[i], rule.max, i);
      }
      _this.balls[rule.type] = ballArr;
    });
    this.buildPlanContent();
    this.render().rotate();//旋转动画
  }
  FastbetNum.prototype.buildPlanContent = function() {
    var balls = this.balls;
    var planArr = [];
    for(var type in balls){
      var ballArr = balls[type];
      planArr.push(ballArr.join(this.option.joinMark));
    }
    this.planContent = planArr.join(this.option.groupJoinMark);
  }

  FastbetNum.Render = function(fastbet) {
    Fastbet.Render.call(this, fastbet);
  }
  FastbetNum.Render.prototype = new Fastbet.Render();
  FastbetNum.Render.prototype._initLottoBox = function() {
    var $box = this._fastbet.$element.find(".draw-lotto-box .box-in");
    var rules = this._fastbet.rules;
    $.each(rules, function(i, rule) {
      var balls=[]
      for(var i=1;i< rule.num+1;i++){
        balls[i]=i
      }
      var $ul = $('<ul class="box-in-balls"></ul>');
      for (var i in balls) {
        $('<li><span class="ui-icon ' + rule.type + 'Ball-30">-</span></li>').appendTo($ul);
      }
      $('<div class="fl"></div>').append($ul).appendTo($box);
    });
  }
  /**
  * 旋转动画
  */
  FastbetNum.Render.prototype.rotate = function() {
    var _fastbet = this._fastbet;
    if(!_fastbet.balls) {
      return;
    }
    var $element =_fastbet.$element;
    // 手柄
    $element.find(".handle-right").addClass("yaohao");
    window.setTimeout(function() {
      $element.find(".handle-right").removeClass("yaohao");
    }, 800);
    // 球旋转
    var $box = this._fastbet.$element.find(".draw-lotto-box .box-in");
    var balls = _fastbet.balls;
    var num = 0;
    var time = 0;
    var i = 0;
    for(var type in balls){
      var ballArr = balls[type];
      var $ul = $box.find("ul:eq(" + i + ")");
      for(var j = 0;j < ballArr.length; j++) {
        time = 1000 + (num++ * CONSTANTS.TIME_HUG);
        (function($ul, j) {
          var $li = $ul.find("li:eq(" + j + ")");
          $li.find("span").text(ballArr[j]);
          if($.browser.msie && $.browser.version <= 9) {
            //处理IE9动画
            var deg = 0;
            var timer  = window.setInterval(function() {
              deg+=9;
              $li.css({"transform":"rotate(" + deg + "deg)"});
              if(deg>=360){
                deg = 0;
              }
            }, 5);
            window.setTimeout(function() {
              window.clearInterval(timer);
              $li.css({"transform":"rotate(0deg)"});
            }, time);
          }else{
            $li.addClass("rotatebox");
            window.setTimeout(function() {
              $li.removeClass("rotatebox");
            }, time);
          }          
        })($ul, j)
      }
      i++;
    }
    var _this = this;
    window.setTimeout(function() {
      _fastbet.isRandoming = false;
      _fastbet.isRandomed = true;
      _this.price();
    }, time);
  }
  //************* 扑克类快投 start
  //************* 扑克类快投 end

  //************* 双色球 start
  var FastbetSsq = function(option) {
    option.joinMark = ",";
    FastbetNum.call(this, option);
  }
  FastbetSsq.prototype = new FastbetNum();
  FastbetSsq.prototype.setRules = function() {
    this.rules = [
      {"type": "red", "min" : 1, "max": 33, "num": 6, "repeat" : false},
      {"type":"blue", "min" : 1, "max": 16, "num": 1, "repeat" : false}
    ];
    this.lotteryChildCode = "10001";
    this.lotteryChildName = "普通投注";
  }
  //************* 双色球 end
  //************* 七乐彩 start
  var FastbetQlc = function(option) {
    option.joinMark = ",";
    FastbetNum.call(this, option);
  }
  FastbetQlc.prototype = new FastbetNum();
  FastbetQlc.prototype.setRules = function() {
    this.rules = [
      {"type": "red", "min" : 1, "max": 30, "num": 7, "repeat" : false}
    ];
    this.lotteryChildCode = "10101";
    this.lotteryChildName = "普通投注";
  }
  //************* 七乐彩 end
  //************* 大乐透 start
  var FastbetDlt = function(option) {
    option.joinMark = ",";
    FastbetNum.call(this, option);
  }
  FastbetDlt.prototype = new FastbetNum();
  FastbetDlt.prototype.setRules = function() {
    this.rules = [
      {"type": "red", "min" : 1, "max": 35, "num": 5, "repeat" : false},
      {"type":"blue", "min" : 1, "max": 12, "num": 2, "repeat" : false}
    ];
    this.lotteryChildCode = "10201";
    this.lotteryChildName = "普通投注";
  }
  //************* 大乐透 end
  //************* 排列5 start
  var FastbetPl5 = function(option) {
    FastbetNum.call(this, option);
  }
  FastbetPl5.prototype = new FastbetNum();
  FastbetPl5.prototype.setRules = function() {
    this.rules = [
      {"type": "red", "min" : 0, "max": 9, "num": 5, "repeat" : true}
    ];
    this.lotteryChildCode = "10301";
    this.lotteryChildName = "普通投注";
  }
  //************* 排列5 end
  //************* 排列3 start
  var FastbetPl3 = function(option) {
    FastbetNum.call(this, option);
  }
  FastbetPl3.prototype = new FastbetNum();
  FastbetPl3.prototype.setRules = function() {
    this.rules = [
      {"type": "red", "min" : 0, "max": 9, "num": 3, "repeat" : true}
    ];
    this.lotteryChildCode = "10401";
    this.lotteryChildName = "普通投注";
  }
  //************* 排列3 end
  //************* 福彩3D start
  var FastbetF3d = function(option) {
    FastbetNum.call(this, option);
  }
  FastbetF3d.prototype = new FastbetNum();
  FastbetF3d.prototype.setRules = function() {
    this.rules = [
      {"type": "red", "min" : 0, "max": 9, "num": 3, "repeat" : true}
    ];
    this.lotteryChildCode = "10501";
    this.lotteryChildName = "普通投注";
  }
  //************* 福彩3D end
  //************* 七星彩 start
  var FastbetQxc = function(option) {
    FastbetNum.call(this, option);
  }
  FastbetQxc.prototype = new FastbetNum();
  FastbetQxc.prototype.setRules = function() {
    this.rules = [
      {"type": "red", "min" : 0, "max": 9, "num": 7, "repeat" : true}
    ];
    this.lotteryChildCode = "10701";
    this.lotteryChildName = "普通投注";
  }
  //************* 七星彩 end
  //************* 11选5类 start
  var Fastbet11x5 = function(option) {
    option.joinMark = ",";
    FastbetNum.call(this, option);
  }
  Fastbet11x5.prototype = new FastbetNum();
  Fastbet11x5.prototype.setRules = function() {
    var categoryId = this.fastbetModel.categoryId || 1;
    // 子玩法-->任八:1, 任二:2, 任三:3, 任四:4, 任五:5, 任六:6, 任七:7, 前一:8, 前二:9, 组二:10, 前三:11, 组三:12
    var lotteryChildCategory = LotteryChildCategory.x115[categoryId]
    if(!lotteryChildCategory) {
      lotteryChildCategory = LotteryChildCategory.x115[1];
    }
    if(categoryId == 9 || categoryId == 11) {
      this.option.joinMark = "|";
    }
    this.rules = [
      {"type": "red", "min" : 1, "max": 11, "num": lotteryChildCategory.num, "repeat" : false, "sort": lotteryChildCategory.sort}
    ];
    this.lotteryChildCode = this.lotteryCode  + lotteryChildCategory.code;
    this.lotteryChildName = lotteryChildCategory.name;
  }
  //************* 11选5类 end
  //************* 快3类 start
  var FastbetK3 = function(option) {
    option.joinMark=",";
    FastbetNum.call(this, option);
  }
  FastbetK3.prototype = new FastbetNum();
  FastbetK3.prototype.setRules = function() {//重新快3随机方法
    var categoryId = this.fastbetModel.categoryId || 8;
    // 子玩法-->二不同号:1, 三同号通选:2, 三同号单选:3, 三连号通选:4, 三不同号:5, 二同号复选:6, 二同号单选:7, 和值:8
    var lotteryChildCategory = LotteryChildCategory.k3[categoryId]
    if(!lotteryChildCategory) {
      lotteryChildCategory = LotteryChildCategory.k3[8];
    }
    var min = lotteryChildCategory.min || 1;
    var max = lotteryChildCategory.max || 6;
    var repeat = lotteryChildCategory.repeat || false;
    this.rules = [
      {"type": "red", "min" : min, "max": max, "num": lotteryChildCategory.num, "repeat" : repeat, "sort": lotteryChildCategory.sort}
    ];
    this.lotteryChildCode = this.lotteryCode  + lotteryChildCategory.code;
    this.lotteryChildName = lotteryChildCategory.name;
  }
  FastbetK3.prototype.buildPlanContent = function() {
    var balls = this.balls;
    var newBalls = {};
    var categoryId = this.fastbetModel.categoryId;
    var planContent = "";
    for(var type in balls) {
      var ballArr = balls[type];
      var newBallArr = [];
      if(categoryId == 2) {//三同号通选
        newBallArr = ["*", "*", "*"];
        planContent = "3T";
      } else if(categoryId == 3) {//三同号单选
        newBallArr = [ballArr[0], ballArr[0], ballArr[0]];
        planContent = newBallArr.join("");
      } else if(categoryId == 4) {//三连号通选
        newBallArr = ["*", "*", "*"];
        planContent = "3L";
      } else if(categoryId == 6) {//二同号复选，第一个号码重复
        newBallArr = [ballArr[0], ballArr[0]];
        planContent = newBallArr.join("");
      } else if(categoryId == 7) {//二同号单选， 第一个号码重复
        newBallArr = [ballArr[0], ballArr[0], ballArr[1]];
        planContent = ballArr[0] + "" + ballArr[0] + "#" + ballArr[1];
      } else {//和值、二不同号、三不同号
        newBallArr = ballArr;
        planContent = newBallArr.join(this.option.joinMark);
      }
      newBalls[type] = newBallArr;
    }
    this.balls = newBalls;
    this.planContent = planContent;
  }
  FastbetK3.prototype.formatBall = function(ball, max, index) {
    return ball;
  }
  //************* 快3类 end
  //************* 时时彩类 start
  var FastbetSsc = function(option) {
    option.joinMark = ",";
    FastbetNum.call(this, option);
  }
  FastbetSsc.prototype = new FastbetNum();
  FastbetSsc.prototype.setRules = function() {
    //时时彩目前没有快投配置，使用默认配置
    var categoryId = this.fastbetModel.categoryId || 1;
    // 子玩法-->1:三星直选
    var lotteryChildCategory = LotteryChildCategory.ssc[categoryId]
    if(!lotteryChildCategory) {
      lotteryChildCategory = LotteryChildCategory.ssc[1];
    }
    var max = lotteryChildCategory.max || 9;
    this.rules = [
      {"type": "red", "min" : 1, "max": max, "num": lotteryChildCategory.num, "repeat" : false}
    ];
    this.lotteryChildCode = this.lotteryCode  + lotteryChildCategory.code;
    this.lotteryChildName = lotteryChildCategory.name;
  }
  //************* 时时彩类 end
  //************* 快乐10分类 start
  var FastbetKl10 = function(option) {
    option.joinMark = ",";
    FastbetNum.call(this, option);
  }
  FastbetKl10.prototype = new FastbetNum();
  FastbetKl10.prototype.setRules = function() {
    //快乐10分目前没有快投配置，使用默认配置
    var categoryId = this.fastbetModel.categoryId || 1;
    // 子玩法-->1:选三前值
    var lotteryChildCategory = LotteryChildCategory.kl10[categoryId]
    if(!lotteryChildCategory) {
      lotteryChildCategory = LotteryChildCategory.kl10[1];
    }
    var max = lotteryChildCategory.max || 20;
    this.rules = [
      {"type": "red", "min" : 1, "max": max, "num": lotteryChildCategory.num, "repeat" : false}
    ];
    this.lotteryChildCode = this.lotteryCode  + lotteryChildCategory.code;
    this.lotteryChildName = lotteryChildCategory.name;
  }
  //************* 快乐10分类 end
  //************* 扑克类 start
  var FastbetPoker = function(option) {
    option.joinMark = ",";
    FastbetNum.call(this, option);
  }
  FastbetPoker.prototype = new FastbetNum();
  FastbetPoker.prototype.setRules = function() {
    //扑克目前没有快投配置，使用默认配置
    var categoryId = this.fastbetModel.categoryId || 1;
    // 子玩法-->1:任选二
    var lotteryChildCategory = LotteryChildCategory.poker[categoryId]
    if(!lotteryChildCategory) {
      lotteryChildCategory = LotteryChildCategory.poker[1];
    }
    var max = lotteryChildCategory.max || 13;
    this.rules = [
      {"type": "red", "min" : 1, "max": 13, "num": lotteryChildCategory.num, "repeat" : false}
    ];
    this.lotteryChildCode = this.lotteryCode  + lotteryChildCategory.code;
    this.lotteryChildName = lotteryChildCategory.name;
  }
  FastbetPoker.prototype.formatBall = function (ball) {
    return CONSTANTS.POKER[ball];
  }
  //************* 扑克类 end

  //************* 静态类快投，仅用于页面渲染 start
  var FastbetStatic = function(option) {
    Fastbet.call(this, option);
  }
  FastbetStatic.prototype = new Fastbet();
  FastbetStatic.prototype.initFastbet = function(){
    if(this.option.fastbetModel) {
      this.fastbetModel = this.option.fastbetModel;
      var result = {};
      result.serviceTime = this.fastbetModel.serviceTime;
      result.data = this.fastbetModel;
      this.trigger(CONSTANTS.CAllBACK.INIT_FAST_SUCCESS, result);
    } else {
      this.trigger(CONSTANTS.CAllBACK.INIT_FAST_ERROR, {"errorCode" : 10008});
    }
  }
  //************* 静态类快投，仅用于页面渲染 end


  var LotteryCode = {
    "x115" : [211, 212, 213, 214, 215, 210, 216, 217, 218, 219, 260, 261, 262, 263, 264, 265, 266, 267, 268, 269, 270, 271, 272, 273, 274, 275, 276, 277, 291],
    "k3" : [230, 231, 232, 233, 278, 235, 279, 280, 237, 238, 234, 239, 281, 282, 236, 283],
    "ssc" : [201, 202, 203, 204, 205],
    "kl10" : [222, 220, 221, 284, 286, 288, 285, 287, 289, 290],
    "poker" : [225]
  }

  function getInstance(option) {
    var lotteryCode = option.lotteryCode;
    if(option["static"] && option.fastbetModel) {
      return new FastbetStatic(option);
    }
    if(100 == lotteryCode) {
      return new FastbetSsq(option);
    } else if(101 == lotteryCode) {
      return new FastbetQlc(option);
    } else if(102 == lotteryCode) {
      return new FastbetDlt(option);
    } else if(103 == lotteryCode) {
      return new FastbetPl5(option);
    } else if(104 == lotteryCode) {
      return new FastbetPl3(option);
    } else if(105 == lotteryCode) {
      return new FastbetF3d(option);
    } else if(107 == lotteryCode) {
      return new FastbetQxc(option);
    } else if(LotteryCode.x115.indexOf(lotteryCode) > -1) {
      return new Fastbet11x5(option);
    } else if(LotteryCode.k3.indexOf(lotteryCode) > -1) {
      return new FastbetK3(option);
    } else if(LotteryCode.ssc.indexOf(lotteryCode) > -1) {
      return new FastbetSsc(option);
    } else if(LotteryCode.kl10.indexOf(lotteryCode) > -1) {
      return new FastbetKl10(option);
    } else if(LotteryCode.poker.indexOf(lotteryCode) > -1) {
      return new FastbetPoker(option);
    }
  }




  yicai.fastbet = function(option) {
   return getInstance(option)._init();
  }
})(jQuery, YiCai)
