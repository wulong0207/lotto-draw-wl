$(document).ready(function () {
  //主页高频和地方开奖列表显隐
  $('.h-tab-item .place-head').toggle(function () {
    $(this).siblings(".kj-list").slideDown();
    $(this).find(".arrow-r-g").css({'transform': 'translateY(0.35rem) translateX(-0.2rem) rotate(270deg)'})
  }, function () {
    $(this).siblings(".kj-list").slideUp();
    $(this).find(".arrow-r-g").css('transform', 'rotate(90deg)')
  });
  $('.h-tab-item .place-head:first').click();

  //开奖列表竞足对阵切换
  teamSwiper();

  //竞技彩彩期下拉
  $('.date-down').click(function () {
    $(this).find(".date-down-list").slideToggle(100);
  });

  //竞技彩输入搜索框聚焦事件
  $(".search-input label").click(function () {
    $(this).find("em").hide();
    $(this).siblings(".ic-delete").show();
    $(this).css("text-align", "left");
    $(this).css("width", "85%");
    $(this).siblings("input").focus();
  });
  //竞技彩输入搜索框关闭事件
  $(".search-input .ic-delete").click(function () {
    $(this).siblings("label").css("text-align", "center");
    $(this).siblings("label").css("width", "94%");
    $(this).siblings("label").find("em").show();
    $(this).siblings("input").val("").blur();
    $(this).hide();
    searchTeam();
  });
  //竞技彩开奖详情tab切换
  kjSportSwiper();

  //搜索事件
  $("#search").on('keypress', function (e) {
    var keycode = e.keyCode;
    var searchName = $(this).val();
    if (keycode == '13') {
      e.preventDefault();
      //请求搜索接口
      searchTeam(searchName);
      $(this).blur();
    }
  });
});

//球队搜索
function searchTeam(searchName) {
	searchName = $.trim(searchName).toUpperCase();
	var $noResult =  $("ul.kj-sport-list ~ div.no-result");
	var $p = $("ul.kj-sport-list + p");
	if(searchName == "") {
		$noResult.remove();
		$p.show();
		$("ul.kj-sport-list>li.kj-sport-item:hidden").show();
		return;
	}
	$("ul.kj-sport-list>li.kj-sport-item:visible").hide();
	var $search = $("ul.kj-sport-list>li.kj-sport-item .match-team:contains("+searchName+")");
	if($search.length>0) {
		$noResult.remove();
		$p.show();
		$search.closest("li.kj-sport-item").show();
	}else{
		$p.hide();
		if($noResult.length > 0) {
			$noResult.show();
		}else{
			$p.after("<div class=\"kj-sport-list no-result\">暂搜索不到该球队</div>")
		}
	}
}

//开奖列表竞足对阵自动轮播
function teamSwiper() {
  var mySwiper = new Swiper('.team-swiper-container', {
    direction: 'vertical',
    loop: true,
    speed: 500,
    autoplay: 3000,
    autoplayDisableOnInteraction: false,
  });
}

//竞彩足球开奖详情切换
function kjSportSwiper() {
  if (document.getElementsByClassName("kj-detail-tab-list")[0] && document.getElementsByClassName("kj-detail-content")[0]) {
    var kjhd = document.getElementsByClassName("kj-detail-tab-list")[0].getElementsByTagName("li");
    var kjbd = document.getElementsByClassName("kj-detail-content")[0].getElementsByClassName("kj-detail-item");
    for (var i = 0; i < kjhd.length; i++) {
      kjhd[i].onclick = function () {
        doTabs(this);
      }
    }

    function doTabs(obj) {
      for (var i = 0; i < kjhd.length; i++) {
        if (kjhd[i] == obj) {
          kjhd[i].className = "cur";
          kjbd[i].className = "kj-detail-item cur";
        } else {
          kjhd[i].className = "";
          kjbd[i].className = "kj-detail-item";
        }
      }
    }
  }
}




