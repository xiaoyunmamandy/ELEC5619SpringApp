<%@ include file="/WEB-INF/views/include.jsp"%>
<html>
<head>
<title>self center</title>
<style type="text/css">
.navlink{
color:white;
}
#contentbar{
	margin-top:20px;
	color:white;
	
}

</style>
<script
	src="http://ajax.googleapis.com/ajax/libs/jquery/1.10.2/jquery.min.js"></script>
</head>
<body>
	<div id="selfinfodiv">
	<input type="hidden" value="${model.user.id }" name="userid"/>
	<img src="/imgUrl/${model.user.picture }" height="30px" width="30px"/></br>
	username: ${model.user.userName }</br>points: ${model.user.points }
	</div>
	<div id="contentbar">
	<img src="<c:url value="/resources/decimg/profile.png" />" height="20px" width="20px"/>&nbsp<a href="${pageContext.request.contextPath}/user/selfinfo" class="navlink"/>Profile</a></br></br>
	<img src="<c:url value="/resources/decimg/recipe-icon-6.png" />" height="20px" width="20px"/>&nbsp<a href="${pageContext.request.contextPath}/recipe/userrecipe/${model.user.id }" class="navlink"/>My recipes</a></br></br>
	<img src="<c:url value="/resources/decimg/folder_document.png" />" height="20px" width="20px"/>&nbsp<a href="${pageContext.request.contextPath}/recipe/mycollection/${model.user.id }" class="navlink"/>My collections</a></br></br>
	<img src="<c:url value="/resources/decimg/QA-17.png" />" height="20px" width="20px"/>&nbsp<a href="${pageContext.request.contextPath}/userquestions/${model.user.id }" class="navlink"/>My questions</a></br></br>
	
	</div>

</body>
</html>