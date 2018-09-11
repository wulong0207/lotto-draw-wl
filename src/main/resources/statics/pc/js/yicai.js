(function($){
  "use strict"
  
  var CONSTANTS = {
  	"SUCCESS" : 1,
    "TRUE" : 1,
    "API_CONTENT_TYPE" : "application/json;charset=utf-8",
    "DOMAIN" : "2ncai.com",
    "HEADERS" : {
      "pId" : 1,
      "cId" : 2,
      "vId" : baseData.version,
      "eId" : (function() {
        var matched = $.uaMatch(navigator.appVersion);
    	  return matched.browser + "-" + matched.version;
      })()
    }
  }
  var YiCai = {
    "isUndefined" : function (obj) {
      return obj == null || typeof(obj) == "undefined";
    },
    "isSuccess" : function (result) {
      return !this.isUndefined(result) && result.success == CONSTANTS.SUCCESS;
    },
    "setUrlParam" : function(params) {
      var srcParams = {};// {key:[value1, value2]}
      var regex = /([^?&=]+)=?([^&]*)/g;
      var group = null;
      while((group = regex.exec(window.location.search)) != null) {
        var key = group[1], value=group[2];
        if(!srcParams[key]) {
          srcParams[key]=[];
        }
        srcParams[key].push(value);
      }
      var newParams = $.extend(srcParams, params);
      return "?" + $.param(newParams, true);
    },
    "formatNumber" : function(number) {
      number = Number(number||0).toString();
      var pointIndex = number.indexOf(".");
      var point = "";
      if(pointIndex > -1) {
        var tmp = number;
        number = tmp.substring(0, pointIndex);
        point = tmp.substring(pointIndex + 1);
      }
      var result = "";
      while (number.length > 3) {
        result = ',' + number.slice(-3) + result;
        number = number.slice(0, number.length - 3);
      }
      result = number + result;
      return result + (point == "" ? "" : "." + point);
    },
    "formatMoney" : function(money) {
      var result;
      var t;
      money = Number(money || 0);
      if(money >= 100000000){
        result = money/100000000;
        t = "亿";
      }else if(money >= 10000) {
        result = money/10000;
        t = "万";
      }else{
        result = money;
        t = "";
      }
      result = Math.floor(result * 100) / 100 ;
      return result + t;
    },
    "getTime" : function(timeStr) {
      var arr = timeStr.split(/[- :]/);
      var date = new Date(arr[0], arr[1]-1, arr[2], arr[3]||0, arr[4]||0, arr[5]||0);
      return date.getTime()+date.getTimezoneOffset()*60*1000+8*60*60*1000;
    },
    "lpad" : function(num, n) {
      if(yicai.isUndefined(n)) {
        n = 2;
      }
      return (Array(n).join(0) + num).slice(-n);
    },
    "addNumber" : function(arg1, arg2) {
      var r1, r2, m, c;
      var arg1Str = arg1.toString();
      var arg2Str = arg2.toString();
      try {
        r1 = (arg1Str.indexOf(".") > -1) ? arg1Str.split(".")[1].length : 0;
      } catch (e) {
        r1 = 0;
      }
      try {
        r2 = (arg2Str.indexOf(".") > -1) ? arg2Str.split(".")[1].length : 0;
      } catch (e) {
        r2 = 0;
      }
      c = Math.abs(r1 - r2);
      m = Math.pow(10, Math.max(r1, r2));
      if (c > 0) {
          var cm = Math.pow(10, c);
          if (r1 > r2) {
              arg1 = Number(arg1Str.replace(".", ""));
              arg2 = Number(arg2Str.replace(".", "")) * cm;
          } else {
              arg1 = Number(arg1Str.replace(".", "")) * cm;
              arg2 = Number(arg2Str.replace(".", ""));
          }
      } else {
          arg1 = Number(arg1Str.replace(".", ""));
          arg2 = Number(arg2Str.replace(".", ""));
      }
      return (arg1 + arg2) / m;
    },
    "subNumber" : function(arg1, arg2) {
      var r1, r2, m, n;
      var arg1Str = arg1.toString();
      var arg2Str = arg2.toString();
      try {
        r1 = (arg1Str.indexOf(".") > -1) ? arg1Str.split(".")[1].length : 0;
      } catch (e) {
        r1 = 0;
      }
      try {
        r2 = (arg2Str.indexOf(".") > -1) ? arg2Str.split(".")[1].length : 0;
      } catch (e) {
        r2 = 0;
      }
      m = Math.pow(10, Math.max(r1, r2)); //last modify by deeka //动态控制精度长度
      n = (r1 >= r2) ? r1 : r2;
      return +((arg1 * m - arg2 * m) / m).toFixed(n);
    }
  };

  var yicai = window.yicai = window.YiCai = YiCai;

  /** 
  * 分页
  */
  var Page = function() {
    this._init();
  }
  Page.prototype = {
    "_init": function() {
      $(".pagebox .btn_submit").click(function() {
        var val = $.trim($(this).siblings("input.totxt").val());
        if(!new RegExp("^\\d+$").test(val)){
          return;
        }
        val = parseInt(val, 10);
        if(val == 0){
          return;
        }
        var $pagebox = $(this).closest(".pagebox");
        var max = parseInt($.trim($pagebox.find("ul.page-list>li:last").prev().text()), 10);
        if(val > max) {
          return;
        }
        var cur = parseInt($.trim($pagebox.find("ul.page-list>li.on").text()), 10);
        if(val == cur) {
          return;
        }
        window.location.href = window.location.pathname + yicai.setUrlParam({pageIndex : val - 1}) + window.location.hash;
      });
    }
  }
  
  YiCai.CONSTANTS = CONSTANTS;
  yicai.page = Page;

})(jQuery)