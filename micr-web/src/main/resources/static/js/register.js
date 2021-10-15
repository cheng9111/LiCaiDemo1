//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}


//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//注册协议确认
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});

	//phone的blur
	$("#phone").on("blur",function(){
		let phone = $.trim( $("#phone").val() );
		if( phone == ""){
			showError("phone","必须输入手机号!!!");
		} else if( phone.length != 11){
			showError("phone","手机号格式不正确");
		} else if( !/^1[1-9]\d{9}$/.test(phone)){
			showError("phone","手机号格式不正确");
		} else {
			showSuccess("phone");
			//判断手机号是否注册过
			$.ajax({
				url: contextPath+"/loan/help/phone",
				data:{
					phone:phone
				},
				dataType:"json",
				success:function(resp){
					if( resp.code == 0 ){
						showSuccess("phone")
					} else {
						showError("phone",resp.msg)
					}
				},
				error:function(){
					alert("请求失败,可以重新操作")
				}
			})

		}

	})



	//密码loginPassword
	$("#loginPassword").on("blur",function(){
		let pwd = $.trim( $("#loginPassword").val() );
		if( pwd == ""){
			showError("loginPassword","必须输入密码");
		} else if( pwd.length < 6 || pwd.length > 20){
			showError("loginPassword","密码长度是6-20位");
		} else if ( !/^[0-9a-zA-Z]+$/.test(pwd) ){
			showError("loginPassword","只能使用数字和大小写英文字母");
		} else if( !/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(pwd)){
			showError("loginPassword","密码应同时包含英文和数字");
		} else {
			showSuccess("loginPassword");
		}
	})

	//获取验证的按钮
	$("#messageCodeBtn").on("click",function(){
		//按钮
		let btn = $("#messageCodeBtn");
		if(btn.hasClass("on")){
			return;
		}
		//调用phone的blur事件
		$("#phone").blur();
		//获取Err的html的内容
		let err = $("#phoneErr").text();
		if( err == ""){ //手机号格式正确 , 发验证码
			//让按钮的背景色是 灰色的
			btn.addClass("on");
			//倒计时
			$.leftTime(5,function(d){
				let second = parseInt(d.s);
				if( second == 0 ){
					btn.text("获取验证码");
					btn.removeClass("on");
				} else {
					btn.text( second+"秒后获取");
				}
			})
			//发起请求,告诉服务器发送验证码
			$.ajax({
				url:contextPath+"/loan/help/sendSMS",
				data:{
					phone: $.trim( $("#phone").val() )
				},
				dataType:"json",
				success:function(resp){
					if( resp.code != 0 ){
						showError("messageCode",resp.msg);
					}
				},
				error:function(){
					alert("请求从新获取验证码")
				}
			})
		}
	})


	//验证码的文本框
	$("#messageCode").on("blur",function(){
		let code = $.trim( $("#messageCode").val() );
		if( code == "" ){
			showError("messageCode","必须输入验证码");
		} else if( code.length != 6){
			showError("messageCode","验证码是6位的");
		} else if( isNaN(code)){
			showError("messageCode","验证是数字的");
		} else {
			showSuccess("messageCode");
		}
	})

	//注册按钮单击
	$("#btnRegist").on("click",function(){
		//1.检查所有数据
		$("#phone").blur();
		$("#loginPassword").blur();
		$("#messageCode").blur();

		//2.判断数据验证完成
		let err = $("[id $= 'Err']").text();
		if( err == ""){
			let phone = $.trim($("#phone").val());
			let pwd = $.trim($("#loginPassword").val());
			let code = $.trim($("#messageCode").val());

			$.ajax({
				url: contextPath+"/loan/register",
				type:"post",
				data:{
					"phone":phone,
					"pwd": $.md5(pwd),
					"code":code
				},
				dataType:"json",
				success:function(resp){
					if( resp.code == 0 ){
						window.location.href= contextPath+"/loan/page/realName";
					} else {
						alert(resp.msg);
					}
				},
				error:function(){
					alert("请求错误,稍后重试!!!")
				}
			})
		}

	})

});
