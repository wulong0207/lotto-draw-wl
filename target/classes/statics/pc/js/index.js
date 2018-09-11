(function($, yicai) {
	// 购彩导航
	$(function() {
		$.get(baseData.contextPath + "/index/operlottery.html", function(html) {
			var $nav = $(".header-navbar .lotterys .lotterys-list-box").append(html);
			$nav.find(".navLi_content dl").each(function() {
				if($(this).find("ul > li").length == 0) {
					$(this).remove();
				}
			});
			$( ".nav_li" ).each( function( index ){
				$( this ).mouseover( function(){
					$( ".navLi_hover" ).show();
					$( ".arrow_blue" ).show();
					$( this ).find( ".arrow_blue" ).hide();
					$(this).addClass("cur").siblings().removeClass("cur");
					$( ".navLi_hover .navLi_content" ).hide().eq( index ).show();
				});
				$( ".menuShow" ).mouseleave( function(){
					$( ".navLi_hover" ).hide();
					$(".nav_li").removeClass("cur");
					$( ".arrow_blue" ).show();
				});
			});
		}, "html");
	})
	// 客服电话、安卓下载地址
	$(function() {
		$.ajax({
			"url" : baseData.apiPath + "/pc/v1.0/home/customertel",
			"type" : "GET",
			"headers" : yicai.CONSTANTS.HEADERS,
			"dataType" : "json",
			"success" : function(result) {
				if(yicai.isSuccess(result)) {
					$("#customertel").text(result.data);
				}else{
					console.error(result);
				}
			}
		});
		$.ajax({
			"url" : baseData.apiPath + "/pc/v1.0/home/channelVersion",
			"type" : "GET",
			"headers" : yicai.CONSTANTS.HEADERS,
			"dataType" : "json",
			"success" : function(result) {
				if(yicai.isSuccess(result)) {
					$(".a-app-android").attr("href", result.data.wapAppUrl);
				}else{
					console.error(result);
				}
			}
		});
	})
	// 用户
	$(function() {
		$("body").on("user.refresh", function() {
			var user = yicai.user;
			var token = user.getToken();
			if(yicai.isUndefined(token)){
				$(".header .login_after_li").hide();
				$(".header .mylottery_nogoods").hide();
				$(".header .mylottery_goods").hide();
				return;
			}
			var OrderConstant = {
				"BuyType":{
					1: "代购", 2: "追号", 3: "合买"
				}
			}
			//用户信息
			$.ajax({
				"url" : baseData.apiPath + "/pc/v1.0/member/index",
				"type" : "POST",
				"headers" : yicai.CONSTANTS.HEADERS,
				"contentType": yicai.CONSTANTS.API_CONTENT_TYPE,
				"data" : JSON.stringify({"token":token}),
				"dataType" : "json",
				"success" : function(result) {
					if(!yicai.isSuccess(result)){
						$(".header .login_after_li").remove();
						return;
					}
					var userInfo = result.data;
					$(".header .login_before_li").remove();
					//用户信息
					var $accountBox = $(".header .login_after_li .account-box");
					var nickName = userInfo["nk_nm"] || userInfo["acc"] ||  userInfo["mob"] || userInfo["em"];
					$accountBox.find(".afterArrow").text("你好，" + nickName);
					$accountBox.find(".money-detail em.num").text(yicai.formatNumber(userInfo["use_blc"] || 0));
					// 安全等级
					var sfIntgl = userInfo["sf_intgl"];
					var $safeBarBg = $accountBox.find(".safe .safe-bar-bg");
					$safeBarBg.find("em.num i").text(sfIntgl);
					$safeBarBg.find("span.safe-bar").css("width", sfIntgl+"%");
					if(sfIntgl >= 100) {
						$accountBox.find(".safe .safe-bar-tip").text("高").css("color","#2eb570");
						$safeBarBg.find("span.safe-bar").css("background-color","#2eb570");
					}else if(sfIntgl >= 60) {
						$accountBox.find(".safe .safe-bar-tip").text("中").css("color","#ff9900");
						$safeBarBg.find("span.safe-bar").css("background-color","#ff9900");
					}else{
						$accountBox.find(".safe .safe-bar-tip").text("低").css("color","#ed1c24");
						$safeBarBg.find("span.safe-bar").css("background-color","#ed1c24");
					}
					// 余额
					var userStatus = user.getStatus() || {};
					var $hideMoney = $("#hideMoney");
					if($hideMoney.hasClass("showMoney") && !userStatus.moneyHide) {
						$hideMoney.click();
					}else if($hideMoney.hasClass("hideMoney") && userStatus.moneyHide){
						$hideMoney.click();
					}
					$(".header .login_after_li").show();
				}
			});

			// 用户订单
			// 订单信息
			var queryHomeOrderUrl =baseData.apiPath + "/pc/v1.0/home/queryHomeOrderInfoList";
			var $func = $.when($.ajax({
				"url" : queryHomeOrderUrl,
				"type" : "POST",
				"data" : {"token":token, "status": 1},
				"headers" : yicai.CONSTANTS.HEADERS,
				"dataType" : "json"
			}), $.ajax({
				"url" : queryHomeOrderUrl,
				"type" : "POST",
				"data" : {"token":token, "status": 2},
				"headers" : yicai.CONSTANTS.HEADERS,
				"dataType" : "json"
			}));
			$func.done(function(param1,  param2){
				var result1 = param1[0], result2 = param2[0];
				if(!yicai.isSuccess(result1) && !yicai.isSuccess(result2)){
					$(".header .mylottery_nogoods").hide();
					$(".header .mylottery_goods").hide();
					return;
				}
				var data1 = result1.data, data2 = result2.data;
				var orderlist1 = data1.orderListInfoBOs || [], orderlist2 = data2.orderListInfoBOs || [];
				if(orderlist1.length == 0 && orderlist2.length == 0) {
					$(".mylottery_nologin").hide();
					$(".header .mylottery_goods").hide();
					$(".header .mylottery_nogoods").show();
					$(".mylottery_nogoods .total_goods em:eq(0)").text(data1.betNum || 0);
					$(".mylottery_nogoods .total_goods em:eq(1)").text(data1.winNum || 0);
					return;
				}
				$(".mylottery_nologin").hide();
				$(".header .mylottery_nogoods").hide();
				$(".mylottery_goods .total_goods em:eq(0)").text(data1.betNum || 0);
				$(".mylottery_goods .total_goods em:eq(1)").text(data1.winNum || 0);
				var $tabContent = $(".mylottery_goods .tab_content");
				// 未完成订单
				if(orderlist1.length == 0) {
					$("<ul class=\"tab_list noOrder\"></ul>").appendTo($tabContent);
				}else{
					var $ul = $("<ul class=\"tab_list\"></ul>").appendTo($tabContent);
					for(var i = 0; i < orderlist1.length; i++) {
						var order = orderlist1[i];
						var $li = $("<li><span class=\"l_name\"><span></span><br><em class=\"num\"></em></span><span class=\"l_money\"><em class=\"c999\"><em class=\"num red\"></em>元</em><span></span></span><span class=\"l_result\"><em class=\"red\"></em></span><span class=\"l_btn\"><a class=\"btn_arrowR\"></a></span></li>");
						$li.appendTo($ul);
						$li.find(".l_name > span").text(order.lotteryName);
						if(order.orderUnionStatus == 1) {
							$li.find(".l_name > em").append(order.showDate.substr(5, 11)+"截止").addClass("red" );
						}else{
							$li.find(".l_name > em").append(order.showDate.substr(5)).addClass( "c999");
						}
						$li.find(".l_money > em> em.num").text(yicai.formatNumber(order.orderAmount));
						$li.find(".l_money > span").html("&nbsp;&nbsp;" + OrderConstant.BuyType[order.buyType]);
						$li.find(".l_result > em").text(order.orderUnionStatusText);
						(function(orderCode) {
							$li.click(function() {
								window.open(baseData.webCpUrl + "/order/" + orderCode, "_blank");
							});
						})(order.orderCode)
					}
				}
				// 已完成订单
				if(orderlist2.length == 0) {
					$("<ul class=\"tab_list noOrder\"></ul>").appendTo($tabContent).hide();
				}else{
					var $ul = $("<ul class=\"tab_list\"></ul>").appendTo($tabContent);
					for(var i = 0; i < orderlist2.length; i++) {
						var order = orderlist2[i];
						var $li = $("<li><span class=\"l_name\"><span></span><br><em class=\"num\"></em></span><span class=\"l_money\"><em class=\"c999\"><em class=\"num red\"></em>元</em><span></span></span><span class=\"l_result\"><em class=\"red\"></em></span><span class=\"l_btn\"><a class=\"btn_arrowR\"></a></span></li>");
						$li.appendTo($ul);
						$li.find(".l_name > span").text(order.lotteryName);
						$li.find(".l_name > em").append(order.showDate.substr(5)).addClass( "c999");
						$li.find(".l_money > em> em.num").text(yicai.formatNumber(order.orderAmount));
						$li.find(".l_money > span").html("&nbsp;&nbsp;" + OrderConstant.BuyType[order.buyType]);
						$li.find(".l_result > em").text(order.orderUnionStatusText);
						(function(orderCode) {
							$li.click(function() {
								window.open(baseData.webCpUrl + "/order/" + orderCode, "_blank");
							});
						})(order.orderCode)
					}
					$ul.hide();
				}
				$(".header .mylottery_goods").show();
			});
		})
		//隐藏余额
		$("#hideMoney").click(function(){
			if($(this).hasClass("hideMoney")){
				$(this).siblings('.money-detail').hide();
				$(this).removeClass('hideMoney').addClass('showMoney');
				$(this).html('显示余额');
				yicai.user.updateStatus({moneyHide:true});
			}
			else{
				$(this).siblings('.money-detail').show();
				$(this).removeClass('showMoney').addClass('hideMoney');
				$(this).html('隐藏');
				yicai.user.updateStatus({moneyHide:false});
			}
		});
		
		//我的订单选项卡
		$(".mylottery_goods .tab .tab-a").click(function(){
			index=$(".mylottery_goods .tab .tab-a").index(this);
			$(this).addClass("cur").siblings().removeClass("cur");
			$(".mylottery_goods .tab_content .tab_list").eq(index).show().siblings().hide();
		});
		
		//退出登录
		$(".login_after_li .logout").click(function() {
			 yicai.user.logout();
		});
		
		$("body").trigger("user.refresh");
	});
	$(function() {
	  //选择彩期模拟select
	  $(".periods").click(function (event) {
	    $(this).next(".periodsSelect").slideToggle().siblings(".periodsSelect").slideUp();
	    var perWid = $(this).width();
	    $(".periods").removeClass("down");
	    $(".periodsSelect").hide();
	    var ps = $(this).next(".periodsSelect");
	    if (ps.css("display", "none")) {
	      $(this).addClass("down");
	      ps.css('width', perWid + 30).show();
	    } else {
	      ps.hide();
	    }
	    event.stopPropagation();
	  })
	  //点击区域外隐藏模拟select
	  $(".periodsSelect li").click(function (e) {
	    var periods = $(this).text();
	    $(this).parent().prev(".periods").text(periods);
	  })
	  $(".laydate-icon").click(function (e) {
	    $(".periodsSelect").addClass("showDown");
	    $(".periods").removeClass("down");
	  });
	  $('body').click(function (e) {
	    $(".periodsSelect").hide();
	    $(".periods").removeClass("down");
	  })
	  
	  //选项卡
	  var index = 0;
	  $(document).on("click", ".tab-head .tab", function () {
	    index = $(".tab-head .tab").index(this);
	    $(this).addClass("cur").siblings().removeClass("cur");
	    $(this).next(".tab").addClass("lineNone").siblings().removeClass("lineNone");
	    $(".tab-body .tab-content ").eq(index).show().siblings().hide();
	  });
	
	  //彩果详情隔行换色
	  $(".zc-table tbody tr:nth-child(even)").css("background-color", "#f5f5f5");
		
	});
	//地方彩往期开奖
	$(function() {
		var _pageIndex = 0;
		function goPage(page) {
			if(page<0) {
				page = 0;
			}
			var lotteryKey = baseData.lotteryKey;
			$.ajax({
				"url" : baseData.apiPath + "/pc/v1.0/draw/"+lotteryKey,
				"type" : "GET",
				"headers" : yicai.CONSTANTS.HEADERS,
				"data" : {pageIndex:page, pageSize:5},
				"dataType" : "json",
				"success" : function(result) {
					if(!yicai.isSuccess(result)) {
						yicai.message.info(result.message);
						return;
					}
					if(!result.data || !result.data.data || result.data.data.length == 0) {
						return;
					}
					_pageIndex = page;
					$(".dfc-det-table table tr:gt(0)").remove();
					var $table = $(".dfc-det-table tbody");
					$.each(result.data.data, function(i, pastDrawLottery) {
						var $tr = $("<tr></tr>").addClass((i%2==0)?"whiteBg":"bgf5");
						var $td;
						//彩期
						$("<td></td>").html(pastDrawLottery.issueCode).appendTo($tr);
						//号码
						$td = $("<td></td>");
						if(pastDrawLottery.drawCode) {
							var $numListSpan = $('<span class="num-list"></span>').appendTo($td);
							var drawCode = pastDrawLottery.drawCode;
							var drawCodeArr= new Array();
							if(drawCode.indexOf(",")>=0 && drawCode.indexOf("|")>=0) {
								$.each(drawCode.split("|"), function(j, codeArr) {
									drawCodeArr[j]=codeArr.split(",");
								});
							}else{
								drawCodeArr[0]=drawCode.split(/[,\\|]/);
							}
							if(drawCodeArr.length>0) {
								$.each(drawCodeArr[0], function(j, code) {
									$('<b class="red"></b>').html(code+"&nbsp;").appendTo($numListSpan);
								})
							}
							if(drawCodeArr.length>1) {
								$.each(drawCodeArr[1], function(j, code) {
									$('<b class="blue"></b>').html(code+"&nbsp;").appendTo($numListSpan);
								})
							}
						}else{
							$td.html("号码获取中");
						}
						$tr.append($td)
						//查看按钮
						$td = $("<td></td>");
						$("<a>查看</a>").attr("href",baseData.contextPath+"/"+baseData.lotteryKey+"/"+pastDrawLottery.issueCode+".html").appendTo($td);
						$tr.append($td);
						$tr.appendTo($table);
					});
				}
			});
		}
		//往期开奖
		$("#prevPast").click(function() {
			goPage(_pageIndex-1);
		});
		$("#nextPast").click(function() {
			goPage(_pageIndex+1);
		});
	});
})(jQuery, YiCai)
