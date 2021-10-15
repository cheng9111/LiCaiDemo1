$(function(){
	//手机号
	$("#phone").on("blur",function(){
		let phone = $.trim( $("#phone").val() );
		if( phone == ""){
			$("#showId").text("必须填写手机号")
		} else if( phone.length != 11){
			$("#showId").text("手机号格式不正确");
		} else if( !/^1[1-9]\d{9}$/.test(phone)) {
			$("#showId").text("手机号格式不正确")
		} else {
			$("#showId").text("");
		}
	})

	//密码
	$("#loginPassword").on("blur",function(){
		let pwd = $.trim( $("#loginPassword").val() );
		if( pwd == ""){
			$("#showId").text("必须输入密码");
		} else if( pwd.length < 6 || pwd.length > 20){
			$("#showId").text("密码长度是6-20位");
		} else if ( !/^[0-9a-zA-Z]+$/.test(pwd) ){
			$("#showId").text("只能使用数字和大小写英文字母");
		} else if( !/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(pwd)){
			$("#showId").text("密码应同时包含英文和数字");
		} else {
			$("#showId").text("");
		}
	})


	//注册按钮
	$("#btnLogin").on("click",function(){

		$("#phone").blur();
		$("#loginPassword").blur();

		let err = $.trim( $("#showId").text() );
		if(err == ""){

			//手机号, 密码, 短信验证码
			let phone = $.trim( $("#phone").val() );
			let pwd = $.trim( $("#loginPassword").val() );
			let code = "123456";

			//做ajax
			$.ajax({
				url: contextPath + "/user/login",
				type:"post",
				data:{
					"phone":phone,
					"pwd":$.md5(pwd),
					"code":code
				},
				dataType:"json",
				success:function(resp){
					if( resp.code == 0 ){
                       //登录成功,返回到来源页面
					   window.location.href = jsReturnUrl;
					} else {
						$("#showId").text(resp.msg);
					}
				},
				error:function(){
					alert("请重新登录");
				}
			})
		}
	})
})