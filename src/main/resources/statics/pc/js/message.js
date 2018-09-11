(function($, yicai) {

  var buttons = {
    "ok" : {
      "id" : "ok",
      "name" : "确定",
      "callback" : function(message) {
        message.close();
      }
    },
    "close" : {
      "id" : "close",
      "name" : "关闭",
      "className" : "cancel",
      "callback" : function(message) {
        message.close();
      }
    },
    "cancel" : {
      "id" : "cancel",
      "name" : "取消",
      "className" : "cancel",
      "callback" : function(message) {
        message.close();
      }
    }
  }
 
  var defaults = {
    "close" : true,
    "type" : "info",
    "showBtn" : true
  } 
  var Message = function(message, option) {
    this.message = message;
    this.option = $.extend({}, defaults, option, true);
    this.showBtn = this.option.btns && this.option.btns.length > 0 ? true : false;
    this._init();
  }

  Message.prototype = {
    "_init" : function() {
      this._render();
      this._event();
    },
    "_render" : function() {
      var _this = this;
      var $message = $('<div class="message"><div class="message-overlay"></div><div class="message-main"style="margin-top: -108.5px;"></div></div>').appendTo("body");
      var $main = $message.find(".message-main");
      if(!yicai.isUndefined(this.option.title) || this.option.close) {
        var $head = $('<div class="message-head"></div>').appendTo($main);
        if(!yicai.isUndefined(this.option.title)) {
          $head.append('<span>' + this.option.title + '</span>');
        }
        if(this.option.close) {
          $head.append('<i class="icon icon-close"></i>');
        }
      }
      var $body = $('<div class="message-body"></div>').appendTo($main);
      var $content = $('<div class="message-content"></div>').appendTo($body)
      if(this.option.icon && this.option.icon != "none") {
        var $type = $('<div class="message-' + this.option.type + '"></div>').appendTo($content);
        $type.append('<div class="message-middle">' + this.message + '</div>');
      } else {
        $content.append('<div>' + this.message + '</div>');
      }
      
      var btns = this.option.btns || 0;
      if(btns.length > 0) {
        var $btns = $('<div class="message-btns"></div>').appendTo($body);
        $.each(btns, function(i, btn) {
          var $btn = $('<a>' + btn.name + '</a>').appendTo($btns);
          if(!yicai.isUndefined(btn.className)) {
            $btn.addClass(btn.className);
          }
          if(!yicai.isUndefined(btn.callback)) {
            $btn.click(function() {
              btn.callback(_this, this);
            });
          }
        });
      }
      this.$element = $message;
      return this;
    },
    "_event" : function() {
      var _this = this;
      this.$element.find(".icon-close").click(function() {
        _this.close();
      });
    },
    "show" : function() {
      this.$element.show()
      return this;
    },
    "close" : function() {
      this.$element.hide().remove();
      return this;
    }
  }

  Message.info = function(message, option) {
  	option = $.extend({}, defaults, option, true);
  	option.type = "info";
    option.icon = option.type;
  	var btns = option.btns
  	if(btns) {
  		$.each(btns, function(i, btn) {
	  		if(buttons[btn.id]) {
	  			btn = $.extend({}, buttons[btn.id], btn);
	  		}
	  		option.btns[i] = btn;
	  	});
  	} else if(option.showBtn){
  		option.btns = [buttons["ok"], buttons["close"]]
  	}
  	if(yicai.isUndefined(option.title)) {
  		option.title="温馨提示";
  	}
    var message = new Message(message, option);
  }

  Message.loading = function(message, option) {
  	option = $.extend({}, defaults, option, true);
  	option.type = "loading";
    option.icon = option.type;
  	var btns = option.btns
    if(!btns && option.close) {
		  option.btns = [buttons["close"]]
	  }
  	if(yicai.isUndefined(option.title)) {
  		option.title="";
  	}
  	var message = new Message(message, option);
  }

  Message.error = function(message, option) {
  	option = $.extend({}, defaults, option, true);
  	option.type = "error";
    option.icon = option.type;
  	var btns = option.btns
  	if(btns) {
  		$.each(btns, function(i, btn) {
	  		if(buttons[btn.id]) {
	  			btn = $.extend({}, buttons[btn.id], btn);
	  		}
	  		option.btns[i] = btn;
	  	});
  	} else if(option.showBtn){
  		option.btns = [buttons["ok"], buttons["close"]]
  	}
  	if(yicai.isUndefined(option.title)) {
  		option.title="温馨提示";
  	}
    var message = new Message(message, option);
  }

  Message.warn = function(message, option) {
  	option = $.extend({}, defaults, option, true);
	  option.type = "warn";
    option.icon = option.type;
  	var btns = option.btns
  	if(btns) {
  		$.each(btns, function(i, btn) {
	  		if(buttons[btn.id]) {
	  			btn = $.extend({}, buttons[btn.id], btn);
	  		}
	  		option.btns[i] = btn;
	  	});
  	} else if(option.showBtn){
  		option.btns = [buttons["ok"], buttons["close"]]
  	}
  	if(yicai.isUndefined(option.title)) {
  		option.title="温馨提示";
  	}
    var message = new Message(message, option);
  }


  Message.success = function(message, option) {
    option = $.extend({}, defaults, option, true);
	  option.type = "success";
    option.icon = option.type;
  	var btns = option.btns
  	if(btns) {
  		$.each(btns, function(i, btn) {
	  		if(buttons[btn.id]) {
	  			btn = $.extend({}, buttons[btn.id], btn);
	  		}
	  		option.btns[i] = btn;
	  	});
  	} else if(option.showBtn){
  		option.btns = [buttons["ok"], buttons["close"]]
  	}
  	if(yicai.isUndefined(option.title)) {
  		option.title="温馨提示";
  	}
    var message = new Message(message, option);
  }

  Message.alert = function(message, option) {
    option = $.extend({}, defaults, option, true);
    option.icon = "none";
    var btns = option.btns
    if(btns) {
      $.each(btns, function(i, btn) {
        if(buttons[btn.id]) {
          btn = $.extend({}, buttons[btn.id], btn);
        }
        option.btns[i] = btn;
      });
    } else if(option.showBtn){
      option.btns = [buttons["ok"], buttons["close"]]
    }
    if(yicai.isUndefined(option.title)) {
      option.title="温馨提示";
    }
    var message = new Message(message, option);
  }

  yicai.message = function(message, option) {
    return new Message(message, option);
  };

  yicai.message.info = Message.info;
  yicai.message.loading = Message.loading;
  yicai.message.error = Message.error;
  yicai.message.warn = Message.warn;
  yicai.message.success = Message.success;
  yicai.message.alert = Message.alert;

})(jQuery, YiCai)
