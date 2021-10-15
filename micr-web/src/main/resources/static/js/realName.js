

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

//同意实名认证协议
$(function() {

	//姓名
	$("#realName").on("blur",function(){
		let name = $.trim( $("#realName").val() );
		if( name == ""){
			showError("realName","必须输入姓名");
		} else if( name.length < 2){
			showError("realName","姓名至少是2个字符");
		} else if( !/^[\u4e00-\u9fa5]{0,}$/.test(name)){
			showError("realName","姓名必须是中文");
		} else {
			showSuccess("realName");
		}
	})

	//身份证号
	$("#idCard").on("blur",function(){
		let card = $.trim( $("#idCard").val() );
		if( card == ""){
			showError("idCard","必须输入身份证号");
		} else if( !/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(card)){
			showError("idCard","身份证格式不正确");
		} else {
			showSuccess("idCard");
		}
	});

	//认证按钮
	$("#btnRealName").on("click",function(){
		$("#realName").blur();
		$("#idCard").blur();

		let err = $("[id $='Err']").text();
		if( err == ""){
			let phone= $.trim( $("#phone").val() );
			let name = $.trim( $("#realName").val() );
			let card = $.trim( $("#idCard").val() );
			$.ajax({
				url: contextPath + "/loan/realName",
				type:"post",
				data:{
					"phone":phone,
					"name":name,
					"card":card
				},
				dataType:"json",
				success:function(resp){
					if(resp.code == 0 ){
						//进入到用户中心
						window.location.href = contextPath + "/loan/page/myCenter";
					} else {
						alert(resp.msg);
					}
				},
				error:function(){
					alert("请稍后重试");
				}
			})
		}
	})



	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRealName").attr("disabled", false);
			$("#btnRealName").removeClass("fail");
		} else {
			$("#btnRealName").attr("disabled","disabled");
			$("#btnRealName").addClass("fail");
		}
	});
});